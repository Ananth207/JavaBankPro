package com.ananth.BankLoginSystem;
import java.util.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Random;

@SpringBootApplication
public class BankLoginSystemApplication {
	@Autowired

	public EmailSenderService senderService;
	Scanner sc = new Scanner(System.in);
	//	public int otp_created = senderService.generateOTP(6);
	Random random=new Random();

	public static int otp_created;
	public void generateOTP()
	{
		otp_created = random.nextInt(100000,999999);
	}

	public void send_email() {

		SpringApplication.run(BankLoginSystemApplication.class);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void triggerMail() throws MessagingException{

		generateOTP();
		System.out.println("Enter your email again for verification: ");
		String em = sc.next();
		senderService.sendEmail(em,"OTP Verification","Your Registration OTP is: ",otp_created);
	}

}
