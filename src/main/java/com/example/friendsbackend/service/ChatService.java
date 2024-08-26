package com.example.friendsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.domain.Chat;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.ChatRequest;
import com.example.friendsbackend.modal.vo.MessageVo;

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
    void saveMessage(MessageVo myMessage, int isRead);

    /**
     * 读取未读信息
     *
     * @param userId 接受消息的用户id
     * @return 当前用户未读的消息
     */
    List<MessageVo> findUnreadMessage(Long userId);

    /**
     * 更新未读信息为已读
     *
     * @param myMessage 消息体
     */
    void updateReadMessage(MessageVo myMessage);

    /**
     * 查询私聊聊天记录
     *
     * @param chatRequest 队伍id和聊天用户的id
     * @param privateChat 类型：私聊
     * @param loginUser 登录用户
     * @return list
     */
    List<MessageVo> getPrivateChat(ChatRequest chatRequest, int privateChat, User loginUser);

    /**
     * 大模型历史消息
     *
     * @param loginUser 登录用户
     * @return 登录用户与大模型历史聊天信息
     */
    List<MessageVo> getAiLastChat(User loginUser);

    /**
     * 删除缓存中的key
     *
     * @param key redisKey
     * @param id 大厅聊天是只有key（CACHE_CHAT_HALL）,其它的聊天都为key + id
     */
    void deleteKey(String key, String id);

    /**
     * 查询大厅聊天记录
     *
     * @param hallChat 大厅聊天
     * @param loginUser 登录用户
     * @return list（发送者信息（用户名称，用户账号，用户头像），接收方用户信息，发送内容，发送类型，是否为登陆者发送的信息，是否为管理员，发送时间
     */
    List<MessageVo> getHallChat(int hallChat, User loginUser);

    /**
     * 查询队伍聊天记录
     *
     * @param chatRequest 队伍id，用户id
     * @param teamChat 聊天类型：队伍聊天
     * @param loginUser 登录用户
     * @return list（发送者信息（用户名称，用户账号，用户头像），发送内容，发送类型，是否为登陆者发送的信息，是否为管理员，发送时间
     */
    List<MessageVo> getTeamChat(ChatRequest chatRequest, int teamChat, User loginUser);

    /**
     * 利用大模型获取当前用户问题的答案
     *
     * @param loginUser 登录用户
     * @return 大模型答案
     */
    List<MessageVo> getAiAnswer(ChatRequest chatRequest, User loginUser);
}
