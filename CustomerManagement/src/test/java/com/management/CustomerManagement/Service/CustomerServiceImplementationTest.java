package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import com.management.CustomerManagement.Models.CustomerJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceImplementationTest {
    @Autowired
    CustomerService customerService;

    @Autowired
    DependantService dependantService;

    @Test
    @DirtiesContext
    void saveCustomerTest() {
        Customer customer = new Customer();
        customer.setPhoneNumber("46527383");
        customer.setName("rovin");
        customer.setEmail("rovin@msn.com");
        Customer savedCustomer = customerService.saveCustomer(customer);

        assertNotNull(savedCustomer);
        assertNotEquals(0, savedCustomer.getId());
    }

    @Test
    void customerExists() {
        boolean adminExists = customerService.customerExists("admin@msn.com");
        assertTrue(adminExists);
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer();
        customer.setPhoneNumber("46527383");
        customer.setName("rovin");
        customer.setEmail("rovin@msn.com");
        Customer saved = customerService.saveCustomer(customer);

        Customer z = customerService.getCustomer(saved.getId());

        assertNotNull(z);
    }

    @Test
    void getCustomers() {
        Customer customer = new Customer();
        customer.setPhoneNumber("46527383");
        customer.setName("rovin");
        customer.setEmail("rovin@msn.com");
        customerService.saveCustomer(customer);

        List<Customer> customerList = customerService.getCustomers();

        assertTrue(customerList.size() > 0);
    }

    @Test
    @DirtiesContext
    @Transactional
    void addDependant() {
        Customer customer = new Customer();
        customer.setPhoneNumber("46527383");
        customer.setName("rovin");
        customer.setEmail("rovin@msn.com");
        Customer saved = customerService.saveCustomer(customer);

        Dependant dependant = new Dependant();
        dependant.setCustomer(saved);
        dependant.setName("Child");
        dependant.setDob(new Date().getTime());
        Dependant savedDependant = dependantService.saveDependant(dependant);
        System.out.println(savedDependant);
        assertTrue(savedDependant.getId() != 0);
    }

    @Test
    @DirtiesContext
    @Transactional
    void deleteCustomer() throws InterruptedException {
        Customer customer = new Customer();
        customer.setPhoneNumber("46527383");
        customer.setName("rovin");
        customer.setEmail("rovin@msn.com");
        Customer saved = customerService.saveCustomer(customer);

        int customerId = saved.getId();
        customerService.deleteCustomer(customer);

        Customer isDeletedCustomer = customerService.getCustomer(customerId);
        assertNull(isDeletedCustomer);
    }

    @Test
    @DirtiesContext
    @Transactional
    void getDependants() {
        Customer newCustomer = new Customer();
        newCustomer.setEmail("email@email.com");
        newCustomer.setName("Testing");
        newCustomer.setPhoneNumber("7658768");

        Customer savedCustomer = customerService.saveCustomer(newCustomer);

        Dependant dependant = new Dependant();
        dependant.setCustomer(savedCustomer);
        dependant.setName("Child");
        dependant.setDob(new Date().getTime());

        dependant.setCustomer(savedCustomer);
        dependantService.saveDependant(dependant);

        List<Dependant> dependantList = customerService.getDependants(savedCustomer.getId());
        System.out.println(dependantList);
        assertTrue(dependantList.size() > 0);
    }

    @Test
    @DirtiesContext
    @Transactional
    void customerExistsByEmailTest(){
        Customer newCustomer = new Customer();
        newCustomer.setEmail("email@email.com");
        newCustomer.setName("Testing");
        newCustomer.setPhoneNumber("7658768");

        Customer savedCustomer = customerService.saveCustomer(newCustomer);
        boolean exists = customerService.customerExists(savedCustomer.getEmail());

        Assertions.assertTrue(exists);
    }

    @Test
    @DirtiesContext
    @Transactional
    void customerExistsByIdTest(){
        Customer newCustomer = new Customer();
        newCustomer.setEmail("email@email.com");
        newCustomer.setName("Testing");
        newCustomer.setPhoneNumber("7658768");
        Customer savedCustomer = customerService.saveCustomer(newCustomer);

        boolean exists = customerService.customerExistsId(savedCustomer.getId());

        Assertions.assertTrue(exists);
    }

    @Test
    @Transactional
    void getCustomJsonTest(){
        Customer newCustomer = new Customer();
        newCustomer.setEmail("email@email.com");
        newCustomer.setName("Testing");
        newCustomer.setPhoneNumber("7658768");
        Customer savedCustomer = customerService.saveCustomer(newCustomer);
        CustomerJson customer = customerService.getCustomerJson(savedCustomer.getId());

        Assertions.assertNotEquals(0, customer.getId());
        Assertions.assertNotNull(customer);
    }

    @Test
    @Transactional
    void getCustomersJsonTest(){
        Customer newCustomer = new Customer();
        newCustomer.setEmail("email@email.com");
        newCustomer.setName("Testing");
        newCustomer.setPhoneNumber("7658768");
        customerService.saveCustomer(newCustomer);
        List<CustomerJson> customer = customerService.getCustomersJson();

        Assertions.assertNotEquals(0, customer.size());
        Assertions.assertNotNull(customer);
    }
}