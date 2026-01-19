package com.example.login.service;

import com.example.login.entity.Rol;
import com.example.login.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // Crear un rol
    @Transactional
    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    // Listar todos los roles
    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    // Buscar rol por nombre
    @Transactional(readOnly = true)
    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
    }
}
