package com.ludonexus.battleforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.ludonexus.battleforge.model.Participation;

public interface ParticipationRepository extends ListCrudRepository<Participation, Long> {
	boolean existsByGameIdAndPlayerId(Long gameId, Long playerId);
	Optional<Participation> getByGameIdAndPlayerId(Long gameId, Long playerId);
	List<Participation> findAllByPlayerId(Long playerId);
	List<Participation> findAllByGameId(Long gameId);
	void deleteByGameId(Long GameId);
	void deleteByPlayerId(Long playerId);
}
