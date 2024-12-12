package com.ludonexus.battleforge.dto;

import lombok.Data;

@Data
public class ParticipationDTO {    // Utilisé pour afficher une participation
    private Long playerId;
    private Long gameId;
    private Integer score;
    private Boolean victory;
}
