package cn.why41bg.chatgpt.api.domain.vx.service.impl;

import cn.why41bg.chatgpt.api.domain.vx.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.entity.UserBehaviorRequestEntity;
import cn.why41bg.chatgpt.api.domain.vx.service.IVxUserBehaviorService;
import cn.why41bg.chatgpt.api.types.common.Constants;
import cn.why41bg.chatgpt.api.types.sdk.vx.XmlUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Classname VxUserBehaviorService
 * @Description 面向微信的，处理微信发来的用户行为具体实现类
 * @Author 魏弘宇
 * @Date 2024/3/14 01:34
 */
@Service
public class VxUserBehaviorService implements IVxUserBehaviorService {

    @Value("${vx.config.originalId}")
    private String originalId;

    @Value("${openai.api.code-ttl}")
    private long codeTtl;

    @Value("${openai.api.code-len}")
    private int codeLen;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 请求生成验证码
     * @param request 用户行为请求DTO
     * @return 验证码
     */
    @Override
    public String doUserAskForCodeBehavior(UserBehaviorRequestEntity request) {

        // 根据用户的唯一标识符在数据库中查询该用户之前是否申请过验证码
        // 如果之前申请的验证码还没有失效，直接返回给用户
        String isCodeExistKey = Constants.OPENID_CODE_PREFIX + request.getOpenId();
        String code = stringRedisTemplate.opsForValue().get(isCodeExistKey);
        if (StringUtils.isBlank(code)) {
            // 验证码不存在，为用户创建验证码
            code = RandomStringUtils.randomNumeric(codeLen);
            String key = Constants.CODE_PREFIX + code;
            // 检查生成的验证码是否与其他用户生成且还存在验证码重复
            while (StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(key))) {
                code = RandomStringUtils.randomNumeric(codeLen);
                key = Constants.CODE_PREFIX + code;
            }
            stringRedisTemplate.opsForValue().set(key, request.getOpenId(), codeTtl, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(isCodeExistKey, code, codeTtl, TimeUnit.MINUTES);
        }

        // 在公众号中响应用户
        MessageEntity res = new MessageEntity();
        res.setToUserName(request.getOpenId());
        res.setFromUserName(originalId);
        res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        res.setMsgType("text");
        res.setContent(String.format("您的验证码为：%s 有效期%d分钟！\nqmin大小姐祝您旅途愉快～", code, codeTtl));
        return XmlUtil.beanToXml(res);
    }

    @Override
    public String doDefaultBehavior(UserBehaviorRequestEntity userBehaviorRequestEntity) {
        return XmlUtil.beanToXml(MessageEntity.builder()
                .toUserName(userBehaviorRequestEntity.getOpenId())
                .fromUserName(originalId)
                .createTime(String.valueOf(System.currentTimeMillis() / 1000L))
                .msgType("text")
                .content("qmin大小姐驾到，通通闪开！\n哼，速速高呼【404】，qmin大小姐赏你一发验证码！")
                .build()
        );
    }
}
