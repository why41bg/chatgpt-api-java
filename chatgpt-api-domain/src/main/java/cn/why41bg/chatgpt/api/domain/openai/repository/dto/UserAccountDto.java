package cn.why41bg.chatgpt.api.domain.openai.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Classname UserAccountDto
 * @Description 将UserAccount对象传递给基础设施层使用
 * @Author 魏弘宇
 * @Date 2024/3/17 12:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDto {

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
     * 可用模型，多个模型用英文逗号隔开
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
