package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Dao.ForgotPasswordDao;
import com.management.CustomerManagement.Entity.ForgotPassword;
import com.management.CustomerManagement.Entity.User;
import com.management.CustomerManagement.Exception.BadRequestException;
import com.management.CustomerManagement.Exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class PasswordServiceImplementation implements PasswordService{
    @Autowired
    ForgotPasswordDao forgotPasswordDao;

    @Autowired
    UserService userService;

    Logger logger;

    PasswordServiceImplementation(){
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public ForgotPassword findByKey(String key) {
        logger.info("Searching for password request");
        List<ForgotPassword> passwordList = forgotPasswordDao.findAll();
        ForgotPassword returnValue = null;
        for (ForgotPassword p: passwordList) if(Objects.equals(p.getKeyValue(), key)) returnValue = p;
        return returnValue;
    }

    @Override
    @Transactional
    public ForgotPassword save(ForgotPassword passwordRequest) {
        logger.info("Password request saved " + passwordRequest.getKeyValue());
        return forgotPasswordDao.save(passwordRequest);
    }

    @Override
    @Transactional
    public boolean deleteRequest(ForgotPassword request) {
        forgotPasswordDao.delete(request);
        logger.info("Deleted the password request");
        return true;
    }

    @Override
    public ForgotPassword findByUserId(int userId) {
        Optional<ForgotPassword> passwordOptional = forgotPasswordDao.findByUserId(userId);
        return passwordOptional.orElse(null);
    }

    @Override
    public ForgotPassword findByEmail(String email) {
        MyUser user = userService.getUserByEmail(email);

        Optional<ForgotPassword> optionalForgotPassword = forgotPasswordDao.findByUserId(user.getId());

        if(optionalForgotPassword.isEmpty()) throw new UserNotFoundException("Email not found");
        return optionalForgotPassword.get();
    }
}
