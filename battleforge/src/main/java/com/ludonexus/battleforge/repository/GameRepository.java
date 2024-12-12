package com.ludonexus.battleforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.ludonexus.battleforge.model.Game;

public interface GameRepository extends ListCrudRepository<Game, Long> {
	Optional<Game> getGameById(Long gameId);
	List<Game> findAllByHostId(Long hostId);
	void deleteByHostId(Long hostId);
}
