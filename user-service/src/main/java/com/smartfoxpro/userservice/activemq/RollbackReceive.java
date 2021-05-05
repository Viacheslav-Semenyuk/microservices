package com.smartfoxpro.userservice.activemq;

import com.google.gson.Gson;
import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class RollbackReceive {

    @Autowired
    private Gson gson;

    @Autowired
    private UserRepository userRepository;

    @JmsListener(destination = "rollback-user-queue")
    public void update(String oldEntity) {
        userRepository.save(gson.fromJson(oldEntity, User.class));
    }
    @JmsListener(destination = "rollback-user-delete-queue")
    public void delete(String oldEntity) {
        userRepository.delete(gson.fromJson(oldEntity, User.class));
    }
}
