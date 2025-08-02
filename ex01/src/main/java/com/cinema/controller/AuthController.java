package com.cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/signIn")
    public String signIn(Authentication authentication) {
        // Redirect authenticated users based on PDF requirements
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/panel/halls";
            }
            return "redirect:/profile";
        }
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(Authentication authentication) {
        // Redirect authenticated users based on PDF requirements
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/panel/halls";
            }
            return "redirect:/profile";
        }
        return "signUp";
    }

    @PostMapping("/signUp")
    public String handleSignUp(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        // For now, just redirect back to signUp with a message
        // Real registration will be implemented in Exercise 01 (Validation)
        redirectAttributes.addFlashAttribute("message",
                "Registration functionality will be implemented in Exercise 01. Please use test accounts for now.");

        return "redirect:/signUp";
    }
}