package cn.why41bg.chatgpt.api.domain.vx.service.impl;

import cn.why41bg.chatgpt.api.domain.vx.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.entity.UserBehaviorRequestEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.valobj.MsgTypeValObj;
import cn.why41bg.chatgpt.api.domain.vx.service.IVxUserBehaviorService;
import cn.why41bg.chatgpt.api.types.common.Constants;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
import cn.why41bg.chatgpt.api.types.sdk.vx.XmlUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Classname VxUserBehaviorService
 * @Description 面向微信的，处理微信发来的用户行为具体实现类
 * @Author 魏弘宇
 * @Date 2024/3/14 01:34
 */
@Component
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

        // 根据用户的唯一标识符在数据库中查询是否存在对应的验证码
        String isCodeExistKey = Constants.CODE_PREFIX + request.getOpenId();
        String existCode = stringRedisTemplate.opsForValue().get(isCodeExistKey);

        // 判断验证码是否已经存在，即判断用户先前是否获取过验证码以及验证码是否失效
        // TODO 解决不同用户验证码重复问题
        if (StringUtils.isBlank(existCode)) {
            // 验证码不存在，则为用户创建验证码，并存入数据库
            String code = RandomStringUtils.randomNumeric(codeLen);
            String existCodeKey = Constants.CODE_PREFIX + code;
            // 存入两个键值对，分别是：
            // code:{code} -> {openId}
            // code:{openId} -> {code}
            stringRedisTemplate.opsForValue().set(existCodeKey, request.getOpenId(), codeTtl, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(isCodeExistKey, code, codeTtl, TimeUnit.MINUTES);
            existCode = code;
        }

        // 文本事件类型对应的反馈信息
        MessageEntity res = new MessageEntity();
        res.setToUserName(request.getOpenId());
        res.setFromUserName(originalId);
        res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        res.setMsgType("text");
        res.setContent(String.format("您的验证码为：%s 有效期%d分钟！\nqmin大小姐祝您旅途愉快～", existCode, codeTtl));
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
