<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '../services/authService';

const router = useRouter();

// Variables reactivas
const correo = ref('');
const numeroCredencial = ref('');
const rolSeleccionado = ref('');
const error = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  error.value = '';
  
  if (!rolSeleccionado.value) {
    error.value = "Por favor selecciona un rol.";
    return;
  }

  isLoading.value = true;

  try {
    const usuario = await authService.login(correo.value, numeroCredencial.value, rolSeleccionado.value);
    
    console.log("Login exitoso:", usuario);
    // Puedes quitar el alert si prefieres que entre directo
    // alert(`Bienvenido ${usuario.nombre} (${usuario.rol})`);
    
    // --- LÓGICA DE REDIRECCIÓN POR ROL ---
    switch (usuario.rol) {
      case 'ADMINISTRADOR':
        router.push('/admin');
        break;
      case 'GESTOR':
        router.push('/gestor');
        break;
      case 'OPERADOR':
        router.push('/operador');
        break;
      case 'ALUMNO':
      case 'PROFESOR':
      case 'FUNCIONARIO':
        router.push('/home'); // Estos 3 roles van a la vista general de usuario
        break;
      default:
        console.warn("Rol no reconocido:", usuario.rol);
        router.push('/home'); // Por seguridad, mandamos al home por defecto
    }
    // -------------------------------------

  } catch (err) {
    if (err.response && err.response.status === 401) {
        error.value = "Datos incorrectos. Verifica tu Rol, Correo y Credencial.";
    } else {
        console.error(err);
        error.value = "Error de conexión con el servidor.";
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h2>Acceso PIU</h2>
      
      <form @submit.prevent="handleLogin">
        
        <div class="form-group">
          <label>Rol de Usuario</label>
          <select v-model="rolSeleccionado" required>
            <option disabled value="">Selecciona tu rol</option>
            <option value="ADMINISTRADOR">Administrador</option>
            <option value="GESTOR">Gestor</option>
            <option value="OPERADOR">Operador</option>
            <option value="ALUMNO">Alumno</option>
            <option value="PROFESOR">Profesor</option>
            <option value="FUNCIONARIO">Funcionario</option>
          </select>
        </div>

        <div class="form-group">
          <label>Correo Institucional</label>
          <input 
            type="email" 
            v-model="correo" 
            placeholder="ejemplo@usach.cl"
            required
          />
        </div>

        <div class="form-group">
          <label>Número de Credencial</label>
          <input 
            type="text" 
            v-model="numeroCredencial" 
            placeholder="Ej: 12MAR345"
            required
          />
        </div>

        <div v-if="error" class="error-msg">
          {{ error }}
        </div>

        <button type="submit" :disabled="isLoading">
          {{ isLoading ? 'Verificando...' : 'Ingresar' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #34495e;
}

input, select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #000000;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  background-color: #d0c8c8; /* Cambié el fondo gris oscuro original a blanco para mejor lectura */
}

input:focus, select:focus {
  border-color: #ea7600;
  outline: none;
}

button {
  width: 100%;
  padding: 0.9rem;
  background-color: #ea7600;
  color: rgb(137, 115, 115);
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 1rem;
}

button:hover {
  background-color: #d35400;
}

button:disabled {
  background-color: #fab1a0;
  cursor: not-allowed;
}

.error-msg {
  color: #e74c3c;
  margin-top: 10px;
  text-align: center;
  font-size: 0.9rem;
  background-color: #fadbd8;
  padding: 0.5rem;
  border-radius: 4px;
}
</style>