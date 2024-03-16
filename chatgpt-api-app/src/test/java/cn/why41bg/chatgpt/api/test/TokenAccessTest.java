package cn.why41bg.chatgpt.api.test;

import cn.why41bg.chatgpt.api.domain.auth.model.entity.AuthResultEntity;
import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenAccessTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IAuthService authService;

    @Value("${openai.api.access.access-num}")
    private String accessNum;  // 访问频次

    @Value("${openai.api.access.access-fresh-time}")
    private long accessFreshTime;  // 访问频次刷新时间

    @Before
    public void addCode() {
        String key = "code:000111";
        String val = "im a fake openId";
        stringRedisTemplate.opsForValue().set(key, val);
    }

    @Test
    public void testTokenExpire() throws InterruptedException {
        AuthResultEntity authResultEntity = authService.doLogin("000111");
        String token = authResultEntity.getToken();

        String key = Constants.ACCESS_PREFIX + token;

        stringRedisTemplate.opsForValue().set(
                key, accessNum, accessFreshTime, TimeUnit.HOURS);

        Thread.sleep(1000 * 5);  // 暂停5s

        Long expire = stringRedisTemplate.getExpire(key);
        System.out.println("TTL：" + expire);

        stringRedisTemplate.opsForValue().getAndDelete(key);
    }

}
