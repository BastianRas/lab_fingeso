package com.fingeso.config;

import com.fingeso.model.Evento;         // <--- Importamos Evento
import com.fingeso.model.Piu;
import com.fingeso.model.Rol;
import com.fingeso.model.Usuario;
import com.fingeso.repository.EventoRepository; // <--- Importamos Repo de Eventos
import com.fingeso.repository.PiuRepository;
import com.fingeso.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate; // <--- Necesario para las fechas

@Configuration
public class DataInitializer {

    // Inyectamos los 3 repositorios: Usuarios, PIUs y Eventos
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PiuRepository piuRepo, EventoRepository eventoRepo) {
        return args -> {

            // 1. USUARIOS
            crearUsuarioSiNoExiste(usuarioRepo, "Admin", "Sistema", "admin@usach.cl", "00000000", "admin123", Rol.ADMINISTRADOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Visita", "Estudiante", "visita@usach.cl", "11111111", "visita123", Rol.ALUMNO);
            crearUsuarioSiNoExiste(usuarioRepo, "Juan", "Gestor", "gestor@usach.cl", "22222222", "gestor123", Rol.GESTOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Pedro", "Operador", "operador@usach.cl", "33333333", "operador123", Rol.OPERADOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Laura", "Profesora", "profe@usach.cl", "44444444", "profe123", Rol.PROFESOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Carlos", "Funcionario", "func@usach.cl", "55555555", "func123", Rol.FUNCIONARIO);

            // 2. PIUS (Dispositivos)
            if (piuRepo.count() == 0) {
                piuRepo.save(new Piu("PIU-001", "Edificio M - Piso 1", "Activo"));
                piuRepo.save(new Piu("PIU-002", "Biblioteca Central", "Inactivo"));
                piuRepo.save(new Piu("PIU-003", "Casino EAO", "Activo"));
                piuRepo.save(new Piu("PIU-004", "Departamento Informática", "Mantenimiento"));
                System.out.println("--> PIUs iniciales cargados.");
            }

            // 3. EVENTOS (Noticias)
            if (eventoRepo.count() == 0) {
                eventoRepo.save(new Evento("Feria de Software 2026", "Presentación de proyectos finales de Ingeniería.", LocalDate.now().plusDays(10), "Académico"));
                eventoRepo.save(new Evento("Suspensión de Agua", "Corte programado en el sector norte del campus.", LocalDate.now().plusDays(1), "Aviso"));
                eventoRepo.save(new Evento("Charla IA Generativa", "Expositor invitado de Google en el Auditorio.", LocalDate.now().plusDays(5), "Cultural"));
                System.out.println("--> Eventos iniciales cargados.");
            }

        }; // <--- Cierre del lambda (args -> {})
    }      // <--- ¡ESTA ES LA LLAVE QUE TE FALTABA! (Cierre del método initDatabase)

    // Método auxiliar (ahora sí está fuera del método anterior)
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
            System.out.println("--> Usuario creado: " + rol);
        }
    }
}