package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Dao.CustomerDao;
import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import com.management.CustomerManagement.Exception.CustomerNotFoundException;
import com.management.CustomerManagement.Models.CustomerJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerServiceImplementation implements CustomerService{
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DependantService dependantService;

    @Autowired
    private EmailService emailService;

    Logger logger;

    CustomerServiceImplementation(){
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerDao.save(customer);
    }

    @Override
    public Boolean customerExists(String email) {
        List<Customer> customerList = customerDao.findByEmail(email);
        return customerList.size() > 0;
    }

    @Override
    public Boolean customerExistsId(int id) {
        Optional<Customer> optionalCustomer = customerDao.findById(id);
        return optionalCustomer.isPresent();
    }

    @Override
    public Customer getCustomer(int id) {
        Optional<Customer> optionalCustomer = customerDao.findById(id);

        return optionalCustomer.orElse(null);
    }

    @Override
    public CustomerJson getCustomerJson(int id) {
        Optional<Customer> optionalCustomer = customerDao.findById(id);

        if(optionalCustomer.isEmpty()) throw new CustomerNotFoundException("Customer not found");
        return optionalCustomer.map(CustomerJson::new).get();
    }

    @Override
    public List<Customer> getCustomers() {
        return customerDao.findAll();
    }

    @Override
    public List<CustomerJson> getCustomersJson() {
        List<Customer> customerList = customerDao.findAll();
        return customerList.stream().map(CustomerJson::new).toList();
    }

    @Override
    @Transactional
    public Boolean deleteCustomer(Customer customer) throws InterruptedException {
        String customerEmail = customer.getEmail(); // Save email string
        List<Dependant> dependantList = customer.getDependants();

        for (Dependant dependant: dependantList) dependantService.deleteDependant(dependant); //Deletes dependants with customer

        customerDao.delete(customer);
        logger.info("Customer deleted from db");
        emailService.sendMail(customerEmail, "Profile deleted", "Your profile was deleted");
        return true;
    }

    @Override
    public List<Dependant> getDependants(int id) {
        Customer customer = getCustomer(id);
        if(customer == null) throw new CustomerNotFoundException("Customer Not Found");
        logger.info("Got dependant for customer");
        return customer.getDependants() == null ? new ArrayList<>() : customer.getDependants();
    }

}
