package com.acme.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * A utility class for creating secure password hashes as well as validating
 * passwords.
 * 
 * @see http://crackstation.net/hashing-security.html
 * @see http://www.jasypt.org/howtoencryptuserpasswords.html
 * @see http://seanmonstar.com/post/707158385/a-basic-lesson-in-password-hashing
 */
public final class PasswordHashing {

	private static final SecureRandom random = new SecureRandom();
	private static final int SALT_LENGTH = 64;
	private static final int ITERATION_COUNT = 1000;

	public static String hashPassword(String password) {
		Preconditions.checkNotNull(password);
		String salt = getRandomSalt();
		return getHash(password, salt);
	}

	private static String getHash(String password, String salt) {
		HashFunction func = Hashing.sha256();
		HashCode result = func.hashString(salt + password, Charsets.UTF_8);
		for (int i = 0; i < ITERATION_COUNT; i++) {
			result = func.hashBytes(result.asBytes());
		}
		return new StringBuilder(result.toString()).insert(password.length(),
				salt).toString();
	}

	public static boolean validatePassword(String password, String correctHash) {
		Preconditions.checkNotNull(password);
		Preconditions.checkNotNull(correctHash);
		Preconditions.checkArgument(correctHash.length() == SALT_LENGTH * 2);
		String salt = correctHash.substring(password.length(), SALT_LENGTH
				+ password.length());
		return getHash(password, salt).equals(correctHash);
	}

	static String getRandomSalt() {
		String randomHexString = new BigInteger(SALT_LENGTH * 4, random)
				.toString(16);
		String result = Strings.padStart(randomHexString, SALT_LENGTH, '0');
		return result;
	}

	private PasswordHashing() {
		throw new AssertionError();
	}

	/*public static void main(String[] args) {
		String password = "admin";
		String hash = hashPassword(password);
		System.out.println(password);
		System.out.println(hash);
		System.out.println(validatePassword(password, hash));
	}*/
}
