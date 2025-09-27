package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.ChatMessageDto;
import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.dto.InterviewResponseDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<InterviewResponseDto> startInterview(@RequestBody InterviewRequestDto requestDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        InterviewResponseDto response = interviewService.startNewInterview(requestDto, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/chat")
    public ResponseEntity<Map<String, String>> postChatMessage(
            @PathVariable Long interviewId,
            @RequestBody ChatMessageDto chatMessageDto,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        String aiReply = interviewService.handleUserResponse(interviewId, chatMessageDto.getMessage(), currentUser.getId());
        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    @PostMapping("/{interviewId}/generate-feedback")
    public ResponseEntity<Map<String, String>> generateFeedback(@PathVariable Long interviewId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        String feedback = interviewService.generateFeedback(interviewId, currentUser.getId());
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Interview>> getInterviewHistory(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(interviewService.getInterviewHistoryForUser(currentUser.getId()));
    }
}