package com.ludonexus.playersphere.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlayerDTO {
    Long id;
    String username;
    String email;
    Integer level;
    Integer totalPoints;
    List<PlayerFriendDTO> friends;
}
