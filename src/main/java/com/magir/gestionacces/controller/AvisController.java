package com.magir.gestionacces.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.magir.gestionacces.entity.Avis;
import com.magir.gestionacces.service.AvisService;


@RestController
@RequestMapping("avis")
public class AvisController {
	
	@Autowired
	private AvisService avisService;
	
	@PostMapping("creerAvis")
	public String creerAvis(@RequestBody Avis avis) {
		
		avisService.creerAvis(avis);
		return "Avis ajouté avec succès";
	}

}
