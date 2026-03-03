package com.fingeso.config;

import com.fingeso.model.*;
import com.fingeso.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    // Inyectamos los repositorios: Usuarios, PIUs, Eventos, Carreras, Clases y Matrícula
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PiuRepository piuRepo, EventoRepository eventoRepo,
                                   CarreraRepository carreraRepo, ClaseRepository claseRepo,
                                   MatriculaAlumnoRepository matriculaRepo) {
        return args -> {

            // 1. USUARIOS
            crearUsuarioSiNoExiste(usuarioRepo, "Admin", "Sistema", "admin@usach.cl", "00000000", "admin123", Rol.ADMINISTRADOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Bastian", "Ramos", "visita@usach.cl", "11111111", "visita123", Rol.ALUMNO);
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
                eventoRepo.save(new Evento("Bienvenida Cachorros 2026", "Actividad en el Foro Griego.", LocalDate.now().plusDays(5), "Cultural"));
                eventoRepo.save(new Evento("Corte de Luz Programado", "Sector EAO por mantención.", LocalDate.now().plusDays(2), "Aviso"));
                eventoRepo.save(new Evento("Charla de Ciberseguridad", "Auditorio Depto Informática.", LocalDate.now().plusDays(10), "Académico"));
                System.out.println("--> Eventos iniciales cargados.");
            }

            // 4. CARRERAS, CLASES Y MATRÍCULA
            if (carreraRepo.count() == 0) {
                // 1. Crear Carrera
                Carrera ingInfo = new Carrera();
                ingInfo.setNombre("Ingeniería en Informática");
                ingInfo.setFacultad("Facultad de Ingeniería");
                carreraRepo.save(ingInfo);

                // 2. Crear Clases
                Clase fingeso = new Clase();
                fingeso.setNombre("Fundamentos de Ingeniería de Software");
                fingeso.setCodigo("INF-3454");
                fingeso.setHorario("L2 W2");
                fingeso.setSala("D-101");
                fingeso.setProfesor("Laura Profesora"); // String para el frontend
                fingeso.setSemestre("2026-1");
                fingeso.setCarrera(ingInfo);

                Clase sistemas = new Clase();
                sistemas.setNombre("Sistemas Operativos");
                sistemas.setCodigo("INF-3321");
                sistemas.setHorario("M3 J3");
                sistemas.setSala("D-203");
                sistemas.setProfesor("Laura Profesora");
                sistemas.setSemestre("2026-1");
                sistemas.setCarrera(ingInfo);

                Clase redes = new Clase();
                redes.setNombre("Redes de Computadores");
                redes.setCodigo("INF-3410");
                redes.setHorario("V1 V2");
                redes.setSala("E-105");
                redes.setProfesor("Laura Profesora");
                redes.setSemestre("2026-1");
                redes.setCarrera(ingInfo);

                // Asignar el Usuario Profesor (Opcional, pero bueno tenerlo en la BD)
                usuarioRepo.findByCorreo("profe@usach.cl").ifPresent(profe -> {
                    fingeso.setProfesorUsuario(profe);
                    sistemas.setProfesorUsuario(profe);
                    redes.setProfesorUsuario(profe);
                });

                claseRepo.saveAll(List.of(fingeso, sistemas, redes));

                // 3. Crear Matrícula para el Alumno
                usuarioRepo.findByCorreo("visita@usach.cl").ifPresent(alumno -> {
                    if (matriculaRepo.count() == 0) {
                        MatriculaAlumno matricula = new MatriculaAlumno();
                        matricula.setAlumno(alumno);
                        matricula.setCarrera(ingInfo);

                        // ✨ AQUÍ ESTÁ LA LÍNEA CORREGIDA ✨
                        matricula.setEstadoMatricula(EstadoMatricula.ACTIVA);

                        matricula.setClases(List.of(fingeso, sistemas, redes));

                        matriculaRepo.save(matricula);
                        System.out.println("--> Matrícula del alumno cargada exitosamente.");
                    }
                });
            }

        };
    }

    // Método auxiliar
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