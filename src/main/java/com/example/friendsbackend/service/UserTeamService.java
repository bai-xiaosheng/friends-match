package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author BDS
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service
* @createDate 2024-05-22 21:22:37
*/
public interface UserTeamService extends IService<UserTeam> {

    /**
     * 查找用户加入的队伍
     *
     * @param userId 用户id
     * @return 用户加入的队伍列表,同时还包含一个公共队伍id 0
     */
    List<String> findTeamList(Long userId);
}
