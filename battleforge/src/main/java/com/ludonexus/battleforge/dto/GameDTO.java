package com.ludonexus.battleforge.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ludonexus.battleforge.model.GameType;

import lombok.Data;

@Data
public class GameDTO {
	private Long id;
	private LocalDateTime datetime = LocalDateTime.now();
	private GameType gameType;
	private Integer maxScore;
	private Long hostId;
	private List<GameParticipationDTO> participations;
}
