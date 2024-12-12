package com.ludonexus.playersphere.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import com.ludonexus.playersphere.dto.CreatePlayerRequestDTO;
import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.dto.UpdatePlayerRequestDTO;
import com.ludonexus.playersphere.model.Player;
import com.ludonexus.playersphere.repository.FriendshipRepository;
import com.ludonexus.playersphere.repository.PlayerRepository;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void createPlayer_success() {
        CreatePlayerRequestDTO creationDTO = new CreatePlayerRequestDTO();
        creationDTO.setUsername("testUser");
        creationDTO.setEmail("test@example.com");

        when(playerRepository.existsByUsername(creationDTO.getUsername())).thenReturn(false);
        when(playerRepository.existsByEmail(creationDTO.getEmail())).thenReturn(false);

        Player savedPlayer = new Player();
        BeanUtils.copyProperties(creationDTO, savedPlayer);
        savedPlayer.setId(1L);

        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);

        PlayerDTO result = playerService.createPlayer(creationDTO);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void createPlayer_usernameExists_throwsException() {
        CreatePlayerRequestDTO creationDTO = new CreatePlayerRequestDTO();
        creationDTO.setUsername("testUser");
        creationDTO.setEmail("test@example.com");

        when(playerRepository.existsByUsername(creationDTO.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayer(creationDTO);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getAllPlayers_success() {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setUsername("testUser1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setUsername("testUser2");

        when(playerRepository.findAll()).thenReturn(List.of(player1, player2));

        List<PlayerDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.get(0).getUsername());
        assertEquals("testUser2", result.get(1).getUsername());
    }

    @Test
    void getPlayerById_success() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerDTO result = playerService.getPlayerById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getPlayerById_notFound_throwsException() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.getPlayerById(1L);
        });

        assertEquals("Player not found", exception.getMessage());
    }

    @Test
    void updatePlayer_success() {
        Player existingPlayer = new Player();
        existingPlayer.setId(1L);
        existingPlayer.setUsername("oldUser");
        existingPlayer.setEmail("old@example.com");

        UpdatePlayerRequestDTO updateDTO = new UpdatePlayerRequestDTO();
        updateDTO.setUsername("newUser");
        updateDTO.setEmail("new@example.com");

        when(playerRepository.findById(1L)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.existsByUsername(updateDTO.getUsername())).thenReturn(false);
        when(playerRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);

        Player updatedPlayer = new Player();
        BeanUtils.copyProperties(updateDTO, updatedPlayer);
        updatedPlayer.setId(1L);

        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);

        PlayerDTO result = playerService.updatePlayer(1L, updateDTO);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void deletePlayer_success() {
        doNothing().when(friendshipRepository).deleteByPlayerIdOrFriendId(1L, 1L);
        doNothing().when(playerRepository).deleteById(1L);

        playerService.deletePlayer(1L);

        verify(friendshipRepository, times(1)).deleteByPlayerIdOrFriendId(1L, 1L);
        verify(playerRepository, times(1)).deleteById(1L);
    }
}

