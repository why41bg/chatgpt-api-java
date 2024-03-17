package cn.why41bg.chatgpt.api.infrastructure.hander;

import cn.why41bg.chatgpt.api.types.exception.CreateAccountException;
import cn.why41bg.chatgpt.api.types.model.Response;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
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
        log.info("ChatGPT服务异常，处理中。。。");
        // 此处可以添加相应的异常处理逻辑
        log.error(chatgptException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.CHATGPT_ERROR.getCode())
                .info(ResponseCode.CHATGPT_ERROR.getInfo())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<String> doRuntimeException(RuntimeException runtimeException) {
        log.info("系统运行异常，处理中。。。");
        // 此处可以添加相应的异常处理逻辑
        log.error(runtimeException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }

    @ExceptionHandler(CreateAccountException.class)
    public Response<String> doCreateAccountException(CreateAccountException createAccountException) {
        log.info("用户账户创建异常，处理中。。。");
        // 此处可以添加相应的异常处理逻辑
        log.error(createAccountException.getMessage());
        return Response.<String>builder()
                .code(ResponseCode.ACCOUNT_ERROR.getCode())
                .info(ResponseCode.ACCOUNT_ERROR.getInfo())
                .build();
    }
}
