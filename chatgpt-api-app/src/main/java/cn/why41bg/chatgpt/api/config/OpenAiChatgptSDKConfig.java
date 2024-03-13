package cn.why41bg.chatgpt.api.config;

import cn.why41bg.chatgpt.session.IOpenAiSession;
import cn.why41bg.chatgpt.session.OpenAiSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname OpenAiChatgptSDKConfig
 * @Description openai-chatgpt-sdk配置类
 * @Author 魏弘宇
 * @Date 2024/3/12 21:04
 */
@Configuration
public class OpenAiChatgptSDKConfig {

    @Value("${openai.chatgpt.sdk.config.api-key}")
    private String apiKey;

    @Value("${openai.chatgpt.sdk.config.api-host}")
    private String apiHost;

    @Value("${openai.chatgpt.sdk.config.auth-token}")
    private String authToken;

    @Bean
    public IOpenAiSession openAiSession(){
        cn.why41bg.chatgpt.session.Configuration configuration = new cn.why41bg.chatgpt.session.Configuration();
        configuration.setApiKey(this.apiKey);
        configuration.setApiHost(this.apiHost);
        configuration.setAuthToken(authToken);

        OpenAiSessionFactory openAiSessionFactory = new OpenAiSessionFactory(configuration);

        return openAiSessionFactory.openSession();
    }

}
