package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import cn.why41bg.chatgpt.api.types.common.Constants;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname SensitiveWordFilter
 * @Description 敏感词汇过滤实现类
 * @Author 魏弘宇
 * @Date 2024/3/15 15:31
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private SensitiveWordBs words;

    @Value("${openai.api.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity data) {
        // 判断是否是白名单用户，白名单用户不做处理
        if (this.isInWhiteList(aggregate)) {
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS)
                    .data(aggregate)
                    .build();
        }

        // 非白名单用户进行敏感词过滤
        ChatgptProcessAggregate newChatgptProcessAggregate = new ChatgptProcessAggregate();
        newChatgptProcessAggregate.setToken(aggregate.getToken());
        newChatgptProcessAggregate.setModel(aggregate.getModel());
        // 对原始Message进行敏感词过滤，整形得到新的Message
        List<MessageEntity> newMessage = aggregate.getMessages().stream()
                .map(message -> {
                            String content = message.getContent();
                            String replacedContent = words.replace(content);
                            return MessageEntity.builder()
                                    .role(message.getRole())
                                    .name(message.getName())
                                    .content(replacedContent)
                                    .build();
                        }
                ).collect(Collectors.toList());

        // 结果返回
        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .data(newChatgptProcessAggregate)
                .type(LogicCheckTypeValObj.SUCCESS)
                .build();
    }

    /**
     * 白名单过滤
     * @param aggregate 请求体聚合根
     * @return 白名单过滤结果
     */
    public boolean isInWhiteList(ChatgptProcessAggregate aggregate) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(aggregate.getOpenId())) return true;
        }
        return false;
    }
}
