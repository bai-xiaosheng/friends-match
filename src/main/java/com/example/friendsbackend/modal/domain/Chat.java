package com.example.friendsbackend.modal.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息表
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat implements Serializable {
    /**
     * 聊天记录id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息id
     */
    private Long sendUserId;

    /**
     * 接收消息id
     */
    private Long recUserId;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 发送方名称
     */
    private String sendUserName;

    /**
     * 聊天类型 1-私聊 2-群聊
     */
    private Integer chatType;

    /**
     * 是否已读 1-已读 0-未读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date sendTime;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 要发送信息的队伍id
     */
    private Long teamId;

    /**
     * 逻辑删除 0-正常 1-删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}