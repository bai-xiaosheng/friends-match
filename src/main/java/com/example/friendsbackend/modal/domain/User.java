package com.example.friendsbackend.modal.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户名称
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
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;
    /**
     * 添加的好友
     */
    private String friendsIds;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 用户更新时间
     */
    private Date updateTime;
    /**
     * 最后登录时间
     */
    private Date lastTime;

    /**
     * 是否删除（逻辑删除） 0-正常 1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户权限 0-普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 是否为会员 0-普通用户 1-vip 2-svip
     */
    private String vipState;

    /**
     * tags 用户标签
     */
    private String tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}