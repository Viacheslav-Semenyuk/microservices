package com.smartfoxpro.userservice.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
public class SendMessage {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void send(String nameQueue, String msg) {
        jmsMessagingTemplate.convertAndSend(nameQueue, msg);
    }

}
