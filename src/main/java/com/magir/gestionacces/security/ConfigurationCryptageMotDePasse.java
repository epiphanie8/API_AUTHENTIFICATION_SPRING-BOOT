package com.magir.gestionacces.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//fonction seperer pour eviter les bocle
@Configuration
public class ConfigurationCryptageMotDePasse {
	
	
	//creer un encodage de mot de passe
		@Bean
		BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	

}
