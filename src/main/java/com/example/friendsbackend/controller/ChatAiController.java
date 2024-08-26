package com.example.friendsbackend.controller;


import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import com.example.friendsbackend.component.XfXhStreamClient;
import com.example.friendsbackend.config.XfXhConfig;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.request.ChatRequest;
import com.example.friendsbackend.modal.vo.MessageVo;
import com.example.friendsbackend.service.ChatService;
import com.example.friendsbackend.service.UserService;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.example.friendsbackend.constant.ChatConstant.AI_CHAT;


@RestController
@RequestMapping("/chatAi")
@Slf4j
public class ChatAiController {



    @Resource
    private UserService userService;

    @Resource
    private ChatService chatService;



    @GetMapping("/getLastMes")
    public BaseResponse<List<MessageVo>> getLastMes(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(chatService.getAiLastChat(loginUser));
    }

    /**
     * 发送问题，
     *
     * @param chatRequest 队伍id 接收方id 发送的内容
     * @return 星火大模型的回答
     */
    @PostMapping("/sendQuestion")
    public BaseResponse<List<MessageVo>> sendQuestion(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 如果是无效字符串，则不对大模型进行请求
        if (chatRequest == null || StrUtil.isBlank(chatRequest.getText())) {
            return ResultUtils.error(Code.SYSTEM_ERROR,"输入参数有误，请重新尝试");
        }
        List<MessageVo> result = chatService.getAiAnswer(chatRequest, loginUser);
        return ResultUtils.success(result);
    }
}
