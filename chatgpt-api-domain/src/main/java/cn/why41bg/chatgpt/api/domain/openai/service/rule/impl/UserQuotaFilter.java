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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ThreadPoolExecutor taskExecutor;

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate aggregate,
            UserAccountQuotaEntity account) {
        if (account.getSurplusQuota() > 0) {
            // 用户余额充足，直接在Redis中执行余额扣减操作，然后返回，开启异步线程修改Mysql
            // TODO 由于对用户进行扣余额操作不存在资源共享，因此暂时不需要考虑并发问题
            account.setSurplusQuota(account.getSurplusQuota() - 1);
            String accountKey = cn.why41bg.chatgpt.api.types.common.Constants.OPENID_ACCOUNT_PREFIX + aggregate.getOpenId();
            String accountJson = com.alibaba.fastjson2.JSON.toJSONString(account);
            stringRedisTemplate.opsForValue().set(accountKey, accountJson, 3, TimeUnit.HOURS);

            // 提交异步任务
            taskExecutor.execute(() -> openAiRepository.subAccountQuota(account.getOpenId()));

            // 返回结果
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS).data(aggregate).build();
        }

        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .info("个人账户，总额度【" + account.getTotalQuota() + "】次，已耗尽！")
                .type(LogicCheckTypeValObj.REFUSE).data(aggregate).build();
    }

}

