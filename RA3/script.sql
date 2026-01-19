-- Borrar base de datos si existe
DROP DATABASE IF EXISTS login_web;

-- Crear base de datos
CREATE DATABASE login_web;

-- Usar la base
USE login_web;

-- Tabla de roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    intentos_fallidos INT NOT NULL DEFAULT 0
);

-- Tabla que relaciona usuarios con roles (muchos a muchos)
CREATE TABLE usuario_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Insertar roles básicos
INSERT INTO roles (nombre) VALUES ('ADMIN');
INSERT INTO roles (nombre) VALUES ('USUARIO');

-- Insertar usuario admin de prueba
INSERT INTO usuarios (username, password, email, activo, intentos_fallidos)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK', 'admin@example.com', TRUE, 0);

-- Relacionar admin con rol ADMIN
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
         JOIN roles r ON r.nombre='ADMIN'
WHERE u.username='admin';
