package com.example.friendsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.modal.domain.UserTeam;
import com.example.friendsbackend.service.UserTeamService;
import com.example.friendsbackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author BDS
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-05-22 21:22:37
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




