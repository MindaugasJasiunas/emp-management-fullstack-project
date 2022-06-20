package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> getUsers();
    User createUser(User user) throws Exception;
    User getUserByUsername(String username) throws UserNotFoundException;
    String generateTokenForUser(String username);
    User getUserByPublicId(String publicId) throws UserNotFoundException;
    User updateUser(User user, String publicId) throws Exception;
    void deleteUser(String publicId) throws UserNotFoundException;
    void validateUser(String username, String password) throws Exception;
    User resetPassword(String email);
    User getUserByEmail(String email);
    void updateProfilePicture(String email, MultipartFile profileImage) throws IOException;
    byte[] getUserProfileImage(String username, String fileName) throws IOException;

    void updatePassword(String emailByLink, String newPassword);
}
