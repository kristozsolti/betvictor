package com.betvictor.loremipsum.repository.controller;

import com.betvictor.loremipsum.repository.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/betvictor")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/history")
    public ResponseEntity<Object> getLastTenItems() {
        return historyService.getLastTenItems();
    }

}
