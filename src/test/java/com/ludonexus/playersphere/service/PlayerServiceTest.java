package com.ludonexus.playersphere.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
import org.mockito.junit.jupiter.MockitoExtension;

import com.ludonexus.playersphere.dto.PlayerDTO;
import com.ludonexus.playersphere.model.Player;
import com.ludonexus.playersphere.repository.FriendshipRepository;
import com.ludonexus.playersphere.repository.PlayerRepository;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    
    @Mock
    private FriendshipRepository friendshipRepository;
    
    @InjectMocks // Injecte automatiquement les mocks dans le service
    private PlayerService playerService;
    
    // Données de référence pour les tests
    private Player refPlayer;
    private Player refFriend;
    private PlayerDTO refPlayerDTO;
    
    @BeforeEach
    void setUp() {
        // Préparation des données de test réutilisables
        refPlayer = Player.builder()
            .id(1L)
            .username("testPlayer")
            .email("test@player.com")
            .level(1)
            .totalPoints(0)
            .build();
            
        refFriend = Player.builder()
            .id(2L)
            .username("testFriend")
            .email("test@friend.com")
            .level(1)
            .totalPoints(0)
            .build();
            
        refPlayerDTO = PlayerDTO.builder()
            .username("testPlayer")
            .email("test@player.com")
            .level(1)
            .build();
    }
    
    /*========================================
     *  Create Player Tests
     *========================================*/

    @Test
    void createPlayer_WithUniqueUsernameAndEmail_Should() {
        when(playerRepository.existsByUsername(refPlayerDTO.getUsername())).thenReturn(false);
        when(playerRepository.existsByEmail(refPlayerDTO.getEmail())).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(refPlayer);
        
        PlayerDTO result = playerService.createPlayer(refPlayerDTO);
        
        assertNotNull(result);
        assertEquals(refPlayerDTO.getUsername(), result.getUsername());
        assertEquals(refPlayerDTO.getEmail(), result.getEmail());
        assertEquals(refPlayerDTO.getLevel(), result.getLevel());
        assertEquals(0, result.getTotalPoints());       // According to statement, let's assume that the totalPoints variable at creation always equals 0
        assertTrue(result.getFriends().isEmpty());      // According to statement, let's assume that the friend list at creation is always empty 
    }
    
    @Test
    void createPlayer_WithExistingUsername_ShouldThrowException() {
        when(playerRepository.existsByUsername(refPlayerDTO.getUsername())).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(refPlayerDTO));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_WithExistingEmail_ShouldThrowException() {
        when(playerRepository.existsByUsername(refPlayerDTO.getUsername())).thenReturn(false);
        when(playerRepository.existsByEmail(refPlayerDTO.getEmail())).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(refPlayerDTO));
        verify(playerRepository, never()).save(any());
    }

    /*========================================
     *  Get All Players Tests
     *========================================*/

    @Test
    void getAllPlayers_ShouldReturnAllPlayersAsDTO() {
        List<Player> players = List.of(refPlayer, refFriend);
        when(playerRepository.findAll()).thenReturn(players);
        
        List<PlayerDTO> result = playerService.getAllPlayers();
        
        assertEquals(2, result.size());
        assertEquals(refPlayer.getUsername(), result.get(0).getUsername());
        assertEquals(refPlayer.getEmail(), result.get(0).getEmail());
        assertEquals(refFriend.getUsername(), result.get(1).getUsername());
        assertEquals(refFriend.getEmail(), result.get(1).getEmail());
    }

    /*========================================
     *  Update Player Tests
     *========================================*/

    @Test
    void updatePlayer_WithValidData_ShouldUpdateAndReturnDTO() {
        PlayerDTO updateDTO = PlayerDTO.builder()
            .username("updatedName")
            .email("updated@email.com")
            .level(2)
            .build();
            
        when(playerRepository.findById(refPlayer.getId())).thenReturn(Optional.of(refPlayer));
        when(playerRepository.existsByUsername(updateDTO.getUsername())).thenReturn(false);
        when(playerRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(refPlayer);
        
        PlayerDTO result = playerService.updatePlayer(refPlayer.getId(), updateDTO);
        
        assertNotNull(result);
        assertEquals(updateDTO.getUsername(), result.getUsername());
        assertEquals(updateDTO.getEmail(), result.getEmail());
        assertEquals(updateDTO.getLevel(), result.getLevel());
    }

    @Test
    void updatePlayer_WithNonExistentId_ShouldThrowException() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class,
            () -> playerService.updatePlayer(99L, refPlayerDTO));
            
        verify(playerRepository, never()).save(any());
    }

    /*========================================
     *  Delete Player Tests
     *========================================*/

    @Test
    void deletePlayer_ShouldRemoveFriendshipsAndSuccess() {
        playerService.deletePlayer(refPlayer.getId());
        
        verify(friendshipRepository).deleteByPlayerIdOrFriendId(refPlayer.getId(), refPlayer.getId());
        verify(playerRepository).deleteById(refPlayer.getId());
    }

    /*========================================
     *  Add Friend Tests
     *========================================*/

    @Test
    void addFriend_ShouldCreateBidirectionalFriendship() {
        when(playerRepository.findById(refPlayer.getId())).thenReturn(Optional.of(refPlayer));
        when(playerRepository.findById(refFriend.getId())).thenReturn(Optional.of(refFriend));
        when(friendshipRepository.existsByPlayerIdAndFriendId(refPlayer.getId(), refFriend.getId()))
            .thenReturn(false);
            
        playerService.addFriend(refPlayer.getId(), refFriend.getId());

        // Check that the repository has been created and stored the friendship in both direction (pla -> fri & fri -> pla)
        verify(friendshipRepository).save(argThat(friendship -> 
            friendship.getPlayer().getId().equals(refPlayer.getId()) &&
            friendship.getFriend().getId().equals(refFriend.getId())
        ));
        verify(friendshipRepository).save(argThat(friendship -> 
            friendship.getPlayer().getId().equals(refFriend.getId()) &&
            friendship.getFriend().getId().equals(refPlayer.getId())
        ));
    }
    
    @Test
    void addFriend_WithNonExistentPlayer_ShouldThrowException() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> playerService.addFriend(99L, refFriend.getId())
        );
        
        // Check that repository didn't save any friendship
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void addFriend_WithNonExistentFriend_ShouldThrowException() {
        when(playerRepository.findById(refPlayer.getId())).thenReturn(Optional.of(refPlayer));
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, 
            () -> playerService.addFriend(refPlayer.getId(), 99L)
        );
        
        // Check that repository didn't save any friendship
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void addFriend_WithExistingFriendship_ShouldThrowException() {
        when(playerRepository.findById(refPlayer.getId())).thenReturn(Optional.of(refPlayer));
        when(playerRepository.findById(refFriend.getId())).thenReturn(Optional.of(refFriend));
        when(friendshipRepository.existsByPlayerIdAndFriendId(refPlayer.getId(), refFriend.getId()))
            .thenReturn(true);
            
        assertThrows(
            IllegalArgumentException.class,
            () -> playerService.addFriend(refPlayer.getId(), refFriend.getId())
        );
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void addFriend_WithSamePlayerAndFriend_ShouldThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> playerService.addFriend(refPlayer.getId(), refPlayer.getId())
        );
        verify(friendshipRepository, never()).save(any());
    }
}