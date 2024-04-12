package com.magir.gestionacces.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.magir.gestionacces.dto.AuthenticationDTO;
import com.magir.gestionacces.entity.Jwt;
import com.magir.gestionacces.entity.Utilisateur;
import com.magir.gestionacces.implementation.UtilisateurServiceImpl;
import com.magir.gestionacces.repository.JwtRepository;
import com.magir.gestionacces.service.UtilisateurService;

import aj.org.objectweb.asm.signature.SignatureWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Transactional
@AllArgsConstructor
@Service
public class JwtService {
	
	private static final String BEARER = "bearer";

	/******************* generation du token *************************/
	private final String ENCRYPTION_KEY = "7ae9bca9d30e21b6e9cb173f31de83c94c5cd1d03f76e6458534c51a13ff58a1";
	
	private UtilisateurServiceImpl utilisateurServiceImpl;
	private JwtRepository jwtRepository;
	
	
	public Map<String, String> generate(String username){
		
		//aller chercher l'utilisateur connecter
		Utilisateur utilisateur = (Utilisateur) utilisateurServiceImpl.loadUserByUsername(username);
		//desactiver d'abord les token non desactiver avant de generer un autre
		this.disableTokens(utilisateur);
		final Map<String, String> jwtMap = this.generateJwt(utilisateur);
	/************** inserer le jwt dans notre base de donnée dans la table jwt **********/
		final Jwt jwt = Jwt.
			builder()
			.desactiver(false)
			.expire(false)
			.valeur(jwtMap.get(BEARER))
			.utilisateur(utilisateur)
			.build();
		this.jwtRepository.save(jwt);
	/**************fin insertion du jwt dans notre base de données**********/
		return jwtMap;
		
	}
	
	//desactivation tous les token encore valide
	public void disableTokens(Utilisateur utilisateur) {
		final List<Jwt> jwtList = this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
				jwt ->{
					jwt.setDesactiver(true);
					jwt.setExpire(true);
				}
				).collect(Collectors.toList());
	}

	//traitement de la generation du jwt

	private Map<String, String> generateJwt(Utilisateur utilisateur) {
		
		//date de creation du token en milliseconde
				final long createTime = System.currentTimeMillis();
				//date dexpiration  du token en milliseconde + 30min
				final long expireTime = createTime + 30 * 60 * 1000;
				
		//definir mes cles
		Map<String, Object> claims =  Map.of(
				"nom", utilisateur.getNom(),
				Claims.EXPIRATION, new Date(expireTime),
				Claims.SUBJECT,utilisateur.getEmail()
				);
		
		
		final String bearer = Jwts.builder()
		.setIssuedAt(new Date(createTime))
		.setExpiration(new Date(expireTime))
		.setSubject(utilisateur.getEmail())
		.setClaims(claims)
		//la signature avec laquelle on genere le jwt
		.signWith(getKey(), SignatureAlgorithm.HS256)
		.compact();
		
		
		return Map.of(BEARER, bearer);
	}

	private Key getKey() {
		//definir la cle de chiffrement
		final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
		
		//generer la clé
		return Keys.hmacShaKeyFor(decoder);
	}
	
	/******************* fin generation du token *************************/

		
	
	/******************* filtrer le token *************************/

	//retourner le username en fonction du token. username=email
	public String extractUsername(String token) {
		
		return this.getClaim(token, Claims::getSubject);
	}

	//Verifier si le token a expirer au pas
	public boolean isTokenExpired(String token) {
		Date expirationDate = getExpirationDateFromToken(token);
		return expirationDate.before(new Date());
	}

	//obtenire la date dexpiration du token
	private Date getExpirationDateFromToken(String token) {
		return this.getClaim(token, Claims::getExpiration);
	}

	//methode qui retourne tous les Claims
	private <T> T getClaim(String token, Function<Claims, T> function) {//Function<Claims, T> en fonction de tous les claims, on veux extraire quelque chose
		
		Claims claims = getAllClaims(token);
		return function.apply(claims);
	}
	
	private Claims getAllClaims(String token) {
		return Jwts.parser()
					.setSigningKey(this.getKey())
					.build()
					.parseClaimsJws(token)
					.getBody();
					
	}

	/*******************fin du filtre le token *************************/
	
	/************************** verification du token *******************/
	//verifier si le token existe dans la base de données
		public Jwt tokenByValue(String valeur) {
			
			return this.jwtRepository.findByValeurAndDesactiverAndExpire(
					valeur,
					false,
					false).orElseThrow(() -> new RuntimeException("Token incorrecte"));
			
			
		}
	
	/***************************fin de veriication du token *******************/
		
		/***************************deconnexion *******************/
		
		public void deconnexion() {
		Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		Jwt jwt = this.jwtRepository.findByUtilisateurEmailAndDesactiveAndExprire(
				utilisateur.getEmail(),
				false,
				false).orElseThrow(() -> new RuntimeException("Token invalide"));
		jwt.setExpire(true);
		jwt.setDesactiver(true);
		this.jwtRepository.save(jwt);
		}
	


}
