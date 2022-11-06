package com.management.CustomerManagement.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.management.CustomerManagement.Entity.Customer;
import com.management.CustomerManagement.Entity.Dependant;
import com.management.CustomerManagement.Exception.ErrorMessage;
import com.management.CustomerManagement.Models.AuthenticationRequest;
import com.management.CustomerManagement.Models.AuthenticationResponse;
import com.management.CustomerManagement.Models.CustomerJson;
import com.management.CustomerManagement.Models.SignupRequest;
import com.management.CustomerManagement.Service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private CustomerService customerService;

    String jwt = "";
    int dummyCusId = 0;
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @BeforeEach
    void setUp() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("admin", "password");

        String requestJson = setRequestBodyToString(authRequest);
        String json = getResponse("/api/v1/auth/login", requestJson, false);

        AuthenticationResponse rsp = new ObjectMapper().readValue(json, AuthenticationResponse.class);

        this.jwt = rsp.getJwt();
    }

    @Test
//    @DirtiesContext
    @Transactional
    void createCustomerTest() throws Exception {
        //Set customer
        Customer cuz = new Customer();
        cuz.setName("test");
        cuz.setEmail("Rovin@weml.com");
        cuz.setPhoneNumber("4651366");

        String requestJson = setRequestBodyToString(cuz);
        String json = getResponse("/api/v1/customer", requestJson, true);

        Customer user = new ObjectMapper().readValue(json, Customer.class);
        Assertions.assertEquals(Customer.class, user.getClass());
        this.dummyCusId = user.getId();
    }

    @Test
    @DirtiesContext
    void createUserTest() throws Exception {
        SignupRequest credentials = new SignupRequest("new@email.com", "new@email.com", "test1234");

        String requestJson = setRequestBodyToString(credentials);
        String json = getResponse("/api/v1/auth/create", requestJson, false);

        AuthenticationResponse rsp = new ObjectMapper().readValue(json, AuthenticationResponse.class);
        Assertions.assertNotNull(rsp.getJwt());
        Assertions.assertNotNull(rsp.getUser());
    }

    @Test
    @DirtiesContext
    @Transactional
    void getCustomerJsonTest() throws Exception {
        //Set requestbody
//        int testid = dummyCusId != 0 ? dummyCusId : 262;
        String json = getResponseGet("/api/v1/customer/243", null, true);

        CustomerJson user = new ObjectMapper().readValue(json, CustomerJson.class);
        Assertions.assertEquals(CustomerJson.class, user.getClass());
    }

    @Test
    @DirtiesContext
    @Transactional
    void getCustomerTest() throws Exception {
        //Set requestbody
//        int testid = dummyCusId != 0 ? dummyCusId : 262;
        String json = getResponseGet("/api/v1/v2/customer/243", null, true);

        Customer user = new ObjectMapper().readValue(json, Customer.class);
        Assertions.assertEquals(Customer.class, user.getClass());
    }

    @Test
    @DirtiesContext
    void deleteCustomer() throws Exception {
        int testid = 377;
        getResponseDelete("/api/v1/customer/" + testid, null, true);
        String rspJson = getResponseGet("/api/v1/customer/" + testid, null, true);

        ErrorMessage rsp = new ObjectMapper().readValue(rspJson, ErrorMessage.class);
        Assertions.assertEquals("Customer not found", rsp.getMessage());
        Assertions.assertEquals(404, rsp.getCode());
    }

    @Test
    void getAllCustomers() throws Exception {
        String json = getResponseGet("/api/v1/customers", null, true);

        List customerList = new ObjectMapper().readValue(json, List.class);
        Assertions.assertNotEquals(0, customerList.size());
    }

    @Test
    void getCustomerDependants() throws Exception {
        String json = getResponseGet("/api/v1/customer/243/dependants", null, true);

        List customerList = new ObjectMapper().readValue(json, List.class);
        Assertions.assertNotEquals(0, customerList.size());
    }

    @Test
    void addDependantToCustomer() throws Exception{
        Dependant newDependant = new Dependant();
        Customer customer = customerService.getCustomer(256);
        newDependant.setCustomer(customer);
        newDependant.setGender("Female");
        newDependant.setName("Name");
        newDependant.setDob(334455655);
        String requestJson = setRequestBodyToString(newDependant);
        String json = getResponse("/api/v1/customer/256/dependants", requestJson, true);

        Dependant savedDependant = new ObjectMapper().readValue(json, Dependant.class);
        Assertions.assertNotEquals(0, savedDependant.getId());
    }

    @Test
    @DirtiesContext
    void deleteDependant() throws Exception {
        getResponseDelete("/api/v1/dependants/245", null, true);

        String rspJson = getResponseGet("/api/v1/dependants/245", null, true);
        ErrorMessage rsp = new ObjectMapper().readValue(rspJson, ErrorMessage.class);

        Assertions.assertEquals("Dependant not found", rsp.getMessage());
//        Assertions.assertEquals(404, rsp.getCode());
    }

    @Test
    void getDependants() throws Exception {
        String json = getResponseGet("/api/v1/dependants", null, true);

        List dependants = new ObjectMapper().readValue(json, List.class);
        Assertions.assertNotEquals(0, dependants.size());
    }

    @Test
    @DirtiesContext
    void saveDependant() throws Exception {
        Dependant newDependant = new Dependant();
        Customer customer = customerService.getCustomer(256);
        newDependant.setCustomer(customer);
        newDependant.setGender("Female");
        newDependant.setName("Name");
        newDependant.setDob(334455655);

        String requestJson = setRequestBodyToString(newDependant);
        String rspJson = getResponse("/api/v1/dependants", requestJson, true);

        Dependant savedDependant = new ObjectMapper().readValue(rspJson, Dependant.class);
        Assertions.assertNotEquals(0, savedDependant.getId());
    }

    @Test
    void getDependant() throws Exception {
        String rspJson = getResponseGet("/api/v1/dependants/245", null, true);

        Dependant dependant = new ObjectMapper().readValue(rspJson, Dependant.class);
        Assertions.assertNotEquals(0, dependant.getId());
    }

    @Test
    void checkCustomerById() throws Exception {
        String rspJson = getResponse("/api/v1/customerExists/256", null, true);

        boolean customerExists = new ObjectMapper().readValue(rspJson, boolean.class);
        Assertions.assertTrue(customerExists);
    }

    @Test
    void checkCustomer() throws Exception {
        Customer customer = customerService.getCustomer(256);
        customer.setEmail("doesnotexists@hotmail.com");
        String requestJson = setRequestBodyToString(customer);
        String rspJson = getResponse("/api/v1/customerExists", requestJson, true);

        boolean customerExists = new ObjectMapper().readValue(rspJson, boolean.class);
        Assertions.assertFalse(customerExists);
    }

    private String setRequestBodyToString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(object);
    }

    private HttpHeaders getHeaders(){
        // Set header
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);

        return new HttpHeaders(header);
    }

    private String getResponse(String urlTemplate, String requestJson, boolean setHeader) throws Exception {
        HttpHeaders headers = setHeader ? getHeaders() : new HttpHeaders();

        requestJson = requestJson == null ? "" : requestJson;

        RequestBuilder request = MockMvcRequestBuilders.post(urlTemplate).headers(headers).content(requestJson).contentType(APPLICATION_JSON_UTF8);

        MvcResult result = mvc.perform(request).andReturn();
        return result.getResponse().getContentAsString();
    }

    private String getResponseGet(String urlTemplate, String requestJson, boolean setHeader) throws Exception {
        HttpHeaders headers = setHeader ? getHeaders() : new HttpHeaders();
        requestJson = requestJson == null ? "" : requestJson;

        RequestBuilder request = MockMvcRequestBuilders.get(urlTemplate).headers(headers).content(requestJson).contentType(APPLICATION_JSON_UTF8);
        MvcResult result = mvc.perform(request).andReturn();
        return result.getResponse().getContentAsString();
    }

    private String getResponseDelete(String urlTemplate, String requestJson, boolean setHeader) throws Exception {
        HttpHeaders headers = setHeader ? getHeaders() : new HttpHeaders();
        requestJson = requestJson == null ? "" : requestJson;

        RequestBuilder request = MockMvcRequestBuilders.delete(urlTemplate).headers(headers).content(requestJson).contentType(APPLICATION_JSON_UTF8);
        MvcResult result = mvc.perform(request).andReturn();
        return result.getResponse().getContentAsString();
    }
}
