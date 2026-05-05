package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Client {
    private Long id;
    private String nume;
    private String prenume;
    private PhoneNumber homePhone;
    private String address;
    private String city;
    private String email;
    private PhoneNumber cellphone;
}
