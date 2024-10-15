package com.backend.jobHub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "email_campaigns")
public class EmailCampaign {
    @Id
    private String campaignId;
    private String senderEmail;
    private List<String> recipients;
    private String subject;
    private String bodyContent;
    private String createdAt;
}
