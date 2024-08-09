package com.example.friendsbackend.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class StringJsonListToLongSet {
    /**
     * 将字符串转为set
     *
     * @param jsonList
     * @return
     */
    public static Set<Long> stringJsonListToLongSet(String jsonList){
        Gson gson = new Gson();
        Set<Long> set = gson.fromJson(jsonList,new TypeToken<Set<Long>>() {
        }.getType());
        return Optional.ofNullable(set).orElse(new HashSet<>());
    }
}
