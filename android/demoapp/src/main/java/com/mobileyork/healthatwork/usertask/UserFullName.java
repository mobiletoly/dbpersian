package com.mobileyork.healthatwork.usertask;


import java.io.Serializable;

public class UserFullName implements Serializable
{
    private String firstName;
    private String middleName;
    private String lastName;

    public UserFullName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }
}
