package com.example.friendsbackend.service.impl;


import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Test
    void userRegister() {
    }

    @Test
    void userLogin() {
    }

    @Test
    void getSafeUser() {
    }

    @Test
    void loginOut() {
    }

    @Test
    void tsetUserSearchByTag() {
        List<String> tagNameList = Arrays.asList("java");
        List<User> userList = userService.searchUserByTag(30,tagNameList);
        Assertions.assertNotNull(userList);

    }
}