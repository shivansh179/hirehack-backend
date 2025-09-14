package com.hirehack.hirehack.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String fullName;
    private String profession;
    private Integer yearsOfExperience;

    @Column(updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER; // Default to USER

    public enum UserRole {
        USER,
        ADMIN
    }
}