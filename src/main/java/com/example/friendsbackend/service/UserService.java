package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author BDS
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-12 15:46:39
*/

public interface UserService extends IService<User> {

    /**
     * 创建用户
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码（二次密码）
     * @param plantId 星球 id，是否允许用户创建的一个条件
     * @return 用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String plantId);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 登录信息
     * @return 用户
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param user 数据库中的用户信息
     * @return 脱敏后的用户信息
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int loginOut(HttpServletRequest request);

    /**
     * 根据标签匹配符合条件的用户
     * @param tagsNameList 标签列表
     * @return 用户列表
     */
    List<User> userSearchByTag(List<String> tagsNameList);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @param request 登录信息
     * @return 是否更新成功
     */
    int updateUser(User user, HttpServletRequest request);

    /**
     * 获取登录用户
     * @param request 请求
     * @return 缓存登录的用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 检查当前登录用户是否为管理员
     * @param request 请求
     * @return 管理员-true
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * @param user 登录用户
     * @return 管理员-true
     */
    boolean isAdmin(User user);

    List<User> matchUsers(long num, User loginUser);
}
