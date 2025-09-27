package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.GeminiApiDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.interfaces.AiInterviewServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService implements AiInterviewServiceInterface {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateInitialQuestion(User user, String role, String skills, String interviewType) {
        String resumeContext = user.getResumeText();
        if (resumeContext == null || resumeContext.isBlank()) {
            resumeContext = "The candidate has not provided a resume. You will have to base your questions solely on the role and skills provided.";
        }

        String prompt = String.format(
            """
            You are an expert AI interviewer named 'Alex'. You are friendly, professional, and engaging. You are about to start an interview with a candidate for a '%s' position, focusing on '%s' skills in a '%s' format.
            Your first task is to greet the candidate warmly, introduce yourself, and then ask your first question.
            If the candidate has provided a resume, your first question MUST be based on a specific project or experience from their resume. If not, create a strong, relevant opening question about their experience.
            
            Example with resume: "Hi there, I'm Alex. It's great to connect with you today. I was looking over your resume and was really interested in the 'Sales Analytics Dashboard' project. Could you start by walking me through your specific role and contributions to that project?"
            Example without resume: "Hi, I'm Alex, and I'll be conducting your interview today. It's great to meet you. To start, could you tell me about a challenging project you've worked on that showcases your skills in %s?"
            
            --- CANDIDATE'S RESUME CONTEXT ---
            %s
            --- END OF RESUME CONTEXT ---
            
            Now, generate your friendly introduction and the first question.
            """,
            role, skills, interviewType, skills, resumeContext
        );
        return callGeminiApi(prompt);
    }

    public String generateNextQuestion(String role, String skills, String interviewType, String resumeText, String chatHistory) {
        String resumeContext = resumeText;
        if (resumeContext == null || resumeContext.isBlank()) {
            resumeContext = "No resume provided.";
        }

        String prompt = String.format(
            """
            You are 'Alex', an expert AI interviewer. You are in the middle of a professional and engaging interview for a '%s' role. You are known for being adaptive and conversational.
            Your task is to analyze the recent chat history and generate the next response.
            
            **YOUR BEHAVIORAL RULES:**
            1.  **Stay in Character**: Always be professional, encouraging, and curious.
            2.  **Ask Follow-Up Questions**: If a candidate gives a good answer, ask a short follow-up question to probe deeper. For example, "That sounds impressive. What was the most challenging part of that?" or "Could you elaborate on the technology stack you used for that?"
            3.  **Handle Inappropriate/Irrelevant Input**: If the user says something vulgar, profane, or provides a name/term that is clearly not relevant to a tech interview (e.g., a celebrity name), do not get angry or confused. Calmly and professionally redirect the conversation. Do not treat it as a valid point.
                -   *Correct Response to Irrelevant Name*: "I'm not familiar with that in this context. Let's get back to the project we were discussing. Could you tell me more about...?"
                -   *Correct Response to Profanity*: "I understand interviews can be intense, but let's maintain a professional conversation. Now, regarding the failover logic, you mentioned..."
            4.  **Acknowledge and Transition**: Briefly acknowledge their last answer before smoothly transitioning to the next logical question based on the role, skills, or their resume.
            5.  **Be Concise**: Your response should be ONLY your spoken part. Do not add labels like "Alex:" or "(acknowledgement)".
            
            --- CANDIDATE'S RESUME CONTEXT ---
            %s
            --- END OF RESUME CONTEXT ---
            
            --- RECENT CHAT HISTORY (Most recent message is last) ---
            %s
            --- END OF CHAT HISTORY ---
            
            Based on the last message from the USER, generate your next response now.
            """,
            role, resumeContext, chatHistory
        );

        return callGeminiApi(prompt);
    }

    public String generateFeedback(String chatHistory, String resumeText, String role, String skills) {
        String prompt = String.format(
                """
                You are a Senior Hiring Manager providing constructive feedback in Markdown format to a candidate after an interview for a '%s' role with a focus on '%s'. You have the full interview transcript and the candidate's resume.
                
                **TASK:**
                Provide structured, professional, and actionable feedback. Address the candidate's strengths and weaknesses directly, referencing specific examples from the interview. If the candidate used unprofessional language or went off-topic, mention it constructively under "Areas for Improvement" as a point about professional communication.

                **FORMAT:**
                # Interview Feedback
                
                ## Overall Summary
                A brief, one-paragraph summary of the candidate's performance.
                
                **Strengths**
                *   (Point 1: What did they do well? Reference a specific answer.)
                *   (Point 2: Highlight another positive aspect.)
                
                **Areas for Improvement**
                *   (Point 1: What could be improved? Be specific and suggest an alternative.)
                *   (Point 2: If applicable, comment on professional communication or clarity.)
                
                **Alignment with Role**
                A short paragraph on how their experience aligns with the target role and the required skills.
                
                --- CANDIDATE'S RESUME ---
                %s
                --- END OF RESUME ---
                
                --- INTERVIEW TRANSCRIPT ---
                %s
                --- END OF TRANSCRIPT ---
                
                Generate the complete feedback now.
                """,
                role, skills, resumeText != null ? resumeText : "No resume was provided.", chatHistory
        );
        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        GeminiApiDto.Part part = new GeminiApiDto.Part(prompt);
        GeminiApiDto.Content content = new GeminiApiDto.Content(List.of(part));
        GeminiApiDto.GeminiRequest requestBody = new GeminiApiDto.GeminiRequest(List.of(content));

        HttpEntity<GeminiApiDto.GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiApiDto.GeminiResponse response = restTemplate.postForObject(apiUrl, entity, GeminiApiDto.GeminiResponse.class);
            if (response != null && response.extractText() != null) {
                return response.extractText();
            }
            return "I'm sorry, I seem to be having a technical issue. Could you please repeat your last answer?";
        } catch (Exception e) {
            e.printStackTrace();
            return "My apologies, I've encountered a connection error. Let's try that again. What were you saying?";
        }
    }
}