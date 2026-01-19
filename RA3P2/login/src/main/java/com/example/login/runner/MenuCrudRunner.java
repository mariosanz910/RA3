package com.example.login.runner;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;

import java.util.Scanner;

// COMENTAR @Component para desactivar el menú de consola
// @Component
@Order(2) // Se ejecuta después de DataLoader
public class MenuCrudRunner implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public MenuCrudRunner(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        System.out.println("\n========================================");
        System.out.println("MENÚ DE CONSOLA DESACTIVADO");
        System.out.println("========================================");
        System.out.println("La aplicación ahora usa interfaz web.");
        System.out.println("Accede a: http://localhost:8080/login");
        System.out.println("========================================\n");

        // Descomentar el código de abajo si quieres usar el menú de consola
        /*
        do {
            System.out.println("\n===== MENÚ CRUD USUARIOS =====");
            System.out.println("1. Crear usuario");
            System.out.println("2. Mostrar usuarios");
            System.out.println("3. Buscar usuario por username");
            System.out.println("4. Actualizar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("6. Login usuario");
            System.out.println("7. Cambiar contraseña");
            System.out.println("8. Listar usuarios paginados");
            System.out.println("0. Salir");
            System.out.print("Elige opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> crearUsuario(sc);
                case 2 -> listarUsuarios();
                case 3 -> buscarUsuario(sc);
                case 4 -> actualizarUsuario(sc);
                case 5 -> eliminarUsuario(sc);
                case 6 -> loginUsuario(sc);
                case 7 -> cambiarContraseña(sc);
                case 8 -> listarUsuariosPaginados(sc);
                case 0 -> {
                    System.out.println("Saliendo...");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Opción incorrecta");
            }

        } while (opcion != 0);
        */
    }

    private void crearUsuario(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setEmail(email);
        usuario.setActivo(true);

        usuarioService.crearUsuario(usuario);
        System.out.println("Usuario creado correctamente");
    }

    private void listarUsuarios() {
        usuarioService.listarUsuarios().forEach(System.out::println);
    }

    private void buscarUsuario(Scanner sc) {
        System.out.print("Username a buscar: ");
        String username = sc.nextLine();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        System.out.println(usuario);
    }

    private void actualizarUsuario(Scanner sc) {
        System.out.print("ID del usuario: ");
        Long id = sc.nextLong();
        sc.nextLine();
        System.out.print("Nuevo email: ");
        String email = sc.nextLine();
        System.out.print("¿Activo? (true/false): ");
        Boolean activo = sc.nextBoolean();
        sc.nextLine();
        usuarioService.actualizarUsuario(id, email, activo);
        System.out.println("Usuario actualizado");
    }

    private void eliminarUsuario(Scanner sc) {
        System.out.print("ID del usuario a eliminar: ");
        Long id = sc.nextLong();
        sc.nextLine();
        usuarioService.eliminarUsuario(id);
        System.out.println("Usuario eliminado");
    }

    private void loginUsuario(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine();

        Usuario usuario;
        try {
            usuario = usuarioService.buscarPorUsername(username);
        } catch (IllegalArgumentException e) {
            System.out.println("Usuario no encontrado");
            return;
        }

        int intentos = 0;
        boolean exito = false;

        while (intentos < 3 && !exito) {
            System.out.print("Password: ");
            String password = sc.nextLine();

            if (!usuario.getActivo()) {
                System.out.println("Usuario bloqueado. Contacta con el administrador.");
                return;
            }

            if (usuario.checkPassword(password)) {
                usuario.actualizarUltimoLogin();
                usuario.setIntentosFallidos(0);
                usuarioService.crearUsuario(usuario);
                System.out.println("Login correcto. Último login actualizado: " + usuario.getUltimoLogin());
                exito = true;
            } else {
                usuario.registrarIntentoFallido();
                usuarioService.crearUsuario(usuario);
                intentos++;
                if (!usuario.getActivo()) {
                    System.out.println("Usuario bloqueado tras 3 intentos fallidos");
                    return;
                } else if (intentos < 3) {
                    System.out.println("Contraseña incorrecta. Te quedan " + (3 - intentos) + " intentos.");
                }
            }
        }

        if (!exito) {
            System.out.println("No se ha podido iniciar sesión tras 3 intentos.");
        }
    }

    private void cambiarContraseña(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Contraseña actual: ");
        String actual = sc.nextLine();
        System.out.print("Nueva contraseña: ");
        String nueva = sc.nextLine();

        try {
            usuarioService.cambiarContraseña(username, actual, nueva);
            System.out.println("Contraseña actualizada correctamente");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarUsuariosPaginados(Scanner sc) {
        System.out.print("Número de página (0-based): ");
        int pagina = sc.nextInt();
        System.out.print("Tamaño de página: ");
        int tamaño = sc.nextInt();
        sc.nextLine();

        Page<Usuario> usuarios = usuarioService.listarUsuariosPaginados(pagina, tamaño);
        usuarios.forEach(System.out::println);
    }
}