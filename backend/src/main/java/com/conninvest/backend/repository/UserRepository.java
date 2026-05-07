package com.conninvest.backend.repository;

import com.conninvest.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    List<User> findByNameContainingIgnoreCaseOrBioContainingIgnoreCase(String name, String bio);
    List<User> findBySeekingCoFounderTrue();
    List<User> findBySeekingCoFounderTrueAndCoFounderAreaIgnoreCase(String coFounderArea);
}
