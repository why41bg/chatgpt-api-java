package cn.why41bg.chatgpt.api.domain.vx.service;

import cn.why41bg.chatgpt.api.domain.vx.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.entity.UserBehaviorRequestEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.valobj.MsgTypeValObj;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
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
public class VxUserBehaviorService implements IVxUserBehaviorService{

    @Value("${vx.config.originalId}")
    private String originalId;

    @Value("${openai.chatgpt.api.code-ttl}")
    private long codeTtl;

    @Value("${openai.chatgpt.api.code-len}")
    private int codeLen;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String doUserBehavior(UserBehaviorRequestEntity userBehaviorRequestEntity) throws Exception {
        // TODO Event 事件类型，暂时忽略处理
        if (MsgTypeValObj.EVENT.getCode().equals(userBehaviorRequestEntity.getMsgType())) {
            return "";
        }

        // Text 文本类型
        if (MsgTypeValObj.TEXT.getCode().equals(userBehaviorRequestEntity.getMsgType())) {

            // 根据用户的唯一标识符在数据库中查询是否存在对应的验证码
            String existCode = stringRedisTemplate.opsForValue().get(userBehaviorRequestEntity.getOpenId());

            // 判断验证码 - TODO 解决不同用户验证码重复问题
            if (StringUtils.isBlank(existCode)) {
                // 验证码不存在，则为用户创建验证码，并存入数据库
                String code = RandomStringUtils.randomNumeric(codeLen);
                stringRedisTemplate.opsForValue().set(code, userBehaviorRequestEntity.getOpenId(), codeTtl, TimeUnit.MINUTES);
                stringRedisTemplate.opsForValue().set(userBehaviorRequestEntity.getOpenId(), code, codeTtl, TimeUnit.MINUTES);
                existCode = code;
            }

            // 文本事件类型对应的反馈信息
            MessageEntity res = new MessageEntity();
            res.setToUserName(userBehaviorRequestEntity.getOpenId());
            res.setFromUserName(originalId);
            res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
            res.setMsgType("text");
            res.setContent(String.format("您的验证码为：%s 有效期%d分钟！", existCode, codeTtl));
            return XmlUtil.beanToXml(res);
        }
        throw new Exception(userBehaviorRequestEntity.getMsgType() + " 未被处理的行为类型 Err！");
    }
}
