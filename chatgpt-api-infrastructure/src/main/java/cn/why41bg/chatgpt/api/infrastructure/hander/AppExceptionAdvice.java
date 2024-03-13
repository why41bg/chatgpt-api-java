package cn.why41bg.chatgpt.api.infrastructure.hander;

import cn.why41bg.chatgpt.api.types.common.Result;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
import cn.why41bg.chatgpt.api.types.exception.TokenCheckException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Classname AppExceptionAdvice
 * @Description 统一异常处理器，处理 Controller 类抛出的异常
 * @Author 魏弘宇
 * @Date 2024/3/13 09:30
 */
@RestControllerAdvice
@Slf4j
public class AppExceptionAdvice {

    @ExceptionHandler(ChatgptException.class)
    public Result doChatgptException(ChatgptException chatgptException) {
        log.info("ChatgptException 异常处理");
        return new Result(ResponseCode.UN_ERROR.getCode(), null, ResponseCode.UN_ERROR.getInfo());
    }

    @ExceptionHandler(TokenCheckException.class)
    public Result doTokenCheckException(TokenCheckException tokenCheckException) {
        log.info("Token错误异常处理");
        return new Result(ResponseCode.TOKEN_ERROR.getCode(), null, ResponseCode.TOKEN_ERROR.getInfo());
    }
}
