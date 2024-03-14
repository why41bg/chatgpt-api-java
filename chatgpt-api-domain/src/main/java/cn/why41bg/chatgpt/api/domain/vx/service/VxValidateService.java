package cn.why41bg.chatgpt.api.domain.vx.service;

import cn.why41bg.chatgpt.api.types.sdk.vx.VxSignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname VxValidateService
 * @Description 面向微信的验签服务实现类
 * @Author 魏弘宇
 * @Date 2024/3/13 17:02
 */
@Service
public class VxValidateService implements IVxValidateService{

    @Value("${vx.config.token}")
    private String token;
    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return VxSignatureUtil.check(token, signature, timestamp, nonce);
    }
}
