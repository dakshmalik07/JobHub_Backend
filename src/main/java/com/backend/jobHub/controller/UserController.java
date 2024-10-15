package com.backend.jobHub.controller;

import com.backend.jobHub.service.EmailService;
import com.backend.jobHub.service.LinkedInService;
import com.backend.jobHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final LinkedInService scraperService;
    private final EmailService emailService;

    @PostMapping("/register")
    public String registerUser(@RequestParam String email) {
        return userService.registerUser(email);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        return userService.verifyEmail(token);
    }

    @PostMapping("/scrape")
    public String scrapeLinkedIn(@RequestParam String keywords, @RequestParam String email) {
        return scraperService.scrapeLinkedIn(keywords, email);
    }

    @GetMapping ("/getEmails")
    public List<String> scrapedEmails(@RequestParam String email){
        return scraperService.scrapedEmails(email);
    }

    @PostMapping("/email-campaign")
    public String createEmailCampaign(
            @RequestParam String senderEmail,
            @RequestParam String sendPassword,
            @RequestParam List<String> recipients,
            @RequestParam String subject,
            @RequestParam String bodyContent) {
        return emailService.createCampaign(senderEmail, sendPassword, recipients, subject, bodyContent);
    }

}
