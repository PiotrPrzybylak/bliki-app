package io.bliki.demo;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
@AllArgsConstructor
public class DemoController {

    private final JdbcTemplate jdbc;

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/v1")
    public String v1(Model model) {
        model.addAttribute("links", getLinks());
        return "v1";
    }

    @GetMapping("/v2")
    public String v2(Model model) {
        model.addAttribute("links", getLinks());
        return "v2";
    }

    @GetMapping("/v3")
    public String v3(Model model) {
        model.addAttribute("links", getLinks());
        return "v3";
    }

    @GetMapping("/v4")
    public String v4(Model model) {
        model.addAttribute("links", getLinks());
        return "v4";
    }

    @GetMapping("/v5")
    public String v5(Model model) {
        model.addAttribute("links", getLinksFromDB());
        return "v4";
    }

    @GetMapping("/v6")
    public String v6(Model model) {
        Map<String, List<Link>> linksMap = new HashMap<>();
        List<Category> categories = getCategories();
        for (Category category : categories) {
            linksMap.put(category.id(), new ArrayList<>());
        }
        List<Link> links = getLinksFromDB();
        for (Link link : links) {
            linksMap.get(link.categoryId()).add(link);
        }
        Map<Category, List<Link>> linksMapWithCategories = new LinkedHashMap<>();
        for (Category category : categories) {
            linksMapWithCategories.put(category, linksMap.get(category.id()));
        }
        model.addAttribute("categories", linksMapWithCategories);
        return "v6";
    }

    private List<Link> getLinksFromDB() {
        return jdbc.query("select * from links",
                (rs, i) -> new Link(
                        rs.getString("href"),
                        rs.getString("text"),
                        rs.getInt("rating"),
                        rs.getString("description"),
                        rs.getString("category_id")));
    }

    private Link[] getLinks() {
        return new Link[]{
                new Link("https://kodologia.pl/blog/czym-jest-system-kontroli-wersji", "Założenie konta na GitHubie", 5, "", ""),
                new Link("https://docs.github.com/en/get-started/signing-up-for-github/signing-up-for-a-new-github-account", "Założenie konta na GitHubie", 2, "", ""),
                new Link("https://www.wikihow.com/Create-an-Account-on-GitHub", "Założenie konta na GitHubie", 4, "", ""),
                new Link("https://www.figma.com/", "Figma", 4, "dużo da się zrobić za darmoszkę", ""),
                new Link("https://balsamiq.com/", "Balsamiq Wireframes", 4, "trzeba bulić kaskę", ""),
        };
    }

    private List<Category> getCategories() {
        return jdbc.query("select * from categories order by id",
                (rs, i) -> new Category(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description")));
    }
}
