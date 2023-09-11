package com.burakdelice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailServiceApplication.class);
    }

    //deneme amaçlı
/*
    @Autowired
    private JavaMailSender javaMailSender;

    @EventListener(ApplicationReadyEvent.class)
    public void sendEmail(){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("${java10_mail_username}");
        mailMessage.setTo("sburak.delice@gmail.com");
        mailMessage.setSubject("AKTIVASYON KODUNUZ.... ");
        mailMessage.setText("lkD345");
        javaMailSender.send(mailMessage);
    }*/
}