package io.bliki.demo;

import io.bliki.user.User;
import io.bliki.user.UserDAO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@AllArgsConstructor
public class DemoController {

    private final JdbcTemplate jdbc;
    private final UserDAO userDAO;
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
                    rs.getString("description"),
                    rs.getString("category_id"),
                    rs.getString("tags"),
                    langs.getOrDefault(rs.getInt("language_id"), langs.get(1))
                    );


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("blikis", getListedBlikis());
        model.addAttribute("categories", getCategories());

        return "blikis";
    }

    @GetMapping("/bliki/{id}")
    public String bliki(@PathVariable String id, Model model) {
        model.addAttribute("bliki", getBliki(id));
        Map<Category, List<Link>> linksMapWithCategories = getLinksMapWithCategories(id);
        model.addAttribute("categories", linksMapWithCategories);
        return "bliki";
    }

    @GetMapping("/category/{id}")
    public String category(@PathVariable String id, Model model) {
        model.addAttribute("category", getCategory(id));
        List<Link> links = getLinksByCategoryId(id);
        model.addAttribute("links", links);
        return "category";
    }

    @GetMapping("/admin/")
    public String adminBlikis(Model model, HttpServletRequest request) {
        model.addAttribute("user", request.getRemoteUser());
        model.addAttribute("blikis", getBlikis());
        return "admin_blikis";
    }

    @GetMapping("/admin/new_link")
    public String newLinkForm(@RequestParam("bliki_id") String blikiId, Model model) {
        model.addAttribute("bliki", getBliki(blikiId));
        model.addAttribute("categories", getCategories());
        model.addAttribute("links", getLinks(blikiId));
        return "admin_new_link";
    }

    @PostMapping("/admin/new_link")
    public String newLink(
            String href,
            String text,
            @RequestParam("category") String categoryId,
            @RequestParam("bliki") String blikiId,
            String description,
            Long rate,
            HttpServletRequest request) {
        User user = userDAO.byEmail(request.getRemoteUser());
        jdbc.update("insert into links" +
                " (href, text, rating, description, category_id, bliki_id, user_id)" +
                "VALUES (?,?,?,?,?::integer,?::integer, ?::integer )", href, text, rate, description, categoryId, blikiId, user.id());
        return "redirect:/admin/";
    }

    @PostMapping("/admin/categories")
    public String newCategory(
            String name,
            String description,
            HttpServletRequest request) {
        User user = userDAO.byEmail(request.getRemoteUser());
        jdbc.update("insert into categories" +
                " (name, description, user_id)" +
                "VALUES (?,?,?::integer )", name, description, user.id());
        return "redirect:/admin/";
    }

    @PostMapping("/admin/rate_link")
    public String rateLink(
            @RequestParam("link_id") Integer linkId,
            Integer rating,
            HttpServletRequest request) {
        User user = userDAO.byEmail(request.getRemoteUser());
        jdbc.update("insert into ratings" +
                " (link_id, rating, user_id)" +
                "VALUES (?,?,?::integer )",linkId, rating, user.id());
        return "redirect:/admin/";
    }


    private Map<Category, List<Link>> getLinksMapWithCategories(String blikiId) {
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
