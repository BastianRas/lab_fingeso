<script setup>
import { ref, onMounted } from 'vue';
import piuService from '../services/piuService';

const pius = ref([]);
const nuevoPiu = ref({ codigo: '', ubicacion: '', estado: 'Activo' });
const mostrandoFormulario = ref(false);

const cargarPius = async () => {
  try {
    const respuesta = await piuService.obtenerTodos();
    pius.value = respuesta.data;
  } catch (error) {
    console.error("Error cargando PIUs:", error);
  }
};

const guardarPiu = async () => {
  try {
    await piuService.crear(nuevoPiu.value);
    await cargarPius();
    nuevoPiu.value = { codigo: '', ubicacion: '', estado: 'Activo' };
    mostrandoFormulario.value = false;
  } catch (error) {
    alert("Error al guardar el PIU");
  }
};

const eliminarPiu = async (id) => {
  if(confirm("¿Estás seguro de eliminar este dispositivo?")) {
    try {
      await piuService.eliminar(id);
      await cargarPius();
    } catch (error) {
      alert("Error al eliminar");
    }
  }
};

onMounted(() => {
  cargarPius();
});
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Gestión de Dispositivos (PIUs)</h1>
      <button class="back-btn" @click="$router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="actions">
        <button class="add-btn" @click="mostrandoFormulario = !mostrandoFormulario">
          {{ mostrandoFormulario ? 'Cancelar' : '+ Nuevo PIU' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>Nuevo Dispositivo</h3>
        <input v-model="nuevoPiu.codigo" placeholder="Código (Ej: PIU-004)" />
        <input v-model="nuevoPiu.ubicacion" placeholder="Ubicación (Ej: Foro)" />
        <select v-model="nuevoPiu.estado">
          <option>Activo</option>
          <option>Inactivo</option>
          <option>Mantenimiento</option>
        </select>
        <button @click="guardarPiu" class="save-btn">Guardar</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Código</th>
            <th>Ubicación Física</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="piu in pius" :key="piu.id">
            <td>{{ piu.codigo }}</td>
            <td>{{ piu.ubicacion }}</td>
            <td>
              <span :class="['badge', piu.estado === 'Activo' ? 'green' : 'red']">
                {{ piu.estado }}
              </span>
            </td>
            <td>
              <button class="delete-btn" @click="eliminarPiu(piu.id)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>
  </div>
</template>

<style scoped>
/* Estilos similares a los anteriores */
.dashboard { padding: 2rem; background-color: #f4f4f4; min-height: 100vh; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.back-btn { background: #555; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.add-btn { background: #ea7600; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.save-btn { background: #27ae60; color: white; border: none; padding: 0.5rem; width: 100%; margin-top: 10px; cursor: pointer; }
.delete-btn { background: #c0392b; color: white; border: none; padding: 0.3rem 0.6rem; border-radius: 4px; cursor: pointer; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.badge { padding: 4px 8px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
.badge.green { background: #d4edda; color: #155724; }
.badge.red { background: #f8d7da; color: #721c24; }
.form-card { background: white; padding: 1rem; margin-bottom: 1rem; border-radius: 8px; display: flex; gap: 0.5rem; flex-wrap: wrap; }
input, select { padding: 0.5rem; border: 1px solid #ccc; border-radius: 4px; flex: 1; }
</style>