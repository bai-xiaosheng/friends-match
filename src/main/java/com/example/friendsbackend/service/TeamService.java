package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.*;
import com.example.friendsbackend.modal.vo.TeamUserVo;
import com.example.friendsbackend.modal.vo.TeamVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

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
    List<TeamVo> listTeam(TeamQueryRequest teamQueryRequest, Boolean isAdmin);

    /**
     * 用户加入队伍
     * @param joinTeamRequest 用户请求加入队伍的信息
     * @param loginUser 登录用户信息
     * @return 是否加入成功
     */
    Boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);

    /**
     * 用户退出队伍
     * @param teamId 退出队伍的id
     * @param loginUser 用户登录信息
     * @return 退出是否成功
     */
    Boolean quitTeam(Long teamId, User loginUser);

    /**
     * 删除队伍
     * @param teamDeleteRequest 队伍删除信息
     * @param loginUser 用户登录信息
     * @return 队伍是否删除
     */
    Boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser);

    /**
     * 根据队伍id查询队伍
     *
     * @param teamId 队伍id
     * @param request http请求
     * @return 查询到的队伍信息
     */
    TeamVo getUsersByTeamId(Long teamId, HttpServletRequest request);

    /**
     * 解散队伍
     *
     * @param teamId 队伍id
     * @param request 登录信息
     * @return 是否成功
     */
    boolean dissolutionTeam(Long teamId, HttpServletRequest request);

    /**
     * 根据用户加入的队伍id获取队伍信息
     *
     * @param teamId 队伍id
     * @param request 登录信息
     * @return 队伍信息
     */
    TeamUserVo getTeamListByTeamIds(Set<Long> teamId, HttpServletRequest request);

    /**
     * 根据内容查询队伍
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    TeamUserVo teamQuery(TeamQueryRequest teamQueryRequest, HttpServletRequest request);

    /**
     * 去除过期和重复的队伍，结果返回值为set
     *
     * @param teamList 队伍列表
     * @return set
     */
    TeamUserVo teamSet(List<Team> teamList);

    /**
     * 剔除队员
     *
     * @param kickOutUserRequest  队伍id，队员id
     * @param loginUser 当前登录用户
     * @return 是否成功剔除
     */
    Boolean kickOutTeamByUserId(KickOutUserRequest kickOutUserRequest, User loginUser);

    /**
     * 转移队伍队长
     *
     * @param transferTeamRequest 转移队长信息
     * @param loginUser 当前登录用户
     * @return 是否转移成功 true - 转移成功
     */
    Boolean transferTeam(TransferTeamRequest transferTeamRequest, User loginUser);

    /**
     * @return set（队伍信息）
     */
    TeamUserVo getTeams();
}
