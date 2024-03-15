package cn.why41bg.chatgpt.api.types.common;

public class Constants {
    public static String SPLIT = ",";

    /**
     * 与验证码服务相关的Redis键的前缀
     */
    public static String CODE_PREFIX = "code:";

    /**
     * 与访问频次限制相关的Redis键的前缀
     */
    public static String ACCESS_PREFIX = "access:";
}
