package com.ludonexus.battleforge.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "games")
public class Game {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
    private LocalDateTime datetime = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType;
    
    @Column(name = "max_score")
    private Integer maxScore = 0;
    
    @Column(name = "host_id")
    private Long hostId;
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    public void updateMaxScore() {
        Integer newMS = 0;
        for (Participation participation : participations) {
            if (participation.getScore() != null && participation.getScore() > newMS) {
                newMS = participation.getScore();
            }
        }
        this.maxScore = newMS;
    }
}
