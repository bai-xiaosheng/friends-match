package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class MessageRequest implements Serializable {
    private static final long serialVersionUID = -2243742213514523257L;
    /**
     * 发送给对方的id
     */
    private Long toId;

    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 发送内容
     */
    private String text;
    /**
     * 聊天类型 1 - 私聊   2 - 队伍内聊天  3 - 大厅内聊天
     */
    private Integer chatType;
    /**
     * 是否为管理员
     */
    private boolean isAdmin;
}
