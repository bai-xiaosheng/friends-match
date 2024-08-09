package com.example.friendsbackend.modal.vo;

import lombok.Data;
import com.example.friendsbackend.modal.domain.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @Author: QiMu
 * @Date: 2023年03月10日 22:13
 * @Version: 1.0
 * @Description:
 */
@Data
public class TeamVo implements Serializable {

    private static final long serialVersionUID = 8860605873381253366L;

    private Long id;

    private String name;

    private String teamAvatarUrl;

    private String password;

    private String description;

    private Integer maxNum;

    private Date expireTime;

    private Integer status;

    private Date createTime;

    private String announce;

    private User user;

    private int hasJoinNum;

    private Set<User> userSet;
}
