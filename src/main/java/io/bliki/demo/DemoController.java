package io.bliki.demo;

import io.bliki.user.User;
import io.bliki.user.UserDAO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
    SQLConfig sQLConfig;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("blikis", sQLConfig.getListedBlikis());
        model.addAttribute("categories", sQLConfig.getCategories());

        return "blikis";
    }

    @GetMapping("/bliki/{id}")
    public String bliki(@PathVariable String id, Model model) {
        model.addAttribute("bliki", sQLConfig.getBliki(id));
        Map<Category, List<Link>> linksMapWithCategories = sQLConfig.getLinksMapWithCategories(id);
        model.addAttribute("categories", linksMapWithCategories);
        return "bliki";
    }

    @GetMapping("/category/{id}")
    public String category(@PathVariable String id, Model model) {
        model.addAttribute("category", sQLConfig.getCategory(id));
        List<Link> links = sQLConfig.getLinksByCategoryId(id);
        model.addAttribute("links", links);
        return "category";
    }

    @GetMapping("/admin/")
    public String adminBlikis(Model model, HttpServletRequest request) {
        model.addAttribute("user", request.getRemoteUser());
        model.addAttribute("blikis", sQLConfig.getBlikis());
        return "admin_blikis";
    }

    @GetMapping("/admin/new_link")
    public String newLinkForm(@RequestParam("bliki_id") String blikiId, Model model) {
        model.addAttribute("bliki", sQLConfig.getBliki(blikiId));
        model.addAttribute("categories", sQLConfig.getCategories());
        model.addAttribute("links", sQLConfig.getLinks(blikiId));
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
}
