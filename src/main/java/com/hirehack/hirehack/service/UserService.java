package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.interfaces.UserServiceInterface;
import com.hirehack.hirehack.util.PhoneNumberUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User updateUserProfile(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        if (userDto.getProfession() != null) {
            user.setProfession(userDto.getProfession());
        }
        if (userDto.getYearsOfExperience() != null) {
            user.setYearsOfExperience(userDto.getYearsOfExperience());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean doesUserExist(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        return userRepository.findByPhoneNumber(normalizedPhoneNumber).isPresent();
    }

    @Transactional
    public void storeResume(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            user.setResumeText(text);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("hasNext", userPage.hasNext());
        response.put("hasPrevious", userPage.hasPrevious());
        
        return response;
    }
}