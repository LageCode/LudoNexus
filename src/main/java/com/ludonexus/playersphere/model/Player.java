package com.ludonexus.playersphere.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)        
    private String username;

    @Column(nullable = false, unique = true, length = 255)        
    private String email;

    private Integer level = 1;
    private Integer totalPoints = 0;
}