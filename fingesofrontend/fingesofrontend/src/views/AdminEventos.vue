<script setup>
import { ref, onMounted } from 'vue';
import eventoService from '../services/eventoService';

const eventos = ref([]);
const mostrandoFormulario = ref(false);
const idEditando = ref(null);

const datosFormulario = ref({ 
  titulo: '', 
  descripcion: '', 
  fecha: '', 
  tipo: 'Académico' 
});

const cargarEventos = async () => {
  try {
    const respuesta = await eventoService.obtenerTodos();
    eventos.value = respuesta.data;
  } catch (error) {
    console.error("Error cargando eventos:", error);
  }
};

const guardarEvento = async () => {
  try {
    if (idEditando.value) {
      await eventoService.actualizar(idEditando.value, datosFormulario.value);
      alert("Evento actualizado correctamente");
    } else {
      await eventoService.crear(datosFormulario.value);
      alert("Evento creado correctamente");
    }
    limpiarFormulario();
    await cargarEventos();
  } catch (error) {
    console.error(error);
    alert("Error al guardar el evento.");
  }
};

const cargarEdicion = (evento) => {
  idEditando.value = evento.id;
  mostrandoFormulario.value = true;
  datosFormulario.value = { 
    titulo: evento.titulo, 
    descripcion: evento.descripcion, 
    fecha: evento.fecha, 
    tipo: evento.tipo 
  };
};

const limpiarFormulario = () => {
  idEditando.value = null;
  datosFormulario.value = { titulo: '', descripcion: '', fecha: '', tipo: 'Académico' };
  mostrandoFormulario.value = false;
};

const eliminarEvento = async (id) => {
  if(confirm("¿Estás seguro de eliminar esta noticia de la cartelera?")) {
    try {
      await eventoService.eliminar(id);
      await cargarEventos();
    } catch (error) {
      alert("Error al eliminar");
    }
  }
};

onMounted(() => {
  cargarEventos();
});
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Gestión de Cartelera (Eventos)</h1>
      <button class="back-btn" @click="$router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="actions">
        <button 
          class="add-btn" 
          @click="mostrandoFormulario ? limpiarFormulario() : (mostrandoFormulario = true)"
          :style="{ backgroundColor: mostrandoFormulario ? '#7f8c8d' : '#27ae60' }"
        >
          {{ mostrandoFormulario ? 'Cancelar' : '+ Nuevo Evento' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>{{ idEditando ? '✏️ Editar Evento' : '✨ Nueva Publicación' }}</h3>
        
        <div class="form-inputs">
          <input v-model="datosFormulario.titulo" placeholder="Título del evento" required />
          <input v-model="datosFormulario.fecha" type="date" required />
          <select v-model="datosFormulario.tipo">
            <option>Académico</option>
            <option>Cultural</option>
            <option>Aviso</option>
          </select>
        </div>
        
        <textarea v-model="datosFormulario.descripcion" placeholder="Descripción completa..." rows="3"></textarea>

        <button @click="guardarEvento" class="save-btn">
          {{ idEditando ? 'Confirmar Cambios' : 'Publicar Evento' }}
        </button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Fecha</th>
            <th>Título</th>
            <th>Tipo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="evento in eventos" :key="evento.id">
            <td>{{ evento.fecha }}</td>
            <td><strong>{{ evento.titulo }}</strong></td>
            <td><span class="badge">{{ evento.tipo }}</span></td>
            <td class="action-buttons">
              <button class="edit-btn" @click="cargarEdicion(evento)">Editar</button>
              <button class="delete-btn" @click="eliminarEvento(evento.id)">Eliminar</button>
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
.form-card { background: white; padding: 1.5rem; margin-top: 1rem; margin-bottom: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border-left: 5px solid #26a75b; }
.form-inputs { display: flex; gap: 10px; margin: 15px 0; }
input, select { padding: 0.8rem; border: 1px solid #ccc; border-radius: 4px; flex: 1; }
textarea { width: 100%; padding: 0.8rem; border: 1px solid #ccc; border-radius: 4px; margin-bottom: 15px; resize: vertical; box-sizing: border-box; }
.save-btn { background: #2980b9; color: white; border: none; padding: 0.8rem 2rem; cursor: pointer; border-radius: 4px; font-weight: bold; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.badge { background: #34495e; padding: 4px 10px; border-radius: 12px; font-size: 0.85rem; color: white; }
.action-buttons { display: flex; gap: 5px; }
.delete-btn { background: #c0392b; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-left: 5px; }
.edit-btn { background: #f39c12; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-right: 5px; }
</style>