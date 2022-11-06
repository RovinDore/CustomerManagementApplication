package com.management.CustomerManagement.Service;

import com.management.CustomerManagement.Entity.ForgotPassword;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordServiceImplementationTest {

    @Autowired
    PasswordService passwordService;

    @Test
    void findByKey() {
        ForgotPassword request = passwordService.findByKey("hdfctohakx");
        assertNotNull(request);
    }

    @Test
    @DirtiesContext
    void save() {
        ForgotPassword newRequest = new ForgotPassword(319, "testkey");
        ForgotPassword savedRequest = passwordService.save(newRequest);

        assertNotNull(savedRequest);
    }

    @Test
    @DirtiesContext
    @Transactional
    void deleteRequest() {
        ForgotPassword request = passwordService.findByKey("hdfctohakx");
        assertTrue(passwordService.deleteRequest(request));

        ForgotPassword rq = passwordService.findByKey("hdfctohakx");
        assertNull(rq);
    }
}