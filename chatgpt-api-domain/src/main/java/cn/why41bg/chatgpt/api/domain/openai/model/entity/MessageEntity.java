package cn.why41bg.chatgpt.api.domain.openai.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname MessageEntity
 * @Description Message实体类
 * @Author 魏弘宇
 * @Date 2024/3/12 21:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    /**
     * 消息发布人角色
     */
    private String role;

    /**
     * 消息实际内容
     */
    private String content;

    /**
     * 消息发布人姓名，可选
     */
    private String name;

}
