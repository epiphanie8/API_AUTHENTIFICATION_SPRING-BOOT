package com.magir.gestionacces.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.magir.gestionacces.entity.Jwt;
import com.magir.gestionacces.implementation.UtilisateurServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtFilter extends OncePerRequestFilter {

	/******************************* filtrer le token recu a la connexion**********************/
	private UtilisateurServiceImpl utilisateurServiceImpl;
	private JwtService jwtService;
	
	
	public JwtFilter(UtilisateurServiceImpl utilisateurServiceImpl, JwtService jwtService) {
		this.utilisateurServiceImpl = utilisateurServiceImpl;
		this.jwtService = jwtService;
	}

	//connexion:filtrer les jwt lors de lauthentification pour recuperer lutilisateur conecté
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		String token =null;
		Jwt tokenDansLaBDD = null;
		String username = null;
		boolean isTokenExpired = true;
		
		//extraire le token
		final String authorization = request.getHeader("Authorization");
		if(authorization != null && authorization.startsWith("Bearer")) {
		//recuperer le token a partir du 7iem caractere
		token = authorization.substring(7);
		//aller verifier que le token na pa expirer: celui de la base de données
		tokenDansLaBDD = this.jwtService.tokenByValue(token);
		isTokenExpired = jwtService.isTokenExpired(token);
		//aller chercher le username grace au UserDetailService
		username = jwtService.extractUsername(token);
		
		
		}
		//si le token na pas expirer etbverifier qu'un utilisateur est connecté et que le contexte est null
		if(!isTokenExpired 
				&& username != null 
				&& tokenDansLaBDD.getUtilisateur().getEmail().equals(username)
				&& SecurityContextHolder.getContext().getAuthentication() == null
		) 
		{
			//aller chercher alors lutilisateur connecté
			UserDetails userDetails = utilisateurServiceImpl.loadUserByUsername(username);
			//creer une donnée dauthentification dans le context
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		
		//si toute ces conditions son respectees, continuer a filtrer notre requete et notre reponse
		filterChain.doFilter(request, response);
		
		
	}
	/******************************* fin du filtrer**********************/

}
