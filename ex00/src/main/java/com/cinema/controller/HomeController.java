package com.cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cinema.security.UserPrincipal;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            model.addAttribute("user", userPrincipal.getUser());
        }
        return "profile";
    }

    @GetMapping("/admin/panel/halls")
    public String adminHalls() {
        return "admin/halls";
    }

    @GetMapping("/admin/panel/films")
    public String adminFilms() {
        return "admin/films";
    }

    @GetMapping("/admin/panel/sessions")
    public String adminSessions() {
        return "admin/sessions";
    }
}
