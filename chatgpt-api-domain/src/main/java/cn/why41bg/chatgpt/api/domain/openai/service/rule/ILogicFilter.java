package cn.why41bg.chatgpt.api.domain.openai.service.rule;

import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;

/**
 * @Interface ILogicFilter
 * @Description 规则过滤接口
 * @Author 魏弘宇
 * @Date 2024/3/15 00:45
 */
public interface ILogicFilter<T> {

     RuleLogicEntity<ChatgptProcessAggregate> filter
             (ChatgptProcessAggregate aggregate, T data);
}
