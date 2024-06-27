package com.betvictor.loremipsum.repository.controller;

import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.repository.service.HistoryService;
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

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryService historyService;

    @Test
    public void getRequest_shouldReturnOk() throws Exception {
        List<LoremIpsumResponse> expectedResponse = List.of(
                new LoremIpsumResponse("word", 10, 100, 1000));
        Mockito.when(historyService.getLastTenItems())
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // Perform the GET request with parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/betvictor/history"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    private static String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}