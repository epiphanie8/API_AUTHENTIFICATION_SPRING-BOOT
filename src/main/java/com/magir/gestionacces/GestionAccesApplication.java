package com.magir.gestionacces;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //pour programmer une tache qui permet de vider la table jwt a un moment bien precis
@SpringBootApplication
public class GestionAccesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionAccesApplication.class, args);
	}

}
