package io.bliki.user;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", this::mapUser);
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("admin"),
                rs.getString("name"),
                rs.getString("phone"));
    }

    public User byEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", this::mapUser, email);
    }

    public User byId(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", this::mapUser, id);
    }
}