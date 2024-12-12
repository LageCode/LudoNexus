package com.ludonexus.playersphere.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UpdatePlayerRequestDTO extends CreatePlayerRequestDTO {
	@Positive(message = "Total points must always be higher or equal to 0.")
	private Integer totalPoints;
}
