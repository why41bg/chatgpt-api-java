package cn.why41bg.chatgpt.api.domain.vx.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgTypeValObj {

    EVENT("event","事件消息"),
    TEXT("text","文本消息");

    private final String code;

    private final String description;

}
