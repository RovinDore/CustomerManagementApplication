package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.Dependant;

import java.util.List;


public interface DependantService {
    Dependant saveDependant(Dependant dependant);
    Dependant getDependant(int id);
    List<Dependant> getDependants();
    boolean deleteDependant(Dependant dependant);
}
