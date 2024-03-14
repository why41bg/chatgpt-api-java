package cn.why41bg.chatgpt.api.trigger.http;

import cn.why41bg.chatgpt.api.domain.vx.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.vx.model.entity.UserBehaviorRequestEntity;
import cn.why41bg.chatgpt.api.domain.vx.service.IVxUserBehaviorService;
import cn.why41bg.chatgpt.api.domain.vx.service.IVxValidateService;
import cn.why41bg.chatgpt.api.types.sdk.vx.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Classname VxPortalController
 * @Description 微信公众号对接服务控制器
 * @Author 魏弘宇
 * @Date 2024/3/13 16:17
 */
@Slf4j
@RestController
@RequestMapping("/api/${openai.chatgpt.api.version}/wx/portal/{appid}")
public class VxPortalController {

    @Resource
    private IVxValidateService vxValidateService;

    @Resource
    private IVxUserBehaviorService vxUserBehaviorService;

    /**
     * 处理微信服务器发来的get请求，进行签名的验证
     * @param appid 微信端AppID
     * @param signature 微信端发来的签名
     * @param timestamp 微信端发来的时间戳
     * @param nonce 微信端发来的随机数
     * @param echostr 随机字符串
     * @return 原样返回 echostr
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String validate(@PathVariable String appid,
                           @RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr) {
        try {
            log.info("微信公众号验签信息{}开始 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            boolean isValidate = vxValidateService.checkSign(signature, timestamp, nonce);
            log.info("微信公众号验签信息{}完成 check：{}", appid, isValidate);
            if (!isValidate) {
                return null;
            }
            return echostr;
        } catch (Exception e) {
            log.error("微信公众号验签信息{}失败 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr, e);
            return null;
        }
    }

    /**
     * 处理微信服务器发来的 POST 请求，对应用户行为请求
     * @param appid 微信端AppID
     * @param requestBody Xml请求体
     * @param signature 微信端发来的签名
     * @param timestamp 微信端发来的时间戳
     * @param nonce 微信端发来的随机数
     * @param openid 用户在当前公众号下的唯一标识
     * @param encType 消息加密类型，只有在启动兼容模式或安全模式后才有效
     * @param msgSignature 消息体签名，只有在启动兼容模式或安全模式后才有效
     * @return 请求应答Xml结果
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        try {
            log.info("接收微信公众号来自 {} 信息请求 {}", openid, requestBody);
            // 消息转换
            MessageEntity message = XmlUtil.xmlToBean(requestBody, MessageEntity.class);

            // 构建实体
            UserBehaviorRequestEntity entity = UserBehaviorRequestEntity.builder()
                    .openId(openid)
                    .fromUserName(message.getFromUserName())
                    .msgType(message.getMsgType())
                    .content(StringUtils.isBlank(message.getContent()) ? null : message.getContent().trim())
                    .event(message.getEvent())
                    .createTime(new Date(Long.parseLong(message.getCreateTime()) * 1000L))
                    .build();

            // 处理用户行为
            String xmlResult = vxUserBehaviorService.doUserBehavior(entity);
            log.info("接收微信公众号来自 {} 信息请求完成 {}", openid, xmlResult);
            return xmlResult;
        } catch (Exception e) {
            log.error("接收微信公众号来自 {} 信息请求失败 {}", openid, requestBody, e);
            return "";
        }
    }


}
