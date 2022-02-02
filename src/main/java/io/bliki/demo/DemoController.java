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
    private final RowMapper<Bliki> blikiRowMapper = (rs, i) -> new Bliki(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("description"));

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("blikis", getBliks());
        return "blikis";
    }

    @GetMapping("/bliki/{id}")
    public String bliki(@PathVariable String id, Model model) {
        model.addAttribute("bliki", getBliki(id));
        Map<Category, List<Link>> linksMapWithCategories = getLinksMapWithCategories(id);
        model.addAttribute("categories", linksMapWithCategories);
        return "bliki";
    }

    @GetMapping("/admin/")
    public String adminBlikis(Model model, HttpServletRequest request) {
        model.addAttribute("user", request.getRemoteUser());
        model.addAttribute("blikis", getBliks());
        return "admin_blikis";
    }

    @GetMapping("/admin/new_link")
    public String newLinkForm(@RequestParam("bliki_id") String blikiId, Model model) {
        model.addAttribute("bliki", getBliki(blikiId));
        model.addAttribute("categories", getCategories(blikiId));
        return "admin_new_link";
    }

    @PostMapping("/admin/new_link")
    public String newLink(String href, @RequestParam("category") String categoryId, @RequestParam("bliki") String blikiId, HttpServletRequest request) {
        User user = userDAO.byEmail(request.getRemoteUser());
        jdbc.update("insert into links" +
                " (href, text, rating, description, category_id, bliki_id, user_id)" +
                "VALUES (?,?,?,?,?::integer,?::integer, ?::integer )", href, href, 0, "", categoryId, blikiId, user.id());
        return "redirect:/admin/";
    }


    private Map<Category, List<Link>> getLinksMapWithCategories(String blikiId) {
        Map<String, List<Link>> linksMap = new HashMap<>();
        List<Category> categories = getCategories(blikiId);
        for (Category category : categories) {
            linksMap.put(category.id(), new ArrayList<>());
        }
        List<Link> links = getLinks(blikiId);
        for (Link link : links) {
            linksMap.get(link.categoryId()).add(link);
        }
        Map<Category, List<Link>> linksMapWithCategories = new LinkedHashMap<>();
        for (Category category : categories) {
            linksMapWithCategories.put(category, linksMap.get(category.id()));
        }
        return linksMapWithCategories;
    }

    private List<Link> getLinks(String blikiId) {
        return jdbc.query("select * from links where bliki_id = ?",
                (rs, i) -> new Link(
                        rs.getString("href"),
                        rs.getString("text"),
                        rs.getInt("rating"),
                        rs.getString("description"),
                        rs.getString("category_id")),
                Long.parseLong(blikiId)
        );
    }

    private List<Category> getCategories(String blikiId) {
        return jdbc.query("select * from categories where bliki_id = ?",
                (rs, i) -> new Category(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description")),
                Long.parseLong(blikiId)
        );
    }

    private List<Bliki> getBliks() {
        return jdbc.query("select * from blikis",
                blikiRowMapper);
    }

    private Bliki getBliki(String id) {
        return jdbc.queryForObject(
                "select * from blikis where id = ?",
                blikiRowMapper,
                Long.parseLong(id));
    }
}
