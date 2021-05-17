package com.smartfoxpro.userservice.service.impl;

import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.repository.UserRepository;
import com.smartfoxpro.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        if (userRepository.existsById(user.getId())) {
            //block code for test
            if (user.getName().equals("error")) {
                throw new RuntimeException();
            }
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

}
