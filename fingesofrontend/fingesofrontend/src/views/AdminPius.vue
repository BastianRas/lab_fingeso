<script setup>
import { ref, onMounted } from 'vue';
import piuService from '../services/piuService';

const pius = ref([]);
const idEditando = ref(null);
const mostrandoFormulario = ref(false);

const datosFormulario = ref({
  codigo: '',
  ubicacion: '',
  estado: 'Activo',
  latitud: null,
  longitud: null
});

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
    if (idEditando.value) {
      await piuService.actualizar(idEditando.value, datosFormulario.value);
      alert("PIU actualizado correctamente");
    } else {
      await piuService.crear(datosFormulario.value);
      alert("PIU creado correctamente");
    }
    
    limpiarFormulario();
    await cargarPius();
  } catch (error) {
    console.error(error);
    alert("Error al guardar");
  }
};

const cargarEdicion = (piu) => {
  idEditando.value = piu.id;
  mostrandoFormulario.value = true;

  datosFormulario.value = {
    codigo: piu.codigo,
    ubicacion: piu.ubicacion,
    estado: piu.estado,
    latitud: piu.latitud,
    longitud: piu.longitud
  };
};

const limpiarFormulario = () => {
  idEditando.value = null;
  datosFormulario.value = { codigo: '', ubicacion: '', estado: 'Activo', latitud: null, longitud: null };
  mostrandoFormulario.value = false;
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
        <button class="add-btn" @click="mostrandoFormulario ? limpiarFormulario() : (mostrandoFormulario = true)">
          {{ mostrandoFormulario ? 'Cancelar' : '+ Nuevo PIU' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>{{ idEditando ? 'Editar Dispositivo' : 'Nuevo Dispositivo' }}</h3>
        
        <input v-model="datosFormulario.codigo" placeholder="Código (Ej: PIU-004)" />
        <input v-model="datosFormulario.ubicacion" placeholder="Ubicación (Ej: Foro)" />
        <select v-model="datosFormulario.estado">
          <option>Activo</option>
          <option>Inactivo</option>
          <option>Mantenimiento</option>
        </select>

        <div class="form-inputs">
          <input v-model="datosFormulario.latitud" type="number" step="any" placeholder="Latitud (Ej: -33.4485)" required/>
          <input v-model="datosFormulario.longitud" type="number" step="any" placeholder="Longitud (Ej: -70.6693)" required/>
        </div>

        <div class="form-buttons">
          <button @click="guardarPiu" class="save-btn">
            {{ idEditando ? 'Actualizar' : 'Guardar' }}
          </button>
        </div>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Código</th>
            <th>Ubicación</th>
            <th>Coordenadas</th> <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="piu in pius" :key="piu.id">
            <td><strong>{{ piu.codigo }}</strong></td>
            <td>{{ piu.ubicacion }}</td>

            <td style="font-size: 0.85rem; color: #555;">Lat: {{ piu.latitud }} <br> Lng: {{ piu.longitud }}</td>
            <td>
              <span :class="['badge', piu.estado === 'Activo' ? 'green' : 'red']">
                {{ piu.estado }}
              </span>
            </td>
            <td>
              <button class="edit-btn" @click="cargarEdicion(piu)"> Editar</button>
              <button class="delete-btn" @click="eliminarPiu(piu.id)"> Eliminar</button>
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
.save-btn { background: #27ae60; color: white; border: none; padding: 0.6rem 1.2rem; cursor: pointer; border-radius: 4px; font-weight: bold; width: 100%; }
.delete-btn { background: #c0392b; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-left: 5px; }
.edit-btn { background: #f39c12; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 4px; cursor: pointer; margin-right: 5px; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.badge { padding: 4px 8px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
.badge.green { background: #d4edda; color: #155724; }
.badge.red { background: #f8d7da; color: #721c24; }

.form-card { background: white; padding: 1.5rem; margin-bottom: 1.5rem; border-radius: 8px; display: flex; gap: 0.5rem; flex-wrap: wrap; align-items: center; border-left: 5px solid #ea7600; }
.form-card h3 { width: 100%; margin-top: 0; margin-bottom: 10px; color: #333; }
input, select { padding: 0.6rem; border: 1px solid #ccc; border-radius: 4px; flex: 1; }
.form-buttons { flex-basis: 100%; display: flex; justify-content: flex-end; margin-top: 10px; }
.form-buttons button { width: auto; min-width: 150px; }
</style>