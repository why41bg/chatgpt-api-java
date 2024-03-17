package cn.why41bg.chatgpt.api.domain.openai.service.rule.impl;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.RuleLogicEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname ModelTypeFilter
 * @Description 允许访问的模型过滤
 * @Author 魏弘宇
 * @Date 2024/3/16 17:29
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatgptProcessAggregate> filter(
            ChatgptProcessAggregate chatProcess,
            UserAccountQuotaEntity data) {
        // 1. 用户可用模型
        List<String> allowModelTypeList = data.getAllowModelTypeList();
        String modelType = chatProcess.getModel();

        // 2. 模型校验通过
        if (allowModelTypeList.contains(modelType)) {
            return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                    .type(LogicCheckTypeValObj.SUCCESS)
                    .data(chatProcess)
                    .build();
        }

        // 3. 模型校验拦截
        return RuleLogicEntity.<ChatgptProcessAggregate>builder()
                .type(LogicCheckTypeValObj.REFUSE)
                .info("当前账户不支持使用 " + modelType + " 模型！可以联系客服升级账户。")
                .data(chatProcess)
                .build();
    }

}

