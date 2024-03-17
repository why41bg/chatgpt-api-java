package cn.why41bg.chatgpt.api.infrastructure.persistent.dao;

import cn.why41bg.chatgpt.api.infrastructure.persistent.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Interface IUserAccountDao
 * @Description 用户账户DAO
 * @Author 魏弘宇
 * @Date 2024/3/16 17:12
 */
@Mapper
public interface IUserAccountDao {

    /**
     * 扣减余额
     * @param openId 用户唯一标识
     * @return 此操作影响的行数 -> 1
     */
    int subAccountQuota(String openId);

    /**
     * 查询用户账户
     * @param openId 用户唯一标识
     * @return 用户账户持久化对象
     */
    UserAccountPO queryUserAccount(String openId);

    /**
     * 创建新用户账户
     * @param userAccountPO 用户账户持久化对象
     * @return 创建结果
     */
    boolean insertUserAccount(UserAccountPO userAccountPO);

}
