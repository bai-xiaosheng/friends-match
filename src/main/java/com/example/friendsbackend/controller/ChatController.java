package com.example.friendsbackend.controller;

import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.ChatRequest;
import com.example.friendsbackend.modal.vo.MessageVo;
import com.example.friendsbackend.service.ChatService;
import com.example.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.example.friendsbackend.constant.ChatConstant.*;

@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private UserService userService;

    @Resource
    private ChatService chatService;

    /**
     * 查询私聊聊天记录
     *
     * @param chatRequest 队伍id和接受消息的用户id
     * @param request 登录信息
     * @return List
     */
    @PostMapping("/privateChat")
    public BaseResponse<List<MessageVo>> getPrivateChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request){
        if (chatRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        List<MessageVo> privateChat = chatService.getPrivateChat(chatRequest, PRIVATE_CHAT, loginUser);
        return ResultUtils.success(privateChat);
    }

    /**
     * 查询大厅消息
     *
     * @param request 登录信息
     * @return data：list（发送者信息（用户名称，用户账号，用户头像），发送内容，发送类型，是否为登陆者发送的信息，是否为管理员，发送时间
     */
    @GetMapping("/hallChat")
    public BaseResponse<List<MessageVo>> getHallChat(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<MessageVo> hallChat = chatService.getHallChat(HALL_CHAT, loginUser);
        return ResultUtils.success(hallChat);
    }

    /**
     * 查询队伍聊天信息
     *
     * @param chatRequest 队伍id和用户id
     * @param httpServletRequest 客户端请求
     * @return data：list（发送者信息（用户名称，用户账号，用户头像），发送内容，发送类型，是否为登陆者发送的信息，是否为管理员，发送时间
     */
    @PostMapping("/teamChat")
    public BaseResponse<List<MessageVo>> getTeamChat(@RequestBody ChatRequest chatRequest, HttpServletRequest httpServletRequest){
        if (chatRequest == null){
            throw new BusinessException(Code.PARAMS_ERROR,"参数错误");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<MessageVo> teamChat = chatService.getTeamChat(chatRequest, TEAM_CHAT, loginUser);
        return ResultUtils.success(teamChat);
    }
}
