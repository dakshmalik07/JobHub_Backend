package com.backend.jobHub.repository;


import com.backend.jobHub.entity.ScrapedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapedDataRepository extends MongoRepository<ScrapedData, String> {
    List<ScrapedData> findByUserEmailOrderByDateOfSearchDesc(String email);
}
