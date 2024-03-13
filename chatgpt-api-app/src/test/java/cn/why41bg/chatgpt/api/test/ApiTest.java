package cn.why41bg.chatgpt.api.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    /**
     * http触发测试
     */
    @Test
    public void httpTest() {
        log.info("测试完成");
    }

}
