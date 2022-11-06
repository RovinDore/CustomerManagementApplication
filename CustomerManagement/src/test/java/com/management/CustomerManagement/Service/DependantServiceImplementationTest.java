package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DependantServiceImplementationTest {
    @Autowired
    DependantService dependantService;

    @Autowired
    CustomerService customerService;

    @Test
    @DirtiesContext
    @Transactional
    void saveDependant() {
        Customer customer = customerService.getCustomer(243);
        Dependant dependant = new Dependant();
        dependant.setName("Child");
        dependant.setDob(new Date().getTime());
        dependant.setGender("male");
        dependant.setDescription("hi");
        dependant.setCustomer(customer);
        Dependant savedDependant = dependantService.saveDependant(dependant);

        assertNotEquals(0, savedDependant.getId());
    }

    @Test
    @DirtiesContext
    @Transactional
    void getDependant() {
        Customer cuz = customerService.getCustomers().get(0);
        Dependant dependant = new Dependant();
        dependant.setName("Child");
        dependant.setDob(new Date().getTime());
        dependant.setDescription("hi");
        Dependant savedDependant = dependantService.saveDependant(dependant);

        Dependant getDep = dependantService.getDependant(savedDependant.getId());
        assertNotNull(getDep);
    }

    @Test
    void getDependants() {
        List<Dependant> dependantList = dependantService.getDependants();
        assertTrue(dependantList.size() > 0);
    }

    @Test
    @DirtiesContext
    @Transactional
    void deleteDependant() {
        Dependant dependant = new Dependant();
        dependant.setName("Child");
        dependant.setDob(new Date().getTime());
        dependant.setDescription("hi");
        dependant.setGender("male");
        Dependant savedDependant = dependantService.saveDependant(dependant);

//        List<Dependant> dependantList = dependantService.getDependants();
//        Dependant dependantz = dependantList.get(0);
        int dependantId = savedDependant.getId();

        dependantService.deleteDependant(savedDependant);
        Dependant getDependent = dependantService.getDependant(dependantId);
        assertNull(getDependent);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println(generatedString);
    }
}