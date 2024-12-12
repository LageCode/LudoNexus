package com.ludonexus.playersphere.controller;

import com.ludonexus.playersphere.dto.*;
import com.ludonexus.playersphere.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private PlayerDTO samplePlayerDTO;
    private CreatePlayerRequestDTO createPlayerRequestDTO;
    private UpdatePlayerRequestDTO updatePlayerRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup sample DTOs
        samplePlayerDTO = new PlayerDTO();
        samplePlayerDTO.setId(1L);
        samplePlayerDTO.setUsername("testUser");
        samplePlayerDTO.setEmail("test@example.com");
        
        createPlayerRequestDTO = new CreatePlayerRequestDTO();
        createPlayerRequestDTO.setUsername("newUser");
        createPlayerRequestDTO.setEmail("new@example.com");
        
        updatePlayerRequestDTO = new UpdatePlayerRequestDTO();
        updatePlayerRequestDTO.setUsername("updatedUser");
        updatePlayerRequestDTO.setEmail("updated@example.com");
    }

    @Test
    void createPlayer_ShouldReturnCreatedPlayer() {
        // Given
        when(playerService.createPlayer(any(CreatePlayerRequestDTO.class)))
            .thenReturn(samplePlayerDTO);

        // When
        ResponseEntity<PlayerDTO> response = playerController.createPlayer(createPlayerRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(samplePlayerDTO, response.getBody());
        verify(playerService).createPlayer(createPlayerRequestDTO);
    }

	@Test
    void getPlayer_ShouldReturnPlayer() {
        // Given
        when(playerService.getPlayerById(1L)).thenReturn(samplePlayerDTO);

        // When
        ResponseEntity<PlayerDTO> response = playerController.getPlayer(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(samplePlayerDTO, response.getBody());
        verify(playerService).getPlayerById(1L);
    }

    @Test
    void getAllPlayers_ShouldReturnListOfPlayers() {
        // Given
        List<PlayerDTO> players = Arrays.asList(samplePlayerDTO);
        when(playerService.getAllPlayers()).thenReturn(players);

        // When
        ResponseEntity<List<PlayerDTO>> response = playerController.getAllPlayers();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(players, response.getBody());
        verify(playerService).getAllPlayers();
    }

    @Test
    void updatePlayer_ShouldReturnUpdatedPlayer() {
        // Given
        when(playerService.updatePlayer(eq(1L), any(UpdatePlayerRequestDTO.class)))
            .thenReturn(samplePlayerDTO);

        // When
        ResponseEntity<PlayerDTO> response = playerController.updatePlayer(1L, updatePlayerRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(samplePlayerDTO, response.getBody());
        verify(playerService).updatePlayer(1L, updatePlayerRequestDTO);
    }

    @Test
    void deletePlayer_ShouldReturnNoContent() {
        // Given
        doNothing().when(playerService).deletePlayer(1L);

        // When
        ResponseEntity<Void> response = playerController.deletePlayer(1L);

        // Then
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(playerService).deletePlayer(1L);
    }

    @Test
    void getFriends_ShouldReturnListOfFriends() {
        // Given
        List<PlayerFriendDTO> friends = Arrays.asList(new PlayerFriendDTO());
        samplePlayerDTO.setFriends(friends);
        when(playerService.getPlayerById(1L)).thenReturn(samplePlayerDTO);

        // When
        ResponseEntity<List<PlayerFriendDTO>> response = playerController.getFriends(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(friends, response.getBody());
        verify(playerService).getPlayerById(1L);
    }

    @Test
    void addFriends_SingleFriend_ShouldReturnOk() {
        // Given
        CreateFriendshipRequestDTO requestDTO = new CreateFriendshipRequestDTO();
        requestDTO.setId(2L);
        doNothing().when(playerService).createFriendship(1L, 2L);

        // When
        ResponseEntity<Void> response = playerController.addFriends(1L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(playerService).createFriendship(1L, 2L);
    }

    @Test
    void addFriends_MultipleFriends_ShouldReturnOk() {
        // Given
        CreateFriendshipRequestDTO requestDTO = new CreateFriendshipRequestDTO();
        requestDTO.setIds(Arrays.asList(2L, 3L));
        doNothing().when(playerService).createFriendship(anyLong(), anyLong());

        // When
        ResponseEntity<Void> response = playerController.addFriends(1L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(playerService, times(2)).createFriendship(eq(1L), anyLong());
    }

    @Test
    void removeFriends_SingleFriend_ShouldReturnNoContent() {
        // Given
        CreateFriendshipRequestDTO requestDTO = new CreateFriendshipRequestDTO();
        requestDTO.setId(2L);
        doNothing().when(playerService).deleteFriendShip(1L, 2L);

        // When
        ResponseEntity<Void> response = playerController.removeFriends(1L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(playerService).deleteFriendShip(1L, 2L);
    }

    @Test
    void removeFriends_MultipleFriends_ShouldReturnNoContent() {
        // Given
        CreateFriendshipRequestDTO requestDTO = new CreateFriendshipRequestDTO();
        requestDTO.setIds(Arrays.asList(2L, 3L));
        doNothing().when(playerService).deleteFriendShip(anyLong(), anyLong());

        // When
        ResponseEntity<Void> response = playerController.removeFriends(1L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(playerService, times(2)).deleteFriendShip(eq(1L), anyLong());
    }
}