package com.management.CustomerManagement.Controller;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Models.*;
import com.management.CustomerManagement.Service.EmailService;
import com.management.CustomerManagement.Service.UserService;
import com.management.CustomerManagement.Entity.User;
import com.management.CustomerManagement.Exception.BadRequestException;
import com.management.CustomerManagement.Exception.UserNotFoundException;
import com.management.CustomerManagement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class UserManageController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    EmailService emailService;

    Logger logger;

    UserManageController(){
        logger = LoggerFactory.getLogger(getClass());
    }

    //Authenticate user login
    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        logger.info("AuthRequest " + authenticationRequest);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new UserNotFoundException("Incorrect username or password");
        }

        final MyUser userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt, userDetails));
    }

    @PostMapping("/auth/create")
    public AuthenticationResponse createUser(@Valid @RequestBody SignupRequest signupInfo) throws InterruptedException {
        if(signupInfo.getUsername() == null || signupInfo.getEmail() == null || signupInfo.getPassword() == null) throw  new BadCredentialsException("Missing information");
        return userService.createUser(signupInfo, true);
    }

    @PostMapping("/auth/forgot")
    public ResponseEntity<Object> checkForgottenUser(@Valid @RequestBody ChangePasswordRequest info) throws InterruptedException {
        String userEmail = info.getEmail();
        if(userEmail == null) throw new BadRequestException("Email should not be empty");

        return userService.forgotPassword(userEmail);
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<Object> resetUserPassword(@Valid @RequestBody ResetPasswordRequest info) throws InterruptedException {
        if(info.getKey() == null || info.getUserId() == 0 || info.getPassword() == null) throw new BadRequestException("Invalid request");
        return userService.resetPassword(info);
    }

    @PostMapping("/auth/exists")
    public boolean userExists(@Valid @RequestBody ChangePasswordRequest info) {
        if(info.getEmail() == null) throw new BadRequestException("Invalid request");
        return userService.ifUserExists(info.getEmail());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserResponse> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/user/{userId}")
    public UserResponse getUser(@PathVariable int userId){
        String username = userService.getUsername(userId);
        return new UserResponse(userService.loadUserByUsername(username));
    }

    @PostMapping("/user/{userId}")
    public UserResponse updateUser(@PathVariable int userId, @RequestBody User user){
        User getUser = userService.getUser(userId);
        boolean updatePassword = false;
        getUser.setUsername(user.getUsername());
        getUser.setEmail(user.getEmail());
        if(user.getPassword() != null && !Objects.equals(user.getPassword(), "")){
            getUser.setPassword(user.getPassword());
            updatePassword = true;
        }
        return userService.updateUser(getUser, updatePassword);
    }

}
