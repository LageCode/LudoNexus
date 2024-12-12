package com.ludonexus.battleforge.dto;

import java.util.ArrayList;
import java.util.List;

import com.ludonexus.battleforge.model.GameType;

import lombok.Data;

@Data
public class GameDTO {    // Utilisé pour créer/afficher un jeu
    private Long id;
    private GameType gameType;
    private Long hostId;
    private List<ParticipationDTO> participations = new ArrayList<>();
}
