package cn.why41bg.chatgpt.api.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Classname UserAccountPO
 * @Description 用户账户持久化对象
 * @Author 魏弘宇
 * @Date 2024/3/16 17:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountPO {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 该微信公众号下用户唯一标识
     */
    private String openId;

    /**
     * 总量额度
     */
    private Integer totalQuota;

    /**
     * 剩余额度
     */
    private Integer surplusQuota;

    /**
     * 可用模型；gpt-3.5-turbo,gpt-3.5-turbo-16k,gpt-4,gpt-4-32k
     */
    private String modelTypes;

    /**
     * 账户状态；0-可用、1-冻结
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
