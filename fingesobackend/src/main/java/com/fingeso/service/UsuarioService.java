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

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuarioRepository.findByNumeroCredencial(usuario.getNumeroCredencial()).isPresent()) {
            throw new RuntimeException("El número de credencial ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    // LOGIN (Correo + Credencial + Rol)
    public Usuario loginUsuario(String correo, String numeroCredencial, Rol rol) {

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(correo);

        if (usuarioEncontrado.isPresent()) {
            Usuario usuario = usuarioEncontrado.get();
            boolean credencialCorrecta = usuario.getNumeroCredencial().equals(numeroCredencial);
            boolean rolCorrecto = usuario.getRol() == rol;

            if (credencialCorrecta && rolCorrecto) {
                return usuario;
            }
        }
        return null;
    }


    public Usuario actualizarUsuario(Long id, Usuario detalles) {
        Usuario existente = usuarioRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombre(detalles.getNombre());
            existente.setApellido(detalles.getApellido());
            existente.setCorreo(detalles.getCorreo());
            existente.setNumeroCredencial(detalles.getNumeroCredencial());
            existente.setRol(detalles.getRol());
            return usuarioRepository.save(existente);
        }
        return null;
    }


    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}