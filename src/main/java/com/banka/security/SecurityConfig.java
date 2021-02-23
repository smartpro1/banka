package com.banka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.banka.services.CustomUserDetailsService;

//import static com.banka.security.SecurityConstants.USER_URLS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true,
		prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	 @Bean
	 JwtAuthenticationFilter jwtAuthenticationFilter() {
		 return new JwtAuthenticationFilter();
	 } 
	
	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager() throws Exception{
		return super.authenticationManager();
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception{
    	
    	http.cors().and().csrf().disable()
    	    .exceptionHandling().authenticationEntryPoint(securityAuthenticationEntryPoint)
    	    .and()
    	    .sessionManagement()
    	      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	      .and()
    	      .authorizeRequests()
    	      .antMatchers(
    	    		  "/",
		    		     "/**/*.png",
		    		     "/**/*.gif",
		    		     "/**/*.svg",
		    		     "/**/*.jpg",
		    		     "/**/*.html",
		    		     "/**/*.css",
		    		     "/**/*.js"
    	    		  ).permitAll()
//    	      .antMatchers(USER_URLS).permitAll()
    	    .antMatchers("/api/v1/users/signup","/api/v1/users/login",
    	    		      "/api/v1/users/forgot-password", "/api/v1/users/reset-password", "/api/v1/users/confirm-registration",
                        "/api/v1/users/user-transactions/{userId}").permitAll()
    	       .anyRequest()
    	       .authenticated();
    	
    	 http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
	
   
}
