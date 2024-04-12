package com.magir.gestionacces.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.magir.gestionacces.entity.Acces;
import com.magir.gestionacces.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService{
	
	@Autowired
	JavaMailSender javaMailSender;

	@Override
	public void envoyerNotification(Acces acces) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("epiphanieatta8@gmail.com");
		message.setTo(acces.getUtilisateur().getEmail());
		message.setSubject("Votre code d'activation");
		
		String text = String.format("Bonjour %s; Votre code d'activation est %s . A bientôt;", acces.getUtilisateur().getNom(), acces.getCode());
		
		message.setText(text);
		
		javaMailSender.send(message);
		
	}

}
