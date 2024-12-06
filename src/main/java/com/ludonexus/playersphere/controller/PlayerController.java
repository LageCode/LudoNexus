package com.ludonexus.playersphere.controller;

import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService service;

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        // @Valid checks PlayerDTO constraints
        // @Requestbody thanks to @RestController makes request body accessible through playerDTO variable
        return ResponseEntity.ok(service.createPlayer(playerDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPlayerById(id));
    }

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() { return ResponseEntity.ok(service.getAllPlayers()); }

    /* Endpoints for get, update and delete are the same, that's the request type that defines the action to apply on specififed resource */

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id,
            @Valid @RequestBody PlayerDTO playerDTO) {
        return ResponseEntity.ok(service.updatePlayer(id, playerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        service.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}