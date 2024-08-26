package com.example.friendsbackend.constant;

public interface Constant {
    /**
     * 用户登录态键值
     */
    String USER_LOGIN_STATE = "USER_LOGIN_STATE";

    /**
     * 普通用户权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 用户最近登录表缓存的键值
     */
    String RECENTUSER = "xiaobai:user:recentUser:id:zSet";



    /**
     * 距离当前时间的时间范围，毫秒 2629800000L对应一个月
     * 这里考虑到目前活跃用户数比较少，延长缓存范围为 5 个月
     */
    Long RECENTTIME = 13149000000L;
}
