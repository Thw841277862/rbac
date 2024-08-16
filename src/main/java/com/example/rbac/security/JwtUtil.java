package com.example.rbac.security;



import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT需要提供的功能如下
 * 1.提供token的生成（需要有 用户标识 和 过期时间）
 * 2.提供token的校验
 *
 * @param null
 * @author thw
 * @date 2024/8/12 17:31
 * @return null
 */
@Component
public class JwtUtil {

    //根据私钥和公钥生成RSA
    private static RSA rsa = new RSA("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKPf2CUbYLiIVje6xNayFUXXMXOVfdu9RDyezaFfu3sqV8DQ22aNnUKrxi92KvrK786311ThCHsUbsUulPoS59iB/N/dGiV7onfPzsoeSf3nuEquG7aQGkJzesFsf3LiXMs2cF6RN2kUq0tO07QXl2p3iDxlyt0LMo9jSYWWF8HXAgMBAAECgYABegPQctmBdpGRYeu3m4lSmgzdwaBJXe9cZ60eIe9d39B3rcf5IDGLxS6bUc9ERJaG/jDR3PoJYvJp6HCc0Mipe7rGuIUAzTRyHFgLdl+z2S8cw2Sh4omGvAPKIaayk1PCsLGF7pBGXpqyzQasBRD0AykWlGhhEehoKH+oVYHCMQJBAOHTcQfwhUl6mx6TJRCuKEsDXXZwfzxpDl1C7a4WIGZXO8DfP2BdHCE4OSf+hrw6aAhv911BJ2u9KIv61Fnd5HsCQQC5xUzN4j87RIn72CNIO+d+LQ9GNScn5zdZYv+vBhhxETqGwcASIO7utZGMcacy8X6oEl75Jt1/JL2rhDOmQB9VAkAwGJ5sOHfKzIgZHPG5+b2vLEpNnWwb66wZhNWmTCQeZ9ncHDAdT/dbw+O4ducvU6kwktg2TlgdBC6MkaGEelPdAkEAny6mMmEk54dCaU/6Y6IZKfA4f2N6auHgdrKy/unNNc2ahfAtwhsAQgHJdzvIFooCisg7I2mfMJxt/jNpApM9hQJBAIDwBl0w61cK96EA7BG2NcksZWCVFVKi1UDQ7u+sxO/7GfFt/Z5xHDRw42g5O8zxPfKibJAb1oawezUfBHEZGqs="
            , "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCj39glG2C4iFY3usTWshVF1zFzlX3bvUQ8ns2hX7t7KlfA0NtmjZ1Cq8Yvdir6yu/Ot9dU4Qh7FG7FLpT6EufYgfzf3Role6J3z87KHkn957hKrhu2kBpCc3rBbH9y4lzLNnBekTdpFKtLTtO0F5dqd4g8ZcrdCzKPY0mFlhfB1wIDAQAB");



    /**
     * 生成token
     *
     * @param userDetails
     * @return String
     * @author thw
     * @date 2024/8/12 17:41
     */
    public static String generateToken(UserDetails userDetails) {
        KeyPair keyPair = new KeyPair(rsa.getPublicKey(), rsa.getPrivateKey());
        //当前时间
        LocalDateTime now = LocalDateTime.now();
        //过期时间
        LocalDateTime localDateTime = now.plusHours(2);
        // 将 LocalDateTime 转换为 Date
        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiresAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return "Bearer " + JWT.create()
                .setPayload("userName", userDetails.getUsername())
                .setSigner("RS256", keyPair)
                //签发时间
                .setIssuedAt(issuedAt)
                //过期时间
                .setExpiresAt(expiresAt)
                .sign();
    }


    public static Boolean validateToken(String token) {
        try {
            JWTValidator.of(token).validateDate();
        } catch (ValidateException validateException) {
            return false;
        }
        return true;
    }

    public static String parseToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("userName");
    }
}
