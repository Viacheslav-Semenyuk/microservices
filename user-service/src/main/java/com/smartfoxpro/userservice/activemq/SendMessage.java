package com.smartfoxpro.userservice.activemq;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
public class SendMessage {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendJsonNode(String nameQueue, ObjectNode msg) {
        jmsMessagingTemplate.convertAndSend(nameQueue, msg);
    }

    public void sendString(String nameQueue, String msg) {
        jmsMessagingTemplate.convertAndSend(nameQueue, msg);
    }
}
