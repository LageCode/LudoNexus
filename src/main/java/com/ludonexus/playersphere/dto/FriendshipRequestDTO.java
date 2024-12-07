package com.ludonexus.playersphere.dto;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class FriendshipRequestDTO {
    private Long id;
    private List<Long> ids;

    @AssertTrue(message = "Either 'id' or 'ids' must be provided, but not both")
    private boolean isValid() {
        if (id == null && (ids == null || ids.isEmpty())) {
            return false; // Aucun des deux n'est fourni
        }
        if (id != null && ids != null && !ids.isEmpty()) {
            return false; // Les deux sont fournis
        }
        return true; // L'un ou l'autre est fourni
    }
}