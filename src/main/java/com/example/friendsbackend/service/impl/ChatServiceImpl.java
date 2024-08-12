package com.example.friendsbackend.service.impl;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.vo.WebSocketVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.mapper.ChatMapper;
import com.example.friendsbackend.modal.domain.Chat;

import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.ChatRequest;
import com.example.friendsbackend.modal.request.MessageVo;
import com.example.friendsbackend.service.ChatService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.example.friendsbackend.constant.ChatConstant.*;

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
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,List<MessageVo>> redisTemplate;

    /**
     * 获取双方私聊聊天记录
     *
     * @param chatRequest 队伍id，接收方用户id
     * @param chatType 聊天类型，私聊
     * @param loginUser 登录用户
     * @return list（发送方用户（用户账号，用户名称，用户头像），接收方用户，发送内容，是否为自己，是否为管理员，消息创建时间，队伍id，聊天类型）
     */
    @Override
    public List<MessageVo> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser) {
        if (chatRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        if (chatRequest.getToId() == null){
            throw new BusinessException(Code.PARAMS_ERROR,"对方状态异常，请重试");
        }
        List<MessageVo> messageVos = getCache(CACHE_CHAT_PRIVATE, loginUser.getId() + "to" + chatRequest.getToId());
        if (messageVos != null && !messageVos.isEmpty()){
            saveCache(CACHE_CHAT_PRIVATE,loginUser.getId() + "to" + chatRequest.getToId(), messageVos);
            return messageVos;
        }
        // 从数据库中查询，注意分别查询当前用户为发送方，request.getId()为发送方两种情况
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sendUserId", loginUser.getId());
        queryWrapper.eq("recUserId", chatRequest.getToId());
        List<Chat> sendChats = chatMapper.selectList(queryWrapper);
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sendUserId", chatRequest.getToId());
        queryWrapper.eq("recUserId", loginUser.getId());
        List<Chat> recChats = chatMapper.selectList(queryWrapper);
        // 转换查询结果的格式
        List<MessageVo> messageVoList = new ArrayList<>(chatListToMessageVoList(sendChats, loginUser));
        messageVoList.addAll(chatListToMessageVoList(recChats, PRIVATE_CHAT, loginUser));
        saveCache(CACHE_CHAT_PRIVATE,loginUser.getId() + "to" + chatRequest.getToId(),messageVoList);
        return messageVoList;
    }

    @Override
    public void deleteKey(String key, String id) {
        if (key.equals(CACHE_CHAT_HALL)){
            redisTemplate.delete(key);
        }else {
            redisTemplate.delete(key + id);
        }
    }

    @Override
    public List<MessageVo> getHallChat(int hallChat, User loginUser) {
        if (hallChat != HALL_CHAT){
            throw new BusinessException(Code.PARAMS_ERROR,"不是大厅聊天");
        }
        // 从缓存中读取大厅聊天记录
        List<MessageVo> messageVos = getCache(CACHE_CHAT_HALL, String.valueOf(loginUser.getId()));
        if (messageVos != null && !messageVos.isEmpty()){
            saveCache(CACHE_CHAT_HALL,String.valueOf(loginUser.getId()),messageVos);
            return messageVos;
        }
        // 如果缓存中没有数据，从数据库中读取大厅聊天数据
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chatType",HALL_CHAT);
        List<Chat> chats = chatMapper.selectList(queryWrapper);
        List<MessageVo> messageVoList = chatListToMessageVoList(chats, HALL_CHAT, loginUser);
        saveCache(CACHE_CHAT_HALL, String.valueOf(loginUser.getId()),messageVoList);
        return messageVoList;
    }

    @Override
    public List<MessageVo> getTeamChat(ChatRequest chatRequest, int teamChat, User loginUser) {
        if (chatRequest.getTeamId() == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数有误");
        }
        // 从缓存中读取数据
        List<MessageVo> messageVos = getCache(CACHE_CHAT_TEAM, String.valueOf(loginUser.getId()));
        if (messageVos != null && !messageVos.isEmpty()){
            saveCache(CACHE_CHAT_TEAM, String.valueOf(loginUser.getId()), messageVos);
            return messageVos;
        }
        // 从数据库中读取数据
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",chatRequest.getTeamId());
        List<Chat> chats = chatMapper.selectList(queryWrapper);
        List<MessageVo> messageVoList = chatListToMessageVoList(chats, TEAM_CHAT, loginUser);
        saveCache(CACHE_CHAT_TEAM, String.valueOf(loginUser.getId()), messageVoList);
        return messageVoList;
    }


    public List<MessageVo> chatListToMessageVoList(List<Chat> chatList, int chatType, User loginUser){
        return chatList.stream().map(chat -> {
            MessageVo messageVo = new MessageVo();
            User sendUser = userMapper.selectById(chat.getSendUserId());
            WebSocketVo sendWebSocket = new WebSocketVo();
            BeanUtils.copyProperties(sendUser,sendWebSocket);
            messageVo.setFormUser(sendWebSocket);
            // 如果是私聊，包含接受用户信息，如果是大厅聊天或者队伍聊天，不包含接收方用户信息
            if (chatType == PRIVATE_CHAT) {
                User recUser = userMapper.selectById(chat.getRecUserId());
                WebSocketVo recWebSocket = new WebSocketVo();
                BeanUtils.copyProperties(recUser,recWebSocket);
                messageVo.setToUser(recWebSocket);
            }

            messageVo.setTeamId(chat.getTeamId());
            messageVo.setText(chat.getContent());
            messageVo.setIsMy(loginUser.getId() == chat.getSendUserId());
            messageVo.setChatType(chat.getChatType());
            if (sendUser.getUserRole() == 1){
                messageVo.setIsAdmin(true);
            }else {
                messageVo.setIsAdmin(false);
            }
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            messageVo.setCreateTime(ft.format(new Date()));
            return messageVo;
        }).collect(Collectors.toList());
    }

    public List<MessageVo> chatListToMessageVoList(List<Chat> chatList, User loginUser){
        return chatList.stream().map(chat -> {
            MessageVo messageVo = new MessageVo();
            // 将用户信息转换为 WebSocketVo 信息
            User sendUser = userMapper.selectById(chat.getSendUserId());
            User recUser = userMapper.selectById(chat.getRecUserId());
            WebSocketVo sendWebSocket = new WebSocketVo();
            WebSocketVo recWebSocket = new WebSocketVo();
            BeanUtils.copyProperties(sendUser,sendWebSocket);
            BeanUtils.copyProperties(recUser,recWebSocket);
            messageVo.setFormUser(sendWebSocket);
            messageVo.setToUser(recWebSocket);
            messageVo.setTeamId(chat.getTeamId());
            messageVo.setText(chat.getContent());
            messageVo.setIsMy(loginUser.getId() == chat.getSendUserId());
            messageVo.setChatType(chat.getChatType());
            if (sendUser.getUserRole() == 1){
                messageVo.setIsAdmin(true);
            }else {
                messageVo.setIsAdmin(false);
            }
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            messageVo.setCreateTime(ft.format(new Date()));
            return messageVo;
        }).collect(Collectors.toList());
    }

    public List<MessageVo> getCache(String key, String id){
        ValueOperations<String, List<MessageVo>> stringListValueOperations = redisTemplate.opsForValue();
        List<MessageVo> messageVos;
        if (key.equals(CACHE_CHAT_HALL)){
            messageVos = stringListValueOperations.get(key);
        }else {
            messageVos = stringListValueOperations.get(key + id);
        }
        return messageVos;
    }

    public void saveCache(String key, String id, List<MessageVo> messageVos){
        try {
            ValueOperations<String, List<MessageVo>> stringListValueOperations = redisTemplate.opsForValue();
            // 解决缓存雪崩
//        int i = RandomUtils.
            if (key.equals(CACHE_CHAT_HALL)){
                stringListValueOperations.set(key,messageVos);
            }else {
                stringListValueOperations.set(key + id,messageVos);
            }
        } catch (Exception e) {
            log.error("saveCache error",e);
        }
    }

// 暂时抛弃的方法

    @Override
    public void saveMessage(MessageVo myMessage, int isRead) {
//        try {
//            Chat chat = new Chat();
//            chat.setSendUserId(Long.valueOf(myMessage.getSendUserId()));
//            chat.setRecUserId(Long.valueOf(myMessage.getRecUserId()));
//            chat.setContent(myMessage.getContent());
//            chat.setChatType(myMessage.getMode());
//            chat.setIsRead(isRead);
//            chat.setSendTime(new Date());
//            chat.setDeleteTime(null);
//            chat.setSendUserName(myMessage.getUserName());
//            chat.setTeamId(Long.valueOf(myMessage.getRoomId()));
//            this.save(chat);
//        }catch (Exception e){
//            log.error("saveMessage error", e);
//        }

    }

    @Override
    public List<MessageVo> findUnreadMessage(Long userId) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("recUserId",userId);
        queryWrapper.eq("isRead",0);
        List<Chat> chatMessages = chatMapper.selectList(queryWrapper);
        List<MessageVo> myMessages = new ArrayList<>();
        for (Chat chatMessage : chatMessages){
            MessageVo myMessage = new MessageVo();
            myMessage.setFormUser(new WebSocketVo());
            myMessage.setToUser(new WebSocketVo());
            myMessage.setTeamId(0L);
            myMessage.setText("");
            myMessage.setIsMy(false);
            myMessage.setChatType(0);
            myMessage.setIsAdmin(false);
            myMessage.setCreateTime("");

//            myMessage.setRecUserId(String.valueOf(userId));
//            myMessage.setSendUserId(String.valueOf(chatMessage.getSendUserId()));
//            myMessage.setUserName(chatMessage.getSendUserName());
//            myMessage.setContent(chatMessage.getContent());
//            myMessage.setRoomId(String.valueOf(chatMessage.getTeamId()));
//            myMessage.setMode(chatMessage.getChatType());
//            myMessage.setSendTime(chatMessage.getSendTime());
            myMessages.add(myMessage);

            // 更新未读消息为已读
            chatMessage.setIsRead(1);
            chatMapper.updateById(chatMessage);
        }
        return myMessages;
    }

    @Override
    public void updateReadMessage(MessageVo myMessage) {
//        Chat chat = new Chat();
    }
}




