package cn.why41bg.chatgpt.api.domain.vx.service;

import cn.why41bg.chatgpt.api.domain.vx.model.entity.UserBehaviorRequestEntity;

/**
 * @Interface IVxUserBehaviorService
 * @Description 面向微信的，处理微信发来的用户行为接口
 * @Author 魏弘宇
 * @Date 2024/3/13 16:41
 */
public interface IVxUserBehaviorService {

    /**
     * 用户请求获取验证码
     * @param userBehaviorRequestEntity 用户请求DTO
     * @return Xml MessageEntity
     * @throws Exception 异常
     */
    String doUserAskForCodeBehavior(UserBehaviorRequestEntity userBehaviorRequestEntity) throws Exception;

    String doDefaultBehavior(UserBehaviorRequestEntity userBehaviorRequestEntity);

}
