package cn.why41bg.chatgpt.api.domain.openai.model.aggregates;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
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

    private String openId;

    private String token;

    private String model = ChatGPTModelValObj.GPT_3_5_TURBO.getCode();

    private List<MessageEntity> messages;

    public void parseTokenAndSetOpenId() {
        JWT jwt = JWTUtil.parseToken(token);
        this.openId = String.valueOf(jwt.getPayload("openId"));
    }

}
