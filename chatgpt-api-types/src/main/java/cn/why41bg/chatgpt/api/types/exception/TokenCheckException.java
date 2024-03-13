package cn.why41bg.chatgpt.api.types.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname TokenCheckException
 * @Description Token错误异常类
 * @Author 魏弘宇
 * @Date 2024/3/13 09:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TokenCheckException extends RuntimeException{
    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String info;

    public TokenCheckException(String code) {
        this.code = code;
    }

    public TokenCheckException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public TokenCheckException(String code, String message) {
        this.code = code;
        this.info = message;
    }

    public TokenCheckException(String code, String message, Throwable cause) {
        this.code = code;
        this.info = message;
        super.initCause(cause);
    }
}
