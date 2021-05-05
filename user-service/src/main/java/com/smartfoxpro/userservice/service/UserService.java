package com.smartfoxpro.userservice.service;

import com.smartfoxpro.userservice.entity.User;

import java.util.List;

public interface UserService {

    User getById(Long id);

    User save(User user);

    List<User> findAll();

    User update(User user);

    void delete(User user);
}
