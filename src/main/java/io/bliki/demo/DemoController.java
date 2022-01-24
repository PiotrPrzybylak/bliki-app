package io.bliki.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/v1")
    public String v1(Model model) {
        model.addAttribute("links", new String[]{
                "https://kodologia.pl/blog/czym-jest-system-kontroli-wersji",
                "https://docs.github.com/en/get-started/signing-up-for-github/signing-up-for-a-new-github-account",
                "https://www.wikihow.com/Create-an-Account-on-GitHub",
                "https://www.figma.com/",
                "https://balsamiq.com/",
        });
        return "v1";
    }

    @GetMapping("/v2")
    public String v2(Model model) {
        model.addAttribute("links", new Link[]{
                new Link("https://kodologia.pl/blog/czym-jest-system-kontroli-wersji","Założenie konta na GitHubie"),
                new Link("https://docs.github.com/en/get-started/signing-up-for-github/signing-up-for-a-new-github-account", "Założenie konta na GitHubie"),
                new Link( "https://www.wikihow.com/Create-an-Account-on-GitHub", "Założenie konta na GitHubie"),
                new Link("https://www.figma.com/", "Figma"),
                new Link( "https://balsamiq.com/", "Balsamiq Wireframes"),
        });
        return "v2";
    }
}
