package com.ludonexus.battleforge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameParticipationDTO {
	private Long id;
	private Long gameId;
	private Long playerId;
	private Integer score;
	private Boolean victory;
}
