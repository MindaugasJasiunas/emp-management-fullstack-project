package com.example.demo.repository;

import com.example.demo.domain.PasswordReset;
import com.example.demo.domain.PasswordResetPK;
import com.example.demo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, PasswordResetPK> {
    Optional<PasswordReset> findByEmail(String email);
    Optional<PasswordReset> findByLink(String link);
    boolean existsPasswordResetByLink(String link);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    void deletePasswordResetByEmail(String email);
}
