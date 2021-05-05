package com.smartfoxpro.rollback.activemq;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartfoxpro.rollback.entity.Request;
import com.smartfoxpro.rollback.entity.Transaction;
import com.smartfoxpro.rollback.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserServiceReceiver {

    @Value("${jms.queue.rollback-user}")
    private String rollbackUserQueue;

    @Autowired
    private Gson gson;

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private TransactionRepository transactionRepository;

    @JmsListener(destination = "user-rollback-queue")
    public void listener(String request) {
        JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);
        saveRequest(gson.fromJson(jsonRequest, Request.class), jsonRequest.get("tx_id").getAsString());
    }

    @JmsListener(destination = "user-rollback-error-queue")
    public void listenerError(String tx_id) {
        Transaction transaction = transactionRepository.findById(tx_id).get();
        Map<String, String> oldEntity = new HashMap<>();
        for (Request request : Lists.reverse(transaction.getRequests())) {
            if (request.getExist()) {
                oldEntity.putAll(request.getOldEntity());
                sendMessage.send("rollback-user-queue", gson.toJson(oldEntity));
            } else {
                sendMessage.send("rollback-user-delete-queue", gson.toJson(oldEntity));
            }

        }
    }

    private void saveRequest(Request request, String tx_id) {
        Transaction transaction = transactionRepository.findById(tx_id)
                .orElse(new Transaction(tx_id));
        List<Request> requestList = transaction.getRequests();
        requestList.add(request);
        transaction.setRequests(requestList);
        transactionRepository.save(transaction);
    }


}
