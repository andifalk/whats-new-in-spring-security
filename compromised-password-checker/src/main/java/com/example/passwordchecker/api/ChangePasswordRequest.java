package com.example.passwordchecker.api;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
