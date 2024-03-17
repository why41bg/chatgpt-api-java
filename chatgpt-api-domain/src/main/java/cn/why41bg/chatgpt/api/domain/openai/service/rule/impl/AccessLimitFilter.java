package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
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
public class AccessLimitFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Value("${openai.api.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity data)
            throws NullPointerException{
        // 白名单过滤
        if (this.isInWhiteList(aggregate)) {
            // 在白名单之中，直接返回
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS)
                    .data(aggregate)
                    .build();
        }

       return RuleLogicEntity.<ChatgptProcessAggregate>builder()
               .type(LogicCheckTypeValObj.SUCCESS)
               .data(aggregate)
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
