package com.fingeso.config;

import com.fingeso.model.Piu;       // <--- IMPORTANTE
import com.fingeso.model.Rol;
import com.fingeso.model.Usuario;
import com.fingeso.repository.PiuRepository; // <--- IMPORTANTE
import com.fingeso.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    // Inyectamos AMBOS repositorios aquí en los argumentos del método
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PiuRepository piuRepo) {
        return args -> {

            // ==========================================
            //           INICIALIZACIÓN DE USUARIOS
            // ==========================================

            // 1. ADMIN
            crearUsuarioSiNoExiste(usuarioRepo, "Admin", "Sistema", "admin@usach.cl", "00000000", "admin123", Rol.ADMINISTRADOR);

            // 2. ALUMNO / VISITA
            crearUsuarioSiNoExiste(usuarioRepo, "Visita", "Estudiante", "visita@usach.cl", "11111111", "visita123", Rol.ALUMNO);

            // 3. GESTOR
            crearUsuarioSiNoExiste(usuarioRepo, "Juan", "Gestor", "gestor@usach.cl", "22222222", "gestor123", Rol.GESTOR);

            // 4. OPERADOR
            crearUsuarioSiNoExiste(usuarioRepo, "Pedro", "Operador", "operador@usach.cl", "33333333", "operador123", Rol.OPERADOR);

            // 5. PROFESOr
            crearUsuarioSiNoExiste(usuarioRepo, "Laura", "Profesora", "profe@usach.cl", "44444444", "profe123", Rol.PROFESOR);

            // 6. FUNCIONARIO
            crearUsuarioSiNoExiste(usuarioRepo, "Carlos", "Funcionario", "func@usach.cl", "55555555", "func123", Rol.FUNCIONARIO);
            
            // ==========================================
            //           INICIALIZACIÓN DE PIUS
            // ==========================================

            if (piuRepo.count() == 0) {
                piuRepo.save(new Piu("PIU-001", "Edificio M - Piso 1", "Activo"));
                piuRepo.save(new Piu("PIU-002", "Biblioteca Central", "Inactivo"));
                piuRepo.save(new Piu("PIU-003", "Casino EAO", "Activo"));
                piuRepo.save(new Piu("PIU-004", "Departamento Informática", "Mantenimiento"));

                System.out.println("--> Datos de PIUs iniciales cargados correctamente.");
            }
        };
    }

    // Método auxiliar para Usuarios
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