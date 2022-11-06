package com.management.CustomerManagement.Dao;

import com.management.CustomerManagement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDao extends JpaRepository<Customer, Integer> {
    List<Customer> findByEmail(String email);
}
