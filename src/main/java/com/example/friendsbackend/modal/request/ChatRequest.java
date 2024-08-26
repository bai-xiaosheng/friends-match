package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRequest implements Serializable {
    private static final long serialVersionUID = 2551900122743807920L;

    /**
     * 队伍聊天室id
     */
    private Long teamId;

    /**
     * 接收消息id
     */
    private Long toId;

    /**
     * 发送的内容
     */
    private String text;
}
