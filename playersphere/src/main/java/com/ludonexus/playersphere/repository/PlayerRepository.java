package com.ludonexus.playersphere.repository;

import com.ludonexus.playersphere.model.Player;

import org.springframework.data.repository.ListCrudRepository;

public interface PlayerRepository extends ListCrudRepository<Player, Long> {

    /** 
     * ListCrudRepository already implements:
     *  - findById
     *  - existsById
     *  - ...
     * */ 
    boolean existsByUsername(String username);    
    boolean existsByEmail(String email);    
}