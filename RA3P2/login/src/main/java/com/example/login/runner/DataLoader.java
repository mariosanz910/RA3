package com.example.login.runner;

import com.example.login.entity.Rol;
import com.example.login.entity.Usuario;
import com.example.login.repository.RolRepository;
import com.example.login.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1) // Se ejecuta primero
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public DataLoader(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre("ADMIN");
            return rolRepository.save(rol);
        });

        Rol rolUser = rolRepository.findByNombre("USER").orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre("USER");
            return rolRepository.save(rol);
        });

        // Crear usuario admin si no existe
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Se hasheará automáticamente en @PrePersist
            admin.setEmail("admin@example.com");
            admin.setActivo(true);

            Set<Rol> rolesAdmin = new HashSet<>();
            rolesAdmin.add(rolAdmin);
            admin.setRoles(rolesAdmin);

            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado con contraseña: admin123");
        }

        // Crear usuario normal si no existe
        if (!usuarioRepository.existsByUsername("user")) {
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword("user123"); // Se hasheará automáticamente en @PrePersist
            user.setEmail("user@example.com");
            user.setActivo(true);

            Set<Rol> rolesUser = new HashSet<>();
            rolesUser.add(rolUser);
            user.setRoles(rolesUser);

            usuarioRepository.save(user);
            System.out.println("Usuario user creado con contraseña: user123");
        }

        System.out.println("\n====================================");
        System.out.println("Datos de prueba cargados");
        System.out.println("Admin: admin / admin123");
        System.out.println("User: user / user123");
        System.out.println("====================================\n");
    }
}