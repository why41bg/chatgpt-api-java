package cn.why41bg.chatgpt.api.domain.chatgpt.service;

import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.domain.chatgpt.model.aggregate.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
import cn.why41bg.chatgpt.api.types.exception.TokenCheckException;
import cn.why41bg.chatgpt.common.Constants;
import cn.why41bg.chatgpt.domain.chat.ChatChoice;
import cn.why41bg.chatgpt.domain.chat.ChatCompletionRequest;
import cn.why41bg.chatgpt.domain.chat.ChatCompletionResponse;
import cn.why41bg.chatgpt.domain.chat.Message;
import cn.why41bg.chatgpt.session.IOpenAiSession;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname ChatService
 * @Description openai chatgpt 接口实现类
 * @Author 魏弘宇
 * @Date 2024/3/13 00:45
 */
@Slf4j
@Service
public class ChatService implements IChatService{

    @Resource
    private IOpenAiSession openAiSession;

    @Resource
    private IAuthService authService;

    @Value("${openai.chatgpt.sdk.config.auth-token}")
    private String token;

    @Override
    public ResponseBodyEmitter chatCompletions(ChatgptProcessAggregate aggregate)
            throws ChatgptException, TokenCheckException{
        // JWT校验
        if(!authService.checkToken(aggregate.getToken())) {
            throw new TokenCheckException(ResponseCode.PRIVILEGES_ERROR.getCode(), ResponseCode.PRIVILEGES_ERROR.getInfo());
        }

        // JWT有效，构建异步响应对象，设置连接时长为 5 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(5 * 60 * 1000L);
        emitter.onCompletion(() -> {
            log.info("流程问答完成");
        });
        emitter.onError(throwable -> {
            log.error("流失问答失败", throwable);
        });
        // 请求应答
        try {
            doMessageResponse(aggregate, emitter);
        } catch (JsonProcessingException e) {
            throw new ChatgptException(ResponseCode.UN_ERROR.getCode(), ResponseCode.UN_ERROR.getInfo());
        }

        // 请求应答返回
        return emitter;
    }

    private void doMessageResponse(ChatgptProcessAggregate aggregate,
                                   ResponseBodyEmitter emitter)
            throws JsonProcessingException {
        // 构建请求消息
        List<Message> messages = aggregate.getMessages().stream()
                .map(entity -> Message.builder()
                        .role(Constants.Role.valueOf(entity.getRole().toUpperCase()))
                        .content(entity.getContent())
                        .name(entity.getName())
                        .build())
                .collect(Collectors.toList());

        // 封装参数
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .stream(true)
                .messages(messages)
                .model(ChatCompletionRequest.Model.GPT_3_5_TURBO.getCode())
                .build();

        // 调用接口发送请求
        openAiSession.chatCompletions(chatCompletionRequest, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
                ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    if (Constants.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = chatChoice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                        emitter.complete();
                        break;
                    }

                    // 发送信息
                    try {
                        emitter.send(delta.getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

    }

}
