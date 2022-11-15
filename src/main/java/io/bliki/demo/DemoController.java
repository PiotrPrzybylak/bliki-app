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
    SQLDAO sQLDAO;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("blikis", sQLDAO.getListedBlikis());
        model.addAttribute("categories", sQLDAO.getCategories());

        return "blikis";
    }

    @GetMapping("/bliki/{id}")
    public String bliki(@PathVariable String id, Model model) {
        model.addAttribute("bliki", sQLDAO.getBliki(id));
        Map<Category, List<Link>> linksMapWithCategories = sQLDAO.getLinksMapWithCategories(id);
        model.addAttribute("categories", linksMapWithCategories);
        return "bliki";
    }

    @GetMapping("/category/{id}")
    public String category(@PathVariable String id, Model model) {
        model.addAttribute("category", sQLDAO.getCategory(id));
        List<Link> links = sQLDAO.getLinksByCategoryId(id);
        model.addAttribute("links", links);
        return "category";
    }

    @GetMapping("/admin/")
    public String adminBlikis(Model model, HttpServletRequest request) {
        model.addAttribute("user", request.getRemoteUser());
        model.addAttribute("blikis", sQLDAO.getBlikis());
        return "admin_blikis";
    }

    @GetMapping("/admin/new_link")
    public String newLinkForm(@RequestParam("bliki_id") String blikiId, Model model, HttpServletRequest request) {
        User user = userDAO.byEmail(request.getRemoteUser());

        model.addAttribute("user", user);
        model.addAttribute("bliki", sQLDAO.getBliki(blikiId));
        model.addAttribute("categories", sQLDAO.getCategories());
        model.addAttribute("links", sQLDAO.getLinks(blikiId));
        model.addAttribute("categoriesMap", sQLDAO.getLinksMapWithCategories(blikiId));
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
        sQLDAO.getJdbc();
        return "redirect:/admin/";
    }

    @PostMapping("/admin/delete_link")
    public String newLink(
            @RequestParam("bliki") String blikiId,
            @RequestParam("link_id") Integer linkId) {
        jdbc.update("delete from links where id = ?", linkId);
        return "redirect:/admin/new_link?blikiId=" + blikiId;
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
}
