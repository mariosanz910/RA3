package com.example.login.controller;

import com.example.login.entity.Rol;
import com.example.login.entity.Usuario;
import com.example.login.service.RolService;
import com.example.login.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioWebController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // Mostrar formulario para crear usuario
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listarRoles());
        return "usuario-form";
    }

    // Guardar nuevo usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String[] roles,
                                 RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password);
            usuario.setEmail(email);
            usuario.setActivo(true);

            // Asignar roles
            Set<Rol> rolesSet = new HashSet<>();
            if (roles != null) {
                for (String nombreRol : roles) {
                    try {
                        Rol rol = rolService.buscarPorNombre(nombreRol);
                        rolesSet.add(rol);
                    } catch (Exception e) {
                        // Ignorar si el rol no existe
                    }
                }
            }
            usuario.setRoles(rolesSet);

            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    // Mostrar formulario para editar usuario
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.listarRoles());
            return "usuario-editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin";
        }
    }

    // Actualizar usuario
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @RequestParam String email,
                                    @RequestParam(required = false) Boolean activo,
                                    @RequestParam(required = false) String[] roles,
                                    RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            usuario.setEmail(email);
            usuario.setActivo(activo != null ? activo : false);

            // Actualizar roles
            Set<Rol> rolesSet = new HashSet<>();
            if (roles != null) {
                for (String nombreRol : roles) {
                    try {
                        Rol rol = rolService.buscarPorNombre(nombreRol);
                        rolesSet.add(rol);
                    } catch (Exception e) {
                        // Ignorar
                    }
                }
            }
            usuario.setRoles(rolesSet);

            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    // Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    // Desactivar usuario
    @GetMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar usuario: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    // Ver detalles de un usuario
    @GetMapping("/ver/{id}")
    public String verUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuario", usuario);
            return "usuario-detalle.html";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin";
        }
    }
}