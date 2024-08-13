package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.request.UserQueryRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author BDS
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-12 15:46:39
*/

public interface UserService extends IService<User> {


    /**
     * 只需要用户账号跟数据库不重复即可创建
     *
     * @param userAccount 用户账号
     * @param userPassword  用户密码
     * @param checkPassword  校验密码（二次密码）
     * @return 用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

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
    List<User> searchUserByTag(long num, List<String> tagsNameList);

    /**
     * 根据当前用户返回其推荐用户
     * @param request 登录信息
     * @param pageSize
     * @param pageNum
     * @return 用户列表
     */
    List<User>  recommendUser(HttpServletRequest request, long pageSize, long pageNum);

    /**
     * 根据用户账号查询用户
     *
     * @param userAccount  用户账号
     * @return 用户列表
     */
    List<User> searchUserByUserAccount(String userAccount);
    /**
     * @param num 数量
     * @param loginUser 当前登录用户
     * @return 与登录用户相匹配的用户
     */
    List<User> matchUsers(long num, User loginUser);
    /**
     * 更新用户信息
     * @param user 更新的用户信息
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
     * 检查当前登录用户是否为管理员
     * @param user 登录用户
     * @return 管理员-true
     */
    boolean isAdmin(User user);

    /**
     * 获取当前登录用户的好友
     *
     * @param loginUser 当前登录用户
     * @return 用户好友
     */
    List<User> getFriendsById(User loginUser);

    /**
     * 根据好友id，删除好友
     *
     * @param loginUser 当前登录用户
     * @param id 要删除的好友id
     * @return 是否成功删除
     */
    boolean deleteFriend(User loginUser, Long id);

    /**
     * 根据用户账号查询好友
     *
     * @param userQueryRequest 查询用户账号
     * @param loginUser 当前登录用户
     * @return 符合条件的用户
     */
    List<User> searchFriend(UserQueryRequest userQueryRequest, User loginUser);
}
