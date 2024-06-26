package com.betvictor.loremipsum.processing.controller;

import com.betvictor.loremipsum.model.LoremIpsumResponse;
import com.betvictor.loremipsum.processing.service.LoremIpsumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/betvictor")
public class LoremIpsumController {
    private final LoremIpsumService loremIpsumService;

    @GetMapping(path = "/text")
    public ResponseEntity<LoremIpsumResponse> generateDummyText(@RequestParam("p") int numOfParagraphs,
                                                                @RequestParam("l") String paragraphLength) {
        return loremIpsumService.getDummyTextAnalytics(numOfParagraphs, paragraphLength);
    }

}
