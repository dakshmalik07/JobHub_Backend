package com.backend.jobHub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String userId;
    private String email;
    private boolean emailVerified;
    private String createdAt;
    private String verificationToken;
}
