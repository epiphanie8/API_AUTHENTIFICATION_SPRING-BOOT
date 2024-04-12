package com.magir.gestionacces.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.magir.gestionacces.implementation.UtilisateurServiceImpl;


@Configuration
@EnableWebSecurity
public class ConfigurationSecurityApplication {
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtFilter jwtFilter;
	
	//creer du constructeur au lieu et place de Autoriwed
	public ConfigurationSecurityApplication(JwtFilter jwtFilter, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.jwtFilter = jwtFilter;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	
	//routes autoriser
	@Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		
		return 
				httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
				auth -> auth
				.requestMatchers("/inscription").permitAll()
				.requestMatchers("/activation").permitAll()
				.requestMatchers("/connexion").permitAll()
				.anyRequest().authenticated()
		/***************************** authentification *******************************/
				
				//filtrer le jwt lors dans la session dauthentification
				).sessionManagement(
						httpSecuritySessionManagementConfiguration ->
						//sans etat, spring verifier a chaque fois quun token lui est donner.il ne garder pas dinformation
							httpSecuritySessionManagementConfiguration.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						)
						//avant dauthentifier lutilisateur avec le filtre de springboot, on le demande dutiliser notre filtre a nos(JwtFilter) et a partir de cela il pourra authentifier lutilisateur
				
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
		
	}

	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	//indiquer a spring ou il doit aller chercher l'utilisateur pour lauthentification
	
	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return daoAuthenticationProvider;
	}
	
	/***************************** fin authentification *******************************/

}
