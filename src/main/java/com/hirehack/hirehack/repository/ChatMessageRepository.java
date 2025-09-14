package com.hirehack.hirehack.repository;

import com.hirehack.hirehack.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByInterviewIdOrderByCreatedAtAsc(Long interviewId);
}