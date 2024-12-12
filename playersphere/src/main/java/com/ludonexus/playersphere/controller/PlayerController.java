package com.ludonexus.playersphere.controller;

import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.dto.PlayerIdRequestDTO;
import com.ludonexus.playersphere.dto.PlayerPointsRequestDTO;
import com.ludonexus.playersphere.exception.InvalidFriendshipException;
import com.ludonexus.playersphere.exception.PlayerAlreadyExistsException;
import com.ludonexus.playersphere.exception.PlayerNotFoundException;
import com.ludonexus.playersphere.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        try {
            List<PlayerDTO> players = playerService.getAllPlayers();
            return players.isEmpty() ? ResponseEntity.noContent().build()
                                   : ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(playerService.getPlayerById(id));
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO created = playerService.createPlayer(playerDTO);
            return ResponseEntity
                .created(URI.create("/api/players/" + created.getId()))
                .body(created);
        } catch (PlayerAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            return ResponseEntity.ok(playerService.updatePlayer(id, playerDTO));
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PlayerAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}/points")
    public ResponseEntity<PlayerDTO> updatePlayerTotalPoints(@PathVariable Long id, @Valid @RequestBody PlayerPointsRequestDTO pointsRequestDTO) {
        try {
            return ResponseEntity.ok(playerService.updatePlayerPoints(id, pointsRequestDTO));
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PlayerAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/friends")
    public ResponseEntity<PlayerDTO> addFriend(@PathVariable Long id, @Valid @RequestBody PlayerIdRequestDTO request) {
        try {
            return ResponseEntity.ok(playerService.addFriend(id, request.getPlayerId()));
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidFriendshipException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/friends")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, @Valid @RequestBody PlayerIdRequestDTO request) {
        try {
            playerService.removeFriend(id, request.getPlayerId());
            return ResponseEntity.noContent().build();
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}