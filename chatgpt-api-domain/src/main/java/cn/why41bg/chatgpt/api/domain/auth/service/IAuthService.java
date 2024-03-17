package cn.why41bg.chatgpt.api.domain.auth.service;

import cn.why41bg.chatgpt.api.domain.auth.model.entity.AuthResultEntity;

/**
 * @Interface IAuthService
 * @Description 面向 Web 应用的鉴权接口
 * @Author 魏弘宇
 * @Date 2024/3/13 17:21
 */
public interface IAuthService {

    /**
     * 使用验证码登陆，检验验证码合法性
     * 通过检验后生成Token，并创建账户
     * @param code 验证码
     * @return 登陆结果
     */
    AuthResultEntity doLogin(String code);

    /**
     * 检验Token合法性
     * @param token token
     * @return 鉴权结果
     */
    boolean checkToken(String token);
}
