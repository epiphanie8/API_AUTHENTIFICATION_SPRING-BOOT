package com.magir.gestionacces.service;

import java.util.Map;
import java.util.Optional;

import com.magir.gestionacces.entity.Utilisateur;

public interface UtilisateurService {

	void inscription(Utilisateur utilisateur);

	void activation(Map<String, String> activation);
}
