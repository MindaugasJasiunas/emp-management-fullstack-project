package com.example.demo.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

import jakarta.mail.Session;

@Service
public class EmailService {
    private final EmailConstant emailConstant;

    public EmailService(EmailConstant emailConstant) {
        this.emailConstant = emailConstant;
    }

    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(emailConstant.SMTP_HOST, emailConstant.GMAIL_SMTP_SERVER);
        properties.put(emailConstant.SMTP_AUTH, true);
        properties.put(emailConstant.SMTP_PORT, emailConstant.DEFAULT_PORT);
        properties.put(emailConstant.SMTP_STARTTLS_ENABLE, true);
//        properties.put(emailConstant.SMTP_STARTTLS_REQUIRED, true); // comment out to work with MailDev
        return Session.getInstance(properties, null);
    }

    private Message createRegistrationEmail(String firstName, String password, String email) throws MessagingException {
        Message msg = new MimeMessage(getEmailSession());
        msg.setFrom(new InternetAddress(emailConstant.FROM_EMAIL));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));  // strict - email certain format
//        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailConstant.CC_EMAIL, false));
        msg.setSubject(emailConstant.MAIL_SUBJECT);
        msg.setText("Hello "+firstName+" \n \n Your new account password is "+password+" \n \n The Support Team");
        msg.setSentDate(new Date());
        msg.saveChanges();
        return msg;
    }

    private Message createResetPasswordEmail(String firstName, String link, String email) throws MessagingException {
        Message msg = new MimeMessage(getEmailSession());
        msg.setFrom(new InternetAddress(emailConstant.FROM_EMAIL));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));  // strict - email certain format
        msg.setSubject(emailConstant.MAIL_SUBJECT);
        msg.setText("Hello "+firstName+", \n \n You can update your account password by visiting "+link+" \n \n The Support Team");
        msg.setSentDate(new Date());
        msg.saveChanges();
        return msg;
    }

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message msg = createRegistrationEmail(firstName, password, email);
        Transport.send(msg, emailConstant.USERNAME, emailConstant.PASSWORD);
    }

    public void sendResetPasswordEmail(String firstName, String link, String email) throws MessagingException {
        Message msg = createResetPasswordEmail(firstName, link, email);
        Transport.send(msg, emailConstant.USERNAME, emailConstant.PASSWORD);
    }
}
