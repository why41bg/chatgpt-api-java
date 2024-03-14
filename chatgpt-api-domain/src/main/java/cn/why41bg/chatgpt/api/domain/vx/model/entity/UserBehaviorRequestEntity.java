package cn.why41bg.chatgpt.api.domain.vx.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Classname UserBehaviorRequestEntity
 * @Description 用户请求行为实体类
 * @Author 魏弘宇
 * @Date 2024/3/13 16:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehaviorRequestEntity {

    private String openId;

    private String fromUserName;

    private String msgType;

    private String content;

    private String event;

    private Date createTime;


}
