package com.betvictor.loremipsum.repository.service;

import com.betvictor.loremipsum.repository.document.ParagraphAnalyticsDocument;
import com.betvictor.loremipsum.repository.repository.ParagraphAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryService {
    private final ParagraphAnalyticsRepository paragraphAnalyticsRepository;

    public ResponseEntity<Object> getLastTenItems() {
        List<ParagraphAnalyticsDocument> result = paragraphAnalyticsRepository.findTop10ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(result);
    }
}
