package com.example. login.controller;

import com. example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // CREATE
    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    // READ - listar todos
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService. listarUsuarios();
    }

    // READ - buscar por username
    @GetMapping("/{username}")
    public Usuario buscarPorUsername(@PathVariable String username) {
        return usuarioService.buscarPorUsername(username);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestParam Boolean activo
    ) {
        return usuarioService.actualizarUsuario(id, email, activo);
    }

    // DELETE lógico
    @DeleteMapping("/desactivar/{id}")
    public void desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
    }

    // DELETE físico
    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService. eliminarUsuario(id);
    }
}