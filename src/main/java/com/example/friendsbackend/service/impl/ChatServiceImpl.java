package com.example.friendsbackend.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.mapper.ChatMapper;
import com.example.friendsbackend.modal.domain.Chat;

import com.example.friendsbackend.modal.request.MyMessage;
import com.example.friendsbackend.service.ChatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author BDS
* @description 针对表【chat(聊天消息表)】的数据库操作Service实现
* @createDate 2024-08-07 16:45:44
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService {
    @Resource
    private ChatMapper chatMapper;

    @Override
    public void saveMessage(MyMessage myMessage, int isRead) {
        try {
            Chat chat = new Chat();
            chat.setSendUserId(Long.valueOf(myMessage.getSendUserId()));
            chat.setRecUserId(Long.valueOf(myMessage.getRecUserId()));
            chat.setContent(myMessage.getContent());
            chat.setChatType(myMessage.getMode());
            chat.setIsRead(isRead);
            chat.setSendTime(new Date());
            chat.setDeleteTime(null);
            chat.setSendUserName(myMessage.getUserName());
            chat.setTeamId(Long.valueOf(myMessage.getRoomId()));
            this.save(chat);
        }catch (Exception e){
            log.error("saveMessage error", e);
        }

    }

    @Override
    public List<MyMessage> findUnreadMessage(Long userId) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("recUserId",userId);
        queryWrapper.eq("isRead",0);
        List<Chat> chatMessages = chatMapper.selectList(queryWrapper);
        List<MyMessage> myMessages = new ArrayList<>();
        for (Chat chatMessage : chatMessages){
            MyMessage myMessage = new MyMessage();
            myMessage.setRecUserId(String.valueOf(userId));
            myMessage.setSendUserId(String.valueOf(chatMessage.getSendUserId()));
            myMessage.setUserName(chatMessage.getSendUserName());
            myMessage.setContent(chatMessage.getContent());
            myMessage.setRoomId(String.valueOf(chatMessage.getTeamId()));
            myMessage.setMode(chatMessage.getChatType());
            myMessage.setSendTime(chatMessage.getSendTime());
            myMessages.add(myMessage);

            // 更新未读消息为已读
            chatMessage.setIsRead(1);
            chatMapper.updateById(chatMessage);
        }
        return myMessages;
    }

    @Override
    public void updateReadMessage(MyMessage myMessage) {
//        Chat chat = new Chat();
    }
}




