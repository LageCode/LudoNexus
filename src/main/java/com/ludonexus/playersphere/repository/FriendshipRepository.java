package com.ludonexus.playersphere.repository;

import com.ludonexus.playersphere.model.Friendship;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

public interface FriendshipRepository extends ListCrudRepository<Friendship, Long> {
    boolean existsByPlayerIdAndFriendId(Long playerId, Long friendId);
    void deleteByPlayerIdAndFriendId(Long playerId, Long friendId);
    void deleteByPlayerIdOrFriendId(Long playerId, Long friendId);
    List<Friendship> findByPlayerId(Long playerId);
}