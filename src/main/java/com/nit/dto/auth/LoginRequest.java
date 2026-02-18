package com.nit.dto.auth;

public record LoginRequest(String username,String password,boolean rememberMe){}
