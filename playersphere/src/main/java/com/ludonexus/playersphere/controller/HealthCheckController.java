package com.ludonexus.playersphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public String checkHealth() {
        try {
            String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
            return String.format("""
                Application Status: Running
                Database Connection: Successful
                PostgreSQL Version: %s
                """, dbVersion);
        } catch (Exception e) {
            return String.format("""
                Application Status: Running
                Database Connection: Failed
                Error: %s
                """, e.getMessage());
        }
    }
}