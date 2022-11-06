package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import com.management.CustomerManagement.Models.CustomerJson;

import java.util.List;


public interface CustomerService {
    Customer saveCustomer(Customer customer);
    Boolean customerExists(String email);
    Boolean customerExistsId(int id);
    Customer getCustomer(int id);
    List<Customer> getCustomers();
//    Customer addProjectById(int clientId, Project project);
//    Customer addProjectById(int clientId, int projectId);
    Boolean deleteCustomer(Customer customer) throws InterruptedException;
    List<Dependant> getDependants(int id);
    CustomerJson getCustomerJson(int id);
    List<CustomerJson> getCustomersJson();
}
