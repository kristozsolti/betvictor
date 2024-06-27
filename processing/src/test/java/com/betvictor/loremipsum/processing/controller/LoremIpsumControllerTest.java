package com.betvictor.loremipsum.processing.controller;

import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.processing.service.LoremIpsumService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoremIpsumController.class)
class LoremIpsumControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoremIpsumService loremIpsumService;

    @Test
    public void getRequestWithValidParamsToGenerateTextAnalytics_shouldReturnOk() throws Exception {
        int numOfParagraphs = 2;
        String paragraphLength = "short";

        // Mock the LoremIpsumService behavior
        LoremIpsumResponse expectedResponse = new LoremIpsumResponse("word", 10, 100, 1000);
        Mockito.when(loremIpsumService.generateTextAnalytics(numOfParagraphs, paragraphLength))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // Perform the GET request with parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/betvictor/text")
                        .param("p", String.valueOf(numOfParagraphs))
                        .param("l", paragraphLength))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    @Test
    public void getRequestWithValidParamsToGenerateTextAnalytics_shouldReturnError() throws Exception {
        String errorMessage = "Error";
        int numOfParagraphs = 2;
        String paragraphLength = "short";

        // Mock the LoremIpsumService behavior
        LoremIpsumResponse expectedResponse = new LoremIpsumResponse("word", 10, 100, 1000);
        Mockito.when(loremIpsumService.generateTextAnalytics(numOfParagraphs, paragraphLength))
                .thenReturn(ResponseEntity.badRequest().body(errorMessage));

        // Perform the GET request with parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/betvictor/text")
                        .param("p", String.valueOf(numOfParagraphs))
                        .param("l", paragraphLength))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    private static String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}