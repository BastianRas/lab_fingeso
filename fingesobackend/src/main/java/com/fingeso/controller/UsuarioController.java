package com.fingeso.controller;

import com.fingeso.dto.LoginRequest;
import com.fingeso.model.Usuario;
import com.fingeso.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
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

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.obtenerUsuarios();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}