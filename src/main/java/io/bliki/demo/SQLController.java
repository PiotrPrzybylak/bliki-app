package io.bliki.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;

public class SQLController {
    private final JdbcTemplate jdbc;

    private final RowMapper<Bliki> blikiRowMapper = (rs, i) ->
            new Bliki(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("description"));

    private final RowMapper<Category> categoryRowMapper = (rs, i) ->
            new Category(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("description"));

    private final Map<Integer, Language> langs = Map.of(
            1, new Language("", "English", "EN", "/gfx/lang_icons/usa.png"),
            2, new Language("", "Polski", "PL", "/gfx/lang_icons/pol.png"),
            3, new Language("", "Español", "ES", "/gfx/lang_icons/esp.png")
    );

    private final RowMapper<Link> linkRowMapper = (rs, i) ->
            new Link(
                    rs.getString("href"),
                    rs.getString("text"),
                    rs.getInt("rating"),
                    rs.getString("description"),
                    rs.getString("category_id"),
                    rs.getString("tags"),
                    langs.getOrDefault(rs.getInt("language_id"), langs.get(1))
            );

    public SQLController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private List<Link> getLinks(String blikiId) {
        return jdbc.query("select * from links where bliki_id = ?",
                linkRowMapper,
                Long.parseLong(blikiId)
        );
    }

    private List<Link> getLinksByCategoryId(String categoryId) {
        return jdbc.query("select * from links where category_id = ?",
                linkRowMapper,
                Long.parseLong(categoryId)
        );
    }

    private List<Category> getCategories() {
        return jdbc.query("select * from categories", categoryRowMapper);
    }

    private Category getCategory(String id) {
        return jdbc.queryForObject(
                "select * from categories where id = ?",
                categoryRowMapper,
                Long.parseLong(id));
    }

    private List<Bliki> getBlikis() {
        return jdbc.query("select * from blikis", blikiRowMapper);
    }

    private List<Bliki> getListedBlikis() {
        return jdbc.query("select * from blikis where listed = true", blikiRowMapper);
    }

    private Bliki getBliki(String id) {
        return jdbc.queryForObject(
                "select * from blikis where id = ?",
                blikiRowMapper,
                Long.parseLong(id));
    }
}
