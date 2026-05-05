package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Client {
    private Long id;
    private String nume;
    PhoneNumber homePhone;
    String adress;
    String city;
    String email;
    PhoneNumber cellphone;
}
