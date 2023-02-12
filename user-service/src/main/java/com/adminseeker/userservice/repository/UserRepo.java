package com.adminseeker.userservice.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adminseeker.userservice.entities.User;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
