package com.magir.gestionacces.implementation;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.magir.gestionacces.TypeRole;
import com.magir.gestionacces.entity.Acces;
import com.magir.gestionacces.entity.Role;
import com.magir.gestionacces.entity.Utilisateur;
import com.magir.gestionacces.repository.UtilisateurRepository;
import com.magir.gestionacces.service.AccesService;
import com.magir.gestionacces.service.UtilisateurService;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class UtilisateurServiceImpl implements UtilisateurService, UserDetailsService{

	@Autowired
	private UtilisateurRepository utilisateurRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AccesService accesService;
	
	@Override
	public void inscription(Utilisateur utilisateur) {
		
		//verifier la validite de l'email
		
		if(!utilisateur.getEmail().contains("@") || !utilisateur.getEmail().contains("."))
			{
				throw new RuntimeException("Votre email est invalide");
			}
		
		//verifier l'existance de l'email dans la base de donnéé
		
		Optional<Utilisateur> existingUtilisateur = utilisateurRepository.findByEmail(utilisateur.getEmail());
		if(existingUtilisateur.isPresent()) {
			
			throw new RuntimeException("L'email existe déjà");
		}
		
		//attribuer un role a l'utilisateur
		Role utilisateurRole = new Role();
		utilisateurRole.setLibelle(TypeRole.UTILISATEUR);
		
		utilisateur.setRole(utilisateurRole);
		
		//crypter le mot de passe
		String myCrypto = passwordEncoder.encode(utilisateur.getMdp());
		utilisateur.setMdp(myCrypto);

		//sauvegarder dans la base de données
		utilisateurRepository.save(utilisateur);
		
		//creer les access
		accesService.creerAcces(utilisateur);
		
	}

	@Override
	public void activation(Map<String, String> activation) {
		Acces myAcces = accesService.lireEnFonctionDuCode(activation.get("code"));
		
		//verification de la date dexpiration du code
		if(Instant.now().isAfter(myAcces.getExpriration()))
		{
			throw new RuntimeException("Votre code a expiré");
		}
		
		//verification de l'existance du l'utilisateur
		Utilisateur utilisateuractiver = utilisateurRepository.findById(myAcces.getUtilisateur().getId()).orElseThrow(() -> new RuntimeException("Utilisater inconnu"));
		utilisateuractiver.setActif(true);
		
		myAcces.setActivation(Instant.now());
		
		//sauvegarder l'utilisateur
		utilisateurRepository.save(utilisateuractiver);
		
		
	}
	
	//authentification

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return utilisateurRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Auncun utilisateur ne correspond a cet identifiant.") );
		
		
	}
	

}
