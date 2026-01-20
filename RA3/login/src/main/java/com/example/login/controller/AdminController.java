package com.example.login. controller;

import com.example.login.entity. Rol;
import com.example. login.entity.Usuario;
import com.example.login.service. RolService;
import com.example.login.service.UsuarioService;
import org.springframework. stereotype.Controller;
import org. springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util. List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public AdminController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // Listar todos los usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin";
    }

    // Mostrar formulario para crear nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", true);
        return "user_form";
    }

    // Guardar nuevo usuario
    @PostMapping("/usuarios")
    public String crearUsuario(@ModelAttribute Usuario usuario,
                               @RequestParam(required = false) String rolNombre,
                               RedirectAttributes redirectAttributes) {
        try {
            // Asignar rol
            if (rolNombre != null && ! rolNombre.isEmpty()) {
                Rol rol = rolService.buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles.add(rol);
                usuario.setRoles(roles);
            } else {
                // Por defecto, asignar rol USUARIO
                Rol rolUsuario = rolService.buscarPorNombre("USUARIO");
                Set<Rol> roles = new HashSet<>();
                roles.add(rolUsuario);
                usuario. setRoles(roles);
            }

            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario:  " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Mostrar formulario para editar usuario existente
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", false);
        return "user_form";
    }

    // Actualizar usuario existente
    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @RequestParam String email,
                                    @RequestParam(required = false) Boolean activo,
                                    @RequestParam(required = false) String rolNombre,
                                    RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            // Actualizar rol si se especificó
            if (rolNombre != null && !rolNombre. isEmpty()) {
                Rol rol = rolService.buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles. add(rol);
                usuario. setRoles(roles);
            }

            // Si activo es null, significa que el checkbox no fue marcado
            Boolean activoFinal = (activo != null) ? activo : false;

            usuarioService.actualizarUsuario(id, email, activoFinal);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}