package com.smartfoxpro.userservice.activemq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.smartfoxpro.userservice.entity.Request;
import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.repository.UserRepository;
import com.smartfoxpro.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RollbackReceive {

    private final String rollbackUserQueue = "rollback-user-queue";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @JmsListener(destination = rollbackUserQueue, containerFactory = "jsaFactory")
    public void rollback(List requests) {
        Map<String, String> oldEntity = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        List<Request> requestList = mapper.convertValue(requests, new TypeReference<List<Request>>() {
        });
        for (Request request : Lists.reverse(requestList)) {
            if (request.getExist()) {
                oldEntity.putAll(request.getOldEntity());
                userRepository.save(mapper.convertValue(oldEntity, User.class));
            } else {
                userService.delete(mapper.convertValue(oldEntity, User.class));
            }
        }

    }

}
