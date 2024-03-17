package cn.why41bg.chatgpt.api.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    PRIVILEGES_ERROR("0003", "权限拦截"),
    CHATGPT_ERROR("0004", "ChatGPT错误"),
    ACCOUNT_ERROR("0005", "账户错误");

    private final String code;
    private final String info;

}
