package com.example.friendsbackend.controller;

import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.FriendAddRequest;
import com.example.friendsbackend.modal.vo.FriendsRecordVO;
import com.example.friendsbackend.service.FriendsService;
import com.example.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/friends")
public class FriendsController {
    @Resource
    private UserService userService;
    @Resource
    private FriendsService friendsService;

    /**
     * 发送好友申请
     *
     * @param friendAddRequest 包含用户id和备注信息
     * @param request 登录信息
     * @return Boolean
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addFriendRecords(@RequestBody FriendAddRequest friendAddRequest, HttpServletRequest request){
        if (friendAddRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean send = friendsService.addFriendRecords(loginUser, friendAddRequest);
        return ResultUtils.success(send, "申请成功");
    }

    /**
     * 当前用户收到的好友申请信息
     *
     * @param request 登录信息
     * @return list（发送方用户id，备注，发送方用户信息）
     */
    @GetMapping("getRecords")
    public BaseResponse<List<FriendsRecordVO>> getRecords(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<FriendsRecordVO> friendsList = friendsService.obtainFriendApplicationRecords(loginUser);
        return ResultUtils.success(friendsList);
    }

    /**
     * 获取当前用户收到的好友申请个数（未读的好友申请）
     *
     * @param request 登录信息
     * @return Integer
     */
    @GetMapping("getRecordCount")
    public BaseResponse<Integer> getRecordCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int recordCount = friendsService.getRecordCount(loginUser);
        return ResultUtils.success(recordCount);
    }

    @GetMapping("getMyRecords")
    public BaseResponse<List<FriendsRecordVO>> getMyRecords(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<FriendsRecordVO> myFriendsList = friendsService.getMyRecords(loginUser);
        return ResultUtils.success(myFriendsList);
    }

    /**
     * 根据申请人id，同意好友请求
     *
     * @param fromId 申请用户id
     * @param request 登录信息
     * @return boolean
     */
    @PostMapping("agree/{fromId}")
    public BaseResponse<Boolean> agreeToApply(@PathVariable("fromId") Long fromId, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        boolean agreeToApplyStatus = friendsService.agreeToApply(loginUser, fromId);
        return ResultUtils.success(agreeToApplyStatus);
    }

    /**
     * 撤销好友申请
     *
     * @param id 申请记录id
     * @param request 登录信息
     * @return boolean
     */
    @PostMapping("canceledApply/{id}")
    public BaseResponse<Boolean> canceledApply(@PathVariable("id") Long id, HttpServletRequest request){
        if (id == null || id < 1){
            throw new BusinessException(Code.PARAMS_ERROR,"参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        boolean agreeToApplyStatus = friendsService.canceledApply(loginUser, id);
        return ResultUtils.success(agreeToApplyStatus,"撤销成功");
    }

    /**
     * 检查好友申请id是否全部已读
     *
     * @param ids  好友申请id
     * @param request 登录信息
     * @return boolean
     */
    @GetMapping("/read")
    public BaseResponse<Boolean> toRead(@RequestParam(required = false) Set<Long> ids, HttpServletRequest request){
        if (ids == null){
            return ResultUtils.success(false,"没有传入待检查的好友申请id");
        }
        User loginUser = userService.getLoginUser(request);
        boolean isRead = friendsService.toRead(loginUser, ids);
        return ResultUtils.success(isRead);
    }
}
