package cn.why41bg.chatgpt.api.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Classname SensitiveWordConfig
 * @Description SensitiveWord配置类
 * @Author 魏弘宇
 * @Date 2024/3/16 02:27
 */
@Component
@Slf4j
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .wordReplace((stringBuilder, chars, wordResult, iWordContext) -> {
                    String sensitiveWord = InnerWordCharUtils.getString(chars, wordResult);
                    log.info("检测到敏感词: {}", sensitiveWord);
                    /* 替换操作，你可以指定的替换为*或者其他
                    else {
                        int wordLength = wordResult.endIndex() - wordResult.startIndex();
                        for (int i = 0; i < wordLength; i++) {
                            stringBuilder.append("");
                        }
                    }*/
                })
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(false)
                .enableNumCheck(true)
                .enableEmailCheck(true)
                .enableUrlCheck(true)
                .enableWordCheck(true)
                .numCheckLen(1024)
                .init();
    }


}
