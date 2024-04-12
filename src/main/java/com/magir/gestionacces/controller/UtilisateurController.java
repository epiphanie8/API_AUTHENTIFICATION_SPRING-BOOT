package com.magir.gestionacces.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.magir.gestionacces.dto.AuthenticationDTO;
import com.magir.gestionacces.entity.Utilisateur;
import com.magir.gestionacces.security.JwtService;
import com.magir.gestionacces.service.UtilisateurService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UtilisateurService utilisateurService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@PostMapping("inscription")
	public String inscription(@RequestBody Utilisateur utilisateur) {
		
		utilisateurService.inscription(utilisateur);
		return "inscription reussie";
	}
	
	@PostMapping("activation")
	public String activation(@RequestBody Map<String, String> activation) {
		utilisateurService.activation(activation);
		return "Votre compte à étè activer avec succès";
		
	}
	
	
	
	@PostMapping("connexion")
	public Map<String, String> connexion(@RequestBody AuthenticationDTO authenticationDTO) {
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
				);
		log.info("resultat {}", authentication.isAuthenticated());
		
		//appeler la methode de generation du jwt si utilisateur authentifier
		if(authentication.isAuthenticated()) {
			return jwtService.generate(authenticationDTO.username());
		}
		
		return null;
		
		
	}
	

}
