package com.acme.security;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * Created by vsko on 05.04.16.
 */
public class SaltPasswordEncoder implements PasswordEncoder {

	private final String secretKey;

	public SaltPasswordEncoder(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String encode(CharSequence charSequence) {
		return new String(Hex.encode((charSequence + ":" + secretKey).getBytes()));
	}

	public String decode(CharSequence charSequence) {
		if(StringUtils.isEmpty(charSequence)) {
			return null;
		}
		String decoded = new String(Hex.decode(charSequence));
		if(!decoded.contains(":") || !decoded.contains(secretKey)) {
			return null;
		}
		return decoded.split(":")[0];
	}

	@Deprecated
	@Override
	public boolean matches(CharSequence charSequence, String s) {
		return false;
	}
}
