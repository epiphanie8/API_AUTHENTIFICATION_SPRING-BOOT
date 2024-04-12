package com.magir.gestionacces.implementation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.magir.gestionacces.entity.Acces;
import com.magir.gestionacces.entity.Utilisateur;
import com.magir.gestionacces.repository.AccesRepository;
import com.magir.gestionacces.service.AccesService;
import com.magir.gestionacces.service.NotificationService;

@Service
public class AccesServiceImpl implements AccesService{
	
	@Autowired
	private AccesRepository accesRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Override
	public void creerAcces(Utilisateur utilisateur) {
		
		Acces myAcess = new Acces();
		
		myAcess.setUtilisateur(utilisateur);
		
		Instant creation = Instant.now();
		myAcess.setCreation(creation);
		
		Instant expriration = creation.plus(10,ChronoUnit.MINUTES);
		myAcess.setExpriration(expriration);
		
		//generer un nombre aleatoire
		
		Random random = new Random();
		int randomInteger = random.nextInt(999999);
		String code = String.format("%06d", randomInteger);
		
		myAcess.setCode(code);
		accesRepository.save(myAcess);
		
		//envoi du mail
		notificationService.envoyerNotification(myAcess);
		
	}
	
	//recuperer le code pour lactivation
	
	
	  public Acces lireEnFonctionDuCode(String code) { return
	  accesRepository.findByCode(code).orElseThrow(() -> new
	  RuntimeException("Votre code est invalide"));
	  
	  }
	 
	

	

}
