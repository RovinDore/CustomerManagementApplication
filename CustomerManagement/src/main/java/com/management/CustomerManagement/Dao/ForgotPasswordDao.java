package com.management.CustomerManagement.Dao;

import com.management.CustomerManagement.Entity.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordDao extends JpaRepository<ForgotPassword, Integer> {
    Optional<ForgotPassword> findByUserId(int userId);
}
