package com.backend.jobHub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "scraped_data")
public class ScrapedData {
    @Id
    private String scrapeId;
    private String postUrl;
    private List<String> extractedEmails;
    private String userEmail;
    private String keywords;
    private String dateOfSearch;
}
