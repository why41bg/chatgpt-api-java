package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.UserAccountStatusVO;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname AccountStatusFilter
 * @Description 账户状态校验
 * @Author 魏弘宇
 * @Date 2024/3/16 17:22
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.ACCOUNT_STATUS)
public class AccountStatusFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity data) {
        // 账户可用，直接放行
        if (UserAccountStatusVO.AVAILABLE.equals(data.getUserAccountStatusVO())) {
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS)
                    .data(aggregate).build();
        }

        // 账户被冻结，返回拒绝信息
        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .info("您的账户已冻结，暂时不可使用。")
                .type(LogicCheckTypeValObj.REFUSE)
                .data(aggregate).build();
    }

}
