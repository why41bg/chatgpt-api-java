package cn.why41bg.chatgpt.api.domain.chatgpt.model.aggregate;

import cn.why41bg.chatgpt.api.domain.chatgpt.model.entity.MessageEntity;
import cn.why41bg.chatgpt.api.domain.chatgpt.model.valobj.ChatGPTModel;
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

    private String token;

    private String model = ChatGPTModel.GPT_3_5_TURBO.getCode();

    private List<MessageEntity> messages;


}
