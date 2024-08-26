package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegister implements Serializable {

    private static final long serialVersionUID = 3079242307089595915L;

    private String username;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
//    private String plantId;
}
