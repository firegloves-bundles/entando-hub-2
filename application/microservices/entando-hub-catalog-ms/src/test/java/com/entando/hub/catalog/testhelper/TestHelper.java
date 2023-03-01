package com.entando.hub.catalog.testhelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestHelper {
    public static String mapToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSequenceNumber(JdbcTemplate jdbcTemplate, String sequenceName) {
        jdbcTemplate.update(String.format("ALTER SEQUENCE %s RESTART WITH 1", sequenceName));
    }
}
