package cn.why41bg.chatgpt.api.test;

import cn.why41bg.chatgpt.api.domain.auth.model.entity.AuthResultEntity;
import cn.why41bg.chatgpt.api.domain.auth.model.valobj.AuthTypeValObj;
import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IAuthService authService;


    @Before
    public void addCode() {
        String key = "code:000111";
        String val = "2425332276";
        stringRedisTemplate.opsForValue().set(key, val);
    }

    @Test
    public void testAuth() {
        AuthResultEntity authResultEntity = authService.doLogin("000111");
        if (AuthTypeValObj.A0000.getCode().equals(authResultEntity.getCode())) {
            System.out.println(authResultEntity.getInfo());
            System.out.println("生成Token：" + authResultEntity.getToken());
            System.out.println("对应账户：" + authResultEntity.getOpenId());
        }
    }

}
