package cn.why41bg.chatgpt.api.trigger.http;

import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.domain.chatgpt.model.aggregate.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.chatgpt.service.IChatService;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.exception.ChatgptException;
import cn.why41bg.chatgpt.api.types.exception.TokenCheckException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Classname ChatgpiServiceController
 * @Description ChatGPT服务请求控制器
 * @Author 魏弘宇
 * @Date 2024/3/13 01:06
 */
@RestController
@Slf4j
@RequestMapping("api/${openai.chatgpt.api.version}/")
@CrossOrigin("${openai.chatgpt.api.cross-origin}")
public class ChatgpiServiceController {

    @Resource
    private IChatService chatService;

    @Resource
    private IAuthService authService;

    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter chatCompletionsStream(@RequestBody ChatgptProcessAggregate aggregate,
                                                     @Nullable @RequestHeader("Authorization") String token,
                                                     HttpServletResponse response) {
        log.info("接收到流式问答请求，使用模型：{} 请求信息：{}", aggregate.getModel(), JSON.toJSONString(aggregate.getMessages()));

        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 构建参数
            if (!authService.checkToken(token)) {
                // token无效
                ResponseBodyEmitter emitter = new ResponseBodyEmitter(5 * 60 * 1000L);
                emitter.send(ResponseCode.PRIVILEGES_ERROR.getCode());
                emitter.complete();
                return emitter;
            }
            aggregate.setToken(token);

            // 3. 请求结果&返回
            return chatService.chatCompletions(aggregate);
        } catch (ChatgptException chatgptException) {
            log.error("ChatGPT发生错误，使用模型：{}", aggregate.getModel(), chatgptException);
            throw chatgptException;
        } catch (TokenCheckException tokenCheckException) {
            log.error("Token校验错误，使用模型：{}", aggregate.getModel(), tokenCheckException);
            throw tokenCheckException;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
