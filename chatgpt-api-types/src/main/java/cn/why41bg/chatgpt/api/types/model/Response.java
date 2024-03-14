package cn.why41bg.chatgpt.api.types.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname Response
 * @Description 各触发层统一返回给调用者的结果对象
 * @Author 魏弘宇
 * @Date 2024/3/14 02:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private String code;

    private String info;

    private T data;

}

