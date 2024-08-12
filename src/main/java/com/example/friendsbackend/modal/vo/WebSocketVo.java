package com.example.friendsbackend.modal.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebSocketVo implements Serializable {

    private static final long serialVersionUID = 8357981780226590862L;

    private long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userUrl;
}
