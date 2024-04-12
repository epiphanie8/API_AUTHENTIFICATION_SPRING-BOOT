package com.magir.gestionacces.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magir.gestionacces.entity.Jwt;

public interface JwtRepository extends JpaRepository<Jwt, Long> {
	
	Optional<Jwt> findByValeur(String valeur);

}
