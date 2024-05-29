package com.example.friendsbackend.modal.request;

import com.example.friendsbackend.common.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class TeamQueryRequest extends PageRequest {

    private static final long serialVersionUID = -1694946533012798843L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍 id 列表
     */
    private List<Long> idList;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 关键词
     */
    private String searchText;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;



    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

}
