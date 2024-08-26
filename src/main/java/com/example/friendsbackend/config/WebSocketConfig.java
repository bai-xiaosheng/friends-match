package com.example.friendsbackend.config;

import com.example.friendsbackend.service.TeamService;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.ws.WebSocket;
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
        WebSocket.userTeamService = userTeamService;
    }

    @Resource
    public void setUserMapper(UserMapper userMapper){
        WebSocket.userMapper = userMapper;
    }

    @Resource
    public void setChatService(ChatService chatService){
        WebSocket.chatService = chatService;
    }

    @Resource
    public void setUserService(UserService userService){WebSocket.userService = userService;}

    @Resource
    public void setTeamService(TeamService teamService){WebSocket.teamService = teamService;}
}
