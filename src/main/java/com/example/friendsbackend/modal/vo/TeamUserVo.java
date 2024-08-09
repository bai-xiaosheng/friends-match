package com.example.friendsbackend.modal.vo;

import lombok.Data;

import java.io.Serializable;

import java.util.Set;

@Data
public class TeamUserVo implements Serializable {
    private static final long serialVersionUID = 4408963399165943029L;

    private Set<TeamVo> teamSet;
}

