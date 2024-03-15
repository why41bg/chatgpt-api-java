package cn.why41bg.chatgpt.api.infrastructure.hander;

import cn.why41bg.chatgpt.api.types.model.Response;
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
    public Response<String> doChatgptException(ChatgptException chatgptException) {
        log.info("ChatgptException异常，处理中。。。");
        log.error(chatgptException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.CHATGPT_ERROR.getCode())
                .info(ResponseCode.CHATGPT_ERROR.getInfo())
                .build();
    }

    @ExceptionHandler(TokenCheckException.class)
    public Response<String> doTokenCheckException(TokenCheckException tokenCheckException) {
        log.info("Token错误异常，处理中。。。");
        log.error(tokenCheckException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.PRIVILEGES_ERROR.getCode())
                .info(ResponseCode.PRIVILEGES_ERROR.getInfo())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<String> doRuntimeException(RuntimeException runtimeException) {
        log.info("系统运行异常，处理中。。。");
        log.error(runtimeException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }
}
