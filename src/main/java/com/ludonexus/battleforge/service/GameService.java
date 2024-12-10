package com.ludonexus.battleforge.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.ludonexus.battleforge.dto.GameDTO;
import com.ludonexus.battleforge.dto.GameParticipationDTO;
import com.ludonexus.battleforge.dto.UpdateParticipationWithScoreRequestDTO;
import com.ludonexus.battleforge.model.Game;
import com.ludonexus.battleforge.model.Participation;
import com.ludonexus.battleforge.repository.GameRepository;
import com.ludonexus.battleforge.repository.ParticipationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
   private final GameRepository gameRepository;
   private final ParticipationRepository participationRepository;

   public GameDTO createGame(GameDTO gameDTO) {
       Game game = new Game();
       BeanUtils.copyProperties(gameDTO, game, "id", "maxScore", "participation");

       Participation hostParticipation = new Participation();
       hostParticipation.setGame(game);
       hostParticipation.setPlayerId(gameDTO.getHostId());
       hostParticipation.setScore(null);
       hostParticipation.setVictory(null);

       game.getParticipations().add(hostParticipation);
       gameRepository.save(game);

       return gameToDTO(game);
   }

   public List<GameDTO> getAllGames() {
       List<GameDTO> dtos = new ArrayList<>();
       for (Game game : gameRepository.findAll()) {
           dtos.add(gameToDTO(game));
       }
       return dtos;
   }

   public GameDTO getGameById(Long gameId) {
       Game game = gameRepository.findById(gameId)
           .orElseThrow(() -> new IllegalArgumentException("Game not found"));
       return gameToDTO(game);
   }

   public GameDTO updateGame(Long gameId, GameDTO gameDTO) {
    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Game not found"));

    BeanUtils.copyProperties(gameDTO, game, "id");   
    game = gameRepository.save(game);
    return gameToDTO(game);
}

   public void deleteGame(Long gameId) {
       gameRepository.deleteById(gameId);
       removeGameParticipations(gameId);
   }

   public GameParticipationDTO updateParticipation(Long gameId, UpdateParticipationWithScoreRequestDTO participationRequestDTO) {
       Game game = gameRepository.getGameById(gameId)
           .orElseThrow(() -> new IllegalArgumentException("Game not found"));
       
       Participation participation = participationRepository.getByGameIdAndPlayerId(gameId, participationRequestDTO.getPlayerId())
           .orElseThrow(() -> new IllegalArgumentException("Participation not found"));

       if (participationRequestDTO.getScore() > game.getMaxScore()) {
           game.setMaxScore(participationRequestDTO.getScore());
           gameRepository.save(game);
       }

       BeanUtils.copyProperties(participationRequestDTO, participation);
       participation = participationRepository.save(participation);
       return participationToDTO(participation);
   }

   public void createParticipation(Long gameId, Long playerId) {
       Game game = gameRepository.getGameById(gameId)
           .orElseThrow(() -> new IllegalArgumentException("Game not found"));
           
       if (participationRepository.existsByGameIdAndPlayerId(gameId, playerId)) {
           throw new IllegalArgumentException("Player already participating");
       }

       Participation participation = new Participation();
       participation.setGame(game);
       participation.setPlayerId(playerId);
       participation.setScore(null);
       participation.setVictory(null);

       participationRepository.save(participation);
   }

   public void removeGameParticipations(Long gameId) {
       participationRepository.deleteByGameId(gameId);
       updateGameMaxScore(gameId);
   }

   public void removePlayerParticipations(Long playerId) {
       List<Game> hostedGames = gameRepository.findAllByHostId(playerId);
       for (Game game : hostedGames) {
           game.setHostId(null);
           gameRepository.save(game);
       }

       participationRepository.deleteByPlayerId(playerId);
   }

   private void updateGameMaxScore(Long gameId) {
       Game game = gameRepository.findById(gameId)
           .orElseThrow(() -> new IllegalArgumentException("Game not found"));

       Integer maxScore = 0;
       for (Participation participation : game.getParticipations()) {
           if (participation.getScore() != null && participation.getScore() > maxScore) {
               maxScore = participation.getScore();
           }
       }

       game.setMaxScore(maxScore);
       gameRepository.save(game);
   }

   private GameDTO gameToDTO(Game game) {
       GameDTO gDTO = new GameDTO();
       BeanUtils.copyProperties(game, gDTO, "participations");

       List<GameParticipationDTO> participationDTOs = new ArrayList<>();
       for (Participation participation : game.getParticipations()) {
           participationDTOs.add(participationToDTO(participation));
       }
       gDTO.setParticipations(participationDTOs);

       return gDTO;
   }

   private GameParticipationDTO participationToDTO(Participation participation) {
       GameParticipationDTO pDTO = new GameParticipationDTO();
       BeanUtils.copyProperties(participation, pDTO);
       pDTO.setGameId(participation.getGame().getId());
       return pDTO;
   }
}