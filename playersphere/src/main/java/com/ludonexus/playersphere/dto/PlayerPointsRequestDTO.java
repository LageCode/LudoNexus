package com.ludonexus.playersphere.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PlayerPointsRequestDTO {
	@Min(0)
    private Integer points;
}
