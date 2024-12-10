package com.ludonexus.battleforge.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class GameDTO {
	private Long id;
	private LocalDateTime datetime = LocalDateTime.now();
	private String gameType;
	private Integer maxScore;
	private Long hostId;
	private List<GameParticipationDTO> participations;
}
