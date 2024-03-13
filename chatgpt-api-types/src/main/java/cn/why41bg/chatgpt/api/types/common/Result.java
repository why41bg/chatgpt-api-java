package cn.why41bg.chatgpt.api.types.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname Result
 * @Description 统一返回结果
 * @Author 魏弘宇
 * @Date 2024/3/13 09:33
 */
@Getter
@AllArgsConstructor
public class Result {

    private final String code;

    private final Object data;

    private final String message;

}
