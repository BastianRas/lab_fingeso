<script setup>
import { ref, onMounted } from 'vue';
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer, LMarker, LPopup, LTooltip, LCircleMarker } from "@vue-leaflet/vue-leaflet";

import piuService from '../services/piuService';

// Coordenadas USACH (Centro aproximado: Foro/EAO)
const zoom = ref(16);
const center = ref([-33.448890, -70.684650]);

// Base de datos simulada de Salas y Edificios
const lugares = ref([
  { id: 1, nombre: "Pabellón Forma (Salas 300-399)", coords: [-33.4495, -70.6850], descripcion: "3er Piso - Escaleras lado norte." },
  { id: 2, nombre: "Departamento Informática", coords: [-33.4485, -70.6835], descripcion: "Laboratorios y oficinas administrativas." },
  { id: 3, nombre: "Biblioteca Central", coords: [-33.4478, -70.6820], descripcion: "Entrada principal por Alameda." },
  { id: 4, nombre: "EAO (Escuela de Artes)", coords: [-33.4498, -70.6860], descripcion: "Auditorios y salas históricas." },
  { id: 5, nombre: "Casino Central", coords: [-33.4480, -70.6855], descripcion: "Almuerzos Junaeb y cafetería." },
]);

const pius = ref([]); 
const busqueda = ref("");
const lugarSeleccionado = ref(null);

// Simulación de carga de PIUs desde backend
const cargarPius = async () => {
  try {
    const response = await piuService.obtenerTodos();
    
    pius.value = response.data.filter(piu => piu.latitud != null && piu.longitud != null);
    console.log("PIUs listos para dibujar:", pius.value); 
    
  } catch (error) {
    console.error("Error cargando los PIUs:", error);
  }
};

// Función para buscar y centrar el mapa
const buscarLugar = () => {
  const query = busqueda.value.toLowerCase();
  
  // Buscar en lugares fijos
  let encontrado = lugares.value.find(l => l.nombre.toLowerCase().includes(query));
  
  // Si no es lugar fijo, buscar en PIUs
  if (!encontrado) {
    encontrado = pius.value.find(p => p.codigo.toLowerCase().includes(query) || p.ubicacion.toLowerCase().includes(query));
    if (encontrado) {
      encontrado.coords = [encontrado.latitud, encontrado.longitud]; 
    }
  }
  
  if (encontrado) {
    center.value = encontrado.coords;
    zoom.value = 18; 
  } else {
    alert("Lugar o PIU no encontrado en el sistema.");
  }
};

const centrarEn = (coords) => {
  center.value = coords;
  zoom.value = 18;
};

onMounted(() => {
  cargarPius();
});
</script>

<template>
  <div class="mapa-container">
    <div class="control-panel">
      <h3>📍 Mapa del Campus y PIUs</h3>
      <div class="search-box">
        <input 
          v-model="busqueda" 
          @keyup.enter="buscarLugar"
          placeholder="Buscar lugar o código de PIU..." 
        />
        <button @click="buscarLugar">Buscar</button>
      </div>
      
      <div class="quick-list">
        <span v-for="lugar in lugares" :key="'lug-'+lugar.id" @click="centrarEn(lugar.coords)" class="pill">
          {{ lugar.nombre }}
        </span>
      </div>
    </div>

    <div class="map-frame" style="height: 400px; width: 100%;">
      <l-map ref="map" v-model:zoom="zoom" v-model:center="center" :use-global-leaflet="false">
        <l-tile-layer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          layer-type="base"
          name="OpenStreetMap"
        ></l-tile-layer>

        <l-marker 
          v-for="lugar in lugares" 
          :key="'lug-mark-'+lugar.id" 
          :lat-lng="lugar.coords"
        >
          <l-tooltip>{{ lugar.nombre }}</l-tooltip>
          <l-popup>
            <strong>{{ lugar.nombre }}</strong>
            <p>{{ lugar.descripcion }}</p>
          </l-popup>
        </l-marker>

        <l-circle-marker 
          v-for="piu in pius" 
          :key="'piu-'+piu.id" 
          :lat-lng="[parseFloat(piu.latitud), parseFloat(piu.longitud)]"
          :radius="8"
          color="#c0392b"
          fill-color="#e74c3c"
          :fill-opacity="0.9"
        >
          <l-tooltip>🖥️ {{ piu.codigo }}</l-tooltip>
          <l-popup>
            <strong>🖥️ {{ piu.codigo }}</strong>
            <p>Ref: {{ piu.ubicacion }}</p>
            <p><em>Estado: {{ piu.estado }}</em></p>
          </l-popup>
        </l-circle-marker>

      </l-map>
    </div>
  </div>
</template>

<style scoped>
.mapa-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  height: 500px; /* Altura del mapa */
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.control-panel h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.search-box input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.search-box button {
  background: #ea7600;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 6px;
  cursor: pointer;
}

.quick-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.pill {
  background: #f0f2f5;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  cursor: pointer;
  white-space: nowrap;
  border: 1px solid #ddd;
}

.pill:hover {
  background: #e0e0e0;
}

.map-frame {
  flex: 1; /* Ocupa el resto del espacio */
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ddd;
  z-index: 1; /* Para que no tape menús */
}

.waze-link {
  display: block;
  margin-top: 5px;
  color: #3498db;
  text-decoration: none;
  font-weight: bold;
}
</style>