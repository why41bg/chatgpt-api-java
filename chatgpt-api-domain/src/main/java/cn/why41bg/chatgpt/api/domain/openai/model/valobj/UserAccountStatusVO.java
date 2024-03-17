package cn.why41bg.chatgpt.api.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAccountStatusVO {

    AVAILABLE(0, "可用"),
    FREEZE(1,"冻结"),
    ;

    private final Integer code;
    private final String info;

    public static UserAccountStatusVO get(Integer code){
        switch (code){
            case 0:
                return UserAccountStatusVO.AVAILABLE;
            case 1:
                return UserAccountStatusVO.FREEZE;
            default:
                return UserAccountStatusVO.AVAILABLE;
        }
    }

}
