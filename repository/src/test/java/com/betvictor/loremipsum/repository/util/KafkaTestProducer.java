package com.betvictor.loremipsum.repository.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaTestProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object payload) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(topic, payload).get(10, TimeUnit.SECONDS);
    }
}
