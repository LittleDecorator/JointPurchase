package com.acme.helper;

public class RegistrationData {

    private String firstName;
    private String lastName;
    private String mail;
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "RegistrationData{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", mail='" + mail + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
}
