package com.cupk.service;

public interface PasswordResetService {
    void saveCode(String email, String code);

    boolean verifyCode(String email, String code);
}