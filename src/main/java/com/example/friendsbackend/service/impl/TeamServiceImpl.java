package com.example.friendsbackend.service.impl;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.mapper.UserTeamMapper;
import com.example.friendsbackend.modal.domain.Team;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.domain.UserTeam;
import com.example.friendsbackend.modal.enums.TeamStatusEnum;
import com.example.friendsbackend.modal.request.*;
import com.example.friendsbackend.modal.vo.TeamUserVo;
import com.example.friendsbackend.modal.vo.TeamVo;
import com.example.friendsbackend.modal.vo.UserVo;
import com.example.friendsbackend.service.TeamService;
import com.example.friendsbackend.mapper.TeamMapper;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author BDS
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-05-22 21:22:15
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        //需求：队长和管理员可以修改队伍信息（队伍名称、描述、最大人数、过期时间、状态）
        //实现：
        //1. 获取用户登录信息，判断用户是否为队长和管理员
        if (teamUpdateRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id < 0){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍id错误");
        }
        Team oldTeam = teamService.getById(id);
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(Code.NO_AUTH);
        }
        //2. 检查修改信息是否为空
        if (teamUpdateRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"修改信息为空");
        }
        //3. 依次检查需要修改的信息,队伍名称、描述、最大人数、过期时间、状态
        // a. 队伍名称
        String name = teamUpdateRequest.getName();
        if (StringUtils.isEmpty(name) && name.length() >= 20){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍名称格式错误");
        }
        // b. 描述
        String description = teamUpdateRequest.getDescription();
        if (!StringUtils.isEmpty(description) && description.length() > 512){
            throw new BusinessException(Code.PARAMS_ERROR,"描述格式错误");
        }
        // c.最大人数
        Integer maxNum = teamUpdateRequest.getMaxNum();
        if (maxNum != null && (maxNum < 1 || maxNum > 20)){
            throw new BusinessException(Code.PARAMS_ERROR,"最大人数格式错误");
        }
        // d.过期时间
        Date expireTime = teamUpdateRequest.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(Code.PARAMS_ERROR,"过期时间错误");
        }
        // e.状态
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByStatus(teamUpdateRequest.getStatus());
        if (statusEnum != null && statusEnum.getStatus() == 2){
            String password = teamUpdateRequest.getPassword();
            if (StringUtils.isEmpty(password) || password.length() > 10){
                throw new BusinessException(Code.PARAMS_ERROR,"设置密码错误");
            }
        }
        // 更新信息
        Team newTeam = new Team();

        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        return teamService.updateById(newTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createTeam(TeamCreateRequest teamCreateRequest, User loginUser) {
        //  a.判断输入参数是否为空
        if (teamCreateRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        //  b. 队伍名称必填，小于 20 个字符串
        String name = teamCreateRequest.getName();
        if (name == null || name.length() >= 20){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍名称格式错误");
        }
        //  c. 最大人数要设置，必须大于 1，小于 20.
        Integer maxNum = teamCreateRequest.getMaxNum();
        if (maxNum < 1 || maxNum >= 20){
            throw new BusinessException(Code.PARAMS_ERROR,"最大人数格式错误");
        }
        //  d. 描述不能超过 512 个字符
        String description = teamCreateRequest.getDescription();
        if (!StringUtils.isEmpty(description) && description.length() > 512){
            throw new BusinessException(Code.PARAMS_ERROR,"描述格式错误");
        }
        //  e. 持续时间要在当前时间之后
        Date expireTime = teamCreateRequest.getExpireTime();
        if (expireTime.before(new Date())){
            throw new BusinessException(Code.PARAMS_ERROR,"过期时间错误");
        }
        //  f. 队伍状态（int）必须大于 0，或者用枚举判断
        Integer status = Optional.ofNullable(teamCreateRequest.getStatus()).orElse(0);
        TeamStatusEnum enumByStatus = TeamStatusEnum.getEnumByStatus(status);
        if (enumByStatus == null){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍状态错误");
        }
        //  g. 如果设置为加密，加密密码不能超过 10 位
        String password = teamCreateRequest.getPassword();
        if (enumByStatus.getStatus() == 2 && password.length() > 10){
            throw new BusinessException(Code.PARAMS_ERROR,"设置密码错误");
        }
        //  h. 用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        long userId = loginUser.getId();
        queryWrapper.eq("userId",userId);
        long count = this.count(queryWrapper);
        if (count >= 5){
            throw new BusinessException(Code.PARAMS_ERROR,"创建队伍数目以达上限");
        }
        //写入数据，队伍表和用户队伍表都要写入
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setMaxNum(maxNum);
        team.setExpireTime(expireTime);
        team.setUserId(userId);
        team.setStatus(status);
        team.setPassword(password);
        // 写入队伍表i
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId < 0){
            throw new BusinessException(Code.SAVE_ERROR);
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result || userTeam.getId() < 0){
            throw new BusinessException(Code.SAVE_ERROR);
        }

        return teamId;
    }

    @Override
    public List<TeamVo> listTeam(TeamQueryRequest teamQueryRequest, Boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //获取用户输入的搜索条件
        if (teamQueryRequest != null){
            //  1. 队伍 id
            Long id = teamQueryRequest.getId();
            if (id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            // 1.5. 队伍 id 列表
            List<Long> idList = teamQueryRequest.getIdList();
            if (CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id",idList);
            }
            //2. 队伍名称 模糊匹配
            String name = teamQueryRequest.getName();
            if (name != null){
                queryWrapper.like("name",name);
            }
            //3. 最大人数 相等
            Integer maxNum = teamQueryRequest.getMaxNum();
            if (maxNum != null && maxNum > 1){
                queryWrapper.eq("maxNum",maxNum);
            }
            //4. 描述 模糊匹配
            String description = teamQueryRequest.getDescription();
            if (description != null){
                queryWrapper.like("description",description);
            }
            //5. 关键字 模糊匹配队伍名称和描述
            String keyWord = teamQueryRequest.getSearchText();
            if (keyWord != null){
                queryWrapper.like("name",keyWord).or(qt->qt.like("description",keyWord));
            }

            //6. 队伍状态 管理员和队长可以找到非公开和加密队伍，普通用户只能找到公开队伍,这里队伍成员应该也能找到
            TeamStatusEnum enumByStatus = TeamStatusEnum.getEnumByStatus(teamQueryRequest.getStatus());
            if (enumByStatus == null){
                enumByStatus = TeamStatusEnum.PUBIC;
            }
            if (!enumByStatus.equals(TeamStatusEnum.PUBIC) && !isAdmin){
                throw new BusinessException(Code.NO_AUTH);
            }
            queryWrapper.eq("status",enumByStatus.getStatus());
        }
        //7. 过期时间 只要没过期都可以访问
        queryWrapper.gt("expireTime",new Date()).or(qt->qt.isNull("expireTime"));

        // 按照条件查询用户
        List<Team> listTeam = this.list(queryWrapper);
        // 如果不存在符合条件的队伍，返回空
        if (listTeam == null){
            return new ArrayList<TeamVo>();
        }

        //队伍信息脱敏
        List<TeamVo> teamUserVoList = new ArrayList<>();
        for (Team team : listTeam){
            // 关联队长信息
            Long userId = team.getUserId();
            if (userId == null){
                continue;
            }

            TeamVo teamUserVo = new TeamVo();
            // 将查询到的信息复制到teamUserVo，由于teamUserVo中没有敏感字段，所以可以实现脱敏
            BeanUtils.copyProperties(team,teamUserVo);
            QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("teamId",team.getId());
            teamUserVo.setHasJoinNum((int) userTeamService.count(queryWrapper1));
            // 获取队长信息
            User user = userService.getById(userId);
            if (user != null){
                teamUserVo.setUser(userService.getSafeUser(user));
            }
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;
    }

    @Override
    public Boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        // 需求：用户可以加入非私有、未过期、人数未满的队伍，但用户能够加入的队伍不能超过 10 个
        // 判空
        if (joinTeamRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"输入参数为空");
        }
        //实现：
        // 判断队伍是否存在
        Long teamId = joinTeamRequest.getTeamId();
        if (teamId == null || teamId < 1){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍 id 错误");
        }
        Team team = teamService.getById(teamId);
        //1. 判断队伍状态，如果是私有，不能加入，加密房间需要输入密码
        TeamStatusEnum enumByStatus = TeamStatusEnum.getEnumByStatus(team.getStatus());
        if (TeamStatusEnum.PRIVATE.equals(enumByStatus)){
            throw new BusinessException(Code.NO_AUTH,"无法加入私有队伍");
        }
        String password = joinTeamRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByStatus) && !team.getPassword().equals(password)){
            throw new BusinessException(Code.PARAMS_ERROR,"密码错误");
        }
        //2. 判断过期时间
        Date expireTime = team.getExpireTime();
        if (expireTime.before(new Date())){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍已过期");
        }
        RLock lock = redissonClient.getLock("xiaobai:join_team");

        try {
            while(true) {
                if (lock.tryLock(0, -1, TimeUnit.MICROSECONDS)) {
                    System.out.println("getLock: " + Thread.currentThread().getId());
                    //3. 查询用户加入了多少个队伍
                    long userId = loginUser.getId();
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", userId);
                    long count1 = userTeamService.count(queryWrapper);
                    if (count1 >= 10) {
                        throw new BusinessException(Code.NO_AUTH, "加入队伍以达上限");
                    }
                    //4. 判断队伍中人数是否达到上限，并且不能加入重复的队伍
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("teamId", teamId);
                    long count = userTeamService.count(queryWrapper);
                    if (count >= team.getMaxNum()) {
                        throw new BusinessException(Code.PARAMS_ERROR, "队伍人数已满");
                    }
                    for (UserTeam userTeam : userTeamService.list(queryWrapper)) {
                        if (userTeam.getUserId().equals(userId)) {
                            throw new BusinessException(Code.PARAMS_ERROR, "已加入该队伍，请勿重复加入");
                        }
                    }
                    //5. 加入队伍，将数据写入 userteam 表
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }

    @Override
    public Boolean quitTeam(Long teamId, User loginUser) {
//        1. 校验请求参数,判断队伍是否存在
        if (teamId == null || teamId < 1){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍 id 参数错误");
        }

//3. 判断自己是否加入队伍
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId", userId);
        long count = userTeamService.count(queryWrapper);
        if (count == 0){
            throw new BusinessException(Code.PARAM_NULL_ERROR,"当前用户未加入队伍");
        }
//4. 判断队伍情况
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        long count1 = userTeamService.count(queryWrapper);
//  a. 如果队伍只剩下一人，解散队伍
        if (count1 == 1){
            userTeamService.remove(queryWrapper);
            this.removeById(teamId);
        }
//  b. 还有其他人
        if (count1 > 1){
            //    ⅰ. 如果自己是队长，将队长传递给第二早加入队伍的成员
            Team team = this.getById(teamId);
            if (userId == team.getUserId()){
                // 查询队伍中最早加入的前两名（队长和预备队长）
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (userTeamList == null || userTeamList.size() <= 1){
                    throw new BusinessException(Code.SYSTEM_ERROR);
                }
                // 查询到预备队长
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextUserTeamUserId = nextUserTeam.getUserId();
                Team newTeam = new Team();
                newTeam.setId(teamId);
                newTeam.setUserId(nextUserTeamUserId);
                boolean updata = this.updateById(newTeam);
                if (!updata){
                    throw new BusinessException(Code.SYSTEM_ERROR,"更新队长失败");
                }
            }
            //    ⅱ. 如果不是队长，直接退出
        }
        queryWrapper.eq("userId",userId);
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser) {
        //1. 检验参数
        if (teamDeleteRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        //2. 判断队伍是否存在
        Long teamId = teamDeleteRequest.getId();
        if (teamId == null || teamId < 1){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍 id 错误");
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(Code.PARAM_NULL_ERROR,"没有该队伍");
        }
        //3. 检查是否为队长或者管理员
        long userId = loginUser.getId();
        if (userId != team.getUserId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(Code.NO_AUTH);
        }
        //4. 删除用户队伍中所有成员的信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean removeUserTeam = userTeamService.remove(queryWrapper);
        //5. 删除队伍表中的信息
//        if (true){
//            throw new BusinessException(Code.SYSTEM_ERROR);
//        }
        boolean removeTeam = this.removeById(teamId);
        return removeUserTeam && removeTeam;
    }

    @Override
    public TeamVo getUsersByTeamId(Long teamId, HttpServletRequest request) {
        // 判断用户是否登录
        User loginUser = userService.getLoginUser(request);
        // 查询数据库中是否存在该队伍
        Team team = teamMapper.selectById(teamId);
        if (team == null){
            return null;
        }
        // 判断登录用户是否有权限访问该队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
        if (team.getStatus() == 1 && loginUser.getUserRole() == 0){
            for (UserTeam userTeam : userTeams){
                if (userTeam.getTeamId().equals(teamId)){
                    break;
                }
            }
            return null;
        }
        // 获取队伍中的其它成员信息
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        Set<User> userCollect = userTeamMapper.selectList(queryWrapper).stream()
                .map((UserTeam userTeam) -> userService.getById(userTeam.getUserId()))
                .collect(Collectors.toSet());
        TeamVo teamVo = new TeamVo();
        teamVo.setId(teamId);
        teamVo.setName(team.getName());
        teamVo.setTeamAvatarUrl(team.getTeamUrl());
        teamVo.setPassword("");
        teamVo.setDescription(team.getDescription());
        teamVo.setMaxNum(team.getMaxNum());
        teamVo.setExpireTime(team.getExpireTime());
        teamVo.setStatus(team.getStatus());
        teamVo.setCreateTime(team.getCreateTime());
        teamVo.setAnnounce("");
        teamVo.setUser(loginUser);
        teamVo.setUserSet(userCollect);
        return teamVo;
    }

    @Override
    public boolean dissolutionTeam(Long teamId, HttpServletRequest request) {
        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 判断用户id是否为队伍队长
        Team team = teamMapper.selectById(teamId);
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(Code.NO_AUTH,"不是该队伍队长");
        }
        // 解散队伍，删除队伍表中的队伍信息，以及用户-队伍表里面的用户信息
        teamMapper.deleteById(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        userTeamMapper.delete(queryWrapper);
        return true;
    }

    @Override
    public TeamUserVo getTeamListByTeamIds(Set<Long> teamId, HttpServletRequest request) {
        userService.getLoginUser(request);
        // 获取所有队伍
        List<Team> teams = this.list();
        // 过滤后的队伍列表
        List<Team> teamList = teams.stream().filter(team -> {
            for (Long tid : teamId) {
                // 保留当前没有过期的队伍和搜索的队伍
                if (!new Date().after(team.getExpireTime()) && tid.equals(team.getId())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return teamSet(teamList);
    }

    @Override
    public TeamUserVo teamQuery(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        userService.getLoginUser(request);
        String searchText = teamQueryRequest.getSearchText();
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teamLambdaQueryWrapper.like(Team::getDescription, searchText.trim())
                .or().like(Team::getName, searchText.trim());
        List<Team> teams = this.list(teamLambdaQueryWrapper);
        // 过滤后的队伍列表
        return teamSet(teams);
    }



    @Override
    public TeamUserVo teamSet(List<Team> teamList) {
        // 过滤过期的队伍
        List<Team> listTeam = teamList.stream()
                .filter(team -> !new Date().after(team.getExpireTime()))
                .collect(Collectors.toList());
        Collections.shuffle(listTeam);
        TeamUserVo teamUserVo = new TeamUserVo();
        Set<TeamVo> users = new HashSet<>();
        listTeam.forEach(team -> {
            TeamVo teamVo = new TeamVo();
            teamVo.setId(team.getId());
            teamVo.setName(team.getName());
            teamVo.setTeamAvatarUrl(team.getTeamUrl());
            teamVo.setDescription(team.getDescription());
            teamVo.setMaxNum(team.getMaxNum());
            teamVo.setExpireTime(team.getExpireTime());
            teamVo.setStatus(team.getStatus());
            teamVo.setCreateTime(team.getCreateTime());
            teamVo.setAnnounce("");
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId",team.getId());
            queryWrapper.select("userId");
            Set<Long> userSet = new HashSet<>();
            List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
            for (UserTeam useTeam : userTeams){
                userSet.add(useTeam.getUserId());
            }
            Set<User> userList = new HashSet<>();
            for (Long id : userSet) {
                userList.add(userService.getById(id));
            }
            User createUser = userService.getById(team.getUserId());
            User safetyUser = userService.getSafeUser(createUser);
            teamVo.setUser(safetyUser);
            userList = userList.stream().map(userService::getSafeUser).collect(Collectors.toSet());
            teamVo.setUserSet(userList);
            users.add(teamVo);
        });
        teamUserVo.setTeamSet(users);
        return teamUserVo;
    }

    @Override
    public Boolean kickOutTeamByUserId(KickOutUserRequest kickOutUserRequest, User loginUser) {
        // 检查队伍id和成员id是否存在
        Long teamId = kickOutUserRequest.getTeamId();
        Long userId = kickOutUserRequest.getUserId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        queryWrapper.eq("userId",userId);
        UserTeam userTeam = userTeamMapper.selectOne(queryWrapper);
        if (userTeam == null){
            throw new BusinessException(Code.PARAMS_ERROR,"队伍中没有该成员");
        }
        Team team = teamMapper.selectById(teamId);
        if (loginUser.getId() != team.getUserId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(Code.NO_AUTH,"权限不足");
        }
        userTeamMapper.delete(queryWrapper);
        return true;
    }

    @Override
    public Boolean transferTeam(TransferTeamRequest transferTeamRequest, User loginUser) {
        // 获取转移的信息
        String userAccount = transferTeamRequest.getUserAccount();
        Long teamId = transferTeamRequest.getTeamId();
        // 判断队伍是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null){
            throw new BusinessException(Code.PARAM_NULL_ERROR,"队伍不存在");
        }
        // 根据用户账号查找用户id
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            throw new BusinessException(Code.PARAMS_ERROR,"用户账号不存在");
        }
        // 判断下一任队长是否在队伍中
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("teamId",teamId);
        queryWrapper1.eq("userId",user.getId());
        UserTeam userTeam = userTeamMapper.selectOne(queryWrapper1);
        if (userTeam == null){
            throw new BusinessException(Code.PARAMS_ERROR,"当前用户未加入队伍");
        }
        // 修改队伍队长
        team.setUserId(user.getId());
        team.setUpdateTime(new Date());
        teamMapper.updateById(team);
        return true;
    }
}




