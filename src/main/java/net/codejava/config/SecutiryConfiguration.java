package net.codejava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.hibernate.criterion.Restrictions.and;


@Configuration
@EnableWebSecurity
public class SecutiryConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	AuthenticationSuccessHandler successHandler;

	@Bean
	public BCryptPasswordEncoder getEncodedPassword(){return new BCryptPasswordEncoder();}
	
	public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/procces-login").setViewName("login");
    }
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService);
		auth.inMemoryAuthentication()
				.withUser("admin").roles("ADMIN").password("{noop}password");
		auth.inMemoryAuthentication()
				.withUser("user").roles("USER").password("{noop}password");
	}
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
				.antMatchers("/").hasAnyAuthority("USER","ADMIN")
		.antMatchers("/login").permitAll()
				.antMatchers("/register").permitAll()
		.antMatchers("/new").hasAuthority("USER")
		.antMatchers("/edit/{id}").hasAnyAuthority("USER")
		.antMatchers("/delete/{id}").hasAnyAuthority("ADMIN","USER")
		.and()
		.formLogin()
		.loginPage("/procces-login")
		.successHandler(successHandler)
		.permitAll()
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login.html")
				.and()
				.exceptionHandling()
				.accessDeniedPage("/access-denied");
}



}
//password encoder with bcrypt encoder spring security example
//here



