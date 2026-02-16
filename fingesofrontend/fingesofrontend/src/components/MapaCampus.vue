<script setup>
import { ref } from 'vue';
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer, LMarker, LPopup, LTooltip } from "@vue-leaflet/vue-leaflet";

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

const busqueda = ref("");
const lugarSeleccionado = ref(null);

// Función para buscar y centrar el mapa
const buscarLugar = () => {
  const encontrado = lugares.value.find(l => 
    l.nombre.toLowerCase().includes(busqueda.value.toLowerCase())
  );
  
  if (encontrado) {
    center.value = encontrado.coords;
    lugarSeleccionado.value = encontrado;
    zoom.value = 18; // Acercar cámara
  } else {
    alert("Lugar no encontrado en la base de datos.");
  }
};

const centrarEn = (lugar) => {
  center.value = lugar.coords;
  lugarSeleccionado.value = lugar;
  zoom.value = 18;
};
</script>

<template>
  <div class="mapa-container">
    
    <div class="control-panel">
      <h3>📍 Ubicación de Salas</h3>
      <div class="search-box">
        <input 
          v-model="busqueda" 
          @keyup.enter="buscarLugar"
          placeholder="Ej: Pabellón Forma, Informática..." 
        />
        <button @click="buscarLugar">Buscar</button>
      </div>
      
      <div class="quick-list">
        <span v-for="lugar in lugares" :key="lugar.id" @click="centrarEn(lugar)" class="pill">
          {{ lugar.nombre }}
        </span>
      </div>
    </div>

    <div class="map-frame">
      <l-map ref="map" v-model:zoom="zoom" v-model:center="center" :use-global-leaflet="false">
        <l-tile-layer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          layer-type="base"
          name="OpenStreetMap"
        ></l-tile-layer>

        <l-marker 
          v-for="lugar in lugares" 
          :key="lugar.id" 
          :lat-lng="lugar.coords"
        >
          <l-tooltip>{{ lugar.nombre }}</l-tooltip>
          <l-popup>
            <strong>{{ lugar.nombre }}</strong>
            <p>{{ lugar.descripcion }}</p>
            <a :href="`https://www.google.com/maps/dir/?api=1&destination=${lugar.coords[0]},${lugar.coords[1]}`" target="_blank" class="waze-link">
              🚗 Cómo llegar (Google Maps)
            </a>
          </l-popup>
        </l-marker>
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