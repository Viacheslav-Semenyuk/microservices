package com.smartfoxpro.rollback.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Transaction {

    @Id
    @NonNull
    private String id;

    private List<Request> requests;
}
