package com.example.friendsbackend.common;

import lombok.Data;

import java.io.Serializable;
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 8518981356416640440L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前页数
     */
    protected int pageNum = 1;
}
