package com.revature.rideshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
//@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        requiresChannel()
//        	.antMatchers("/**")
//        		.requiresSecure()
//        	.and()
//        	http
//        		.authorizeRequests()
//        		.formLogin()
//        		.loginPage("/#/login")
//        		.loginProcessingUrl("/auth")
//        		.defaultSuccessUrl("/#/passenger")
//        		.failureUrl("/#/login?error=true")
//        		.permitAll()
//        	.and()
//        	.exceptionHandling()
//        		.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/#/login"));
    	http.antMatcher("/**").authorizeRequests()
    		.antMatchers("/**")
    			.permitAll()
    			.anyRequest()
    			.authenticated()
//    		.antMatchers("/login")
//    			.permitAll()
    		.and()
    			.exceptionHandling()
    			.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
    		.and()
    			.httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        	.withUser("user").password("password").roles("USER");
    }
}