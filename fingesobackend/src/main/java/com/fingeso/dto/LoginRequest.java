// Archivo: fingesobackend/src/main/java/com/fingeso/dto/LoginRequest.java
package com.fingeso.dto;

import com.fingeso.model.Rol;
import lombok.Data;

@Data
public class LoginRequest {
    private String correo;            // Mantenemos correo
    private String numeroCredencial;  // Cambiamos contraseña por credencial
    private Rol rol;// Agregamos rol

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumeroCredencial() {
        return numeroCredencial;
    }

    public void setNumeroCredencial(String numeroCredencial) {
        this.numeroCredencial = numeroCredencial;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}


