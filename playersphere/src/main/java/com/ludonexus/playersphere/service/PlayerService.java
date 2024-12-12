package com.ludonexus.playersphere.service;

import com.ludonexus.playersphere.dto.CreatePlayerRequestDTO;
import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.dto.PlayerFriendDTO;
import com.ludonexus.playersphere.dto.UpdatePlayerRequestDTO;
import com.ludonexus.playersphere.model.Friendship;
import com.ludonexus.playersphere.model.Player;
import com.ludonexus.playersphere.repository.FriendshipRepository;
import com.ludonexus.playersphere.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional      // Ensures that all DB operations succeed or fail together
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final FriendshipRepository friendshipRepository;

    public PlayerDTO createPlayer(CreatePlayerRequestDTO creationDTO) {
		if (playerRepository.existsByUsername(creationDTO.getUsername())) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (playerRepository.existsByEmail(creationDTO.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		
		Player player = new Player();
		BeanUtils.copyProperties(creationDTO, player);
		
        playerRepository.save(player);

		return convertToDTO(player);
	}

	public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
            .map(player -> {
                return convertToDTO(player);
            })
            .collect(Collectors.toList());
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        return convertToDTO(player);
    }

    public PlayerDTO updatePlayer(Long id, UpdatePlayerRequestDTO updateDTO) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            
        // Check if new username/email are taken by another player
        if (!player.getUsername().equals(updateDTO.getUsername()) 
            && playerRepository.existsByUsername(updateDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!player.getEmail().equals(updateDTO.getEmail()) 
            && playerRepository.existsByEmail(updateDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        BeanUtils.copyProperties(updateDTO, player, "id");
        player = playerRepository.save(player);

        return convertToDTO(player);
    }

    public void deletePlayer(Long id) {
        // friendshipRepository.findByPlayerId(id).forEach(friendship -> {
        //     removeFriend(friendship.getPlayer().getId(), friendship.getId());
        // });
        deleteFriendships(id);
        playerRepository.deleteById(id);
    }

     public void createFriendship(Long playerId, Long friendId) {
        if (playerId.equals(friendId)) {
            throw new IllegalArgumentException("A player cannot be friends with themselves");
        }

        if (friendshipRepository.existsByPlayerIdAndFriendId(playerId, friendId)) {
            throw new IllegalArgumentException("Friendship already exists");
        }

        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            
        Player friend = playerRepository.findById(friendId)
            .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        Friendship friendship1 = new Friendship();
        friendship1.setPlayer(player);
        friendship1.setFriend(friend);
        friendshipRepository.save(friendship1);

        Friendship friendship2 = new Friendship();
        friendship2.setPlayer(friend);
        friendship2.setFriend(player);
        friendshipRepository.save(friendship2);
    }

    public void deleteFriendShip(Long playerId, Long friendId) {
        friendshipRepository.deleteByPlayerIdAndFriendId(playerId, friendId);
        friendshipRepository.deleteByPlayerIdAndFriendId(friendId, playerId);
    }

    public void deleteFriendships(Long playerId) {
        // friendshipRepository.deleteByPlayerId(playerId);
        // friendshipRepository.deleteByFriendId(playerId);
        // refactored by 
        friendshipRepository.deleteByPlayerIdOrFriendId(playerId, playerId);
    }

    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO playerDTO = new PlayerDTO();
        BeanUtils.copyProperties(player, playerDTO);

        if (player.getFriendships() != null && !player.getFriendships().isEmpty()) {
            // TODO
            List<PlayerFriendDTO> friendDTOs = player.getFriendships().stream()
                .map(friendship -> {
                    Player friend = playerRepository.findById(friendship.getFriend().getId())
                        .orElseThrow(() -> new IllegalStateException("Friend not found: " + friendship.getFriend().getId()));

                PlayerFriendDTO friendDTO = new PlayerFriendDTO();
                BeanUtils.copyProperties(friend, friendDTO);
                return friendDTO;
            }).collect(Collectors.toList());
            playerDTO.setFriends(friendDTOs);
        } else {
            playerDTO.setFriends(new ArrayList<>());
        }

        return playerDTO;
    }
}