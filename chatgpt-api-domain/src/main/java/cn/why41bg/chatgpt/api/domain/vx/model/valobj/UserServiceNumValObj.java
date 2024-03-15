package cn.why41bg.chatgpt.api.domain.vx.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserServiceNumValObj {
    CODE_GENERATE_SERVICE("404", "请求获取验证码");

    private final String code;

    private final String info;
}
