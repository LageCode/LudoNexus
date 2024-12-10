package com.ludonexus.battleforge.dto;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class IdRequestDTO {
	private Long id;
    private List<Long> ids;

    @AssertTrue(message = "Either 'id' or 'ids' must be provided, but not both")
    private boolean isValid() {
        if (id == null && (ids == null || ids.isEmpty())) {
            return false; // No one
        }
        if (id != null && ids != null && !ids.isEmpty()) {
            return false; // Both 
        }
        return true; // One or the other (XOR)
    }
}
