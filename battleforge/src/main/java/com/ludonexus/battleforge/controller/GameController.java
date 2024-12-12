package com.ludonexus.battleforge.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ludonexus.battleforge.dto.GameDTO;
import com.ludonexus.battleforge.dto.ParticipationDTO;
import com.ludonexus.battleforge.dto.UpdateParticipationWithScoreRequestDTO;
import com.ludonexus.battleforge.service.GameService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody GameDTO gameDTO) {
        return ResponseEntity.ok(gameService.createGame(gameDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id,
            @Valid @RequestBody GameDTO gameDTO) {
        return ResponseEntity.ok(gameService.updateGame(id, gameDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participations")
    public ResponseEntity<List<ParticipationDTO>> getParticipations(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameById(id).getParticipations());
    }

    @PostMapping("/{id}/participations")
    public ResponseEntity<Void> addParticipations(
            @PathVariable Long id,
            @Valid @RequestBody ParticipationDTO participationDTO) {
                gameService.createParticipation(id, participationDTO.getPlayerId());
                return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/participations")
    public ResponseEntity<ParticipationDTO> updateParticipation(
        @PathVariable Long id,
        @Valid @RequestBody UpdateParticipationWithScoreRequestDTO participationRequestDTO) {
            return ResponseEntity.ok(gameService.updateParticipation(id, participationRequestDTO));
    }

    @DeleteMapping("/{gameId}/participations")
    public ResponseEntity<Void> removeGameParticipations(
        @PathVariable Long gameId) {
            gameService.removeGameParticipations(gameId);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/ofplayer")
    public ResponseEntity<Void> removePlayerParticipations(
            @RequestBody ParticipationDTO participationDTO) {
                gameService.removePlayerParticipations(participationDTO.getPlayerId());
                return ResponseEntity.noContent().build();
    }
}