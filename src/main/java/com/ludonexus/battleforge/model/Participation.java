package com.ludonexus.battleforge.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "participations")
public class Participation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    private Integer score;
    
    private Boolean victory;
}
