package com.management.CustomerManagement.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.management.CustomerManagement.Entity.User;
import com.management.CustomerManagement.Exception.ErrorMessage;
import com.management.CustomerManagement.Models.*;
import com.management.CustomerManagement.Service.UserService;
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
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserManageControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    private String jwt;
    private final int userId = 319;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8
    );

    @BeforeEach
    void setUp() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("admin", "newpassword");

        String requestJson = setRequestBodyToString(authRequest);
        String json = getResponse("/api/v1/auth/login", requestJson, false);

        AuthenticationResponse rsp = new ObjectMapper().readValue(json, AuthenticationResponse.class);

        this.jwt = rsp.getJwt();
    }

    @Test
    @DirtiesContext
    void createUserTest() throws Exception {
        SignupRequest credentials = new SignupRequest("username","handsomerovin@msn.com", "test1234");

        String requestJson = setRequestBodyToString(credentials);
        String json = getResponse("/api/v1/auth/create", requestJson, false);

        AuthenticationResponse rsp = new ObjectMapper().readValue(json, AuthenticationResponse.class);
        Assertions.assertNotNull(rsp.getJwt());
    }

    @Test
    @DirtiesContext
    void authenticateUserTest() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("admin", "newpassword");

        String requestJson = setRequestBodyToString(authRequest);
        String json = getResponse("/api/v1/auth/login", requestJson, false);

        AuthenticationResponse rsp = new ObjectMapper().readValue(json, AuthenticationResponse.class);
        System.out.println(rsp);
        Assertions.assertNotNull(rsp.getJwt());
    }

    @Test
    @DirtiesContext
    void authenticateErrorUserTest() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("adminz", "password");

        String requestJson = setRequestBodyToString(authRequest);
        String json = getResponse("/api/v1/auth/login", requestJson, false);

        ErrorMessage rsp = new ObjectMapper().readValue(json, ErrorMessage.class);
        System.out.println(rsp);
        Assertions.assertEquals("User not found", rsp.getMessage());
        Assertions.assertEquals(404, rsp.getCode());
    }

    @Test
    @DirtiesContext
    void sendForgetPasswordTest() throws Exception {
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest();
        passwordRequest.setEmail("admin@msn.com");

        String requestJson = setRequestBodyToString(passwordRequest);
        String json = getResponse("/api/v1/auth/forgot", requestJson, false);

        ResponseMsg rsp = new ObjectMapper().readValue(json, ResponseMsg.class);
        assertEquals(ResponseMsg.class, rsp.getClass());
    }

    @Test
    @DirtiesContext
    public void resetUserPasswordTest() throws Exception {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest();
        passwordRequest.setPassword("newpassword");
        passwordRequest.setUserId(392);
        passwordRequest.setKey("zeepxlnogm");

        String requestJson = setRequestBodyToString(passwordRequest);
        String json = getResponse("/api/v1/auth/reset", requestJson, false);
        ResponseMsg rsp = new ObjectMapper().readValue(json, ResponseMsg.class);
        assertEquals(ResponseMsg.class, rsp.getClass());

    }

    @Test
    @DirtiesContext
    public void userExistsTest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("admin@msn.com");

        String requestJson = setRequestBodyToString(request);
        String json = getResponse("/api/v1/auth/exists", requestJson, false);

        boolean rsp = new ObjectMapper().readValue(json, boolean.class);
        assertTrue(rsp);
    }

    @Test
    public void getUserTest() throws Exception {
        String json = getResponseGet("/api/v1/user/" + 392, null, true);

        UserResponse rsp = new ObjectMapper().readValue(json, UserResponse.class);
        Assertions.assertEquals(UserResponse.class, rsp.getClass());
    }

    @Test
    public void getUsersTest() throws Exception {
        String json = getResponseGet("/api/v1/users", null, true);

        List rsp = new ObjectMapper().readValue(json, List.class);
        Assertions.assertNotEquals(0, rsp.size());
    }

    @Test
    @DirtiesContext
    public void updateUserTest() throws Exception {
        User user = userService.getUser(392);
        String requestJson = setRequestBodyToString(user);
        String json = getResponse("/api/v1/user/" + 392, requestJson, true);

        UserResponse rsp = new ObjectMapper().readValue(json, UserResponse.class);
        Assertions.assertEquals(UserResponse.class, rsp.getClass());
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
}
