package com.fingeso.service;

import com.fingeso.model.Rol;
import com.fingeso.model.Usuario;
import com.fingeso.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // REGISTRO DE USUARIO
    public Usuario guardarUsuario(Usuario usuario) {
        // Validaciones para no repetir datos
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuarioRepository.findByNumeroCredencial(usuario.getNumeroCredencial()).isPresent()) {
            throw new RuntimeException("El número de credencial ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    // LOGIN PERSONALIZADO (Correo + Credencial + Rol)
    public Usuario loginUsuario(String correo, String numeroCredencial, Rol rol) {
        // 1. Buscamos por correo
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(correo);

        if (usuarioEncontrado.isPresent()) {
            Usuario usuario = usuarioEncontrado.get();
            // 2. Verificamos que la credencial Y el rol coincidan con ese usuario
            boolean credencialCorrecta = usuario.getNumeroCredencial().equals(numeroCredencial);
            boolean rolCorrecto = usuario.getRol() == rol;

            if (credencialCorrecta && rolCorrecto) {
                return usuario;
            }
        }
        return null; // Retorna null si falla alguna validación
    }
}