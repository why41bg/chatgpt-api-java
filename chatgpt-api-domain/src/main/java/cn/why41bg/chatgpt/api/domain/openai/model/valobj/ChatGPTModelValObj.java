package cn.why41bg.chatgpt.api.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGPTModelValObj {

    GPT_3_5_TURBO("gpt-3.5-turbo"),

    GPT_4("gpt-4"),

    GPT_4_32K("gpt-4-32k"),
    ;
    private final String code;

}
