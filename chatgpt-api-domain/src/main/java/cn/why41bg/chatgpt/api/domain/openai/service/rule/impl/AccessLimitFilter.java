package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import cn.why41bg.chatgpt.api.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Classname AccessLimitFilter
 * @Description 频次过滤实现类
 * @Author 魏弘宇
 * @Date 2024/3/15 17:36
 */
@Service
@Slf4j
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.ACCESS_LIMIT)
public class AccessLimitFilter implements ILogicFilter {

    @Value("${openai.api.access.white-list}")
    private String whiteListStr;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(ChatgptProcessAggregate aggregate)
            throws NullPointerException{
        // 白名单过滤
        if (DefaultLogicFactory.isInWhiteList(aggregate, whiteListStr)) {
            // 在白名单之中，直接返回
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS)
                    .data(aggregate)
                    .build();
        }

        // 不在白名单之中，进行访问频次判断
        // 访问频次存储在Redis中，访问限制设置为 10次/24小时
        // 键值对中键的格式为 access:{token}
        String accessKey = Constants.ACCESS_PREFIX + aggregate.getToken();
        String accessNumStr = stringRedisTemplate.opsForValue().get(accessKey);
        if (accessNumStr == null) {
            throw new NullPointerException("非法用户");
        }
        int accessNum = Integer.parseInt(accessNumStr);
        if (accessNum <= 0) {
            // 用户今日访问频次已使用完，直接返回
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.REFUSE)
                    .data(aggregate)
                    // TODO 提示消息中显示具体的CD
                    .info("您24小时内免费访问频次已用完，请24小时后重试")
                    .build();
        }
        // 访问频次没用光则放行
        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .type(LogicCheckTypeValObj.SUCCESS)
                .data(aggregate)
                .build();
    }
}
