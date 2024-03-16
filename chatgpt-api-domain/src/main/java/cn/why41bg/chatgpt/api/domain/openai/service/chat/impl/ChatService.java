package cn.why41bg.chatgpt.api.domain.openai.service.chat.impl;

import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.service.chat.IChatService;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Classname ChatService
 * @Description openai chatgpt 接口实现类
 * @Author 魏弘宇
 * @Date 2024/3/13 00:45
 */
@Slf4j
@Service
public class ChatService implements IChatService {

    @Resource
    private IOpenAiSession openAiSession;

    @Resource
    private DefaultLogicFactory logicFactory;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${openai.api.access.access-num}")
    private Integer accessNum;

    @Value("${openai.api.access.access-fresh-time}")
    private long accessFreshTime;

    @Override
    public ResponseBodyEmitter chatCompletions(ChatgptProcessAggregate aggregate  // token，model，messages字段有效
    )
            throws ChatgptException, TokenCheckException, IOException {
        // 构建异步响应对象，设置连接时长为 5 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(5 * 60 * 1000L);

        // 回调函数注册
        emitter.onCompletion(() -> {
            log.info("流程问答完成");
        });
        emitter.onError(throwable -> {
            log.error("流失问答失败", throwable);
        });

        // 向规则过滤工厂请求服务
        RuleLogicEntity<ChatgptProcessAggregate> ruleLogicEntity = this.doLogicCheck(aggregate,
                DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),  // 调用频次过滤
                DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode());  // 敏感词过滤

        // 规则校验失败直接返回
        if (LogicCheckTypeValObj.REFUSE.equals(ruleLogicEntity.getType())) {
            emitter.send(ruleLogicEntity.getInfo());
            emitter.complete();
            return emitter;
        }

        // 规则校验通过，真正开始请求流式问答服务
        try {
            doMessageResponse(aggregate, emitter);
        } catch (JsonProcessingException e) {
            throw new ChatgptException(ResponseCode.UN_ERROR.getCode(), ResponseCode.UN_ERROR.getInfo());
        }

        // 异步响应对象返回之前要更新访问频次
        String accessKey = cn.why41bg.chatgpt.api.types.common.Constants.ACCESS_PREFIX + aggregate.getToken();
        String oldAccessNumStr = stringRedisTemplate.opsForValue().get(accessKey);
        if (oldAccessNumStr != null) {
            // 如果不为空，说明用户在通过访问频次校验和发起请求并更新访问频次这期间，访问频次数据没有过期，更新频次时不能更新过期时间
            String updatedAccessNumStr = String.valueOf(Integer.parseInt(oldAccessNumStr) - 1);
            stringRedisTemplate.opsForValue().set(accessKey, updatedAccessNumStr, 0);
        } else {
            // 如果为空，说明访问频次到了刷新时间，应该先刷新访问频次，再进行更新
            String updatedAccessNumStr = String.valueOf(accessNum - 1);
            stringRedisTemplate.opsForValue().set(accessKey, updatedAccessNumStr, accessFreshTime, TimeUnit.HOURS);
        }

        // 返回异步响应对象
        return emitter;
    }

    /**
     *
     * @param aggregate 请求聚合对象，token，model，messages属性有效
     * @param emitter 异步传输对象
     */
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
                .model(aggregate.getModel())
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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private RuleLogicEntity<ChatgptProcessAggregate> doLogicCheck(ChatgptProcessAggregate aggregate, String... logics) {
        Map<String, ILogicFilter> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatgptProcessAggregate> entity = null;
        for (String code : logics) {
            entity = logicFilterMap.get(code).filter(aggregate);
            if (!LogicCheckTypeValObj.SUCCESS.equals(entity.getType())) return entity;
        }
        return entity != null ? entity : RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .type(LogicCheckTypeValObj.SUCCESS).data(aggregate).build();
    }


}
