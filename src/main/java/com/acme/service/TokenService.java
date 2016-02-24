package com.acme.service;


import com.acme.model.Credential;

import java.security.Key;

public interface TokenService {

    String createExpToken(Credential credential, Long ttl);

    String createToken(Credential credential);

    Key getKey();

}
