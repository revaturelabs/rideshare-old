//package com.revature.rideshare;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.web.context.request.RequestContextListener;
//
////@Configuration
////@ComponentScan
////@EnableAutoConfiguration
//@SpringBootApplication
//@EnableOAuth2Sso
//public class Application2 extends WebSecurityConfigurerAdapter {
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.antMatcher("/**")
//			.authorizeRequests()
//			.antMatchers("/", "/login").permitAll()
//			.anyRequest().authenticated();
//	}
//	
//	@Bean
//	public RequestContextListener requestContextListener() {
//		return new RequestContextListener();
//	}
//
////	@Autowired
////	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
////		auth.inMemoryAuthentication()
////			.withUser("user").password("password").roles("USER");
////	}
//  
//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(Application2.class, args);
//	}
//
//}
