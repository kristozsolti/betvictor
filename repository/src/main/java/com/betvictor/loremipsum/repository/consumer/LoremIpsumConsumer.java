package com.betvictor.loremipsum.repository.consumer;

import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.repository.document.ParagraphAnalyticsDocument;
import com.betvictor.loremipsum.repository.repository.ParagraphAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoremIpsumConsumer {
    public static final String WORDS_PROCESSED_TOPIC = "words.processed";
    private final ParagraphAnalyticsRepository paragraphAnalyticsRepository;

    @KafkaListener(id = "processedWordsListener",
            topics = WORDS_PROCESSED_TOPIC,
            concurrency = "${spring.kafka.listener.concurrency}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void processMessage(@Payload LoremIpsumResponse loremIpsumResponse,
                               @Header(value = KafkaHeaders.RECEIVED_PARTITION) int partition,
                               @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                               @Header(KafkaHeaders.OFFSET) int offset) {

        log.info("Received message: {} with partitionId: {}, with key: {}, with offset: {}",
                loremIpsumResponse,
                partition,
                key,
                offset);

        try {
            ParagraphAnalyticsDocument paragraphAnalyticsDocument = new ParagraphAnalyticsDocument(
                    loremIpsumResponse.freq_word(),
                    loremIpsumResponse.avg_paragraph_size(),
                    loremIpsumResponse.avg_paragraph_processing_time(),
                    loremIpsumResponse.total_processing_time(),
                    Instant.now()
            );

            paragraphAnalyticsRepository.save(paragraphAnalyticsDocument);
        } catch (Exception e) {
            log.error("Something went wrong while persisting kafka message: {}", loremIpsumResponse, e);
        }
    }
}
