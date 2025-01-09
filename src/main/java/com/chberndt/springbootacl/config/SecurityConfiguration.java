package com.chberndt.springbootacl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Christian Berndt
 */
@Configuration
@EnableTransactionManagement(order = 0)
@EnableMethodSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.GET, "/albums", "/albums/*")
			.permitAll()
			.anyRequest()
			.authenticated()).csrf(AbstractHttpConfigurer::disable).httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {

		UserDetails admin = User.withUsername("admin")
			.password(passwordEncoder().encode("secret"))
			.roles("ADMIN")
			.build();

		UserDetails alice = User.withUsername("alice")
			.password(passwordEncoder().encode("secret"))
			.roles("USER")
			.build();

		UserDetails bob = User.withUsername("bob").password(passwordEncoder().encode("secret")).roles("USER").build();

		UserDetails user = User.withUsername("user").password(passwordEncoder().encode("secret")).roles("USER").build();

		return new InMemoryUserDetailsManager(admin, alice, bob, user);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/h2-console/**");
	}

}
