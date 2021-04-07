package com.banka.security;

public class SecurityConstants {
	public static final String USER_URLS = "/api/v1/users/**";
	 public static final String H2_URL = "/h2-console/**";
	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final long EXPIRATION_TIME = 3_600_000; // 
}
