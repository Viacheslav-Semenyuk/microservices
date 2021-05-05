package com.smartfoxpro.userservice.exception;

import com.smartfoxpro.userservice.activemq.SendMessage;
import com.smartfoxpro.userservice.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionRollbackHandler {

    @Autowired
    private Transaction transaction;

    @Autowired
    private SendMessage sendMessage;

    @ExceptionHandler(Throwable.class)
    public void handle(Throwable e) {
        sendMessage.send("user-rollback-error-queue", transaction.getTx_id());
    }
}
