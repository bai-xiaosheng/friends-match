package com.example.friendsbackend.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.Friends;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.FriendAddRequest;
import com.example.friendsbackend.modal.vo.FriendsRecordVO;
import com.example.friendsbackend.service.FriendsService;
import com.example.friendsbackend.mapper.FriendsMapper;
import com.example.friendsbackend.service.UserService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.example.friendsbackend.constant.FriendConstant.*;
import static com.example.friendsbackend.utils.StringJsonListToLongSet.stringJsonListToLongSet;

/**
* @author BDS
* @description 针对表【friends(好友申请管理表)】的数据库操作Service实现
* @createDate 2024-08-09 19:19:07
*/
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
    implements FriendsService{

    @Resource
    private FriendsMapper friendsMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean addFriendRecords(User loginUser, FriendAddRequest friendAddRequest) {
        // 判断请求是否合理
        Long receiveId = friendAddRequest.getReceiveId();
        User user = userMapper.selectById(receiveId);
        if (user == null){
            throw new BusinessException(Code.PARAM_NULL_ERROR,"对方用户不存在");
        }
        // 备注信息不能超过120个字符
        if (StringUtils.isNotBlank(friendAddRequest.getRemark()) && friendAddRequest.getRemark().length() > 120) {
            throw new BusinessException(Code.PARAMS_ERROR, "申请备注最多120个字符");
        }
        if (loginUser.getId() == friendAddRequest.getReceiveId()) {
            throw new BusinessException(Code.PARAMS_ERROR, "不能添加自己为好友");
        }
        RLock lock = redissonClient.getLock("xiaobai:friends:add:apply");
        try {
            // 抢到锁并执行
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                // 2.条数大于等于1 就不能再添加
                LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
                friendsLambdaQueryWrapper.eq(Friends::getReceiveId, friendAddRequest.getReceiveId());
                friendsLambdaQueryWrapper.eq(Friends::getFromId, loginUser.getId());
                List<Friends> list = this.list(friendsLambdaQueryWrapper);
                list.forEach(friends -> {
                    if (list.size() > 1 && friends.getStatus() == DEFAULT_STATUS) {
                        throw new BusinessException(Code.PARAMS_ERROR, "不能重复申请");
                    }
                });
                Friends newFriend = new Friends();
                newFriend.setFromId(loginUser.getId());
                newFriend.setReceiveId(friendAddRequest.getReceiveId());
                if (StringUtils.isBlank(friendAddRequest.getRemark())) {
                    newFriend.setRemark("我是" + userMapper.selectById(loginUser.getId()).getUserName());
                } else {
                    newFriend.setRemark(friendAddRequest.getRemark());
                }
                return this.save(newFriend);
            }
        } catch (InterruptedException e) {
            log.error("joinFriends error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public List<FriendsRecordVO> obtainFriendApplicationRecords(User loginUser) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiveId",loginUser.getId());
        List<Friends> friends = friendsMapper.selectList(queryWrapper);
        return friends.stream()
                .map(friend -> {
                    FriendsRecordVO friendsRecordVO = new FriendsRecordVO();
                    BeanUtils.copyProperties(friend,friendsRecordVO);
                    User safeUser = userService.getSafeUser(userMapper.selectById(friend.getFromId()));
                    friendsRecordVO.setApplyUser(safeUser);
                    return friendsRecordVO;
                }).collect(Collectors.toList());
    }

    @Override
    public int getRecordCount(User loginUser) {
        //
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiveId",loginUser.getId());
        queryWrapper.eq("isRead",NOT_READ);
        queryWrapper.eq("status",DEFAULT_STATUS);
        return Math.toIntExact(friendsMapper.selectCount(queryWrapper));
    }

    @Override
    public List<FriendsRecordVO> getMyRecords(User loginUser) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromId",loginUser.getId());
        List<Friends> friends = friendsMapper.selectList(queryWrapper);
        return friends.stream()
                .map(friend -> {
                    FriendsRecordVO friendsRecordVO = new FriendsRecordVO();
                    BeanUtils.copyProperties(friend,friendsRecordVO);
                    User safeUser = userService.getSafeUser(userMapper.selectById(friend.getReceiveId()));
                    friendsRecordVO.setApplyUser(safeUser);
                    return friendsRecordVO;
                }).collect(Collectors.toList());
    }

    @Override
    public boolean agreeToApply(User loginUser, Long fromId) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromId",fromId);
        queryWrapper.eq("receiveId",loginUser.getId());
        List<Friends> friends = friendsMapper.selectList(queryWrapper);
        if (friends.size() > 1){
            throw new BusinessException(Code.PARAMS_ERROR,"服务器错误，请联系管理员");
        }
        if (friends.size() < 1){
            throw new BusinessException(Code.PARAMS_ERROR,"该申请不存在");
        }
        AtomicBoolean flag = new AtomicBoolean(false);
        // 1. 更新friend表中信息
        Friends oldFriend = friends.get(0);
        oldFriend.setIsRead(1);
        oldFriend.setStatus(1);
        oldFriend.setUpdateTime(new Date());
        friendsMapper.updateById(oldFriend);
        // 2. 更新用户表中fromId对应的friendsId
        // 读取发送方用户
        User fromUser = userMapper.selectById(fromId);
        // 读取其中中好友id，并转换成set形式
        Set<Long> fromFriendsIds = stringJsonListToLongSet(fromUser.getFriendsIds());
        // 添加当前用户id
        fromFriendsIds.add(loginUser.getId());
        // 将set转换成json字符串
        Gson gson = new Gson();
        // 更新用户friendId并写入数据库
        fromUser.setFriendsIds(gson.toJson(fromFriendsIds));
        userMapper.updateById(fromUser);
        // 3. 更新用户表中loginUserId对应的friendsId
        User recieveUser = userMapper.selectById(loginUser.getId());
        Set<Long> receiveFriendsIds = stringJsonListToLongSet(recieveUser.getFriendsIds());
        receiveFriendsIds.add(fromId);
        recieveUser.setFriendsIds(gson.toJson(receiveFriendsIds));
        userMapper.updateById(recieveUser);
        return true;
    }

    @Override
    public boolean canceledApply(User loginUser, Long id) {
        Friends friends = friendsMapper.selectById(id);
        if (friends.getStatus() != DEFAULT_STATUS){
            throw new BusinessException(Code.PARAMS_ERROR,"该申请已通过或删除");
        }
        friends.setStatus(REVOKE_STATUS);
        friends.setUpdateTime(new Date());
        friendsMapper.updateById(friends);
        return true;
    }

    @Override
    // Transactional指定事物的回滚规则，如果事物中抛出了Exception类及其子类的异常，回滚事务
    @Transactional(rollbackFor = Exception.class)
    public boolean toRead(User loginUser, Set<Long> ids) {
        boolean flag = false;
        for (Long id : ids){
            Friends friends = friendsMapper.selectById(id);
            if (friends != null && friends.getIsRead() == NOT_READ && friends.getStatus() == DEFAULT_STATUS){
                friends.setIsRead(READ);
                friendsMapper.updateById(friends);
            }
        }
        return flag;
    }
}




