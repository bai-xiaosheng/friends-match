package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 6348183686007560011L;
    private String userAccount;
    private String userPassword;

}
