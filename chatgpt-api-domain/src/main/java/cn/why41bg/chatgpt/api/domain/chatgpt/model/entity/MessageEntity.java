package cn.why41bg.chatgpt.api.domain.chatgpt.model.entity;

import lombok.Data;

/**
 * @Classname MessageEntity
 * @Description Message实体类
 * @Author 魏弘宇
 * @Date 2024/3/12 21:54
 */
@Data
public class MessageEntity {

    private String role;

    private String content;

    private String name;

}
