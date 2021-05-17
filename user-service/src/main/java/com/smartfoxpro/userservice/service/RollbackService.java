package com.smartfoxpro.userservice.service;

import com.smartfoxpro.userservice.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface RollbackService {

    void getMetadata(HttpServletRequest request, User user);

}
