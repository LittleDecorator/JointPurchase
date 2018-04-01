package com.acme.security;

/**
 * Created by vkokurin on 12.02.2015.
 */
public class AuthorityConstants {

    public static final String ROLE_DIRECTOR = "ROLE_DIRECTOR";
    public static final String ROLE_SUPERVISOR = "ROLE_SUPERVISOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ANONYMOUS = "ANONYMOUS";
    public static final String ROLE_DIRECTOR_CLUSTER = "ROLE_DIRECTOR_CLUSTER";
    public static final String ROLE_CURATOR = "ROLE_CURATOR";
    public static final String ROLE_SECRET = "ROLE_SECRET";

	/**
	 * There aren't any user with such role
	 */
	@Deprecated
    public static final String ROLE_ENERGETIC = "ROLE_ENERGETIC";

	/**
	 * There aren't any user with such role
	 */
	@Deprecated
    public static final String ROLE_ENGINEER = "ROLE_ENGINEER";

	/**
	 * There aren't any user with such role
	 */
	@Deprecated
    public static final String ROLE_MAIN_ENGINEER = "ROLE_MAIN_ENGINEER";
}
