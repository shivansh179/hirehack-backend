package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.ChatMessageDto;
import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.dto.InterviewResponseDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<InterviewResponseDto> startInterview(@RequestBody InterviewRequestDto requestDto) {
        InterviewResponseDto response = interviewService.startNewInterview(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/chat")
    public ResponseEntity<Map<String, String>> postChatMessage(
            @PathVariable Long interviewId,
            @RequestBody ChatMessageDto chatMessageDto) {
        String aiReply = interviewService.handleUserResponse(interviewId, chatMessageDto.getMessage());
        // We return a simple JSON object like {"reply": "This is the AI's response."}
        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    @PostMapping("/{interviewId}/generate-feedback")
    public ResponseEntity<Map<String, String>> generateFeedback(@PathVariable Long interviewId) {
        String feedback = interviewService.generateFeedback(interviewId);
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    @GetMapping("/history/{phoneNumber}")
    public ResponseEntity<List<Interview>> getInterviewHistory(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(interviewService.getInterviewHistoryForUser(phoneNumber));
    }
}