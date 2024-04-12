package com.magir.gestionacces.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magir.gestionacces.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
	
	Optional<Utilisateur> findByEmail(String email);
	

}
