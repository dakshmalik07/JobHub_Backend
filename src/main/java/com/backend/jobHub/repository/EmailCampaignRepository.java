package com.backend.jobHub.repository;


import com.backend.jobHub.entity.EmailCampaign;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCampaignRepository extends MongoRepository<EmailCampaign, String> {
}
