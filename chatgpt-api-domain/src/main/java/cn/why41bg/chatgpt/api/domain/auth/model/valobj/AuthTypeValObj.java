package cn.why41bg.chatgpt.api.domain.auth.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTypeValObj {
    A0000("0000","验证码验证成功"),
    A0001("0001","验证码不存在"),
    A0002("0002","验证码无效");

    private final String code;
    private final String info;

}
