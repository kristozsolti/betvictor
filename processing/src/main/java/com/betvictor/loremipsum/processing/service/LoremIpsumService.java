package com.betvictor.loremipsum.processing.service;

import com.betvictor.loremipsum.processing.config.KafkaTopicConfig;
import com.betvictor.loremipsum.processing.dto.LoremIpsumResponse;
import com.betvictor.loremipsum.processing.exception.ApiCallException;
import com.betvictor.loremipsum.processing.exception.InvalidParameterException;
import com.betvictor.loremipsum.processing.exception.MostFrequentWordException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoremIpsumService {

    public static final List<String> PARAGRAPH_LENGTHS = Arrays.asList(
            "short",
            "medium",
            "long",
            "verylong"
    );

    private final RestClient restClient;
    private final KafkaService kafkaService;
    private final List<Long> apiRequestProcessingTimes = new ArrayList<>();

    public LoremIpsumService(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
        this.restClient = RestClient.builder()
                    .baseUrl("https://loripsum.net/api")
                    .build();
    }

    public ResponseEntity<LoremIpsumResponse> getDummyTextAnalytics(int numOfParagraphs, String paragraphLength) {
        throwInvalidParameterExceptionIfParamsAreInvalid(numOfParagraphs, paragraphLength);

        long totalProcessingTimeStart = System.currentTimeMillis();
        // Make numOfParagraphs requests to the Lorem Ipsum API
        List<String> paragraphs = makeLoremIpsumRequests(numOfParagraphs, paragraphLength);

        String mostFrequentWord = computeMostFrequentWord(paragraphs);

        int avgParagraphSize = computeAverageParagraphSize(paragraphs);

        long avgParagraphProcessingTime = apiRequestProcessingTimes.stream().mapToLong(Long::longValue).sum() / apiRequestProcessingTimes.size();

        long totalProcessingTimeEnd = System.currentTimeMillis();
        long totalProcessingTime = totalProcessingTimeEnd - totalProcessingTimeStart;

        LoremIpsumResponse response = new LoremIpsumResponse(mostFrequentWord,
                avgParagraphSize,
                avgParagraphProcessingTime,
                totalProcessingTime);

        kafkaService.sendMessageToTopicWithKeySync(response, KafkaTopicConfig.WORDS_PROCESSED_TOPIC, mostFrequentWord);

        return ResponseEntity.ok(response);
    }

    private void throwInvalidParameterExceptionIfParamsAreInvalid(int numOfParagraphs, String paragraphLength) {
        if (numOfParagraphs < 1) {
            throw new InvalidParameterException("Invalid parameter p: The value must be greater than 0.");
        }

        if (!PARAGRAPH_LENGTHS.contains(paragraphLength)) {
            throw new InvalidParameterException(
                    String.format("Invalid parameter l: It must be one of the following strings: %s.",
                            PARAGRAPH_LENGTHS));
        }

    }

    private List<String> makeLoremIpsumRequests(int numOfParagraphs, String paragraphLength) {
        List<String> paragraphs = new ArrayList<>();

        for (int i = 0; i < numOfParagraphs; i++) {
            try {
                long requestStart = System.currentTimeMillis();
                String response = restClient.get()
                        .uri("/{numOfParagraphs}/{paragraphLength}", numOfParagraphs, paragraphLength)
                        .retrieve()
                        .body(String.class);

                long requestEnd = System.currentTimeMillis();

                assert response != null;

                String cleanedParagraph = response.replaceAll("<p>", "").replaceAll("</p>", "");
                paragraphs.add(cleanedParagraph);
                apiRequestProcessingTimes.add(requestEnd - requestStart);
            } catch (Exception e) {
                throw new ApiCallException(e.getMessage());
            }
        }

        return paragraphs;
    }

    private String computeMostFrequentWord(List<String> paragraphs) {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        paragraphs.forEach(paragraph -> {
            String[] words = paragraph.split("\\W+"); // Split by word
            for (String word : words) {
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        });

        Map<String, Integer> sortedWordFrequencies = wordFrequencies.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        Map.Entry<String, Integer> mostFrequentWordEntry = sortedWordFrequencies.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new MostFrequentWordException("Most frequent word was not found."));

        return mostFrequentWordEntry.getKey();
    }

    private int computeAverageParagraphSize(List<String> paragraphs) {
        int totalWordCount = 0;
        for(String paragraph: paragraphs) {
            String[] words = paragraph.split("\\W+"); // Split by word
            totalWordCount += words.length;
        }

        return totalWordCount / paragraphs.size();
    }

}
