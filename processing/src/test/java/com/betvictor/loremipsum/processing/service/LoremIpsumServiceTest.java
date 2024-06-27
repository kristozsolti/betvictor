package com.betvictor.loremipsum.processing.service;


import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.processing.config.KafkaTopicConfig;
import com.betvictor.loremipsum.processing.exception.ApiCallException;
import com.betvictor.loremipsum.processing.exception.InvalidParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static com.betvictor.loremipsum.processing.service.LoremIpsumService.PARAGRAPH_LENGTHS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoremIpsumServiceTest {
    @Mock
    private RestClient restClient;
    @Mock
    private KafkaService kafkaService;
    @InjectMocks
    private LoremIpsumService loremIpsumService;

    @Test
    void generateTextAnalytics_whenParamPIsLessThanOne_shouldThrowInvalidParameterException() {
        RuntimeException exception = assertThrows(InvalidParameterException.class,
                () -> loremIpsumService.generateTextAnalytics(0, "short"));
        assertEquals("Invalid parameter p: The value must be greater than 0.", exception.getMessage());
    }

    @Test
    void generateTextAnalytics_whenParamLIsInvalid_shouldThrowInvalidParameterException() {
        RuntimeException exception = assertThrows(InvalidParameterException.class,
                () -> loremIpsumService.generateTextAnalytics(3, "invalidLengthParam"));
        assertEquals(
                String.format("Invalid parameter l: It must be one of the following strings: %s.", PARAGRAPH_LENGTHS),
                exception.getMessage());
    }

    @Test
    void generateTextAnalytics_whenErrorHappensDuringApiCall_shouldThrowApiCallException() {
        String errorMessage = "Api call error.";

        when(restClient.get())
                .thenThrow(new RuntimeException(errorMessage));

        RuntimeException exception = assertThrows(ApiCallException.class,
                () -> loremIpsumService.generateTextAnalytics(3, "short"));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void generateTextAnalytics_givenExpectedApiResponse_mostFrequentWordShouldBeTestAndAvgParagraphSizeShouldBeFour() {
        int numOfParagraphs = 3;
        String paragraphLength = "short";

        String expectedMostFrequentWord = "test";
        int expectedAvgParagraphSize = 4;

        String expectedApiResponse1 = "<p>Test one, two, three.</p>";
        String expectedApiResponse2 = "<p>Test eins, zwei, drei, vier.</p>";
        String expectedApiResponse3 = """
                <p>Test uno, dos, tres.</p>
                
                <p>Test one, two, three, four, five.</p>""";

        // Mock restClient specs
        RestClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/{numOfParagraphs}/{paragraphLength}", numOfParagraphs, paragraphLength))
                .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        // Mock restClient response
        when(responseSpecMock.body(String.class))
                .thenReturn(expectedApiResponse1)
                .thenReturn(expectedApiResponse2)
                .thenReturn(expectedApiResponse3);

        ResponseEntity<Object> request = loremIpsumService.generateTextAnalytics(numOfParagraphs, paragraphLength);
        assertEquals(HttpStatus.OK, request.getStatusCode());
        assertNotNull(request.getBody());
        LoremIpsumResponse requestBody = (LoremIpsumResponse) request.getBody();

        assertEquals(expectedMostFrequentWord, requestBody.freq_word());
        assertEquals(expectedAvgParagraphSize, requestBody.avg_paragraph_size());
        verify(kafkaService).sendMessageToTopicWithKeySync(requestBody, KafkaTopicConfig.WORDS_PROCESSED_TOPIC, expectedMostFrequentWord);
    }

}