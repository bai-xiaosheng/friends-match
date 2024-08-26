package com.example.friendsbackend.ws;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import com.example.friendsbackend.config.HttpSessionConfig;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.Chat;
import com.example.friendsbackend.modal.domain.Team;
import com.example.friendsbackend.modal.request.MessageRequest;
import com.example.friendsbackend.modal.vo.MessageVo;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.modal.vo.WebSocketVo;
import com.example.friendsbackend.service.ChatService;
import com.example.friendsbackend.service.TeamService;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.service.UserTeamService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.friendsbackend.constant.ChatConstant.*;
import static com.example.friendsbackend.constant.Constant.ADMIN_ROLE;
import static com.example.friendsbackend.constant.Constant.USER_LOGIN_STATE;


/**
 * 聊天控制器
 * @ServerEndpoint("/websocket/{userId}/{teamId}") 中的userId是前端创建会话窗口时当前用户的id,即消息发送者的id
 */
//@ServerEndpoint("/chat")
@Component
@Slf4j
@ServerEndpoint(value = "/websocket/{userId}/{teamId}", configurator = HttpSessionConfig.class)
public class WebSocket {
    // 定义静态变量，在websocket中导入,或者是在这里定义一个方法导入
    public static UserTeamService userTeamService;
    public static UserMapper userMapper;
    public static ChatService chatService;
    public static UserService userService;
    public static TeamService teamService;
    /**
     * 当前在线人数
     */
    private static AtomicInteger onlineCount = new AtomicInteger();
    /**
     * 保存队伍的连接信息
     */
    private static final Map<String, ConcurrentHashMap<String, WebSocket>> ROOMS = new HashMap<>();
    /**
     * 线程安全的无序的集合
     */
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();
    /**
     * 存储在线连接数
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>(0);
    /**
     * 当前信息
     */
    private Session session;

    private HttpSession httpSession;

    public String userId = "";

    public static synchronized int getOnlineCount(){return onlineCount.get();};

    public static synchronized void addOlineCount(){WebSocket.onlineCount.incrementAndGet();};

    public static synchronized void subOnlineCount(){WebSocket.onlineCount.decrementAndGet();}



    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId, @PathParam(value = "teamId") String teamId, EndpointConfig config){
        try {
            HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            User loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
            if (loginUser != null){
                this.session = session;
                this.httpSession = httpSession;
            }
            if (StringUtils.isBlank(userId) || "undefined".equals(userId)){
                if (loginUser != null){
                    sendError(userId,"参数错误");
                }
                return;
            }
            if (!"NaN".equals(teamId)){
                if (!ROOMS.containsKey(teamId)){
                    ConcurrentHashMap<String,WebSocket> room = new ConcurrentHashMap<>();
                    room.put(userId,this);
                    ROOMS.put(teamId,room);
                }else {
                    ConcurrentHashMap<String, WebSocket> stringWebSocketConcurrentHashMap = ROOMS.get(teamId);
                    stringWebSocketConcurrentHashMap.put(userId,this);
                    ROOMS.put(teamId,stringWebSocketConcurrentHashMap);
                }
            }
//            else {
//                SESSIONS.add(session);
//                SESSION_POOL.put(userId,session);
//                log.info("有新用户加入，userId={}, 当前在线人数为：{}", userId, SESSION_POOL.size());
//                sendAllUsers();
//            }
            SESSIONS.add(session);
            SESSION_POOL.put(userId,session);
            log.info("有新用户加入，userId={}, 当前在线人数为：{}", userId, SESSION_POOL.size());
//            System.out.println("有新用户加入，userId="+ userId + ", 当前在线人数为："  + SESSION_POOL.size());
            onlineCount.incrementAndGet();

        }catch (Exception e){
            log.error("onOpen error",e);
        }
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam("teamId") String teamId, Session session){
        try {
            if (!"NaN".equals(teamId)){
                ROOMS.get(teamId).remove(userId);
                log.info("用户退出：当前在线人数为：" + onlineCount.decrementAndGet());
//                System.out.println("用户退出：当前在线人数为：" + onlineCount.decrementAndGet());
            }else {
                if (!SESSION_POOL.isEmpty()){
                    SESSION_POOL.remove(userId);
                    SESSIONS.remove(session);
                }
                log.info("[WebSocket消息] 连接断开，当前在线总数为:" + onlineCount.decrementAndGet());
//                System.out.println("[WebSocket消息] 连接断开，当前在线总数为:" + onlineCount.get());
                sendAllUsers();
            }
//            onlineCount.decrementAndGet();
        }catch (Exception e){
            log.error("WebSocket onClose error:", e);
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId){
        if ("PING".equals(message)){
            sendOneMessage(userId,"pong");
            log.info("心跳包，发送给={}，在线：{}人",userId,onlineCount.get());
            return;
        }
        log.info("服务端收到userId={}的消息：{}",userId,message);
        MessageRequest messageRequest = new Gson().fromJson(message, MessageRequest.class);
        Long toId = messageRequest.getToId();
        Long teamId = messageRequest.getTeamId();
        String text = messageRequest.getText();
        Integer chatType = messageRequest.getChatType();
        User fromUser = userService.getById(userId);
        Team team = teamService.getById(teamId);
        if (chatType == PRIVATE_CHAT){
            // 私聊
            privateChat(fromUser,toId,text,chatType);
        } else if (chatType == TEAM_CHAT) {
            // 队伍聊天
            teamChat(fromUser,text,team,chatType);
        }else {
            // 大厅聊天
            hallChat(fromUser,text,chatType);
        }
    }

    /**
     * 大厅聊天
     *
     * @param fromUser 发送方用户
     * @param text 消息体内容
     * @param chatType 私聊
     */
    private void hallChat(User fromUser, String text, Integer chatType) {
        WebSocketVo webSocketVo = new WebSocketVo();
        BeanUtils.copyProperties(fromUser,webSocketVo);
        MessageVo messageVo = new MessageVo();
        messageVo.setFormUser(webSocketVo);
//        messageVo.setToUser(new WebSocketVo());
//        messageVo.setTeamId(0L);
        messageVo.setText(text);
        messageVo.setChatType(chatType);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        messageVo.setCreateTime(ft.format(date));
        User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
        if (fromUser.getId() == loginUser.getId()){
            messageVo.setIsMy(true);
        }
        if (fromUser.getUserRole() == ADMIN_ROLE){
            messageVo.setIsAdmin(true);
        }
        String json = new Gson().toJson(messageVo);
        sendAllMessage(json);
        saveChat(fromUser.getId(),null,text,null, chatType);
        chatService.deleteKey(CACHE_CHAT_HALL, String.valueOf(fromUser.getId()));
    }

    @OnError
    public void onError(Session session,Throwable error){
        System.out.println("出现异常!");
        log.error("WebSocket error:" + error);
    }

    /**
     * 队伍聊天
     *
     * @param fromUser 发送发用户
     * @param text 聊天信息
     * @param team 队伍
     * @param chatType 聊天类型
     */
    private void teamChat(User fromUser, String text, Team team, Integer chatType) {
        ConcurrentHashMap<String, WebSocket> webSocketConcurrentHashMap = ROOMS.get(team.getId().toString());
        if (webSocketConcurrentHashMap == null){
            sendOneMessage(fromUser.toString(),"队伍不存在");
            return;
        }
        WebSocketVo webSocketVo = new WebSocketVo();
        BeanUtils.copyProperties(fromUser,webSocketVo);
        // 构建消息体
        MessageVo messageVo = new MessageVo();
        messageVo.setFormUser(webSocketVo);
//        messageVo.setToUser(new WebSocketVo());
        messageVo.setTeamId(team.getId());
        messageVo.setText(text);
        messageVo.setChatType(chatType);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        messageVo.setCreateTime(ft.format(date));
//        messageVo.setCreateTime(DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm:ss"));

        User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
        if (loginUser.getId() == fromUser.getId()){
            messageVo.setIsMy(true);
        }
        if (fromUser.getId() == team.getUserId() || fromUser.getId() == ADMIN_ROLE){
            messageVo.setIsAdmin(true);
        }
        String toJson = new Gson().toJson(messageVo);
        try {
            broadcast(String.valueOf(team.getId()),toJson);
            saveChat(fromUser.getId(),null,text, team.getId(), chatType);
            chatService.deleteKey(CACHE_CHAT_TEAM, String.valueOf(team.getId()));
            log.info("队伍聊天，用户{}发送给=,队伍={},在线:{}人",fromUser.getId(),team.getId(),onlineCount.get());
        }catch (Exception e){
            log.error("WebSocket teamChat error:",e);
        }

    }

    /**
     * 私聊
     *
     * @param fromUser  发送方用户
     * @param toId  目标用户id
     * @param text 发送内容
     * @param chatType 聊天类型
     */
    private void privateChat(User fromUser, Long toId, String text, Integer chatType) {
        Session toSession = SESSION_POOL.get(toId.toString());
        if (toSession != null){
//            MessageVo messageVo = chatService.chatResult(fromUser.getId(), toId, text, chatType, DateUtil.date(System.currentTimeMillis()));
            MessageVo messageVo = new MessageVo();
            // 获得发送方websocketvo类型 姓名 账号 头像
            WebSocketVo fromWebSocketVo = new WebSocketVo();
            BeanUtils.copyProperties(fromUser,fromWebSocketVo);
            messageVo.setFormUser(fromWebSocketVo);
            // 获得接收方websocketvo类型 姓名 账号 头像
            WebSocketVo toWebSocketVo = new WebSocketVo();
            BeanUtils.copyProperties(userMapper.selectById(toId),toWebSocketVo);
            messageVo.setToUser(toWebSocketVo);
            messageVo.setText(text);
            messageVo.setIsMy(false);
            messageVo.setChatType(chatType);
            messageVo.setIsAdmin(fromUser.getUserRole() == ADMIN_ROLE);
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            messageVo.setCreateTime(ft.format(new Date()));
            User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
            if (loginUser.getId() == fromUser.getId()){
                messageVo.setIsMy(true);
            }
            String toJson = new Gson().toJson(messageVo);
            sendOneMessage(String.valueOf(toId),toJson);
            log.info("发送给用户id={}，消息：{}",toId,toJson);
        }else {
            log.info("用户id={}不在线",toId);
        }
        saveChat(fromUser.getId(), toId, text, null, chatType);
        chatService.deleteKey(CACHE_CHAT_PRIVATE, fromUser.getId() + "to" + toId);
        chatService.deleteKey(CACHE_CHAT_PRIVATE, toId + "to" + fromUser.getId());
    }

    private void saveChat(long id, Long toId, String text, Long teamId, Integer chatType) {
        // 判断用户是不是你的好友
        User loginUser = userService.getById(id);
        if (chatType == PRIVATE_CHAT){
            String friendsIds = loginUser.getFriendsIds();
            if (!friendsIds.contains(toId.toString())){
                sendError(String.valueOf(id),"用户id={}不是你的好友"+ toId);
                return;
            }
        }
        // 保存聊天信息到数据库
        Chat chat = new Chat();
        chat.setSendUserId(id);
        chat.setRecUserId(toId);
        chat.setContent(text);
        chat.setSendUserName(loginUser.getUserName());
        chat.setChatType(chatType);
        chat.setIsRead(0);
        chat.setTeamId(teamId);
        chatService.save(chat);
    }

    /**
     * 发送所有在线用户消息
     */
    private void sendAllUsers() {
        log.info("[WebSocket消息] 发送所有在线用户的信息");
        HashMap<String,List<WebSocketVo>> listHashMap = new HashMap<>();
        List<WebSocketVo> webSocketList = new ArrayList<>();
        listHashMap.put("users",webSocketList);
        for (Serializable key : SESSION_POOL.keySet()){
            User user = userService.getById(key);
            WebSocketVo webSocketVo = new WebSocketVo();
            BeanUtils.copyProperties(user,webSocketVo);
            webSocketList.add(webSocketVo);
        }
        sendAllMessage(new Gson().toJson(listHashMap));
    }

    public void sendError(String userId, String errorMessage){
        sendOneMessage(userId,errorMessage);
    }

    /**
     * 此为单点消息
     *
     * @param userId  用户编号
     * @param message 消息
     */
    public void sendOneMessage(String userId, String message) {
        Session session = SESSION_POOL.get(userId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    log.info("【WebSocket消息】单点消息：" + message);
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendAllMessage(String message){
        log.info("[WebSocket消息] 广播消息：" + message);
        for (Session session : SESSIONS){
            try {
                if (session.isOpen()){
                    synchronized (session){
                        session.getBasicRemote().sendText(message);
                    }
                }
            } catch (IOException e) {
                log.error("sendAllMessage error",e);
            }
        }
    }

    /**
     * 队伍内群发消息
     *
     * @param teamId 队伍id
     * @param msg 消息体
     */
    public static void broadcast(String teamId, String msg){
        Map<String, WebSocket> stringWebSocketMap = ROOMS.get(teamId);
        //
        for (String key : stringWebSocketMap.keySet()){
            try {
                WebSocket webSocket = stringWebSocketMap.get(key);
                webSocket.sendMessage(msg);
            }catch (Exception e){
                log.error("WebSocket broadcast error:",e);
            }
        }
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
    public synchronized void sendMessage(MessageVo message){
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



    //    /**
//     * WebSocket连接时调用的方法
//     *
//     * @param session 客户端与服务器的会话
//     * @param userId  当前会话的用户id
//     */
//    @OnOpen
//    public void onOpen(Session session,
//                       @PathParam(value = "userId") String userId,
//                       @PathParam(value = "teamId") String teamId,
//                       EndpointConfig config){
//        try {
////            // 将 config 里的 HttpSession取出
////            HttpSession userHttpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
////            // 从 HttpSession 中获取当前登录用户信息
////            User user = (User) userHttpSession.getAttribute(USER_LOGIN_STATE);
////            // 提取信息
////            if (user!=null){
////                this.session = session;
////                this.userId = userId;
////                userList.add(userId);
////            }
//            this.session = session;
//            this.userId = userId;
//            userList.add(userId);
//            // 构建聊天室 0 - 所有聊天用户 roomId - 队伍id
//            List<String> roomIdList = userTeamService.findTeamList(Long.valueOf(userId));
//            for (String romeId : roomIdList){
//                Map<String, WebSocket> chatWebSocketControllers = webSocketSets.getOrDefault(romeId,new ConcurrentHashMap<>());
//                chatWebSocketControllers.put(userId,this);
//                webSocketSets.put(romeId,chatWebSocketControllers);
//            }
//            onlineCount.incrementAndGet();
//            log.info("有新连接加入！" + userId + "当前在线用户数为" + onlineCount.get());
//            System.out.println("有新连接加入！" + userId + "当前在线用户数为" + onlineCount.get());
//            // 读取离线期间收到的消息
//            List<MyMessage> unreadMessages = chatService.findUnreadMessage(Long.valueOf(userId));
//            for (MyMessage unreadMessage : unreadMessages){
//                sendInfo(unreadMessage);
//            }
//        } catch (Exception e){
//            log.error("WebSocket onOpen error",e);
//        }
//    }

//    /**
//     * 结束会话，并关闭连接
//     *
//     * @param userId 要关闭连接的用户id
//     */
//    @OnClose
//    public void onClose(@PathParam("userId") String userId){
//        try {
//            List<String> roomIdList = userTeamService.findTeamList(Long.valueOf(userId));
//            for (String romeId : roomIdList){
//                Map<String, WebSocket> chatWebSocketControllers = webSocketSets.get(romeId);
//                chatWebSocketControllers.remove(userId);
//                webSocketSets.put(romeId,chatWebSocketControllers);
//            }
//            onlineCount.decrementAndGet();
//            System.out.println("当前会话结束");
//        }catch (Exception e){
//            log.error("WebSocket onClose error",e);
//        }
//
//    }

//    /**
//     * 收到客户端的消息后调用的方法
//     *
//     * @param message 客户端发送的消息
//     * @param userId 客户端的用户id
//     */
//    @OnMessage
//    public void onMessage(String message, @PathParam(value = "userId") String userId, @PathParam(value = "teamId") String teamId){
//        try {
//            // 心跳包机制,防止长时间连接没有传输导致连接关闭
//            if ("PING".equals(message)) {
//                this.session.getAsyncRemote().sendText("PING");
//                return;
//            }
//            // 创建gson
//            Gson gson = new Gson();
//            // 将 String 转换为 MyMessage 类
//            MessageVo myMessage = gson.fromJson(message, MessageVo.class);
//            // 读取消息类里的信息
//            String content = myMessage.getContent();
//            // 读取用户名
//            String userName = myMessage.getUserName();
//            // 读取消息类型，单聊还是群聊
//            int messageType = myMessage.getMode();
//            // 如果是单聊
//            if (messageType == 1){
//                // 获取目标用户的id
//                String recUserId = myMessage.getRecUserId();
//                // 发送消息
//                sendInfo(content,recUserId,userId,userName);
//            } else if (messageType == 2) {
//                // 群聊
//                sendGroupInfo(content,teamId,userId);
//            }
//        }catch (Exception e){
//            log.error("WebSocket onMessage error",e);
//        }
//
//    }


//    /**
//     * 单聊
//     *
//     * @param content   发送的消息
//     * @param recUserId 接收方用户id
//     * @param userId    发送发用户id
//     * @param userName
//     */
//    public void sendInfo(String content, String recUserId, String userId, String userName){
//        MessageVo myMessage = new MessageVo();
//        myMessage.setContent(content);
//        myMessage.setMode(1);
//        myMessage.setRoomId("0");
//        myMessage.setUserName(userName);
//        myMessage.setSendTime(new Date());
//        myMessage.setSendUserId(userId);
//        myMessage.setRecUserId(recUserId);
//        Map<String, WebSocket> stringChatWebSocketControllerMap = webSocketSets.get("0");
//        if (stringChatWebSocketControllerMap.containsKey(recUserId)){
//            stringChatWebSocketControllerMap.get(recUserId).sendMessage(myMessage.toString());
//            // 1 - 已读 0 - 未读
//            chatService.saveMessage(myMessage,1);
//        }else {
//            // 目标未上线，先将数据保存到数据库中，等用户上线后发送
//            chatService.saveMessage(myMessage,0);
//        }
//    }

//    /**
//     * 根据消息体发送消息
//     *
//     * @param myMessage 消息体
//     */
//    public void sendInfo(MessageVo myMessage){
//        synchronized (webSocketSets.get("0").get(myMessage.getRecUserId())){
//            webSocketSets.get("0").get(myMessage.getRecUserId()).sendMessage(myMessage.toString());
//        }
//    }

//    /**
//     * 给指定群组发送消息
//     *
//     * @param content  消息内容
//     * @param roomId   队伍id
//     * @param sendUserId  发送信息的用户id
//     */
//    public void sendGroupInfo(String content, String roomId, String sendUserId){
//        MessageVo myMessage = new MessageVo();
//        myMessage.setContent(content);
//        myMessage.setMode(2);
//        myMessage.setSendTime(new Date());
//        myMessage.setSendUserId(sendUserId);
//        myMessage.setRecUserId(sendUserId);
//        User user = userMapper.selectById(sendUserId);
//        myMessage.setUserName(user.getUserName());
//        myMessage.setRoomId(roomId);
//        for (String userId : webSocketSets.get(roomId).keySet()){
//            if (!Objects.equals(sendUserId, userId)){
//                myMessage.setRecUserId(userId);
//                webSocketSets.get(roomId).get(userId).sendMessage(myMessage);
//            }
//        }
//
//    }

//    /**
//     * 给用户加入的所有群组发送消息
//     *
//     * @param content 发送的信息
//     * @param sendUserId 发送信息的用户id
//     */
//    public void sendGroupInfo(String content, String sendUserId){
//        Gson gson = new Gson();
//        MessageVo myMessage = new MessageVo();
//        myMessage.setContent(content);
//        myMessage.setMode(2);
//        myMessage.setSendTime(new Date());
//        myMessage.setSendUserId(sendUserId);
//        User user = userMapper.selectById(sendUserId);
//        myMessage.setUserName(user.getUserName());
//
//        List<String> roomList = userTeamService.findTeamList(Long.valueOf(sendUserId));
//        roomList.remove("0");
//        for (String roomId : roomList){
//            myMessage.setRoomId(roomId);
//            for (String userId : webSocketSets.get(roomId).keySet()){
//                if (!Objects.equals(sendUserId, userId)){
//                    myMessage.setRecUserId(userId);
//                    webSocketSets.get(roomId).get(userId).sendMessage(myMessage);
//                }
//            }
//        }
//
//    }

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

