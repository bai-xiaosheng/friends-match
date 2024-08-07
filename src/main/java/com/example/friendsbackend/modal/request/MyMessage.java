package com.example.friendsbackend.modal.request;

import java.io.Serializable;
import java.util.Date;

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

    public String getRecUserId() {
        return recUserId;
    }

    public void setRecUserId(String recUserId) {
        this.recUserId = recUserId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

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

