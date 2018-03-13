package com.acme.helper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RegistrationData {

    private String firstName;
    private String lastName;
    private String mail;
    private String password;

}
