package com.ludonexus.playersphere.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerIdRequestDTO {
    @NotNull(message = "Player id is required")
    private Long playerId;
} 
