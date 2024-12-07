package com.ludonexus.playersphere.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlayerDTO {
    //@NotNull
    private Long id;

    @Size(min = 3, message = "Username is too short.")
    @Size(max = 16, message = "Username is too long.") 
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Positive(message = "Level must always be higher than 0.")      // or @Min(1) [no built-in param to specifies error message] or Size(min = 1, message="error message")
    private Integer level = 1;

    @PositiveOrZero(message = "Total Points can't be negative.")        // or Min(0) or Size ...
    private Integer totalPoints = 0;

    private List<FriendDTO> friends = new ArrayList<>();
}