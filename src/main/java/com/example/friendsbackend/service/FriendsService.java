package com.example.friendsbackend.service;

import com.example.friendsbackend.modal.domain.Friends;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.FriendAddRequest;
import com.example.friendsbackend.modal.vo.FriendsRecordVO;

import java.util.List;
import java.util.Set;

/**
* @author BDS
* @description 针对表【friends(好友申请管理表)】的数据库操作Service
* @createDate 2024-08-09 19:19:07
*/
public interface FriendsService extends IService<Friends> {

    /**
     * 添加好友：向对方发送好友申请
     *
     * @param loginUser 登录用户
     * @param friendAddRequest 好友请求信息
     * @return 是否添加成功
     */
    boolean addFriendRecords(User loginUser, FriendAddRequest friendAddRequest);

    /**
     * 查询用户收到的所有好友申请信息
     *
     * @param loginUser 当前登录用户
     * @return list
     */
    List<FriendsRecordVO> obtainFriendApplicationRecords(User loginUser);

    /**
     * 获取用户收到的未读好友申请个数
     *
     * @param loginUser 登录用户
     * @return int
     */
    int getRecordCount(User loginUser);

    /**
     * 获取当前用户发送的好友申请
     *
     * @param loginUser 登录用户
     * @return list(自己id，备注信息，对方用户信息)
     */
    List<FriendsRecordVO> getMyRecords(User loginUser);

    /**
     * 同意用户申请
     *
     * @param loginUser 登录用户
     * @param fromId 申请用户id
     * @return boolean
     */
    boolean agreeToApply(User loginUser, Long fromId);

    /**
     * 撤销好友申请信息
     *
     * @param loginUser 登录用户
     * @param id 好友申请信息id
     * @return boolean
     */
    boolean canceledApply(User loginUser, Long id);

    /**
     * 检查好友申请id是否全部已读
     *
     * @param loginUser 登录用户
     * @param ids 好友申请id
     * @return boolean
     */
    boolean toRead(User loginUser, Set<Long> ids);
}
