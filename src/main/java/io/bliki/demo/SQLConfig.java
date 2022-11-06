package io.bliki.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

    @Repository
    public class SQLConfig {
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
                        rs.getString("id"),
                        rs.getString("href"),
                        rs.getString("text"),
                        rs.getInt("rating"),
                        rs.getBigDecimal("community_rating"),
                        rs.getString("description"),
                        rs.getString("category_id"),
                        rs.getString("tags"),
                        langs.getOrDefault(rs.getInt("language_id"), langs.get(1))
                );

        public SQLConfig(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        public Map<Category, List<Link>> getLinksMapWithCategories(String blikiId) {
            Map<String, List<Link>> linksMap = new HashMap<>();
            List<Category> categories = getCategories();
            for (Category category : categories) {
                linksMap.put(category.id(), new ArrayList<>());
            }
            for (Link link : getLinks(blikiId)) {
                linksMap.get(link.categoryId()).add(link);
            }
            Map<Category, List<Link>> linksMapWithCategories = new LinkedHashMap<>();
            for (Category category : categories) {
                List<Link> links = linksMap.get(category.id());
                if (!links.isEmpty()) {
                    linksMapWithCategories.put(category, links);
                }
            }
            return linksMapWithCategories;
        }
        public List<Link> getLinks(String blikiId) {
            return jdbc.query("select * from links where bliki_id = ?",
                    linkRowMapper,
                    Long.parseLong(blikiId)
            );
        }

        public List<Link> getLinksByCategoryId(String categoryId) {
            return jdbc.query("select * from links where category_id = ?",
                    linkRowMapper,
                    Long.parseLong(categoryId)
            );
        }

        public List<Category> getCategories() {
            return jdbc.query("select * from categories", categoryRowMapper);
        }

        public Category getCategory(String id) {
            return jdbc.queryForObject(
                    "select * from categories where id = ?",
                    categoryRowMapper,
                    Long.parseLong(id));
        }

        public List<Bliki> getBlikis() {
            return jdbc.query("select * from blikis", blikiRowMapper);
        }

        public List<Bliki> getListedBlikis() {
            return jdbc.query("select * from blikis where listed = true", blikiRowMapper);
        }

        public Bliki getBliki(String id) {
            return jdbc.queryForObject(
                    "select * from blikis where id = ?",
                    blikiRowMapper,
                    Long.parseLong(id));
        }
    }
