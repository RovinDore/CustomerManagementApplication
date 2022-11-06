package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Dao.DependantDao;
import com.management.CustomerManagement.Entity.Dependant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
public class DependantServiceImplementation implements DependantService{
    @Autowired
    DependantDao dependantDao;

    @Autowired
    CustomerService customerService;

    @Override
    @Transactional
    public Dependant saveDependant(Dependant dependant) {
        return dependantDao.save(dependant);
    }

    @Override
    public Dependant getDependant(int id) {
        Optional<Dependant> optionalDependant = dependantDao.findById(id);
        return optionalDependant.orElse(null);
    }

    @Override
    public List<Dependant> getDependants() {
        return dependantDao.findAll();
    }

    @Override
    public boolean deleteDependant(Dependant dependant) {
        dependantDao.delete(dependant);
        return true;
    }
}
