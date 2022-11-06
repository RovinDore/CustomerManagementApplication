package com.management.CustomerManagement.Dao;

import com.management.CustomerManagement.Entity.Dependant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependantDao extends JpaRepository<Dependant, Integer> {
}
