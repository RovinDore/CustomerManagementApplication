package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Models.ResponseMsg;
import com.management.CustomerManagement.Dao.UserDao;
import com.management.CustomerManagement.Entity.ForgotPassword;
import com.management.CustomerManagement.Entity.User;
import com.management.CustomerManagement.Exception.BadRequestException;
import com.management.CustomerManagement.Exception.UserNotFoundException;
import com.management.CustomerManagement.Models.*;
import com.management.CustomerManagement.util.JwtUtil;
import com.management.CustomerManagement.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordService forgotPasswordDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${frontEndUrl}")
    private String frontEndUrl;

    Logger logger;

    UserService(){
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public MyUser loadUserByUsername(String username) throws UserNotFoundException {
//        logger.info("Searching for user");
        Optional<User> user = userDao.findByUsername(username);

        user.orElseThrow(UserNotFoundException::new);

//        logger.info("Got user " + user.get());
        return user.map(MyUser::new).get();
    }

    public MyUser loadUsernameNoException(String username){
        Optional<User> user = userDao.findByUsername(username);
        if(user.isEmpty()) return null;
        return user.map(MyUser::new).get();
    }

    public boolean ifUserExists(String email){
        Optional<User> user = userDao.findByEmail(email);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));

        logger.info("User exists");
        return true;
    }

    public AuthenticationResponse createAdmin(SignupRequest signupInfo) throws InterruptedException {
        logger.info("Creating new Admin");
        User user = new User();

        //Set user details
        String encodedPass = passwordEncoder.encode(signupInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(signupInfo.getEmail());
        user.setUsername(signupInfo.getUsername());
        user.setRoles("ADMIN");
        user.setPhoneNumber(signupInfo.getPhoneNumber());
        user.setEnabled(true);
        User savedUser = userDao.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername());

        logger.info("Admin created");
        return new AuthenticationResponse(token, savedUser);
    }

    @Transactional
    public AuthenticationResponse createUser(SignupRequest signupInfo, boolean sendEmail) throws InterruptedException {
        logger.info("Creating new user");
        User user = new User();

        //Set user details
        String encodedPass = passwordEncoder.encode(signupInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(signupInfo.getEmail());
        user.setUsername(signupInfo.getUsername());
        user.setRoles("USER");
        user.setPhoneNumber(signupInfo.getPhoneNumber());
        user.setEnabled(true);
        User savedUser = userDao.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername());

        if(sendEmail)
            emailService.sendMail(savedUser.getEmail(), "Welcome!", "Profile created! here is your password " + signupInfo.getPassword() + ". Login here " + frontEndUrl);
        logger.info("User created");
        return new AuthenticationResponse(token, savedUser);
    }

    @Transactional
    public UserResponse updateUser(User user, boolean updatePassword) {
        if(userDao.findById(user.getId()).isPresent()) {
            if(updatePassword) {
                String encodedPass = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPass);
            }
            userDao.save(user);
            logger.info("User saved" + user);
            return new UserResponse(user);
        } else throw new UserNotFoundException();
    }

    @Transactional
    public ResponseEntity<Object> deleteUser(int userId, String email) {
        Optional<User> userOptional = userDao.findByEmail(email);
        Optional<User> userOptionalId = userDao.findById(userId);
        if(userOptional.isPresent() && userOptionalId.isPresent()){
            User userByEmail = userOptional.get();
            User userById = userOptionalId.get();
            if(!Objects.equals(userByEmail.getEmail(), userById.getEmail())) throw new BadRequestException("Invalid request");
            userDao.delete(userByEmail);
            logger.info("User deleted");
            return ResponseEntity.ok(true);
        } else throw new UserNotFoundException();
    }

    @Transactional
    public ResponseEntity<Object> resetPassword(ResetPasswordRequest info) throws InterruptedException {
        ForgotPassword requestKey = forgotPasswordDao.findByKey(info.getKey());

        //Make sure the request is valid and not expired
        if(requestKey == null) throw new BadRequestException("No permission to update password.");
        if(requestKey.getExpDate() < new Date().getTime()) throw new BadRequestException("Password request has expired, please start over process");

        Optional<User> optionalUser = userDao.findById(info.getUserId());

        if(optionalUser.isEmpty()) throw new UserNotFoundException();
        forgotPasswordDao.deleteRequest(requestKey);
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(info.getPassword()));
        userDao.save(user);
        emailService.sendMail(user.getEmail(), "Password Reset", "This is confirmation that you have updated your password");

        logger.info("User password reset");
        ResponseMsg rsp = new ResponseMsg(HttpStatus.OK.toString());
        rsp.setMsg("data", List.of(true));
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> forgotPassword(String compareEmail) throws InterruptedException {
        Optional<User> userInfo = userDao.findByEmail(compareEmail);

        if (userInfo.isEmpty()) throw new UserNotFoundException("Email not found");
        User user = userInfo.get();
        String userEmail = user.getEmail();
        if (userEmail.equals(compareEmail)) {
            PasswordGenerator randomString = new PasswordGenerator();
            ForgotPasswordRequest passwordRequest = new ForgotPasswordRequest(user.getId(),
                    randomString.generatePassword());
            ForgotPassword sql = new ForgotPassword(user.getId(), passwordRequest.getKey());
            //Save details to forget password entity
            ForgotPassword saved = forgotPasswordDao.save(sql);

            //send user email the keycode to reset their password
            logger.info("Sending reset code to user " + userEmail + " " + passwordRequest.getKey());
            emailService.sendMail(userEmail, "Password reset", "Good day " + user.getUsername() +". We have received a request to reset your password. Click this link "+frontEndUrl+"/reset-password/"+passwordRequest.getKey()+"/"+passwordRequest.getUserId()+" to reset your password!");

            ResponseMsg rsp = new ResponseMsg(HttpStatus.OK.toString());
            rsp.setMsg("data", List.of(true));
            return new ResponseEntity<>(rsp, HttpStatus.OK);
        } else throw new BadRequestException("This is the wrong email for this user!");
    }

    public List<UserResponse> getUsers(){
        return userDao.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
    }

    public String getUsername(int userId){
        Optional<User> optionalUser = userDao.findById(userId);

        if(optionalUser.isEmpty()) throw new UserNotFoundException();

        return optionalUser.get().getUsername();
    }

    public User getUser(int userId){
        Optional<User> optionalUser = userDao.findById(userId);

        if(optionalUser.isEmpty()) throw new UserNotFoundException();

        return optionalUser.get();
    }

    public MyUser getUserByEmail(String email){
        Optional<User> user = userDao.findByEmail(email);

        return user.map(MyUser::new).orElse(null);
    }

}
