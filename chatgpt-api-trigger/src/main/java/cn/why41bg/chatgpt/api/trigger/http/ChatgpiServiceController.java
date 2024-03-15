package cn.why41bg.chatgpt.api.trigger.http;

import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.service.chat.IChatService;
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
@RequestMapping("api/${openai.api.version}/")
@CrossOrigin(origins = "${openai.api.cross-origin}")
public class ChatgpiServiceController {

    /**
     * 提供ChatGPT大模型服务
     */
    @Resource
    private IChatService chatService;

    /**
     * 提供鉴权服务
     */
    @Resource
    private IAuthService authService;

    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter chatCompletionsStream(@RequestBody ChatgptProcessAggregate aggregate,
                                                     @Nullable @RequestHeader("Authorization") String token,
                                                     HttpServletResponse response) {
        log.info("接收到流式问答请求，请求模型：{} 请求信息：{}", aggregate.getModel(), JSON.toJSONString(aggregate.getMessages()));

        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. Token有效性检查
            if (!authService.checkToken(token)) {
                // Token无效
                // TODO 魔法值处理
                ResponseBodyEmitter emitter = new ResponseBodyEmitter(5 * 60 * 1000L);
                emitter.send(ResponseCode.PRIVILEGES_ERROR.getCode());
                emitter.complete();
                return emitter;
            }

            // 4. Token有效，将Token添加到聚合对象中
            aggregate.setToken(token);

            // 3. 请求流式问答服务并返回
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
