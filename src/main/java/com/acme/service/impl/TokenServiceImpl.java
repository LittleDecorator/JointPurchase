package com.acme.service.impl;

import com.acme.model.Credential;
import com.acme.service.TokenService;
import com.google.common.collect.Maps;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {


    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    /**
     * Вернем builder токена
     * @param credential
     * @param now
     * @param claims
     * @return
     */
    private JwtBuilder createToken(Credential credential, Date now, Map<String, Object> claims) {
        // создадим builder
        JwtBuilder builder = Jwts.builder().setId(credential.getSubjectId())
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, getKey());
        // добавим роль
        builder.claim("role", credential.getRoleId());
        // добавим доп параметры
        for(Map.Entry<String, Object> entry : claims.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    /**
     * Вернем builder токена
     * @param credential
     * @param now
     * @return
     */
    private JwtBuilder createToken(Credential credential, Date now) {
        return createToken(credential, now, null);
    }

    /**
     * Создадим истекающий токен
     * @param credential
     * @param ttl
     * @return
     */
    public String createExpToken(Credential credential, Long ttl){
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttl);
        JwtBuilder builder = createToken(credential,now);
        builder.setExpiration(exp);
        return builder.compact();
    }

    /**
     * Создания истекающего токена
     * @param builder
     * @param ttl
     * @return
     */
    private String createExpToken(JwtBuilder builder, Long ttl){
        long nowMillis = System.currentTimeMillis();
        Date exp = new Date(nowMillis + ttl);
        builder.setExpiration(exp);
        return builder.compact();
    }

    /**
     * Создадим истекающий токен
     * @param credential
     * @param ttl
     * @param claims
     * @return
     */
    public String createExpToken(Credential credential, Long ttl, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        return createExpToken(createToken(credential, now, claims), ttl);
    }

    public String createToken(Credential credential){
        Date now = new Date(System.currentTimeMillis());
        JwtBuilder builder = createToken(credential, now);
        return builder.compact();
    }

    public Key getKey(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("superSecretKey");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }

}
