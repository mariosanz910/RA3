package com.example.login.controller;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);

            // Actualizar último login
            usuario.actualizarUltimoLogin();
            usuario.setIntentosFallidos(0);
            usuarioService.crearUsuario(usuario);

            model.addAttribute("usuario", usuario);

            // Redirigir según el rol
            boolean esAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (esAdmin) {
                return "redirect:/admin";
            } else {
                return "redirect:/user";
            }
        } catch (Exception e) {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);
            model.addAttribute("usuario", usuario);
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos");
        }

        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);
            model.addAttribute("usuario", usuario);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos");
        }

        return "user";
    }
}