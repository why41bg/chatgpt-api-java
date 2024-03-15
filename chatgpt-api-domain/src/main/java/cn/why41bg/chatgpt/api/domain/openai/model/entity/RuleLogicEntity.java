package cn.why41bg.chatgpt.api.domain.openai.model.entity;

import cn.why41bg.chatgpt.api.domain.openai.model.valobj.LogicCheckTypeValObj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RuleLogicEntity
 * @Description 规则校验结果实体类
 * @Author 魏弘宇
 * @Date 2024/3/15 15:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleLogicEntity<T> {

    private LogicCheckTypeValObj type;

    private String info;

    private T data;

}
