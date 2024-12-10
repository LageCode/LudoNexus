package com.ludonexus.playersphere.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)        
    private String username;

    @Column(nullable = false, unique = true, length = 255)        
    private String email;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private Integer totalPoints = 0;

    @Builder.Default
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendships = new ArrayList<>();
}