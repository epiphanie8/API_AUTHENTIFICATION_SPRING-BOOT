package com.magir.gestionacces.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.magir.gestionacces.entity.Avis;
import com.magir.gestionacces.entity.Utilisateur;
import com.magir.gestionacces.repository.AvisRepository;
import com.magir.gestionacces.service.AvisService;



@Service
public class AvisServiceImpl implements AvisService{

	@Autowired
	private AvisRepository avisRepository;
	
	@Override
	public void creerAvis(Avis avis) {
		
		avisRepository.save(avis);
		
	}

}
