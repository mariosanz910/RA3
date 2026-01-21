package com.example.login.controller;

import com.example.login.entity.Usuario;
import com.example.login.service. UsuarioService;
import org. springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation. GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework. web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web. servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserPanelController {

    private final UsuarioService usuarioService;

    public UserPanelController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/home")
    public String userHome(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        model.addAttribute("usuario", usuario);
        return "user";
    }

    @GetMapping("/cambiar-password")
    public String mostrarCambiarPassword() {
        return "cambiar_password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            Authentication authentication,
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmar,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();

            if (!passwordNueva.equals(passwordConfirmar)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/user/cambiar-password";
            }

            if (passwordNueva.length() < 4) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 4 caracteres");
                return "redirect:/user/cambiar-password";
            }

            usuarioService.cambiarContraseña(username, passwordActual, passwordNueva);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente");
            return "redirect:/user/home";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cambiar-password";
        }
    }
}