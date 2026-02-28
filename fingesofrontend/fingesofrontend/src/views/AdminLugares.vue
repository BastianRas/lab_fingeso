<script setup>
import { ref, onMounted } from 'vue';
import lugarService from '../services/lugarService';
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer, LMarker } from "@vue-leaflet/vue-leaflet";

const lugares = ref([]);
const idEditando = ref(null);
const mostrandoFormulario = ref(false);

const mapCenter = ref([-33.448890, -70.684650]);
const mapZoom = ref(16);

const datosFormulario = ref({
  nombre: '',
  descripcion: '',
  latitud: null,
  longitud: null
});

const seleccionarUbicacion = (evento) => {
  datosFormulario.value.latitud = evento.latlng.lat;
  datosFormulario.value.longitud = evento.latlng.lng;
};

const cargarLugares = async () => {
  try {
    const respuesta = await lugarService.obtenerTodos();
    lugares.value = respuesta.data;
  } catch (error) {
    console.error("Error cargando lugares:", error);
  }
};

const guardarLugar = async () => {
  if (!datosFormulario.value.latitud || !datosFormulario.value.longitud) {
    alert("Por favor, haz clic en el mapa para asignar una ubicación.");
    return;
  }

  try {
    if (idEditando.value) {
      await lugarService.actualizar(idEditando.value, datosFormulario.value);
    } else {
      await lugarService.crear(datosFormulario.value);
    }
    limpiarFormulario();
    await cargarLugares();
  } catch (error) {
    console.error("Error al guardar:", error);
  }
};

const cargarEdicion = (lugar) => {
  idEditando.value = lugar.id;
  mostrandoFormulario.value = true;
  datosFormulario.value = {
    nombre: lugar.nombre,
    descripcion: lugar.descripcion,
    latitud: lugar.latitud,
    longitud: lugar.longitud
  };
  mapCenter.value = [lugar.latitud, lugar.longitud];
};

const limpiarFormulario = () => {
  idEditando.value = null;
  datosFormulario.value = { nombre: '', descripcion: '', latitud: null, longitud: null };
  mostrandoFormulario.value = false;
  mapCenter.value = [-33.448890, -70.684650];
};

const eliminarLugar = async (id) => {
  if(confirm("¿Estás seguro de eliminar este lugar del campus?")) {
    try {
      await lugarService.eliminar(id);
      await cargarLugares();
    } catch (error) {
      alert("Error al eliminar");
    }
  }
};

onMounted(() => {
  cargarLugares();
});
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Gestión de Lugares (Edificios, Patios)</h1>
      <button class="back-btn" @click="$router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="actions">
        <button class="add-btn" @click="mostrandoFormulario ? limpiarFormulario() : (mostrandoFormulario = true)">
          {{ mostrandoFormulario ? 'Cancelar' : '+ Nuevo Lugar' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>{{ idEditando ? 'Editar Lugar' : 'Nuevo Lugar' }}</h3>
        
        <input v-model="datosFormulario.nombre" placeholder="Nombre (Ej: Biblioteca Central)" required style="width: 48%; margin-right: 2%; margin-bottom: 10px;" />
        <input v-model="datosFormulario.descripcion" placeholder="Descripción breve" required style="width: 50%; margin-bottom: 10px;"/>

        <div class="mapa-selector">
          <p style="margin: 0 0 5px 0; font-size: 0.9rem; color: #555;">📍 Haz clic en el mapa para asignar la ubicación exacta:</p>
          <div style="height: 250px; width: 100%; border: 1px solid #ccc; border-radius: 4px; overflow: hidden; z-index: 1;">
            <l-map v-model:zoom="mapZoom" v-model:center="mapCenter" :use-global-leaflet="false" @click="seleccionarUbicacion">
              <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"></l-tile-layer>
              <l-marker v-if="datosFormulario.latitud" :lat-lng="[datosFormulario.latitud, datosFormulario.longitud]"></l-marker>
            </l-map>
          </div>
        </div>

        <div class="form-buttons">
          <button @click="guardarLugar" class="save-btn">
            {{ idEditando ? 'Actualizar Lugar' : 'Guardar Lugar' }}
          </button>
        </div>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Coordenadas</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="lugar in lugares" :key="lugar.id">
            <td><strong>{{ lugar.nombre }}</strong></td>
            <td>{{ lugar.descripcion }}</td>
            <td style="font-size: 0.85rem; color: #555;">Lat: {{ lugar.latitud }} <br> Lng: {{ lugar.longitud }}</td>
            <td>
              <button class="edit-btn" @click="cargarEdicion(lugar)">Editar</button>
              <button class="delete-btn" @click="eliminarLugar(lugar.id)">Eliminar</button>
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
.back-btn { background: #555; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.add-btn { background: #ea7600; color: white; border: none; padding: 0.8rem 1.5rem; border-radius: 4px; cursor: pointer; font-weight: bold;}
.save-btn { background: #27ae60; color: white; border: none; padding: 0.8rem 2rem; cursor: pointer; border-radius: 4px; font-weight: bold; margin-top: 15px; }
.delete-btn { background: #c0392b; color: white; border: none; padding: 6px 10px; border-radius: 4px; cursor: pointer; margin-left: 5px; }
.edit-btn { background: #f39c12; color: white; border: none; padding: 6px 10px; border-radius: 4px; cursor: pointer; margin-right: 5px; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.form-card { background: white; padding: 1.5rem; margin-bottom: 2rem; border-radius: 8px; border-left: 5px solid #3498db; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.mapa-selector { width: 100%; margin-top: 10px; }
.form-buttons { display: flex; justify-content: flex-end; }
</style>