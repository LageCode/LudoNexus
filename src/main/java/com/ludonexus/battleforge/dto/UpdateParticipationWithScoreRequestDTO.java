package com.ludonexus.battleforge.dto;

import lombok.Data;

@Data
public class UpdateParticipationWithScoreRequestDTO {
	private Long playerId;
	private Integer score;
	public Boolean victory;
}
