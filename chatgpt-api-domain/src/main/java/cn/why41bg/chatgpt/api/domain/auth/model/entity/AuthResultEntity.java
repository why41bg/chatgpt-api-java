package cn.why41bg.chatgpt.api.domain.auth.model.entity;

import lombok.*;

/**
 * @Classname AuthResultEntity
 * @Description 返回给 Web 应用的鉴权结果对象
 * @Author 魏弘宇
 * @Date 2024/3/13 17:19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResultEntity {

    private String code;  // 验证码

    private String info;

    private String openId;  // 当前公众号下用户唯一标识符

    private String token;

}
