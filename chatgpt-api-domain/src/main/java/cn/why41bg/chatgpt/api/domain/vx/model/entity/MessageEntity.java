package cn.why41bg.chatgpt.api.domain.vx.model.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.*;

/**
 * @Classname MessageEntity
 * @Description 微信发来的消息实体，具体可参照 <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html">官方文档</a>
 * @Author 魏弘宇
 * @Date 2024/3/13 16:31
 */
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class MessageEntity {

    @XStreamAlias("MsgId")
    private String msgId;

    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamAlias("FromUserName")
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private String createTime;

    @XStreamAlias("MsgType")
    private String msgType;

    @XStreamAlias("Content")
    private String content;

    @XStreamAlias("Event")
    private String event;

    @XStreamAlias("EventKey")
    private String eventKey;

}
