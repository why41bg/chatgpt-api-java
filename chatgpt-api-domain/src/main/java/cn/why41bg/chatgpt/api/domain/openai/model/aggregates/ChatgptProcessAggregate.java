package cn.why41bg.chatgpt.api.domain.openai.model.aggregates;

import cn.why41bg.chatgpt.api.domain.openai.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.openai.model.valobj.ChatGPTModelValObj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname ChatgptProcessAggregate
 * @Description chatgpt接口参数聚合对象
 * @Author 魏弘宇
 * @Date 2024/3/13 00:35
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatgptProcessAggregate {

    /**
     * 该公众号下唯一用户标识
     */
    private String openId;

    private String token;

    /**
     * Web调用ChatGPT服务时由前端传入，默认为GPT_3.5_TURBO模型
     */
    private String model = ChatGPTModelValObj.GPT_3_5_TURBO.getCode();

    /**
     * Web调用ChatGPT服务由前端时传入
     */
    private List<MessageEntity> messages;

}
