package com.ludonexus.playersphere.service;

import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.dto.PlayerPointsRequestDTO;
import com.ludonexus.playersphere.exception.InvalidFriendshipException;
import com.ludonexus.playersphere.exception.PlayerAlreadyExistsException;
import com.ludonexus.playersphere.exception.PlayerNotFoundException;
import com.ludonexus.playersphere.dto.FriendDTO;
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
@Transactional
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final FriendshipRepository friendshipRepository;

    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        if (playerRepository.existsByUsername(playerDTO.getUsername())) {
            throw new PlayerAlreadyExistsException("Username already exists: " + playerDTO.getUsername());
        }
        if (playerRepository.existsByEmail(playerDTO.getEmail())) {
            throw new PlayerAlreadyExistsException("Email already exists: " + playerDTO.getEmail());
        }

        Player player = new Player();
        BeanUtils.copyProperties(playerDTO, player, "id", "friends");
        player = playerRepository.save(player);
        return toDTO(player);
    }

    public PlayerDTO getPlayerById(Long id) {
        return toDTO(findPlayerById(id));
    }

    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = findPlayerById(id);
        
        if (!player.getUsername().equals(playerDTO.getUsername()) 
                && playerRepository.existsByUsername(playerDTO.getUsername())) {
            throw new PlayerAlreadyExistsException("Username already exists: " + playerDTO.getUsername());
        }
        if (!player.getEmail().equals(playerDTO.getEmail()) 
                && playerRepository.existsByEmail(playerDTO.getEmail())) {
            throw new PlayerAlreadyExistsException("Email already exists: " + playerDTO.getEmail());
        }

        BeanUtils.copyProperties(playerDTO, player, "id", "friends");
        player = playerRepository.save(player);
        return toDTO(player);
    }

    public PlayerDTO updatePlayerPoints(Long id, PlayerPointsRequestDTO pointsRequestDTO) {
        Player player = findPlayerById(id);
        player.setTotalPoints(pointsRequestDTO.getPoints());
        player = playerRepository.save(player);
        return toDTO(player);
    }

    public PlayerDTO addFriend(Long playerId, Long friendId) {
        if (playerId.equals(friendId)) {
            throw new InvalidFriendshipException("Player cannot be friends with themselves");
        }

        Player player = findPlayerById(playerId);
        Player friend = findPlayerById(friendId);

        if (friendshipRepository.existsByPlayerIdAndFriendId(playerId, friendId)) {
            throw new InvalidFriendshipException("Friendship already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setPlayer(player);
        friendship.setFriend(friend);
        friendshipRepository.save(friendship);

        // Create reverse friendship
        Friendship reverseFriendship = new Friendship();
        reverseFriendship.setPlayer(friend);
        reverseFriendship.setFriend(player);
        friendshipRepository.save(reverseFriendship);

        return toDTO(player);
    }

    public void removeFriend(Long playerId, Long friendId) {
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        if (!playerRepository.existsById(friendId)) {
            throw new PlayerNotFoundException(friendId);
        }
        
        friendshipRepository.deleteByPlayerIdAndFriendId(playerId, friendId);
        friendshipRepository.deleteByPlayerIdAndFriendId(friendId, playerId);
    }

    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        friendshipRepository.deleteByPlayerIdOrFriendId(id, id);
        playerRepository.deleteById(id);
    }

    private Player findPlayerById(Long id) {
        return playerRepository.findById(id)
            .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    private PlayerDTO toDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        BeanUtils.copyProperties(player, dto, "friends");
        
        if (player.getFriendships() != null) {
            dto.setFriends(player.getFriendships().stream()
                .map(friendship -> {
                    FriendDTO friendDTO = new FriendDTO();
                    Player friend = friendship.getFriend();
                    BeanUtils.copyProperties(friend, friendDTO);
                    return friendDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}