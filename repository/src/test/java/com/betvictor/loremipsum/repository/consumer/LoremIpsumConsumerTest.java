package com.betvictor.loremipsum.repository.consumer;

import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.repository.TestcontainersConfiguration;
import com.betvictor.loremipsum.repository.repository.ParagraphAnalyticsRepository;
import com.betvictor.loremipsum.repository.util.KafkaTestProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class LoremIpsumConsumerTest {
    @Autowired
    private KafkaTestProducer kafkaTestProducer;
    @Autowired
    private ParagraphAnalyticsRepository paragraphAnalyticsRepository;

    CountDownLatch latch = new CountDownLatch(1);

    @AfterEach
    void cleanup() {
        paragraphAnalyticsRepository.deleteAll();
    }

    @Test
    void whenKafkaMessageIsProduced_shouldSaveMessageToTheDatabase() throws ExecutionException, InterruptedException, TimeoutException {
        assertTrue(paragraphAnalyticsRepository.findAll().isEmpty());

        kafkaTestProducer.send("words.processed", new LoremIpsumResponse(
                "test",
                10,
                100,
                1000));

        latch.await(5, TimeUnit.SECONDS);
        assertEquals(1, paragraphAnalyticsRepository.findAll().size());
    }
}