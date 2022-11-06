package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.ForgotPassword;

public interface PasswordService {
    ForgotPassword findByKey(String key);
    ForgotPassword save(ForgotPassword passwordRequest);
    boolean deleteRequest(ForgotPassword request);
    ForgotPassword findByUserId(int userId);
    ForgotPassword findByEmail(String email);
}
