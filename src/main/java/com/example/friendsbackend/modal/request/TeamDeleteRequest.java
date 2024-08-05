package com.example.friendsbackend.modal.request;


import lombok.Data;

import java.io.Serializable;
@Data
public class TeamDeleteRequest implements Serializable {
    private static final long serialVersionUID = -4147434079973828035L;
    /**
     * id
     */
    private Long id;
}
