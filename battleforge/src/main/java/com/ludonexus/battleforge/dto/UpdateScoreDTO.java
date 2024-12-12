package com.ludonexus.battleforge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateScoreDTO {    // Utilisé pour mettre à jour le score
    @NotNull(message = "Player id is required")
    private Long playerId;
    
    private Integer score;
    private Boolean victory;
}
