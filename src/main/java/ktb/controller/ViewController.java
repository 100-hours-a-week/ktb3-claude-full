package ktb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    /**
     * 현재 사용자가 인증되었는지 확인
     */
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    // User views
    @GetMapping("/")
    public String index() {
        // 인증된 사용자는 게시글 목록으로, 아니면 로그인 페이지로
        if (isAuthenticated()) {
            return "redirect:/articles";
        }
        return "forward:/pages/user/login.html";
    }

    @GetMapping("/user/login")
    public String loginPage() {
        // 이미 로그인한 사용자는 게시글 목록으로 리다이렉트
        if (isAuthenticated()) {
            return "redirect:/articles";
        }
        return "forward:/pages/user/login.html";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        // 이미 로그인한 사용자는 게시글 목록으로 리다이렉트
        if (isAuthenticated()) {
            return "redirect:/articles";
        }
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
