package cn.why41bg.chatgpt.api.domain.openai.repository;

import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.repository.dto.UserAccountDto;

/**
 * @Interface IOpenAiRepository
 * @Description 仓储接口
 * @Author 魏弘宇
 * @Date 2024/3/16 17:15
 */
public interface IOpenAiRepository {

    int subAccountQuota(String openId);

    UserAccountQuotaEntity queryUserAccount(String openId);

    boolean insertUserAccount(UserAccountDto userAccountDto);

}

