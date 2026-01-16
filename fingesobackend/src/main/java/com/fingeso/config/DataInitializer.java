package com.fingeso.config;

import com.fingeso.model.Rol;
import com.fingeso.model.Usuario;
import com.fingeso.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Comenta esta línea si quieres desactivar la creación automática en el futuro
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository) {
        return args -> {

            // 1. ADMIN (Credencial: 00000000)
            crearUsuarioSiNoExiste(repository, "Admin", "Sistema", "admin@usach.cl", "00000000", "admin123", Rol.ADMINISTRADOR);

            // 2. ALUMNO / VISITA (Credencial: 11111111)
            crearUsuarioSiNoExiste(repository, "Visita", "Estudiante", "visita@usach.cl", "11111111", "visita123", Rol.ALUMNO);

            // 3. GESTOR (Credencial: 22222222)
            crearUsuarioSiNoExiste(repository, "Juan", "Gestor", "gestor@usach.cl", "22222222", "gestor123", Rol.GESTOR);

            // 4. OPERADOR (Credencial: 33333333)
            crearUsuarioSiNoExiste(repository, "Pedro", "Operador", "operador@usach.cl", "33333333", "operador123", Rol.OPERADOR);

            // 5. PROFESOR (Credencial: 44444444)
            crearUsuarioSiNoExiste(repository, "Laura", "Profesora", "profe@usach.cl", "44444444", "profe123", Rol.PROFESOR);

            // 6. FUNCIONARIO (Credencial: 55555555)
            crearUsuarioSiNoExiste(repository, "Carlos", "Funcionario", "func@usach.cl", "55555555", "func123", Rol.FUNCIONARIO);
        };
    }

    // Método auxiliar para no repetir código (DRY)
    private void crearUsuarioSiNoExiste(UsuarioRepository repository, String nombre, String apellido, String correo, String credencial, String pass, Rol rol) {
        if (repository.findByNumeroCredencial(credencial).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setCorreo(correo);
            usuario.setNumeroCredencial(credencial);
            usuario.setContrasena(pass);
            usuario.setRol(rol);

            repository.save(usuario);
            System.out.println("--> Usuario creado: " + rol + " / Credencial: " + credencial);
        }
    }
}