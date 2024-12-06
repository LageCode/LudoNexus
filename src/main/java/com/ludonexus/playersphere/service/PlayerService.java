package com.ludonexus.playersphere.service;

import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.model.Player;
import com.ludonexus.playersphere.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional      // Ensures that all DB operations succeed or fail together
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;

    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
		if (repository.existsByUsername(playerDTO.getUsername())) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (repository.existsByEmail(playerDTO.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		
		Player player = new Player();
		BeanUtils.copyProperties(playerDTO, player, "id");
		
		player = repository.save(player);
		BeanUtils.copyProperties(player, playerDTO);
		return playerDTO;
	}

	public List<PlayerDTO> getAllPlayers() {
        return repository.findAll().stream()
            .map(player -> {
                PlayerDTO dto = new PlayerDTO();
                BeanUtils.copyProperties(player, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        PlayerDTO dto = new PlayerDTO();
        BeanUtils.copyProperties(player, dto);
        return dto;
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            
        // Check if new username/email are taken by another player
        if (!player.getUsername().equals(playerDTO.getUsername()) 
            && repository.existsByUsername(playerDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!player.getEmail().equals(playerDTO.getEmail()) 
            && repository.existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        BeanUtils.copyProperties(playerDTO, player, "id");
        player = repository.save(player);
        BeanUtils.copyProperties(player, playerDTO);
        return playerDTO;
    }

    public void deletePlayer(Long id) {
        repository.deleteById(id);
    }
}