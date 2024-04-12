package com.magir.gestionacces.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magir.gestionacces.entity.Acces;
import com.magir.gestionacces.entity.Utilisateur;

public interface AccesRepository extends JpaRepository<Acces, Long> {
	
	
	Optional<Acces> findByCode(String code);

}
