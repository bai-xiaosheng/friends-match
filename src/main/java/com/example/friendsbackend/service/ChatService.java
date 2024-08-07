package com.example.friendsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.domain.Chat;
import com.example.friendsbackend.modal.request.MyMessage;

import java.util.List;

/**
* @author BDS
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2024-08-07 16:45:44
*/

public interface ChatService extends IService<Chat> {

    /**
     * 保存用户间传输的信息
     *
     * @param myMessage 消息传输类
     * @param isRead 是否已读
     */
    void saveMessage(MyMessage myMessage, int isRead);

    /**
     * 读取未读信息
     *
     * @param userId 接受消息的用户id
     * @return 当前用户未读的消息
     */
    List<MyMessage> findUnreadMessage(Long userId);

    /**
     * 更新未读信息为已读
     *
     * @param myMessage 消息体
     */
    void updateReadMessage(MyMessage myMessage);
}
