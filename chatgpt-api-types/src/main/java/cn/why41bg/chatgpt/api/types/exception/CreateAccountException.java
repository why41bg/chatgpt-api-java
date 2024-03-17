package cn.why41bg.chatgpt.api.types.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname CreateAccountException
 * @Description 创建用户账户异常类
 * @Author 魏弘宇
 * @Date 2024/3/17 12:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateAccountException extends RuntimeException{
    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String info;

    public CreateAccountException(String code) {
        this.code = code;
    }

    public CreateAccountException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public CreateAccountException(String code, String message) {
        this.code = code;
        this.info = message;
    }

    public CreateAccountException(String code, String message, Throwable cause) {
        this.code = code;
        this.info = message;
        super.initCause(cause);
    }
}
