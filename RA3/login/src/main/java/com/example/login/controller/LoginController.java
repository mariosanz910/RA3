package com.example.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home"; // pantalla después de login
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin"; // solo admin
    }

    @GetMapping("/user")
    public String user() {
        return "user"; // solo usuario normal
    }
}
