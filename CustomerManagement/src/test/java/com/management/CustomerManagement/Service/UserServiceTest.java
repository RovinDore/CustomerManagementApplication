package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Entity.ForgotPassword;
import com.management.CustomerManagement.Entity.User;
import com.management.CustomerManagement.Exception.ErrorMessage;
import com.management.CustomerManagement.Exception.UserNotFoundException;
import com.management.CustomerManagement.Models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    private PasswordService forgotPasswordDao;

    @Test
    void loadUserByUsername() {
        MyUser user = userService.loadUserByUsername("admin");
        assertNotNull(user);
    }

    @Test
    void ifUserExists() {
        boolean exists = userService.ifUserExists("rovindore@hotmail.com");
        assertTrue(exists);
    }

    @Test
    @DirtiesContext
    void createUser() throws InterruptedException {
        SignupRequest signupRequest = new SignupRequest("dummyaccount", "dummyz@msn.com", "test1234");
        AuthenticationResponse response = userService.createUser(signupRequest, false);

        assertNotNull(response.getJwt());
    }

    @Test
    @DirtiesContext
    void createAdmin() throws InterruptedException {
        SignupRequest signupRequest = new SignupRequest("adminAccount", "admin@admin.com", "password");
        AuthenticationResponse response = userService.createAdmin(signupRequest);

        assertNotNull(response.getJwt());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        User user = userService.getUser(319);
        UserResponse updatedUser = userService.updateUser(user, false);

        assertNotNull(updatedUser);
    }

    @Test
    @DirtiesContext
    void deleteUser() throws InterruptedException {
        SignupRequest request = new SignupRequest("dummy", "dummy@msn.com", "dumy123");
        AuthenticationResponse user = userService.createUser(request,false);
        int userId = user.getUser().getId();
        String userEmail = user.getUser().getEmail();
        userService.deleteUser(userId, userEmail);
        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId), "Different exception thrown");
    }

    @Test
    void resetPassword() throws InterruptedException {
        MyUser user = userService.loadUsernameNoException("dummy"); //get user
        userService.forgotPassword(user.getEmail()); //send request email
        ForgotPassword requestDb = forgotPasswordDao.findByUserId(user.getId()); //get key from db
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setKey(requestDb.getKeyValue());
        request.setPassword("newpassword");
        request.setUserId(user.getId());

        userService.resetPassword(request);
    }

    @Test
    void forgotPassword() throws InterruptedException {
        userService.forgotPassword("dore.rovin@gmail.com");
        ForgotPassword entry = forgotPasswordDao.findByEmail("dore.rovin@gmail.com");

        assertNotNull(entry);
    }

    @Test
    void getUsers() {
        List<UserResponse> userList = userService.getUsers();

        assertNotEquals(0, userList.size());
    }

    @Test
    void getUsername() {
        String username = userService.getUsername(301);
        assertEquals("admin", username);
    }

    @Test
    void getUser() {
        User user = userService.getUser(301);

        assertNotNull(user);
    }
}