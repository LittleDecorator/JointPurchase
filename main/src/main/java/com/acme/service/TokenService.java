package com.acme.service;

import com.acme.gen.domain.Credential;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    //get token builder base
    private JwtBuilder createToken(Credential credential, Date now) {
        return Jwts.builder()
                .setId(credential.getSubjectId())
                .claim("role", credential.getRoleId())
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, getKey());
    }

    //return expiring token
    public String createExpToken(Credential credential, Long ttl){
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttl);
        JwtBuilder builder = createToken(credential,now);
        builder.setExpiration(exp);
        return builder.compact();
    }

    public String createToken(Credential credential){
        Date now = new Date(System.currentTimeMillis());
        JwtBuilder builder = createToken(credential,now);
        return builder.compact();
    }

    public Key getKey(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("superSecretKey");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }
}
