package com.management.CustomerManagement.Dao;

import com.management.CustomerManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String userName);
    Optional<User> findByEmail(String email);
}
