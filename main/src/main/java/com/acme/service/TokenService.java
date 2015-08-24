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

public interface TokenService {

    String createExpToken(Credential credential, Long ttl);

    String createToken(Credential credential);

    Key getKey();

}
