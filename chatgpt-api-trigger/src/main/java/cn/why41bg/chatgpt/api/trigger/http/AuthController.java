package cn.why41bg.chatgpt.api.trigger.http;

import cn.why41bg.chatgpt.api.domain.auth.model.entity.AuthResultEntity;
import cn.why41bg.chatgpt.api.domain.auth.model.valobj.AuthTypeValObj;
import cn.why41bg.chatgpt.api.domain.auth.service.IAuthService;
import cn.why41bg.chatgpt.api.types.enums.ResponseCode;
import cn.why41bg.chatgpt.api.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Classname AuthController
 * @Description 鉴权服务控制器
 * @Author 魏弘宇
 * @Date 2024/3/13 16:16
 */
@Slf4j
@RestController
@CrossOrigin("${openai.api.cross-origin}")
@RequestMapping("/api/${openai.api.version}/auth/")
public class AuthController {

    @Resource
    private IAuthService authService;

    /**
     * 用户使用验证码登陆，如果验证码有效，则发放Token
     * @param code 验证码
     * @return 登陆响应结果
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Response<String> doLogin(@RequestParam String code) {
        log.info("鉴权登录校验开始，用户输入验证码: {}", code);
        try {
            AuthResultEntity authResultEntity = authService.doLogin(code);
            log.info("鉴权登录校验完成，用户输入验证码: {} 结果: {}", code, JSON.toJSONString(authResultEntity));
            // 拦截，鉴权失败
            if (!AuthTypeValObj.A0000.getCode().equals(authResultEntity.getCode())) {
                return Response.<String>builder()
                        .code(ResponseCode.PRIVILEGES_ERROR.getCode())
                        .info(ResponseCode.PRIVILEGES_ERROR.getInfo())
                        .build();
            }

            // 放行，鉴权成功
            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(authResultEntity.getToken())
                    .build();

        } catch (Exception e) {
            log.error("鉴权登录校验失败，验证码: {}", code);
            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
