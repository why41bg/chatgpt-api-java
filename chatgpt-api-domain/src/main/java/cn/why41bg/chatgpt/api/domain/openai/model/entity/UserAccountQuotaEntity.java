package cn.why41bg.chatgpt.api.domain.openai.model.entity;

import cn.why41bg.chatgpt.api.domain.openai.model.valobj.UserAccountStatusVO;
import cn.why41bg.chatgpt.api.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @Classname UserAccountQuotaEntity
 * @Description 用户账户余额实体类
 * @Author 魏弘宇
 * @Date 2024/3/16 16:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountQuotaEntity {

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
     * 账户状态
     */
    private UserAccountStatusVO userAccountStatusVO;

    /**
     * 可用模型列表
     */
    private List<String> allowModelTypeList;

    /**
     * 在数据库中，可用模型使用字符串存储，多个可用模型之间使用英文逗号隔开
     * 因此需要调用此方法将此转为List结构
     * @param modelTypes 允许账户使用模型，多个模型之间用英文逗号隔开
     */
    public void parseAndSetAllowModelType(String modelTypes) {
        this.allowModelTypeList = Arrays.asList(modelTypes.split(Constants.SPLIT));
    }

}
