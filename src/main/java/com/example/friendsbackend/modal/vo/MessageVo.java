package com.example.friendsbackend.modal.vo;

import com.example.friendsbackend.modal.vo.WebSocketVo;
import lombok.Data;

import java.io.Serializable;


@Data
public class MessageVo implements Serializable {


    private static final long serialVersionUID = 7574976971770905699L;
    // 接收信息用户id
    private WebSocketVo formUser;
    private WebSocketVo toUser;
    private Long teamId;
    private String text;
    private Boolean isMy = false;
    private Integer chatType;
    private Boolean isAdmin = false;
    private String createTime;

}

