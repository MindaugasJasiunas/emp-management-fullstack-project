package com.example.demo.email;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

@AllArgsConstructor

@ConstructorBinding
@ConfigurationProperties(prefix = "mail")
public class EmailConstant {
    public final String USERNAME;
    public final String PASSWORD;
    public final String SIMPLE_MAIL_TRANSFER_PROTOCOL;
    public final String FROM_EMAIL;
    public final String CC_EMAIL;
    public final String MAIL_SUBJECT;
    public final String GMAIL_SMTP_SERVER;
    public final String SMTP_HOST;
    public final String SMTP_AUTH;
    public final String SMTP_PORT;
    public final int DEFAULT_PORT;
    public final String SMTP_STARTTLS_ENABLE;
    public final String SMTP_STARTTLS_REQUIRED;
}
