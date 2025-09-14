package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerOrUpdateUser(UserDto userDto) {
        // Find user by phone number, or create a new one
        User user = userRepository.findByPhoneNumber(userDto.getPhoneNumber())
                .orElse(new User());

        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setFullName(userDto.getFullName());
        user.setProfession(userDto.getProfession());
        user.setYearsOfExperience(userDto.getYearsOfExperience());

        return userRepository.save(user);
    }

    public boolean doesUserExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public void storeResume(String phoneNumber, MultipartFile file) throws IOException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + phoneNumber));

        // Extract text from the PDF file
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            user.setResumeText(text);
            userRepository.save(user);
        }
    }
}