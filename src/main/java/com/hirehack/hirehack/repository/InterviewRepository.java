package com.hirehack.hirehack.repository;

import com.hirehack.hirehack.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // --- Methods you get for FREE from JpaRepository ---
    // Spring automatically provides the code for these:
    // save(Interview interview);
    // findById(Long id);
    // findAll();
    // deleteById(Long id);
    // count();
    // existsById(Long id);
    // ...and many more!


    // --- Custom methods YOU write (by declaring their name) ---

    /**
     * Finds all interviews for a specific user, identified by their phone number.
     * Results are ordered by the creation date in descending order (newest first).
     * Uses a JOIN query to properly access the user's phone number.
     */
    @Query("SELECT i FROM Interview i JOIN i.user u WHERE u.phoneNumber = :phoneNumber ORDER BY i.createdAt DESC")
    List<Interview> findByUserPhoneNumberOrderByCreatedAtDesc(@Param("phoneNumber") String phoneNumber);

    /**
     * Counts how many interviews have a specific status (e.g., "COMPLETED").
     * Spring Data JPA writes the "SELECT COUNT(*) ..." query for this.
     */
    long countByStatus(String status);

    /**
     * Finds all interviews and orders them by creation date.
     * Used by the Admin Dashboard.
     */
    List<Interview> findAllByOrderByCreatedAtDesc();

}