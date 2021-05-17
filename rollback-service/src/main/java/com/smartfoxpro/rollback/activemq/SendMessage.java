package com.smartfoxpro.rollback.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SendMessage {

    @Autowired
    private  JmsMessagingTemplate jmsMessagingTemplate;

    public void sendList(String nameQueue, List msg) {
        jmsMessagingTemplate.convertAndSend(nameQueue, msg);
    }

}
