package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;


@Data
public class TransferTeamRequest implements Serializable {
    private static final long serialVersionUID = -6119912852151581286L;
    // 转交给用户的账号
    private String userAccount;
    // 队伍id
    private Long teamId;
}
