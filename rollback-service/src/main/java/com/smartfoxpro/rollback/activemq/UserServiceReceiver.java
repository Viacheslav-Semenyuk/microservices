package com.smartfoxpro.rollback.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartfoxpro.rollback.entity.Request;
import com.smartfoxpro.rollback.entity.Transaction;
import com.smartfoxpro.rollback.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserServiceReceiver {

    private final String rollbackUserQueue = "rollback-user-queue";
    private final String userRollbackQueue = "user-rollback-queue";
    private final String userRollbackErrorQueue = "user-rollback-error-queue";

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private TransactionRepository transactionRepository;

    @JmsListener(destination = userRollbackQueue, containerFactory = "jsaFactory")
    public void listener(ObjectNode request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String tx_id = request.get("tx_id").asText();
        request.remove("tx_id");
        saveRequest(mapper.treeToValue(request, Request.class), tx_id);
    }

    @JmsListener(destination = userRollbackErrorQueue, containerFactory = "jsaFactory")
    public void listenerError(String tx_id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(tx_id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            sendMessage.sendList(rollbackUserQueue, transaction.getRequests());
        }
    }

    private void saveRequest(Request request, String tx_id) {
        Transaction transaction = transactionRepository.findById(tx_id)
                .orElse(new Transaction(tx_id));
        List<Request> requests = transaction.getRequests() == null ? new ArrayList<>() : transaction.getRequests();
        requests.add(request);
        transaction.setRequests(requests);
        transactionRepository.save(transaction);
    }


}
