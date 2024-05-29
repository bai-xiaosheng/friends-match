package com.example.friendsbackend.modal.enums;

import java.util.Objects;

public enum TeamStatusEnum {
    PUBIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");

    private Integer status;

    private String text;

    public static TeamStatusEnum getEnumByStatus(Integer status){
        if (status == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values){
            if (Objects.equals(teamStatusEnum.getStatus(), status)){
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
