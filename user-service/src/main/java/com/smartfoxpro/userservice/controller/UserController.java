package com.smartfoxpro.userservice.controller;

import com.google.inject.internal.cglib.core.$ClassNameReader;
import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.service.RollbackService;
import com.smartfoxpro.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private RollbackService rollbackService;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/user")
    public User save(@RequestBody User user, HttpServletRequest request) {
        rollbackService.getMetadata(request, user);
        return userService.save(user);
    }

    @PutMapping("/user")
    public User update(@RequestBody User user, HttpServletRequest request) {
        rollbackService.getMetadata(request, user);
        return userService.update(user);
    }

    @DeleteMapping("/user")
    public void delete(@RequestBody User user, HttpServletRequest request) {
        rollbackService.getMetadata(request, user);
        userService.delete(user);
    }
}
