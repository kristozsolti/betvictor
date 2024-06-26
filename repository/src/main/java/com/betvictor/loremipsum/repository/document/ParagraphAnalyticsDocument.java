package com.betvictor.loremipsum.repository.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Document(collection = "paragraphAnalytics")
public class ParagraphAnalyticsDocument {
    private String id;
    private String freq_word;
    private int avg_paragraph_size;
    private long avg_paragraph_processing_time;
    private long total_processing_time;
    private Instant createdAt;

    public ParagraphAnalyticsDocument(String freq_word, int avg_paragraph_size, long avg_paragraph_processing_time, long total_processing_time) {
        this.id = UUID.randomUUID().toString();
        this.freq_word = freq_word;
        this.avg_paragraph_size = avg_paragraph_size;
        this.avg_paragraph_processing_time = avg_paragraph_processing_time;
        this.total_processing_time = total_processing_time;
        this.createdAt = Instant.now();
    }
}
