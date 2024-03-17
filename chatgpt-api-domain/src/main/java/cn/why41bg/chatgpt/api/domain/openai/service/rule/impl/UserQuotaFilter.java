package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.repository.IOpenAiRepository;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Classname UserQuotaFilter
 * @Description 用户额度扣减规则过滤
 * @Author 魏弘宇
 * @Date 2024/3/16 17:24
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.USER_QUOTA)
public class UserQuotaFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity account) {
        if (account.getSurplusQuota() > 0) {
            // 扣减账户额度；因为是个人账户数据，无资源竞争，所以直接使用Mysql数据库。
            // TODO 优化为 Redis 扣减提高效率。
            int updateCount = openAiRepository.subAccountQuota(account.getOpenId());
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .info("当前账户剩余额度【" + updateCount + "】次！")
                    .type(LogicCheckTypeValObj.SUCCESS).data(aggregate).build();
        }

        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .info("个人账户，总额度【" + account.getTotalQuota() + "】次，已耗尽！")
                .type(LogicCheckTypeValObj.REFUSE).data(aggregate).build();
    }

}

