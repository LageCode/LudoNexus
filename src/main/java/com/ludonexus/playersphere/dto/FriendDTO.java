package com.ludonexus.playersphere.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FriendDTO {
	private Long id;

	@Size(min = 3, message = "Username is too short.")
    @Size(max = 16, message = "Username is too long.")
	private String username;
}
