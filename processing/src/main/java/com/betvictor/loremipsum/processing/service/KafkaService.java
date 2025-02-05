package com.betvictor.loremipsum.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {
    private static final String SUCCESS_MESSAGE = "Kafka message successfully sent to topic: {}, with key: {}, data: {}";
    private static final String ERROR_MESSAGE = "Something went wrong while sending message to kafka topic: {}, data: {}";

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendMessageToTopicWithKeySync(final Object message, final String topic, final String key) {
        try {
            kafkaTemplate.send(topic, key, message).get(10, TimeUnit.SECONDS);
            handleSuccess(topic, message, key);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            handleFailure(topic, message, e);
        }
    }

    private static void handleSuccess(String topic, Object message, String key) {
        log.info(SUCCESS_MESSAGE, topic, key, message);
    }

    private static void handleFailure(String topic, Object message, Exception ex) {
        log.error(ERROR_MESSAGE, topic, message, ex);
    }
}
