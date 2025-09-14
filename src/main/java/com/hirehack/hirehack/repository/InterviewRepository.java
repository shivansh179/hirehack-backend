package com.hirehack.hirehack.repository;

import com.hirehack.hirehack.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Spring Data JPA writes the SQL for this automatically based on the method name.
     */
    List<Interview> findByUserPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

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