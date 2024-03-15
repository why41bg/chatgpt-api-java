package cn.why41bg.chatgpt.api.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LogicCheckTypeValObj {

    SUCCESS("0000", "规则校验通过"),
    REFUSE("0001", "规则校验拒绝");

    private final String code;

    private final String info;

}
