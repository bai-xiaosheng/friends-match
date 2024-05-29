package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class JoinTeamRequest implements Serializable {


    private static final long serialVersionUID = -5247551090319390342L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
