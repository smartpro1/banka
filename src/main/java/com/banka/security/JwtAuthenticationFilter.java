package com.banka.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.banka.services.CustomUserDetailsService;
import static com.banka.security.SecurityConstants.HEADER_STRING;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// extract token from request header
			String bearerToken = request.getHeader(HEADER_STRING);
			if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
				bearerToken = bearerToken.substring(7, bearerToken.length());
			}
			
			// validate token and load user associated with the token if successful
			if(StringUtils.hasText(bearerToken) && jwtTokenProvider.validateJwtToken(bearerToken)) {
				Long adminId = jwtTokenProvider.getUserIdFromJwtToken(bearerToken);
				UserDetails userDetails = customUserDetailsService.loadUserById(adminId);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				
				// build and set the authentication in Spring Security
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}  catch(Exception ex) {
			logger.error("could not set user authentication: " + ex);
			ex.printStackTrace();
		}
		
		filterChain.doFilter(request, response);
	}

}
