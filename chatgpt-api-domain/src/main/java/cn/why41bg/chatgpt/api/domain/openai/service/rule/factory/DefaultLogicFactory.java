package cn.why41bg.chatgpt.api.domain.openai.service.rule.factory;

import cn.why41bg.chatgpt.api.domain.openai.annotation.LogicStrategy;
import cn.why41bg.chatgpt.api.domain.openai.model.aggregates.ChatgptProcessAggregate;
import cn.why41bg.chatgpt.api.domain.openai.service.rule.ILogicFilter;
import cn.why41bg.chatgpt.api.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname DefaultLogicFactory
 * @Description 默认的规则过滤工厂，生产规则过滤器
 * @Author 魏弘宇
 * @Date 2024/3/15 00:43
 */
@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter> logicFilterMap = new ConcurrentHashMap<>();

    /**
     * 白名单过滤
     * @param aggregate 请求体聚合根
     * @return 白名单过滤结果
     */
    public static boolean isInWhiteList(ChatgptProcessAggregate aggregate,
                                        String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(aggregate.getOpenId())) return true;
        }
        return false;
    }

    public DefaultLogicFactory(List<ILogicFilter> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public Map<String, ILogicFilter> openLogicFilter() {
        return logicFilterMap;
    }


    @AllArgsConstructor
    @Getter
    public enum LogicModel {

        ACCESS_LIMIT("ACCESS_LIMIT", "访问次数过滤"),
        SENSITIVE_WORD("SENSITIVE_WORD", "敏感词过滤"),
        ;

        private final String code;

        private final String info;
    }
}
