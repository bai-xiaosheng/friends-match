package com.example.friendsbackend.modal.vo;

import lombok.Data;

import java.util.Date;
@Data
public class TeamUserVo {
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 已加入队伍人数
     */
    private Integer hasJoinNum;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 队长信息
     */
    private UserVo userVo;

    /**
     * 当前用户是否加入队伍
     */
    private boolean hasJoin = false;
}
