package com.example.friendsbackend.controller;
import java.io.IOException;
import java.util.Date;
import java.util.*;

import com.example.friendsbackend.config.HttpSessionConfig;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.request.MyMessage;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.ChatService;
import com.example.friendsbackend.service.UserTeamService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 聊天控制器
 * @ServerEndpoint("/websocket/{userId}/{teamId}") 中的userId是前端创建会话窗口时当前用户的id,即消息发送者的id
 */
//@ServerEndpoint("/chat")
@ServerEndpoint(value = "/websocket/{userId}/{teamId}", configurator = HttpSessionConfig.class)
@Component
@Slf4j
public class ChatWebSocketController {
    // 定义静态变量，在websocket中导入
    public static UserTeamService userTeamService;
    public static UserMapper userMapper;
    public static ChatService chatService;
    // 记录当前在线人数
    private static AtomicInteger onlineCount = new AtomicInteger();
    // 存放每个客户端对应的websocket
    public static Map<String,Map<String,ChatWebSocketController>> webSocketSets = new HashMap<>();
    // 存放所有连接人的信息
    public static Set<String> userList = new HashSet<>();
    // 与客户端连接的会话，通过会话传递消息
    private Session session;

    public String userId = "";

    /**
     * WebSocket连接时调用的方法
     *
     * @param session 客户端与服务器的会话
     * @param userId  当前会话的用户id
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam(value = "userId") String userId,
                       @PathParam(value = "teamId") String teamId,
                       EndpointConfig config){
        try {
//            // 将 config 里的 HttpSession取出
//            HttpSession userHttpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
//            // 从 HttpSession 中获取当前登录用户信息
//            User user = (User) userHttpSession.getAttribute(USER_LOGIN_STATE);
//            // 提取信息
//            if (user!=null){
//                this.session = session;
//                this.userId = userId;
//                userList.add(userId);
//            }
            this.session = session;
            this.userId = userId;
            userList.add(userId);
            // 构建聊天室 0 - 所有聊天用户 roomId - 队伍id
            List<String> roomIdList = userTeamService.findTeamList(Long.valueOf(userId));
            for (String romeId : roomIdList){
                Map<String, ChatWebSocketController> chatWebSocketControllers = webSocketSets.getOrDefault(romeId,new ConcurrentHashMap<>());
                chatWebSocketControllers.put(userId,this);
                webSocketSets.put(romeId,chatWebSocketControllers);
            }
            onlineCount.incrementAndGet();
            log.info("有新连接加入！" + userId + "当前在线用户数为" + onlineCount.get());
            System.out.println("有新连接加入！" + userId + "当前在线用户数为" + onlineCount.get());
            // 读取离线期间收到的消息
            List<MyMessage> unreadMessages = chatService.findUnreadMessage(Long.valueOf(userId));
            for (MyMessage unreadMessage : unreadMessages){
                sendInfo(unreadMessage);
            }
        } catch (Exception e){
            log.error("WebSocket onOpen error",e);
        }
    }

    /**
     * 结束会话，并关闭连接
     *
     * @param userId 要关闭连接的用户id
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId){
        try {
            List<String> roomIdList = userTeamService.findTeamList(Long.valueOf(userId));
            for (String romeId : roomIdList){
                Map<String, ChatWebSocketController> chatWebSocketControllers = webSocketSets.get(romeId);
                chatWebSocketControllers.remove(userId);
                webSocketSets.put(romeId,chatWebSocketControllers);
            }
            onlineCount.decrementAndGet();
            System.out.println("当前会话结束");
        }catch (Exception e){
            log.error("WebSocket onClose error",e);
        }

    }

    /**
     * 收到客户端的消息后调用的方法
     *
     * @param message 客户端发送的消息
     * @param userId 客户端的用户id
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "userId") String userId, @PathParam(value = "teamId") String teamId){
        try {
            // 心跳包机制,防止长时间连接没有传输导致连接关闭
            if ("PING".equals(message)) {
                this.session.getAsyncRemote().sendText("PING");
                return;
            }
            // 创建gson
            Gson gson = new Gson();
            // 将 String 转换为 MyMessage 类
            MyMessage myMessage = gson.fromJson(message, MyMessage.class);
            // 读取消息类里的信息
            String content = myMessage.getContent();
            // 读取用户名
            String userName = myMessage.getUserName();
            // 读取消息类型，单聊还是群聊
            int messageType = myMessage.getMode();
            // 如果是单聊
            if (messageType == 1){
                // 获取目标用户的id
                String recUserId = myMessage.getRecUserId();
                // 发送消息
                sendInfo(content,recUserId,userId,userName);
            } else if (messageType == 2) {
                // 群聊
                sendGroupInfo(content,teamId,userId);
            }
        }catch (Exception e){
            log.error("WebSocket onMessage error",e);
        }

    }

    @OnError
    public void onError(Session session,Throwable error){
        System.out.println("出现异常!");
        log.error("WebSocket error:" + error);
    }

    /**
     * 保证发送消息的线程安全 synchronized
     *
     * @param content 要发送的消息
     */
    public synchronized void sendMessage(String content){
        // this.session.getAsyncRemote() 获取当前异步消息发送的实例
//        this.session.getAsyncRemote().sendText(content);
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(content);
            } catch (IOException e) {
                log.error("sendMessage error",e);
            }
        }
        // this.session.getBasicRemote() 获取当前同步消息发送的实例
    }
    public synchronized void sendMessage(MyMessage message){
        // this.session.getAsyncRemote() 获取当前异步消息发送的实例
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message.toString());
            } catch (IOException e) {
                log.error("sendMessage error",e);
            }
        }
        // this.session.getBasicRemote() 获取当前同步消息发送的实例
    }

    /**
     * 单聊
     *
     * @param content   发送的消息
     * @param recUserId 接收方用户id
     * @param userId    发送发用户id
     * @param userName
     */
    public void sendInfo(String content, String recUserId, String userId, String userName){
        MyMessage myMessage = new MyMessage();
        myMessage.setContent(content);
        myMessage.setMode(1);
        myMessage.setRoomId("0");
        myMessage.setUserName(userName);
        myMessage.setSendTime(new Date());
        myMessage.setSendUserId(userId);
        myMessage.setRecUserId(recUserId);
        Map<String, ChatWebSocketController> stringChatWebSocketControllerMap = webSocketSets.get("0");
        if (stringChatWebSocketControllerMap.containsKey(recUserId)){
            stringChatWebSocketControllerMap.get(recUserId).sendMessage(myMessage.toString());
            // 1 - 已读 0 - 未读
            chatService.saveMessage(myMessage,1);
        }else {
            // 目标未上线，先将数据保存到数据库中，等用户上线后发送
            chatService.saveMessage(myMessage,0);
        }
    }

    /**
     * 根据消息体发送消息
     *
     * @param myMessage 消息体
     */
    public void sendInfo(MyMessage myMessage){
        synchronized (webSocketSets.get("0").get(myMessage.getRecUserId())){
            webSocketSets.get("0").get(myMessage.getRecUserId()).sendMessage(myMessage.toString());
        }
    }

    /**
     * 给指定群组发送消息
     *
     * @param content  消息内容
     * @param roomId   队伍id
     * @param sendUserId  发送信息的用户id
     */
    public void sendGroupInfo(String content, String roomId, String sendUserId){
        MyMessage myMessage = new MyMessage();
        myMessage.setContent(content);
        myMessage.setMode(2);
        myMessage.setSendTime(new Date());
        myMessage.setSendUserId(sendUserId);
        myMessage.setRecUserId(sendUserId);
        User user = userMapper.selectById(sendUserId);
        myMessage.setUserName(user.getUserName());
        myMessage.setRoomId(roomId);
        for (String userId : webSocketSets.get(roomId).keySet()){
            if (!Objects.equals(sendUserId, userId)){
                myMessage.setRecUserId(userId);
                webSocketSets.get(roomId).get(userId).sendMessage(myMessage);
            }
        }

    }

    /**
     * 给用户加入的所有群组发送消息
     *
     * @param content 发送的信息
     * @param sendUserId 发送信息的用户id
     */
    public void sendGroupInfo(String content, String sendUserId){
        Gson gson = new Gson();
        MyMessage myMessage = new MyMessage();
        myMessage.setContent(content);
        myMessage.setMode(2);
        myMessage.setSendTime(new Date());
        myMessage.setSendUserId(sendUserId);
        User user = userMapper.selectById(sendUserId);
        myMessage.setUserName(user.getUserName());

        List<String> roomList = userTeamService.findTeamList(Long.valueOf(sendUserId));
        roomList.remove("0");
        for (String roomId : roomList){
            myMessage.setRoomId(roomId);
            for (String userId : webSocketSets.get(roomId).keySet()){
                if (!Objects.equals(sendUserId, userId)){
                    myMessage.setRecUserId(userId);
                    webSocketSets.get(roomId).get(userId).sendMessage(myMessage);
                }
            }
        }

    }

//    public List<String> findRoomList(String userId){
//        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("teamId");
//
//        queryWrapper.eq("userId",Long.valueOf(userId));
//        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
//        List<String> roomIdList = new ArrayList<>();
//        roomIdList.add("0");
//        if (!userTeams.isEmpty()){
//            for (UserTeam userTeam : userTeams){
//                roomIdList.add(String.valueOf(userTeam.getTeamId()));
//            }
//        }
//        return roomIdList;
//    }
}

