package com.example.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.modal.domain.Team;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.domain.UserTeam;
import com.example.friendsbackend.modal.dto.TeamQuery;
import com.example.friendsbackend.modal.request.*;
import com.example.friendsbackend.modal.vo.TeamUserVo;
import com.example.friendsbackend.modal.vo.TeamVo;
import com.example.friendsbackend.service.TeamService;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.TryCastExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 用户控制
 *
 * @author BDS
 */
@RestController
@RequestMapping("/team")
//@CrossOrigin(origins = {"http://localhost:3000"})
//@CrossOrigin(origins = "http://81.70.22.11:3000", allowCredentials = "true")
@Slf4j
//@Api(tags = "用户中心")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/create")
    public BaseResponse<Long> teamCreate(@RequestBody TeamCreateRequest teamCreateRequest, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (teamCreateRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        long teamId = teamService.createTeam(teamCreateRequest, loginUser);
        return ResultUtils.success(teamId);
    }

    @GetMapping("/{teamId}")
    public BaseResponse<TeamVo> getUsersByTeamId(@PathVariable("teamId") Long teamId, HttpServletRequest request) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(Code.PARAMS_ERROR, "输入队伍id有误");
        }
        TeamVo teams = teamService.getUsersByTeamId(teamId, request);
        return ResultUtils.success(teams);
    }
//    @GetMapping("/teams")
//    public BaseResponse<List<TeamVo>> teamList(TeamQueryRequest teamQueryRequest,HttpServletRequest request){
//        // 判断当前用户是否为管理员，没有登陆或者不是管理员返回false
//        boolean isAdmin = userService.isAdmin(request);
//        List<TeamVo> teamUserVos = teamService.listTeam(teamQueryRequest,isAdmin);
//        // 判断当前用户是否加入队伍
//        try {
//            User loginUser = userService.getLoginUser(request);
//            // 获取当前推荐的队伍 id
//            List<Long> teamIdList = teamUserVos.stream().map(TeamVo::getId).collect(Collectors.toList());
//            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//            userTeamQueryWrapper.eq("userId",loginUser.getId());
//            userTeamQueryWrapper.in("teamId",teamIdList);
//            // 根据队伍 id 和个人 id 查询用户队伍
//            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
//            // 得到用户加入的推荐队伍的 id
//            Set<Long> hasJoinTeamList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
////            teamUserVos.forEach(teamVo -> {
////                // 如果当前队伍用户已加入，将 hasJoin设置为 true
////                boolean hasJoin  = hasJoinTeamList.contains(teamVo.getId());
////                teamVo.setHasJoin(hasJoin);
////            });
//        } catch (Exception ignored) {
//        }
//
//        return ResultUtils.success(teamUserVos);
//    }
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> teamListPage(TeamQueryRequest teamQueryRequest){
        if (teamQueryRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryRequest,team);
        Page<Team> page = new Page<>(teamQueryRequest.getPageNum(),teamQueryRequest.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(teamPage);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        //修改
        Boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        return ResultUtils.success(result);
    }
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        if (joinTeamRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(joinTeamRequest,loginUser);
        return ResultUtils.success(result);
    }
    @PostMapping("/{teamId}")
    public BaseResponse<Boolean> dissolutionByTeamId(@PathVariable("teamId") Long teamId, HttpServletRequest request) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(Code.NO_AUTH, "队伍不存在");
        }
        boolean dissolutionTeam = teamService.dissolutionTeam(teamId, request);
        return ResultUtils.success(dissolutionTeam);
    }
    @GetMapping("/teamsByIds")
    public BaseResponse<TeamUserVo> getTeamListByTeamIds(@RequestParam(required = false) Set<Long> teamId, HttpServletRequest request) {
        if (teamId.isEmpty()) {
            throw new BusinessException(Code.PARAMS_ERROR, "队伍id为空");
        }
        TeamUserVo teams = teamService.getTeamListByTeamIds(teamId, request);
        return ResultUtils.success(teams);
    }

    @PostMapping("/quit/{teamId}")
    public BaseResponse<Boolean> quitTeam(@PathVariable("teamId") Long teamId,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (teamId == null || teamId == 0){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        Boolean result = teamService.quitTeam(teamId,loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (teamDeleteRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        Boolean result = teamService.deleteTeam(teamDeleteRequest,loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamVo>> listMyJoinTeam(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        // 从用户队伍表中查询当前用户加入的队伍id
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        List<Long> idList = userTeamList.stream()
                .map(UserTeam::getTeamId)
                .collect(Collectors.toList());
        TeamQueryRequest teamQueryRequest = new TeamQueryRequest();
        teamQueryRequest.setIdList(idList);
        List<TeamVo> teamVos = teamService.listTeam(teamQueryRequest, true);
        return ResultUtils.success(teamVos);
    }

    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamVo>> listMyCreateTeam(TeamQueryRequest teamQueryRequest,HttpServletRequest request){
        if (teamQueryRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQueryRequest.setUserId(loginUser.getId());
        List<TeamVo> teamUserVos = teamService.listTeam(teamQueryRequest, true);
        return ResultUtils.success(teamUserVos);
    }


    @GetMapping("/search/id")
    public BaseResponse<Team> searchById(@RequestParam long id){
        if (id < 0){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        return ResultUtils.success(team);
    }

    @PostMapping("/search")
    public BaseResponse<TeamUserVo> teamQuery(@RequestBody TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR, "请输入正确信息");
        }
        TeamUserVo teams = teamService.teamQuery(teamQueryRequest, request);
        return ResultUtils.success(teams);
    }
    @GetMapping("/teams")
    public BaseResponse<TeamUserVo> getTeams(){
        TeamUserVo teams = teamService.getTeams();
        return ResultUtils.success(teams);
    }

    @PostMapping("/kickOutUser")
    public BaseResponse<Boolean> kickOutTeamByUserId(@RequestBody KickOutUserRequest kickOutUserRequest, HttpServletRequest request) {
        if (kickOutUserRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR, "参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean kickOut = teamService.kickOutTeamByUserId(kickOutUserRequest, loginUser);
        return ResultUtils.success(kickOut);
    }

    @PostMapping("/transfer")
    public BaseResponse<Boolean> transferTeam(@RequestBody TransferTeamRequest transferTeamRequest, HttpServletRequest request) {
        if (transferTeamRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR, "参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean transferTeam = teamService.transferTeam(transferTeamRequest, loginUser);
        return ResultUtils.success(transferTeam);
    }
}
