package com.example.login.service;

import com.example.login.entity.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CREATE (sobrescribe si existe)
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        usuarioRepository.findByUsername(usuario.getUsername())
                .ifPresent(usuarioRepository::delete);
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(usuarioRepository::delete);
        return usuarioRepository.save(usuario);
    }

    // READ
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    // UPDATE
    @Transactional
    public Usuario actualizarUsuario(Long id, String email, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setEmail(email);
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    // DELETE
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Cambiar contraseña
    @Transactional
    public Usuario cambiarContraseña(String username, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.checkPassword(passwordActual)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        usuario.setPassword(usuario.hashPassword(passwordNueva));
        return usuarioRepository.save(usuario);
    }

    // Listado paginado
    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuariosPaginados(int numeroPagina, int tamañoPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);
        return usuarioRepository.findAll(pageable);
    }

    // DELETE lógico: desactiva un usuario sin borrarlo de la base de datos
    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(false); // marcar como inactivo
        usuarioRepository.save(usuario);
    }

}
