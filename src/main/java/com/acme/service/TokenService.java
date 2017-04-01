package com.acme.service;


import com.acme.model.Credential;

import java.security.Key;
import java.util.Map;

public interface TokenService {

    String createExpToken(Credential credential, Long ttl);

    String createExpToken(Credential credential, Long ttl, Map<String,Object> claims);

    String createToken(Credential credential);

    Key getKey();

}
