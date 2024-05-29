package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.*;
import com.example.friendsbackend.modal.vo.TeamUserVo;

import java.util.List;

/**
* @author BDS
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-05-22 21:22:15
*/
public interface TeamService extends IService<Team> {

    /**
     * 更新队伍信息
     * @param teamUpdateRequest 队伍更新信息
     * @param loginUser 用户登录信息
     * @return 更新是否成功
     */
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 创建队伍
     * @param teamCreateRequest 创建队伍信息
     * @param loginUser 登录信息
     * @return 队伍 id
     */
    public long createTeam(TeamCreateRequest teamCreateRequest, User loginUser);

    /**
     * 查询符合条件的队伍
     * @param teamQueryRequest 队伍查询条件信息
     * @param isAdmin 是否为管理员
     * @return 符合查询条件的队伍
     */
    List<TeamUserVo> listTeam(TeamQueryRequest teamQueryRequest, Boolean isAdmin);

    /**
     * 用户加入队伍
     * @param joinTeamRequest 用户请求加入队伍的信息
     * @param loginUser 登录用户信息
     * @return 是否加入成功
     */
    Boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);

    /**
     * 用户退出队伍
     * @param quitTeamRequest 退出信息
     * @param loginUser 用户登录信息
     * @return 退出是否成功
     */
    Boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser);

    /**
     * 删除队伍
     * @param teamDeleteRequest 队伍删除信息
     * @param loginUser 用户登录信息
     * @return 队伍是否删除
     */
    Boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser);
}
