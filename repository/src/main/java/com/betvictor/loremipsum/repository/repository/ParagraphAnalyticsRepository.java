package com.betvictor.loremipsum.repository.repository;

import com.betvictor.loremipsum.repository.document.ParagraphAnalyticsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParagraphAnalyticsRepository extends MongoRepository<ParagraphAnalyticsDocument, String> {
    List<ParagraphAnalyticsDocument> findTop10ByOrderByCreatedAtDesc();
}
