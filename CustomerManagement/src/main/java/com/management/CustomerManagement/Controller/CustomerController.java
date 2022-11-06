package com.management.CustomerManagement.Controller;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Exception.UserNotFoundException;
import com.management.CustomerManagement.Models.CustomerJson;
import com.management.CustomerManagement.Service.UserService;
import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import com.management.CustomerManagement.Exception.BadRequestException;
import com.management.CustomerManagement.Exception.CustomerNotFoundException;
import com.management.CustomerManagement.Exception.DependantNotFoundException;
import com.management.CustomerManagement.Models.SignupRequest;
import com.management.CustomerManagement.Service.CustomerService;
import com.management.CustomerManagement.Service.DependantService;
import com.management.CustomerManagement.Service.EmailService;
import com.management.CustomerManagement.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    DependantService dependantService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    Logger logger;

    CustomerController(){
        logger = LoggerFactory.getLogger(getClass());
    }

    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerJson getCustomerJson(@PathVariable int customerId){
        logger.info("Getting customer");
        CustomerJson customer = customerService.getCustomerJson(customerId);

        if(customer == null) throw new CustomerNotFoundException("Customer not found");
        logger.info("Got customer " + customer);
        return customer;
    }

    @GetMapping("/v2/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Customer getCustomer(@PathVariable int customerId){
        logger.info("Getting customer");
        Customer customer = customerService.getCustomer(customerId);

        if(customer == null) throw new CustomerNotFoundException("Customer not found");
        logger.info("Got customer " + customer);
        return customer;
    }

    @DeleteMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable int customerId) throws InterruptedException {
        logger.info("Deleting customer");
        Customer customer = customerService.getCustomer(customerId);
        if(customer == null) throw new CustomerNotFoundException("Customer not found");

        String customerEmail = customer.getEmail();
        customerService.deleteCustomer(customer);

        //Delete user if attached to customer
        try{
            MyUser user = userService.getUserByEmail(customerEmail);
            userService.deleteUser(user.getId(), customerEmail);
        } catch (UserNotFoundException e){
            logger.info("Customer not linked to user | " + e.getMessage());
        } catch (BadRequestException e){
            logger.info("Customer credentials doesn't match | " + e.getMessage());
        } catch (Exception e){
            logger.info("Something went wrong | " + e.getMessage());
        }

        logger.info("Customer deleted");
        Map<String, Boolean> deleted = new HashMap<>();
        deleted.put("Deleted", true);
        return ResponseEntity.ok(deleted);
    }

    @PostMapping("/customer")
    public Customer saveCustomer(@Valid @RequestBody Customer customer) throws InterruptedException {
        logger.info("Saving customer");
        boolean isNewCustomer = customer.getId() == 0;

        Customer savedCustomer = customerService.saveCustomer(customer);
        if(isNewCustomer){
            String customerPassword = new PasswordGenerator().getPassword();
            SignupRequest credentials = new SignupRequest(savedCustomer.getEmail(), savedCustomer.getEmail(), customerPassword);
            userService.createUser(credentials, true);
        }
        return savedCustomer;
    }

    @GetMapping("/customers")
    public List<CustomerJson> getAllCustomers(){
        return customerService.getCustomersJson();
    }

    @GetMapping("/customer/{customerId}/dependants")
    public List<Dependant> getCustomerDependants(@PathVariable int customerId){
        logger.info("Getting customer dependants");
        return customerService.getDependants(customerId);
    }

    @PostMapping("/customer/{customerId}/dependants")
    public Dependant addDependantToCustomer(@PathVariable int customerId,@Valid @RequestBody Dependant dependant) throws InterruptedException {
        logger.info("Creating dependant to user");
        Customer customer = customerService.getCustomer(customerId);
        if(customer == null) throw new CustomerNotFoundException("Customer not found");
        if(dependant == null) throw new BadRequestException("Dependant invalid");

        int dependantId = dependant.getId();
        dependant.setCustomer(customer);
        Dependant savedDependant = dependantService.saveDependant(dependant);

        if(savedDependant != null && dependantId == 0) {
            String emailBody = "Hi there '" + savedDependant.getName() + "' has been registered to you as a dependant.";
            emailService.sendMail(customer.getEmail(), "Dependant Creation", emailBody);
        }
        return savedDependant;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/dependants/{dependantId}")
    public ResponseEntity<?> deleteDependant(@PathVariable int dependantId) throws InterruptedException {
        Dependant dependant = dependantService.getDependant(dependantId);

        if(dependant == null) throw new CustomerNotFoundException("Dependant not found");
        String customerEmail = dependant.getCustomer().getEmail();
        String dependantName = dependant.getName();
        dependantService.deleteDependant(dependant);
        logger.info("Dependant deleted");

        emailService.sendMail(customerEmail, "Dependant De-registered", "The dependant '" + dependantName + "' has been de-registered");
        Map<String, Boolean> deleted = new HashMap<>();
        deleted.put("Deleted", true);

        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/dependants")
    public List<Dependant> getDependants(){
        logger.info("Getting all dependants");
        return dependantService.getDependants();
    }

    @PostMapping("/dependants")
    public Dependant saveDependant(@Valid @RequestBody Dependant dependant){
        logger.info("Saving dependant");
        return dependantService.saveDependant(dependant);
    }

    @GetMapping("/dependants/{dependantId}")
    public Dependant getDependant(@PathVariable int dependantId){
        logger.info("Getting dependants ");
        Dependant dependant = dependantService.getDependant(dependantId);

        if(dependant == null) throw new DependantNotFoundException("Dependant not found");

        return dependant;
    }

    @PostMapping("/customerExists")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkCustomer(@RequestBody Customer customer) {
        logger.info("Checking if user exists");
        if(customer.getEmail() == null || Objects.equals(customer.getEmail(), ""))
            throw new BadRequestException("Please enter a valid email");
        return customerService.customerExists(customer.getEmail().trim());
    }

    @PostMapping("/customerExists/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkCustomerById(@PathVariable int customerId) {
        logger.info("Checking if user exists");
        if(customerId == 0) throw new BadRequestException("Please enter a valid customer id");
        return customerService.customerExistsId(customerId);
    }

}
