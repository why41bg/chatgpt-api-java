package cn.why41bg.chatgpt.api.domain.openai.service.chat.impl;

import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.repository.IOpenAiRepository;
import cn.why41bg.chatgpt.api.domain.openai.service.chat.IChatService;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
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
    private IOpenAiRepository openAiRepository;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseBodyEmitter chatCompletions(ChatgptProcessAggregate aggregate)
            throws ChatgptException, IOException {
        // 构建异步响应对象，设置连接时长为 1 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(60 * 1000L);

        // 回调函数注册
        emitter.onCompletion(() -> log.info("流程问答完成"));
        emitter.onError(throwable -> log.error("流失问答失败", throwable));

        // 请求仓储服务获取账户
        // 首先尝试从Redis中获取账户
        String accountKey = cn.why41bg.chatgpt.api.types.common.Constants.OPENID_ACCOUNT_PREFIX + aggregate.getOpenId();
        String accountJson = stringRedisTemplate.opsForValue().get(accountKey);
        // 解决缓存穿透
        if ("".equals(accountJson)) {
            emitter.send("非法请求");
            emitter.complete();
            return emitter;
        }
        UserAccountQuotaEntity userAccountQuotaEntity;
        if (accountJson == null) {
            // Redis中不存在账户，从Mysql中获取账户
            userAccountQuotaEntity = openAiRepository.queryUserAccount(aggregate.getOpenId());
            // 并将获取到的账户序列化之后缓存到Redis中，设置缓存时间为3小时
            accountJson = com.alibaba.fastjson2.JSON.toJSONString(userAccountQuotaEntity);
            stringRedisTemplate.opsForValue().set(accountKey, accountJson, 3, TimeUnit.HOURS);
        } else {
            // Redis中存在账户，进行反序列化
            userAccountQuotaEntity = com.alibaba.fastjson2.JSON.parseObject(accountJson, UserAccountQuotaEntity.class);
        }

        // 向规则过滤工厂请求服务
        RuleLogicEntity<ChatgptProcessAggregate> ruleLogicEntity = this.doLogicCheck(aggregate, userAccountQuotaEntity,
                DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode(),
                DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode(),
                DefaultLogicFactory.LogicModel.USER_QUOTA.getCode()
        );

        // 规则校验失败直接返回
        if (LogicCheckTypeValObj.REFUSE.equals(ruleLogicEntity.getType())) {
            emitter.send(ruleLogicEntity.getInfo());
            emitter.complete();
            return emitter;
        }

        // 规则校验通过，真正开始请求流式问答服务
        // 这里才是真正开始调用模型进行服务阶段
        try {
            doMessageResponse(aggregate, emitter);
        } catch (JsonProcessingException e) {
            throw new ChatgptException(ResponseCode.UN_ERROR.getCode(), ResponseCode.UN_ERROR.getInfo());
        }

        // 流式问答服务完成后，最后返回异步响应对象
        return emitter;
    }

    /**
     * @param aggregate 请求对象
     * @param emitter   异步响应对象
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

    private RuleLogicEntity<ChatgptProcessAggregate> doLogicCheck(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity data,
            // 请求过滤策略
            String... logics) {
        Map<String, ILogicFilter<UserAccountQuotaEntity>> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatgptProcessAggregate> entity = null;
        for (String code : logics) {
            if (DefaultLogicFactory.LogicModel.NULL.getCode().equals(code)) continue;
            entity = logicFilterMap.get(code).filter(aggregate, data);
            if (!LogicCheckTypeValObj.SUCCESS.equals(entity.getType())) return entity;
        }
        return entity != null ? entity : RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .type(LogicCheckTypeValObj.SUCCESS).data(aggregate).build();

    }
}