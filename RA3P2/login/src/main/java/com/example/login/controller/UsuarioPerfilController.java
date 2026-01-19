package com.example.login.controller;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class UsuarioPerfilController {

    private final UsuarioService usuarioService;

    public UsuarioPerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Mostrar formulario de cambio de contraseña
    @GetMapping("/cambiar-password")
    public String mostrarFormularioCambiarPassword(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);
            model.addAttribute("usuario", usuario);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos");
        }

        return "cambiar-password";
    }

    // Cambiar contraseña
    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                  @RequestParam String passwordNueva,
                                  @RequestParam String passwordConfirmar,
                                  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            // Verificar que las contraseñas coinciden
            if (!passwordNueva.equals(passwordConfirmar)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/perfil/cambiar-password";
            }

            // Verificar que la nueva contraseña no esté vacía
            if (passwordNueva == null || passwordNueva.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La nueva contraseña no puede estar vacía");
                return "redirect:/perfil/cambiar-password";
            }

            usuarioService.cambiarContraseña(username, passwordActual, passwordNueva);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña cambiada correctamente");

            // Redirigir según rol
            boolean esAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (esAdmin) {
                return "redirect:/admin";
            } else {
                return "redirect:/user";
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil/cambiar-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña");
            return "redirect:/perfil/cambiar-password";
        }
    }
}