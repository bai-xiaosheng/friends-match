package com.example.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.modal.domain.UserTeam;
import com.example.friendsbackend.service.UserTeamService;
import com.example.friendsbackend.mapper.UserTeamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author BDS
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-05-22 21:22:37
*/
@Service
@Slf4j
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public List<String> findTeamList(Long userId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("teamId");
        queryWrapper.eq("userId",userId);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
//        List<UserTeam> userTeamss = this.list(queryWrapper);
        List<String> roomIdList = new ArrayList<>();
        roomIdList.add("0");
        if (!userTeams.isEmpty()){
            for (UserTeam userTeam : userTeams){
                roomIdList.add(String.valueOf(userTeam.getTeamId()));
            }
        }
        return roomIdList;
    }
}




