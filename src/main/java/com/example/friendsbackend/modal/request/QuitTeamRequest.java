package com.example.friendsbackend.modal.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class QuitTeamRequest implements Serializable {

    private static final long serialVersionUID = 1106167152765698596L;

    /**
     * id
     */
    private Long teamId;
}
