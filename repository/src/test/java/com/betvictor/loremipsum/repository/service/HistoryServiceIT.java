package com.betvictor.loremipsum.repository.service;

import com.betvictor.loremipsum.repository.TestcontainersConfiguration;
import com.betvictor.loremipsum.repository.document.ParagraphAnalyticsDocument;
import com.betvictor.loremipsum.repository.repository.ParagraphAnalyticsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class HistoryServiceIT {
    @Autowired
    private ParagraphAnalyticsRepository paragraphAnalyticsRepository;

    private HistoryService historyService;

    @BeforeEach
    void setup() {
        paragraphAnalyticsRepository.saveAll(initMockDocuments());
        historyService = new HistoryService(paragraphAnalyticsRepository);
    }

    @AfterEach
    void cleanup() {
        paragraphAnalyticsRepository.deleteAll();
    }

    @Test
    void getLastTenItems_shouldReturnTenDocumentsOrderedByCreatedAtDesc() {
        ResponseEntity<Object> request = historyService.getLastTenItems();
        assertEquals(HttpStatus.OK, request.getStatusCode());
        assertNotNull(request.getBody());

        List<ParagraphAnalyticsDocument> result = (List<ParagraphAnalyticsDocument>) request.getBody();
        assertEquals(10, result.size());

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i)
                    .getCreatedAt()
                    .isAfter(result.get(i + 1)
                            .getCreatedAt()));
        }

    }

    private List<ParagraphAnalyticsDocument> initMockDocuments() {
        List<ParagraphAnalyticsDocument> documents = new LinkedList<>();

        for (int i = 0; i < 15; i++) {
            ParagraphAnalyticsDocument document = new ParagraphAnalyticsDocument(
                    "test",
                    10,
                    100,
                    1000,
                    Instant.now().plusSeconds(i)
            );
            documents.add(document);
        }

        return documents;
    }

}