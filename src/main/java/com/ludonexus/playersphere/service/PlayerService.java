package com.ludonexus.playersphere.service;

import com.ludonexus.playersphere.dto.FriendDTO;
import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.model.Friendship;
import com.ludonexus.playersphere.model.Player;
import com.ludonexus.playersphere.repository.FriendshipRepository;
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
    private final PlayerRepository playerRepository;
    private final FriendshipRepository friendshipRepository;

    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
		if (playerRepository.existsByUsername(playerDTO.getUsername())) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (playerRepository.existsByEmail(playerDTO.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		
		Player player = new Player();
		BeanUtils.copyProperties(playerDTO, player, "id", "totalPoints");
		
        playerRepository.save(player);

		return playerToDTO(player);
	}

	public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
            .map(player -> {
                return playerToDTO(player);
            })
            .collect(Collectors.toList());
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        return playerToDTO(player);
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            
        // Check if new username/email are taken by another player
        if (!player.getUsername().equals(playerDTO.getUsername()) 
            && playerRepository.existsByUsername(playerDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!player.getEmail().equals(playerDTO.getEmail()) 
            && playerRepository.existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        BeanUtils.copyProperties(playerDTO, player, "id");
        player = playerRepository.save(player);
       
        return playerToDTO(player);
    }

    public void deletePlayer(Long id) {
        // friendshipRepository.findByPlayerId(id).forEach(friendship -> {
        //     removeFriend(friendship.getPlayer().getId(), friendship.getId());
        // });
        removeFriendships(id);
        playerRepository.deleteById(id);
    }

     public void addFriend(Long playerId, Long friendId) {
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

    public void removeFriendShip(Long playerId, Long friendId) {
        friendshipRepository.deleteByPlayerIdAndFriendId(playerId, friendId);
        friendshipRepository.deleteByPlayerIdAndFriendId(friendId, playerId);
    }

    public void removeFriendships(Long playerId) {
        // friendshipRepository.deleteByPlayerId(playerId);
        // friendshipRepository.deleteByFriendId(playerId);
        // refactored by 
        friendshipRepository.deleteByPlayerIdOrFriendId(playerId, playerId);
    }

    public PlayerDTO playerToDTO(Player player) {
        PlayerDTO playerDTO = new PlayerDTO();
        BeanUtils.copyProperties(player, playerDTO);

        List<FriendDTO> friendDTOs = player.getFriendships().stream()
            .map(friendship -> {
                Player friend = playerRepository.findById(friendship.getFriend().getId())
                    .orElseThrow(() -> new IllegalStateException("Friend not found: " + friendship.getFriend().getId()));

            return FriendDTO.builder()
                .id(friend.getId())
                .username(friend.getUsername())
                .build();
        }).collect(Collectors.toList());
        playerDTO.setFriends(friendDTOs);

        return playerDTO;
    }
}