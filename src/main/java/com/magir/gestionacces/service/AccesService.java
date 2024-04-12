package com.magir.gestionacces.service;

import com.magir.gestionacces.entity.Acces;
import com.magir.gestionacces.entity.Utilisateur;


public interface AccesService {
	
	void creerAcces(Utilisateur utilisateur);
	Acces lireEnFonctionDuCode(String code);

}
