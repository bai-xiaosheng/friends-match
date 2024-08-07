package com.example.friendsbackend.config;

import com.example.friendsbackend.controller.ChatWebSocketController;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.service.ChatService;
import com.example.friendsbackend.service.UserTeamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Resource
    public void setUserTeamService(UserTeamService userTeamService){
        ChatWebSocketController.userTeamService = userTeamService;
    }

    @Resource
    public void setUserMapper(UserMapper userMapper){
        ChatWebSocketController.userMapper = userMapper;
    }

    @Resource
    public void setChatService(ChatService chatService){
        ChatWebSocketController.chatService = chatService;
    }
}
