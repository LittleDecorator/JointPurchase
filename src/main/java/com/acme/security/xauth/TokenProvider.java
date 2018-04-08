package com.acme.security.xauth;

import com.acme.model.Subject;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    //@Value("${authentication.xauth.secret}")
    //private String secretKey;
    //@Value("${authentication.xauth.tokenValidityInSeconds}")
    //private int tokenValidity;

    @Autowired
    TokenService tokenService;

    //@Autowired
    //AuthService authService;

    //@Autowired
    //SubjectRepository subjectRepository

    //public Token createToken(UserDetails userDetails) {
    //    long expires = System.currentTimeMillis() + 1000L * tokenValidity;
    //    String token = userDetails.getUsername() + ":" + expires + ":" + computeSignature(userDetails, expires);
    //    return new Token(token, expires);
    //}

    //private String computeSignature(UserDetails userDetails, long expires) {
    //    MessageDigest digest;
    //    try {
    //        digest = MessageDigest.getInstance("MD5");
    //    } catch(NoSuchAlgorithmException nsae) {
    //        throw new IllegalStateException("No MD5 algorithm available!");
    //    }
    //    return new String(Hex.encode(digest.digest((userDetails.getUsername() + ":" + expires + ":" +
    //        userDetails.getPassword() + ":" + secretKey).getBytes())));
    //}

    public String getUserIdFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(authToken).getBody();
        //Subject subject = subjectRepository.findOne(claims.getId());
        return claims.getId();
    }

    public boolean validateToken(String authToken) {

        return tokenService.validate(authToken);

    }
    //public boolean validateToken(String authToken, UserDetails userDetails) {
    //    List<String> parts = Lists.newArrayList(Splitter.on(":").split(authToken));
    //if (parts.size() < 3)
    //return false;
    //    long expires = Long.parseLong(parts.get(1));
    //    String signature = parts.get(2);
    //    String signatureToMatch = computeSignature(userDetails, expires);
    //    return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
    //}
}
