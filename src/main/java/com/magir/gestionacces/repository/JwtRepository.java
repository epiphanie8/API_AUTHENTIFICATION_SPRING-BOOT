package com.magir.gestionacces.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.magir.gestionacces.entity.Jwt;

public interface JwtRepository extends JpaRepository<Jwt, Long> {
	
	Optional<Jwt> findByValeurAndDesactiverAndExpire(String valeur, boolean desactiver, boolean expire);
	
	@Query("FROM Jwt j WHERE j.expire = :expire AND j.desactiver = :desactiver AND j.utilisateur.email = :email")
	Optional<Jwt> findByUtilisateurEmailAndDesactiveAndExprire(String email, boolean desactiver, boolean expire);
	
	@Query("FROM Jwt j WHERE j.utilisateur.email = :email")
	Stream<Jwt> findUtilisateur(String email);

}
