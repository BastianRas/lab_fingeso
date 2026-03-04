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

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PiuRepository piuRepo, LugarRepository lugarRepo, EventoRepository eventoRepo,
                                   CarreraRepository carreraRepo, ClaseRepository claseRepo,
                                   MatriculaAlumnoRepository matriculaRepo, NotaRepository notaRepo) {
        return args -> {

            // 1. USUARIOS
            crearUsuarioSiNoExiste(usuarioRepo, "Admin", "Sistema", "admin@usach.cl", "00000000", "admin123", Rol.ADMINISTRADOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Bastián", "Ramos", "visita@usach.cl", "11111111", "visita123", Rol.ALUMNO);
            crearUsuarioSiNoExiste(usuarioRepo, "Juan", "Gestor", "gestor@usach.cl", "22222222", "gestor123", Rol.GESTOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Pedro", "Operador", "operador@usach.cl", "33333333", "operador123", Rol.OPERADOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Laura", "Profesora", "profe@usach.cl", "44444444", "profe123", Rol.PROFESOR);
            crearUsuarioSiNoExiste(usuarioRepo, "Carlos", "Funcionario", "func@usach.cl", "55555555", "func123", Rol.FUNCIONARIO);

            // 2. PIUS
            if (piuRepo.count() == 0) {
                piuRepo.save(new Piu("PIU-001", "Edificio M - Piso 1", "Activo", null, null));
                piuRepo.save(new Piu("PIU-002", "Biblioteca Central", "Activo", -33.45038138777661, -70.68297743797304));
                piuRepo.save(new Piu("PIU-003", "Casino EAO", "Activo", -33.450085979552824, -70.6860512495041));
                piuRepo.save(new Piu("PIU-004", "EAO", "Activo", -33.450085979552824, -70.68643748760225));
                piuRepo.save(new Piu("PIU-005", "Casa Central", "Activo", -33.44901623529311, -70.68309009075166));
                piuRepo.save(new Piu("PIU-006", "DIINF", "Activo", -33.449732383537714, -70.68740308284761));
                System.out.println("--> PIUs cargados en BD.");
            }

            // 3. LUGARES (Nuevos datos reales para el mapa)
            if (lugarRepo.count() == 0) {
                lugarRepo.save(new Lugar("Departamento Informática", "Laboratorios y oficinas administrativas", -33.44974399292344, -70.68734407424928));
                lugarRepo.save(new Lugar("Biblioteca Central", "Entrada principal por Enrique Kirberg", -33.45033928657135, -70.68308472633363));
                lugarRepo.save(new Lugar("Gimnasio Usach", "", -33.44964999881604, -70.68536996841432));
                lugarRepo.save(new Lugar("Escuela de Artes y Oficios", "", -33.45011996833435, -70.68628191947938));
                lugarRepo.save(new Lugar("Salón de las Artes Víctor Jara", "", -33.45011996833435, -70.68506419658662));
                lugarRepo.save(new Lugar("Aula Magna", "", -33.44988274594342, -70.68664669990541));
                lugarRepo.save(new Lugar("Departamento de Ingeniería Industrial", "", -33.450509369720265, -70.688374042511));
                lugarRepo.save(new Lugar("Laboratorio de Física", "", -33.45005730587905, -70.68413078784944));
                lugarRepo.save(new Lugar("ED", "", -33.4510464722112, -70.68435072898866));
                lugarRepo.save(new Lugar("Facultad de Derecho", "", -33.446436234244125, -70.6830793619156));
                lugarRepo.save(new Lugar("Edificio FAE", "", -33.44914421925125, -70.67998945713045));
                System.out.println("--> Lugares cargados en BD.");
            }

            // 3. EVENTOS
            if (eventoRepo.count() == 0) {
                eventoRepo.save(new Evento("Bienvenida Cachorros 2026", "Actividad en el Foro Griego.", LocalDate.now().plusDays(5), "Cultural"));
                eventoRepo.save(new Evento("Corte de Luz Programado", "Sector EAO por mantención.", LocalDate.now().plusDays(2), "Aviso"));
            }

            // 4. CARRERAS, CLASES, MATRÍCULA Y NOTAS
            if (carreraRepo.count() == 0) {
                Carrera ingInfo = new Carrera();
                ingInfo.setNombre("Ingeniería en Informática");
                ingInfo.setFacultad("Facultad de Ingeniería");
                carreraRepo.save(ingInfo);

                Clase fingeso = new Clase();
                fingeso.setNombre("Fundamentos de Ingeniería de Software");
                fingeso.setCodigo("INF-3454");
                fingeso.setHorario("L2 W2");
                fingeso.setSala("D-101");
                fingeso.setProfesor("Laura Profesora");
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

                claseRepo.saveAll(List.of(fingeso, sistemas));

                usuarioRepo.findByCorreo("visita@usach.cl").ifPresent(alumno -> {
                    if (matriculaRepo.count() == 0) {
                        MatriculaAlumno matricula = new MatriculaAlumno();
                        matricula.setAlumno(alumno);
                        matricula.setCarrera(ingInfo);
                        matricula.setEstadoMatricula(EstadoMatricula.ACTIVA);
                        matricula.setClases(List.of(fingeso, sistemas));
                        matriculaRepo.save(matricula);

                        // ✨ AQUÍ CREAMOS LAS NOTAS REALES EN LA BASE DE DATOS ✨
                        if (notaRepo.count() == 0) {
                            notaRepo.save(new Nota("PEP 1", 6.5, 30, alumno, fingeso));
                            notaRepo.save(new Nota("PEP 2", 5.2, 30, alumno, fingeso));
                            notaRepo.save(new Nota("Laboratorio", 6.8, 40, alumno, fingeso));

                            notaRepo.save(new Nota("PEP 1", 4.8, 40, alumno, sistemas));
                            notaRepo.save(new Nota("Proyecto Final", 6.0, 60, alumno, sistemas));
                            System.out.println("--> Notas del alumno cargadas en BD.");
                        }
                    }
                });
            }
        };
    }

    private void crearUsuarioSiNoExiste(UsuarioRepository repository, String nombre, String apellido, String correo, String credencial, String pass, Rol rol) {
        // ✨ CORRECCIÓN: Verificamos por correo para evitar el error de llave duplicada
        if (repository.findByCorreo(correo).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setCorreo(correo);
            usuario.setNumeroCredencial(credencial);
            usuario.setContrasena(pass);
            usuario.setRol(rol);
            repository.save(usuario);
        }
    }
}