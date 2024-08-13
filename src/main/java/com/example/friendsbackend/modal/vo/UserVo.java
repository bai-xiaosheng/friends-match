package com.example.friendsbackend.modal.vo;

import lombok.Data;

@Data
public class UserVo {

    /**
     * 账户名称
     */
    private String userName;

    /**
     * 头像
     */
    private String userUrl;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 性别 0-女 1-男
     */
    private Integer gender;



    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 添加的好友
     */
    private String friendsIds;

}
