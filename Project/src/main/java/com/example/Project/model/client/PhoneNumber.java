package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhoneNumber {
    private CountryCode code;
    private String number;
}
