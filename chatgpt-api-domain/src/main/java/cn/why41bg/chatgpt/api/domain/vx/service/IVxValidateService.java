package cn.why41bg.chatgpt.api.domain.vx.service;

import java.security.NoSuchAlgorithmException;

/**
 * @Interface IVxValidateService
 * @Description 面向微信的验签接口，为微信公众号接入提供验签服务
 * @Author 魏弘宇
 * @Date 2024/3/13 16:38
 */
public interface IVxValidateService {

    boolean checkSign(String signature, String timestamp, String nonce);

}
