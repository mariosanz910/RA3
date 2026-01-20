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
    intentos_fallidos INT NOT NULL DEFAULT 0,
    fecha_creacion DATETIME,
    fecha_actualizacion DATETIME,
    ultimo_login DATETIME
);

-- Tabla que relaciona usuarios con roles (muchos a muchos)
CREATE TABLE usuario_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insertar roles básicos
INSERT INTO roles (nombre) VALUES ('ADMIN');
INSERT INTO roles (nombre) VALUES ('USUARIO');

-- Insertar usuario admin de prueba
-- Password: admin (hasheada con BCrypt)
INSERT INTO usuarios (username, password, email, activo, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK', 'admin@example.com', TRUE, 0, NOW(), NOW());

-- Insertar usuario normal de prueba
-- Password: user123
INSERT INTO usuarios (username, password, email, activo, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES ('user', '$2a$10$8K1p/a0dL3. uPdLU1/xpL. cLYOHdCCxzCHhKY1bXhEJLgFWe2m6zi', 'user@example.com', TRUE, 0, NOW(), NOW());

-- Relacionar admin con rol ADMIN
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
JOIN roles r ON r.nombre='ADMIN'
WHERE u.username='admin';

-- Relacionar user con rol USUARIO
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
JOIN roles r ON r.nombre='USUARIO'
WHERE u. username='user';
