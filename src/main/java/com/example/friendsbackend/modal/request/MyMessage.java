package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MyMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    // 接收信息用户id
    private String recUserId;
    // 发送方信息用户id
    private String sendUserId;
    // 发送方用户id
    private String userName;
    // 消息内容
    private String content;
    // 房间/队伍id
    private String roomId;
    //消息类型  1 代表单聊 2 代表群聊
    private int mode;
    // 发送消息的时间
    private Date sendTime;


    @Override
    public String toString() {
        return "MyMessage{" +
                "recUserId='" + recUserId + '\'' +
                ", sendUserId='" + sendUserId + '\'' +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", roomId='" + roomId + '\'' +
                ", mode='" + mode + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}

