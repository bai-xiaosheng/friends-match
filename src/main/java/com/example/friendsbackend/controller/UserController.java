package com.example.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.UserLoginRequest;
import com.example.friendsbackend.modal.request.UserRegister;
import com.example.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户控制
 *
 * @author BDS
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = {"http://localhost:3000"})
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Slf4j
//@Api(tags = "用户中心")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        return ResultUtils.success(userService.userLogin(userAccount, userPassword, request));
    }
    @PostMapping("/loginOut")
    public BaseResponse<Integer> userLogout(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.loginOut(request));
    }
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegister userRegister) {
        if (userRegister == null) {
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        String userAccount = userRegister.getUserAccount();
        String userPassword = userRegister.getUserPassword();
        String checkPassword = userRegister.getCheckPassword();
        String plantId = userRegister.getPlantId();
        long id = userService.userRegister(userAccount, userPassword, checkPassword,plantId);
        return ResultUtils.success(id);
    }
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        User safeUser = userService.getSafeUser(loginUser);
        return ResultUtils.success(safeUser);
    }
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String userName, HttpServletRequest request) {
        //判断当前用户权限
        if (!userService.isAdmin(request)){
            throw new BusinessException(Code.PARAM_NULL_ERROR);
        }
        //查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)){
            queryWrapper.select("userName", userName);
        }
        List<User> users = userService.list(queryWrapper);
        //脱敏
        return ResultUtils.success(users.stream().map(user -> {
                    return userService.getSafeUser(user);
                }
        ).collect(Collectors.toList()));
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(Code.PARAM_NULL_ERROR);
        }
        List<User> userList = userService.userSearchByTag(tagNameList);
        return ResultUtils.success(userList);
    }
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUser(long pageSize, long pageNum, HttpServletRequest request){
        //todo
        // 把业务代码放在业务层
        //加入缓存
        User loginUser = userService.getLoginUser(request);
        //根据用户id查询缓存值
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String rediasKey = String.format("xiaobai:user:recommend:%s",loginUser.getId());
        List<User> userList = (List<User>) opsForValue.get(rediasKey);
        //如果有缓存值，直接返回
        if (userList != null){
            return ResultUtils.success(userList);
        }
        //如果没有缓存值，从数据库读取
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回的用户信息要脱敏
        userList = userPage.getRecords().stream().map(
                user -> userService.getSafeUser(user)).collect(Collectors.toList());
        //写入缓存
        try {
            opsForValue.set(rediasKey,userList,10, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            log.error("redias set key error",e);
        }
        return ResultUtils.success(userList);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        //判断输入数据是否为空以及是否提供id
        if (user == null){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        //判断当前用户权限
        User loginUser = userService.getLoginUser(request);
//        if (!userService.checkRole(loginUser) && loginUser.getId() != user.getId()){
//            throw new BusinessException(Code.NO_AUTH);
//        }
        //修改数据
        int result = userService.updateUser(user, request);
        return ResultUtils.success(result);

    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(long id, HttpServletRequest request) {
        //判断当前用户权限
        if (!userService.isAdmin(request)){
            throw new BusinessException(Code.NO_AUTH);
        }
        //按照id删除
        if (id < 0) {
            return ResultUtils.error(Code.PARAM_NULL_ERROR);
        }
        return ResultUtils.success(userService.removeById(id));
    }

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUser(long num,HttpServletRequest request){
        if (num < 1 || num > 20){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userResult = userService.matchUsers(num,loginUser);
        return ResultUtils.success(userResult);
    }

}
