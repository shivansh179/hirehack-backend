package com.hirehack.hirehack.dto;

import java.util.List;
// Using Java Records for concise DTOs
public class GeminiApiDto {
    public record Part(String text) {}
    public record Content(List<Part> parts) {}
    public record GeminiRequest(List<Content> contents) {}

    // --- Response Body Classes ---
    public record Candidate(Content content) {}
    public record GeminiResponse(List<Candidate> candidates) {
        // Helper method to easily extract the first response text
        public String extractText() {
            if (this.candidates != null && !this.candidates.isEmpty()) {
                Candidate firstCandidate = this.candidates.get(0);
                if (firstCandidate.content() != null && !firstCandidate.content().parts().isEmpty()) {
                    return firstCandidate.content().parts().get(0).text();
                }
            }
            return "No response text found.";
        }
    }
}