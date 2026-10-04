package com.whistledrop.dto;

public record LoginResponse(String token, String tokenType, long expiresInSeconds) {}
