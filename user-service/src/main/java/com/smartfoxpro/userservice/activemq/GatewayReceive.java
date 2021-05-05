package com.smartfoxpro.userservice.activemq;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartfoxpro.userservice.entity.Transaction;
import com.smartfoxpro.userservice.entity.User;
import com.smartfoxpro.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class GatewayReceive {

    @Value("${jms.queue.user-rollback}")
    private String userRollbackQueue;

    @Autowired
    private Gson gson;

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private UserService userService;

    @Autowired
    private Transaction transaction;

    @JmsListener(destination = "gateway-user-queue")
    public void listener(String request) {
        JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);
        JsonParser jsonParser = new JsonParser();
        if (jsonRequest.get("request").getAsJsonObject().has("id")) {
            Long id = jsonRequest.get("request").getAsJsonObject().get("id").getAsLong();
            User user = userService.getById(id);
            jsonRequest.addProperty("exist", true);
            jsonRequest.add("oldEntity", gson.toJsonTree(user));
        } else {
            jsonRequest.addProperty("exist", false);
            jsonRequest.add("oldEntity", gson.toJsonTree(gson.toJsonTree(jsonParser.parse("{}"))));
        }
        transaction.setTx_id(jsonRequest.get("tx_id").getAsString());
        sendMessage.send(userRollbackQueue, jsonRequest.toString());
    }
}
