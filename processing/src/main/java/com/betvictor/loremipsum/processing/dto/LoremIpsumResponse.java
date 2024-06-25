package com.betvictor.loremipsum.processing.dto;

public record LoremIpsumResponse(
        String freq_word,
        int avg_paragraph_size,
        long avg_paragraph_processing_time,
        long total_processing_time
) {}
