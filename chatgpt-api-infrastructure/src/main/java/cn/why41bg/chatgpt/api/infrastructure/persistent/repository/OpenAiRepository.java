package cn.why41bg.chatgpt.api.infrastructure.persistent.repository;

import cn.why41bg.chatgpt.api.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.UserAccountStatusVO;
import cn.why41bg.chatgpt.api.domain.openai.repository.IOpenAiRepository;
import cn.why41bg.chatgpt.api.domain.openai.repository.dto.UserAccountDto;
import cn.why41bg.chatgpt.api.infrastructure.persistent.dao.IUserAccountDao;
import cn.why41bg.chatgpt.api.infrastructure.persistent.po.UserAccountPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @Classname OpenAiRepository
 * @Description 仓储服务实现类
 * @Author 魏弘宇
 * @Date 2024/3/16 17:14
 */
@Repository
public class OpenAiRepository implements IOpenAiRepository {

    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public int subAccountQuota(String openId) {
        return userAccountDao.subAccountQuota(openId);
    }

    @Override
    public UserAccountQuotaEntity queryUserAccount(String openId) {
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openId);
        if (null == userAccountPO) return null;
        UserAccountQuotaEntity userAccountQuotaEntity = UserAccountQuotaEntity.builder()
                .openId(userAccountPO.getOpenId())
                .totalQuota(userAccountPO.getTotalQuota())
                .surplusQuota(userAccountPO.getSurplusQuota())
                .userAccountStatusVO(UserAccountStatusVO.get(userAccountPO.getStatus()))
                .build();
        userAccountQuotaEntity.parseAndSetAllowModelType(userAccountPO.getModelTypes());
        return userAccountQuotaEntity;
    }

    @Override
    public boolean insertUserAccount(UserAccountDto userAccountDto) {
        UserAccountPO userAccountPO = UserAccountPO.builder()
                .openId(userAccountDto.getOpenId())
                .totalQuota(userAccountDto.getTotalQuota())
                .surplusQuota(userAccountDto.getSurplusQuota())
                .modelTypes(userAccountDto.getModelTypes())
                .createTime(userAccountDto.getCreateTime())
                .updateTime(userAccountDto.getUpdateTime())
                .status(userAccountDto.getStatus()).build();
        return userAccountDao.insertUserAccount(userAccountPO);
    }
}

