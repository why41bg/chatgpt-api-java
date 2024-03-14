package cn.why41bg.chatgpt.api.domain.auth.service;

import cn.why41bg.chatgpt.api.domain.auth.model.entity.AuthResultEntity;
import cn.why41bg.chatgpt.api.domain.auth.model.valobj.AuthTypeValObj;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Classname AuthService
 * @Description 为 Web 应用提供鉴权服务的实现类
 * @Author 魏弘宇
 * @Date 2024/3/13 17:24
 */
@Slf4j
@Service
public class AuthService implements IAuthService{

    @Value("${openai.chatgpt.api.secret-key}")
    private String secretKey;

    @Value("${openai.chatgpt.api.token-ttl}")
    private long tokenTtl;

    @Value("${openai.chatgpt.api.code-len}")
    private int codeLen;

    /**
     * 创建一个 HMAC256 算法的密钥
     */
    private Algorithm algorithm;

    private String base64EncodedSecretKey;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 该初始化方法发生在属性注入之后自动调用
     */
    @PostConstruct
    private void init() {
        this.base64EncodedSecretKey = Base64.encodeBase64String(secretKey.getBytes());
        this.algorithm = Algorithm.HMAC256(Base64.decodeBase64(Base64.encodeBase64String(secretKey.getBytes())));
    }

    @Override
    public AuthResultEntity doLogin(String code) {
        // 验证码格式检验
        if (!code.matches("\\d{" + codeLen +"}")) {
            log.info("用户输入验证码无效 {}", code);
            return AuthResultEntity.builder()
                    .code(AuthTypeValObj.A0002.getCode())
                    .info(AuthTypeValObj.A0002.getInfo())
                    .build();
        }

        // 校验判断，非成功则直接返回，成功则进行下一步生成 Token
        AuthResultEntity authResultEntity = this.checkCode(code);
        if (!authResultEntity.getCode().equals(AuthTypeValObj.A0000.getCode())) {
            return authResultEntity;
        }

        // 获取 Token 并返回
        Map<String, Object> chaim = new HashMap<>();
        chaim.put("openId", authResultEntity.getOpenId());
        String token = encode(authResultEntity.getOpenId(), tokenTtl, chaim);
        authResultEntity.setToken(token);

        return authResultEntity;

    }

    /**
     * 检查验证码是否在缓存中存在，即判断用户是否从公众号获取了验证码
     * @param code 验证码
     * @return 检验结果
     */
    private AuthResultEntity checkCode(String code) {
        // 根据验证码从缓存中获取用户唯一标识符进行校验，如果验证码存在，使用一次之后直接移除
        String openId = stringRedisTemplate.opsForValue().getAndDelete(code);
        if (!StringUtils.isBlank(openId)) {
            stringRedisTemplate.opsForValue().getAndDelete(openId);
        }

        // 验证码不存在则直接返回结果
        if (StringUtils.isBlank(openId)){
            log.info("鉴权，用户输入的验证码不存在 {}", code);
            return AuthResultEntity.builder()
                    .code(AuthTypeValObj.A0001.getCode())
                    .info(AuthTypeValObj.A0001.getInfo())
                    .build();
        }

        // 验证码校验成功
        return AuthResultEntity.builder()
                .code(AuthTypeValObj.A0000.getCode())
                .info(AuthTypeValObj.A0000.getInfo())
                .openId(openId)
                .build();
    }

    @Override
    public boolean checkToken(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(jwtToken);
            // 校验不通过会抛出异常
            // 判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
            return true;
        } catch (Exception e) {
            log.error("JWT isVerify Err {}", jwtToken, e);

            return false;
        }
    }

    /**
     * 生成JWT
     * @param issuer 签发人
     * @param ttlMillis Token TTL
     * @param claims 额外载荷，非隐私信息
     * @return JWT
     */
    protected String encode(String issuer, long ttlMillis, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                // 荷载部分
                .setClaims(claims)
                // 这个是JWT的唯一标识，一般设置成唯一的，这个方法可以生成唯一标识
                .setId(UUID.randomUUID().toString())
                // 签发时间
                .setIssuedAt(new Date(nowMillis))
                // 签发人，也就是JWT是给谁的（逻辑上一般都是username或者userId）
                .setSubject(issuer)
                // 生成JWT使用的算法和秘钥
                .signWith(SignatureAlgorithm.HS256, base64EncodedSecretKey);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            builder.setExpiration(new Date(expMillis));
        }
        return builder.compact();
    }

}
