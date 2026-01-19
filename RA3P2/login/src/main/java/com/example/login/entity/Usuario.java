package com.example.login.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(nullable = false)
    private int intentosFallidos = 0; // contador de intentos fallidos

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();


    public Usuario(Object o, String username, String password, String email, boolean b, Object o1, Object o2, Object o3) {
    }

    // -------------------------------
    // Callbacks JPA
    // -------------------------------
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.password = hashPassword(this.password); // Hashear password
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // -------------------------------
    // Métodos de seguridad con BCrypt
    // -------------------------------
    public String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.password);
    }

    public void actualizarUltimoLogin() {
        this.ultimoLogin = LocalDateTime.now();
    }

    // -------------------------------
    // Métodos de intentos fallidos
    // -------------------------------
    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3) {
            this.activo = false; // bloquea usuario
            System.out.println("Usuario bloqueado por 3 intentos fallidos");
        }
    }

    public void resetIntentosFallidos() {
        this.intentosFallidos = 0;
    }
}
