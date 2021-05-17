package com.smartfoxpro.userservice.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;


@Getter
@Setter
@ToString
public class Request {

    private String service;

    private String endpoint;

    private String method;

    private Map<String, String> request;

    private Boolean exist;

    private Map<String, String> oldEntity;

}
