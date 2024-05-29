package com.example.friendsbackend.service.impl;

import com.example.friendsbackend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void test(){

        String string1 = "java";
        String string2 = "python";
        String string3 = "java";
        System.out.println(AlgorithmUtils.minDistance(string1, string2));
        System.out.println(AlgorithmUtils.minDistance(string1, string3));

        List<String> list1 = Arrays.asList("java", "python");
        List<String> list2 = Arrays.asList("java","python");
        System.out.println(AlgorithmUtils.minDistance(list1, list2));
    }
}
