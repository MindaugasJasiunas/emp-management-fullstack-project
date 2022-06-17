package com.example.demo.service;

import com.example.demo.domain.PasswordReset;
import com.example.demo.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Random;

@Service
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;
    @Value("${base_reset_url}")
    String baseUrl;

    public PasswordResetService(PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    public String generatePasswordResetLink(String email){
        PasswordReset pr = new PasswordReset();
        pr.setEmail(email);
        String generatedLink = generateLink(email);
        pr.setLink(generatedLink);
        passwordResetRepository.save(pr);
        return baseUrl+generatedLink;
    }

    private String generateLink(String email){
        // generate random string
        String randomString = generateRandomString(45);
        // check if not exists
        if(passwordResetRepository.existsPasswordResetByLink(randomString)){
            generateLink(email); // generate new
        }
        return randomString;
    }
    private String generateRandomString(int length){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public String getEmailByLink(String link){
        System.out.println(passwordResetRepository.findByLink(link));
        if(passwordResetRepository.findByLink(link).isPresent()){
            return passwordResetRepository.findByLink(link).get().getEmail();
        }
        return null;
    }

    @Transactional
    public void deleteDBEntryByEmail(String email) {
        passwordResetRepository.deletePasswordResetByEmail(email);
    }
}
