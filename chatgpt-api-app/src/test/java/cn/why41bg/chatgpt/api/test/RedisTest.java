package cn.why41bg.chatgpt.api.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RedisTest
 * @Description TODO 类描述
 * @Author 魏弘宇
 * @Date 2024/3/13 21:26
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        stringRedisTemplate.opsForValue().set(
                "k1", "v1", 5, TimeUnit.MINUTES);
    }

}
