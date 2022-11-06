package com.management.CustomerManagement.util;

import java.util.Random;

public class PasswordGenerator {
    private String password;

    public PasswordGenerator() {
        password = generatePassword();
    }

    public String generatePassword(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        this.password = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        //        System.out.println(generatedString);
        return this.password;
    }

    public String getPassword() {
        return password;
    }
}
