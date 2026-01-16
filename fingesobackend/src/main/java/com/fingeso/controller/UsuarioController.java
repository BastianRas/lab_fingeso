package com.fingeso.controller;

import com.fingeso.dto.LoginRequest;
import com.fingeso.model.Usuario;
import com.fingeso.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.loginUsuario(
                loginRequest.getCorreo(),
                loginRequest.getNumeroCredencial(),
                loginRequest.getRol()
        );

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(401).body("Error: Datos incorrectos. Verifica correo, credencial y rol.");
        }
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
    }
}