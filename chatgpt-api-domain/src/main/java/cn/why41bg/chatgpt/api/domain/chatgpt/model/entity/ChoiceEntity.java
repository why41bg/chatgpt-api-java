package cn.why41bg.chatgpt.api.domain.chatgpt.model.entity;

import lombok.Data;

/**
 * @Classname ChoiceEntity
 * @Description 控制是否为流式问答实体类
 * @Author 魏弘宇
 * @Date 2024/3/12 21:54
 */
@Data
public class ChoiceEntity {

    /**
     * stream = true 请求参数里返回的属性是 delta
     */
    private MessageEntity delta;

    /**
     * stream = false 请求参数里返回的属性是 message
     */
    private MessageEntity message;

}
