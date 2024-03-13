package cn.why41bg.chatgpt.api.domain.chatgpt.service;

import cn.why41bg.chatgpt.api.domain.chatgpt.model.aggregate.ChatgptProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @Interface IChatService
 * @Description openai chatgpt 接口规范
 * @Author 魏弘宇
 * @Date 2024/3/13 00:43
 */
public interface IChatService {

    /**
     * 流式问答接口
     * @param aggregate 请求聚合信息
     * @return 异步传输对象
     */
    ResponseBodyEmitter chatCompletions(ChatgptProcessAggregate aggregate);
}
