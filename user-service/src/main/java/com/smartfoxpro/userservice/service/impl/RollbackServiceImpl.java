package com.smartfoxpro.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartfoxpro.userservice.activemq.SendMessage;
import com.smartfoxpro.userservice.entity.Transaction;
import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.service.RollbackService;
import com.smartfoxpro.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class RollbackServiceImpl implements RollbackService {

    private final String userRollbackQueue = "user-rollback-queue";

    @Autowired
    private UserService userService;

    @Autowired
    private Transaction transaction;

    @Autowired
    private SendMessage sendMessage;

    @Override
    public void getMetadata(HttpServletRequest request, User user) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ObjectMapper mapper = new ObjectMapper();
        node.put("tx_id", request.getParameter("tx_id"));
        node.put("service", "user-service");
        node.put("endpoint", request.getServletPath());
        node.put("method", request.getMethod());
        node.putPOJO("request", user);
        if (user.getId() != null) {
            User oldUser = userService.getById(user.getId());
            node.put("exist", true);
            node.putPOJO("oldEntity", oldUser);
        } else {
            node.put("exist", false);
            try {
                node.putPOJO("oldEntity", mapper.readTree("{}"));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
        transaction.setTx_id(request.getParameter("tx_id"));
        sendMessage.sendJsonNode(userRollbackQueue, node);
    }
}
