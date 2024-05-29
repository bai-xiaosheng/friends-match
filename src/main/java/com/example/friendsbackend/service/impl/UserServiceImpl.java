package com.example.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.exception.BusinessException;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.friendsbackend.constant.Constant.ADMIN_ROLE;
import static com.example.friendsbackend.constant.Constant.USER_LOGIN_STATE;

/**
* @author BDS
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-05-12 15:46:39
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    public static final String SALT = "YUPI";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String plantId) {
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword))
            return 0;
        if (userAccount.length() < 4) {
            throw new BusinessException(Code.PARAMS_ERROR,"账户长度小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(Code.PARAMS_ERROR,"密码长度小于8位");
        }
        //账户不能包含特殊符号
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(Code.PARAMS_ERROR,"账户包含特殊字符");
        }
        //两次密码校验
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(Code.PARAMS_ERROR,"两次密码不一致");
        }
        //星球id不能重复
        QueryWrapper<User> queryWrapperId = new QueryWrapper<>();
        queryWrapperId.eq("plantId", plantId);
        long countId = userMapper.selectCount(queryWrapperId);
        if (countId > 0) {
            throw new BusinessException(Code.PARAMS_ERROR,"当前星球账户已注册");
        }
        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(Code.PARAMS_ERROR,"当前账户名已注册");
        }
        //2.密码加密
        String savePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(savePassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(Code.SAVE_ERROR,"请重试");
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request 客户端请求
     * @return 用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(Code.PARAMS_ERROR,"输入参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(Code.PARAMS_ERROR,"账户长度小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(Code.PARAMS_ERROR,"密码长度小于8位");
        }
        //账户不能包含特殊符号
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(Code.PARAMS_ERROR,"账户包含特殊字符");
        }

        //2.密码加密
        String savePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //检查用户账号信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", savePassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot math userPassword");
            throw new BusinessException(Code.PARAM_NULL_ERROR,"数据库中不包含该账户");
        }
        //3.用户信息脱敏
        User safeUser = getSafeUser(user);
        //4.记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        //返回脱敏后的用户信息
        return safeUser;

    }
    @Override
    public User getSafeUser(User originUser){
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setUserName(originUser.getUserName());
        safeUser.setUserUrl(originUser.getUserUrl());
        safeUser.setProfile(originUser.getProfile());
        safeUser.setGender(originUser.getGender());
        safeUser.setPlantId(originUser.getPlantId());
        safeUser.setTags(originUser.getTags());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setUserRole(originUser.getUserRole());
        return safeUser;
    }

    @Override
    public int loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    /**
     * @param tagsNameList 标签列表
     * @return 具有搜索标签的用户
     */
    @Override
    public List<User> userSearchByTag(List<String> tagsNameList){
        if (CollectionUtils.isEmpty(tagsNameList)){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
//        //1.sql查询   like "%java%"
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName:tagsNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        //用户信息脱敏
        return  userList.stream().map(this::getSafeUser).collect(Collectors.toList());
        //内存查询
        //1.先将所有数据放到内存中
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userMapper.selectList(queryWrapper);
//        Gson gson = new Gson();
        //2.判断
//        Set<String> tagNameSet = new HashSet<>(tagsNameList);
//        List<User> filterUser = new ArrayList<>();
//        for (User user:userList){
//            String tags = user.getTags();
//            if (StringUtils.isBlank(tags)){
//                continue;
//            }
//            Set<String> tempTagName = gson.fromJson(tags,new TypeToken<Set<String>>(){}.getType());
//            boolean containAllTags = true;
//            for (String tagName : tagNameSet){
//                if (!tempTagName.contains(tagName)){
//                    containAllTags = false;
//                    break;
//                }
//            }
//            if (containAllTags){
//                filterUser.add(user);
//            }
//        }
//        return filterUser;

//        return  userList.stream().filter(user -> {
//            String tags = user.getTags();
//            if (StringUtils.isBlank(tags)) {
//                return false;
//            }
//            Set<String> tempTagName = gson.fromJson(tags, new TypeToken<Set<String>>() {
//            }.getType());
//            for (String tagName : tagsNameList) {
//                if (!tempTagName.contains(tagName)) {
//                    return false;
//                }
//            }
//            return true;
//        }).map(this::getSafeUser).collect(Collectors.toList());

    }

    @Override
    public int updateUser(User user, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (user == null){
            throw new BusinessException(Code.PARAM_NULL_ERROR);
        }
        //
        Long id = user.getId();
        if (id == null){
            throw new BusinessException(Code.PARAMS_ERROR,"未提供id");
        }
        //判断当前用户权限
        if (!isAdmin(loginUser) && loginUser.getId() != user.getId()){
            throw new BusinessException(Code.NO_AUTH);
        }

        //修改信息
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(Code.NO_LOGIN);
        }
        User userLogin = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userLogin == null){
            throw new BusinessException(Code.NO_LOGIN);
        }
        return userLogin;
    }

    /**
     * 检查用户权限
     *
     * @param request session
     * @return 是否为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null || currentUser.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User user){
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        // 1. 查询到所有用户 id 和标签
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        // 2. 获取用户标签，使用 gson 将 json 格式数据转换为字符串列表
        long loginUserId = loginUser.getId();
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> loginUserTagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 3. 根据标签查询相似度高的用户
        // 存储用户信息与其对应的相似度得分
        List<Pair<User,Integer>> list = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            if (StringUtils.isBlank(userTags) || user.getId() == loginUserId){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            int minDistance = AlgorithmUtils.minDistance(loginUserTagList, userTagList);
            list.add(new Pair<>(user,minDistance));
        }
        // 对 list 中的数据进行排序
        List<Pair<User, Integer>> topUserPairList = list.stream()
                .sorted(Comparator.comparingInt(Pair::getValue))
                .limit(num)
                .collect(Collectors.toList());
        // 读取当前 list 中用户对应的 id
        List<Long> topUserIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        // 读取 id 列表对应用户信息并且将用户信息脱敏
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",topUserIdList);
        Map<Long, List<User>> userIdListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafeUser)
                .collect(Collectors.groupingBy(User::getId));
        //
        List<User> resultList = new ArrayList<>();
        for (Long userId : topUserIdList) {
            resultList.add(userIdListMap.get(userId).get(0));
        }
        return resultList;
    }
}




