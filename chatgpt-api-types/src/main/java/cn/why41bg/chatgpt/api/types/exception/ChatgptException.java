package cn.why41bg.chatgpt.api.types.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatgptException extends RuntimeException {

    private static final long serialVersionUID = 5317680961212299217L;

    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String info;

    public ChatgptException(String code) {
        this.code = code;
    }

    public ChatgptException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public ChatgptException(String code, String message) {
        this.code = code;
        this.info = message;
    }

    public ChatgptException(String code, String message, Throwable cause) {
        this.code = code;
        this.info = message;
        super.initCause(cause);
    }

}
