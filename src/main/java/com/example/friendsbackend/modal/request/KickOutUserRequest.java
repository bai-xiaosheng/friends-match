package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;


@Data
public class KickOutUserRequest implements Serializable {
    private static final long serialVersionUID = 5733340462955698625L;
    Long teamId;
    Long userId;
}
