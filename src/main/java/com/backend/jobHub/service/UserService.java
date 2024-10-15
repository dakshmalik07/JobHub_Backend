package com.backend.jobHub.service;

import com.backend.jobHub.entity.User;
import com.backend.jobHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String registerUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            String token = generateToken();

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setEmailVerified(false);
            newUser.setCreatedAt(LocalDateTime.now().toString());
            newUser.setVerificationToken(token);

            emailService.sendVerificationEmail(email, token);
            userRepository.save(newUser);
            return "Verification email sent!";
        }
        return "Email already verified!";
    }

    public String verifyEmail(String token) {
        Optional<User> user = userRepository.findByVerificationToken(token);

        if (user.isPresent()) {
            User verifiedUser = user.get();
            verifiedUser.setEmailVerified(true);
//            verifiedUser.setVerificationToken(null);
            userRepository.save(verifiedUser);
            return "Email verified successfully!";
        }
        return "Invalid or expired token.";
    }
}
