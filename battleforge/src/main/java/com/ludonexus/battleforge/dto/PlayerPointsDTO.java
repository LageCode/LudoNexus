package com.ludonexus.battleforge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerPointsDTO {    // Utilisé pour synchroniser avec PlayerSphere
    @NotNull(message = "Points to add is required")
    private Integer points;
}
