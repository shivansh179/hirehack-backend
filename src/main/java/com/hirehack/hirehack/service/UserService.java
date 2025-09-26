package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User registerOrUpdateUser(UserDto userDto) {
        User user = userRepository.findByPhoneNumber(userDto.getPhoneNumber())
                .orElse(new User());

        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setFullName(userDto.getFullName());
        user.setProfession(userDto.getProfession());
        user.setYearsOfExperience(userDto.getYearsOfExperience());

        if (user.getRole() == null) {
            user.setRole(User.UserRole.USER);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean doesUserExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Transactional
    public void storeResume(String phoneNumber, MultipartFile file) throws IOException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            user.setResumeText(text);
            userRepository.save(user);
        }
    }
}