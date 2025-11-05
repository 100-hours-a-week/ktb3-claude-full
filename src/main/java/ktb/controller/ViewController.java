package ktb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    // User views
    @GetMapping("/")
    public String index() {
        return "forward:/pages/user/login.html";
    }

    @GetMapping("/user/login")
    public String loginPage() {
        return "forward:/pages/user/login.html";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "forward:/pages/user/signup.html";
    }

    @GetMapping("/user/edit")
    public String userEditPage() {
        return "forward:/pages/user/edit.html";
    }

    @GetMapping("/user/password")
    public String passwordUpdatePage() {
        return "forward:/pages/user/password.html";
    }

    // Article views
    @GetMapping("/articles")
    public String articleListPage() {
        return "forward:/pages/article/list.html";
    }

    @GetMapping("/article/{id}")
    public String articleDetailPage(@PathVariable Long id) {
        return "forward:/pages/article/detail.html";
    }

    @GetMapping("/article/new")
    public String articleCreatePage() {
        return "forward:/pages/article/edit.html";
    }

    @GetMapping("/article/{id}/edit")
    public String articleEditPage(@PathVariable Long id) {
        return "forward:/pages/article/edit.html";
    }
}
