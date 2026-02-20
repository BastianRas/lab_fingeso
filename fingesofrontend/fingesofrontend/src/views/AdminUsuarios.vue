<script setup>
import { ref, onMounted } from 'vue';
import usuarioService from '../services/usuarioService';

const usuarios = ref([]);
const mostrandoFormulario = ref(false);
const idEditando = ref(null);

const datosFormulario = ref({ 
  nombre: '', 
  apellido: '',
  correo: '',
  numeroCredencial: '',
  rol: 'Alumno' 
});

const cargarUsuarios = async () => {
  try {
    const respuesta = await usuarioService.obtenerTodos();
    usuarios.value = respuesta.data;
  } catch (error) {
    console.error("Error cargando usuarios:", error);
  }
};

const guardarUsuario = async () => {
  try {
    if (idEditando.value) {
      await usuarioService.actualizar(idEditando.value, datosFormulario.value);
      alert("Usuario actualizado correctamente");
    } else {
      await usuarioService.crear(datosFormulario.value);
      alert("Usuario registrado correctamente");
    }
    limpiarFormulario();
    await cargarUsuarios();
  } catch (error) {
    console.error(error);
    alert("Error al guardar. Si es nuevo, verifica que el correo o credencial no existan ya.");
  }
};

const cargarEdicion = (usuario) => {
  idEditando.value = usuario.usuarioId;
  mostrandoFormulario.value = true;
  datosFormulario.value = { 
    nombre: usuario.nombre, 
    apellido: usuario.apellido, 
    correo: usuario.correo, 
    numeroCredencial: usuario.numeroCredencial,
    rol: usuario.rol 
  };
};

const limpiarFormulario = () => {
  idEditando.value = null;
  datosFormulario.value = { nombre: '', apellido: '', correo: '', numeroCredencial: '', rol: 'ALUMNO' };
  mostrandoFormulario.value = false;
};

const eliminarUsuario = async (id) => {
  if(confirm("¿Estás seguro de eliminar este usuario del sistema?")) {
    try {
      await usuarioService.eliminar(id);
      await cargarUsuarios();
    } catch (error) {
      alert("Error al eliminar");
    }
  }
};

onMounted(() => {
  cargarUsuarios();
});
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Gestión de Usuarios</h1>
      <button class="back-btn" @click="$router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="actions">
        <button 
          class="add-btn" 
          @click="mostrandoFormulario ? limpiarFormulario() : (mostrandoFormulario = true)"
          :style="{ backgroundColor: mostrandoFormulario ? '#7f8c8d' : '#8e44ad' }"
        >
          {{ mostrandoFormulario ? 'Cancelar' : '+ Nuevo Usuario' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>{{ idEditando ? 'Editar Usuario' : 'Registrar Usuario' }}</h3>
        
        <div class="form-inputs">
          <input v-model="datosFormulario.nombre" placeholder="Nombre" required />
          <input v-model="datosFormulario.apellido" placeholder="Apellido" required />
        </div>
        <div class="form-inputs">
          <input v-model="datosFormulario.correo" type="email" placeholder="Correo institucional" required />
          <input v-model="datosFormulario.numeroCredencial" placeholder="Número de Credencial" required />
          <select v-model="datosFormulario.rol">
            <option value="ADMINISTRADOR">Administrador</option>
            <option value="GESTOR">Gestor</option>
            <option value="OPERADOR">Operador</option>
            <option value="PROFESOR">Profesor</option>
            <option value="FUNCIONARIO">Funcionario</option>
            <option value="ALUMNO">Alumno</option>
          </select>
        </div>

        <button @click="guardarUsuario" class="save-btn">
          {{ idEditando ? 'Confirmar Cambios' : 'Registrar Usuario' }}
        </button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Correo</th>
            <th>Credencial</th>
            <th>Rol</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in usuarios" :key="user.usuarioId">
            <td><strong>{{ user.nombre }} {{ user.apellido }}</strong></td>
            <td>{{ user.correo }}</td>
            <td>{{ user.numeroCredencial }}</td>
            <td><span class="badge">{{ user.rol }}</span></td>
            <td class="action-buttons">
              <button class="edit-btn" @click="cargarEdicion(user)">Editar</button>
              <button class="delete-btn" @click="eliminarUsuario(user.usuarioId)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; background-color: #f4f4f4; min-height: 100vh; font-family: 'Segoe UI', sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.header h1 { color: #2c3e50; margin: 0; }
.back-btn { background: #555; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.add-btn { background: #ea7600; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.form-card { background: white; padding: 1.5rem; margin-top: 1rem; margin-bottom: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border-left: 5px solid #8e44ad; }
.form-inputs { display: flex; gap: 10px; margin: 15px 0; }
input, select { padding: 0.8rem; border: 1px solid #ccc; border-radius: 4px; flex: 1; }
.save-btn { background: #8e44ad; color: white; border: none; padding: 0.8rem 2rem; cursor: pointer; border-radius: 4px; font-weight: bold; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.badge { background: #34495e; padding: 4px 10px; border-radius: 12px; font-size: 0.85rem; color: white; }
.action-buttons { display: flex; gap: 5px; }
.delete-btn { background: #c0392b; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-left: 5px; }
.edit-btn { background: #f39c12; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-right: 5px; }
</style>