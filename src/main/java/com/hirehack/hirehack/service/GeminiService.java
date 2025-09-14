package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.GeminiApiDto;
import com.hirehack.hirehack.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically injects RestTemplate via constructor
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateInitialQuestion(User user, String role, String skills, String interviewType) {
        // --- FIX IS HERE ---
        String resumeContext = user.getResumeText();
        if (resumeContext == null || resumeContext.isBlank()) {
            resumeContext = "The candidate has not provided a resume. Base your questions solely on the role and skills provided.";
        }
        // --- END OF FIX ---

        String prompt = String.format(
                "You are an expert AI interviewer for a top tech company. You are about to interview a candidate for a '%s' position. " +
                        "The candidate has mentioned their key skills are: '%s'. The interview format is '%s'. " +
                        "You have access to their resume context, which is provided below. " +
                        "Your task is to formulate a single, powerful, open-ended introductory question. If there is a resume, make the question directly inspired by it. If not, create a strong opening question based on the role. " +
                        "DO NOT use generic greetings like 'Hello'. Dive straight into the question." +
                        "\n\n--- CANDIDATE'S RESUME CONTEXT ---\n%s\n--- END OF CONTEXT ---\n\n" +
                        "Now, generate only the first question.",
                role, skills, interviewType, resumeContext // Use the safe resumeContext variable
        );
        return callGeminiApi(prompt);
    }






    public String generateNextQuestion(String role, String skills, String interviewType, String resumeText, String chatHistory) {
        String resumeContext = resumeText;
        if (resumeContext == null || resumeContext.isBlank()) {
            resumeContext = "No resume provided.";
        }

        // THE PROMPT IS UPDATED HERE FOR DIRECTNESS
        String prompt = String.format(
                "You are an expert AI interviewer. You are in the middle of a focused, professional interview for a '%s' role. " +
                        "Your task is to ask the next logical question based on the candidate's last answer and their resume context. " +
                        "CRITICAL INSTRUCTION: Your response MUST contain ONLY the question itself. Do not add any conversational filler, greetings, or acknowledgements. " +
                        "For example, DO NOT say 'Thank you, my next question is...'. Just ask the question. " +
                        "Bad response: 'That's interesting. Now, can you tell me about a time you failed?' " +
                        "Good response: 'Tell me about a time you failed.' " +
                        "\n\n--- CANDIDATE'S RESUME CONTEXT ---\n%s\n--- END OF CONTEXT ---\n\n" +
                        "--- CHAT HISTORY ---\n%s\n--- END OF HISTORY ---\n\n" +
                        "Generate the next question now:",
                role, resumeContext, chatHistory
        );
        return callGeminiApi(prompt);
    }


    private String callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        // Build the request body using our DTOs
        GeminiApiDto.Part part = new GeminiApiDto.Part(prompt);
        GeminiApiDto.Content content = new GeminiApiDto.Content(List.of(part));
        GeminiApiDto.GeminiRequest requestBody = new GeminiApiDto.GeminiRequest(List.of(content));

        HttpEntity<GeminiApiDto.GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiApiDto.GeminiResponse response = restTemplate.postForObject(apiUrl, entity, GeminiApiDto.GeminiResponse.class);
            if (response != null) {
                return response.extractText();
            }
            return "Sorry, I received an empty response. Could you please rephrase?";
        } catch (Exception e) {
            // Log the error in a real application
            e.printStackTrace();
            return "I'm sorry, I encountered a technical issue. Could you please repeat your last answer?";
        }
    }


    public String generateFeedback(String chatHistory, String resumeText, String role, String skills) {
        String prompt = String.format(
                "You are a Senior Hiring Manager providing constructive feedback to a candidate after an interview for a '%s' role. " +
                        "You have the full interview transcript and the candidate's resume. " +
                        "Your task is to provide structured feedback in Markdown format. The feedback should include: " +
                        "1. A brief overall summary of the performance. " +
                        "2. **Strengths**: 2-3 bullet points highlighting what the candidate did well, referencing specific answers from the transcript. " +
                        "3. **Areas for Improvement**: 2-3 actionable bullet points on where they could improve, suggesting better ways to phrase their answers or structure their stories. " +
                        "4. **Alignment with Role**: A short paragraph on how their experience from their resume and answers aligns with the target role and skills ('%s')." +
                        "\n\n--- CANDIDATE'S RESUME ---\n%s\n--- END OF RESUME ---\n\n" +
                        "--- INTERVIEW TRANSCRIPT ---\n%s\n--- END OF TRANSCRIPT ---\n\n" +
                        "Generate the feedback now.",
                role, skills, resumeText, chatHistory
        );
        return callGeminiApi(prompt);
    }

}