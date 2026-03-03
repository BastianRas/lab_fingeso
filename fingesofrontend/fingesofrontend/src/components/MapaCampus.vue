<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // ✨ IMPORTAMOS EL ENRUTADOR
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer, LMarker, LPopup, LTooltip, LCircleMarker, LPolyline } from "@vue-leaflet/vue-leaflet";
import L from "leaflet";
import 'leaflet-routing-machine';
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';

import piuService from '../services/piuService';
import lugarService from '../services/lugarService';

const router = useRouter(); // ✨ INICIAMOS EL ENRUTADOR

// Coordenadas USACH (Centro aproximado: Foro/EAO)
const zoom = ref(16);
const center = ref([-33.448890, -70.684650]);
const lugares = ref([]);

const pius = ref([]); 
const piuSeleccionado = ref(null);
const busqueda = ref("");
const rutaCoordenadas = ref([]);

// ✨ FUNCIÓN PARA VOLVER ATRÁS MAGÍCAMENTE
const volverAtras = () => {
  router.back(); 
};

// Simulación de carga de PIUs y lugares desde el backend
const cargarDatosMapa = async () => {
  try {
    const resPius = await piuService.obtenerTodos();
    pius.value = resPius.data.filter(piu => piu.latitud != null && piu.longitud != null);

    const resLugares = await lugarService.obtenerTodos();
    lugares.value = resLugares.data.map(lugar => ({
      ...lugar,
      coords: [lugar.latitud, lugar.longitud]
    }));

  } catch (error) {
    console.error("Error cargando datos del mapa:", error);
  }
};

onMounted(() => {
  cargarDatosMapa();
});

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

// Función para probar ruteo peatonal entre dos puntos del campus
const trazarRutaHacia = async (destino) => {
  if (!piuSeleccionado.value) {
    alert("Por favor, selecciona un PIU como punto de origen.");
    return;
  }

  const origenLng = parseFloat(piuSeleccionado.value.longitud);
  const origenLat = parseFloat(piuSeleccionado.value.latitud);
  const destinoLng = parseFloat(destino.coords[1]);
  const destinoLat = parseFloat(destino.coords[0]);

  try {
    const url = `https://routing.openstreetmap.de/routed-foot/route/v1/foot/${origenLng},${origenLat};${destinoLng},${destinoLat}?overview=full&geometries=geojson`;
    
    const respuesta = await fetch(url);
    const datos = await respuesta.json();

    if (datos.code === "Ok" && datos.routes.length > 0) {
      rutaCoordenadas.value = datos.routes[0].geometry.coordinates.map(coord => [coord[1], coord[0]]);
      
      center.value = [(origenLat + destinoLat) / 2, (origenLng + destinoLng) / 2];
      zoom.value = 16;
    } else {
      alert("No se encontró una ruta peatonal válida entre estos dos puntos.");
    }
  } catch (error) {
    console.error("Error al calcular la ruta:", error);
    alert("Error al contactar al servidor de rutas.");
  }
};
</script>

<template>
  <div class="mapa-container">
    
    <div class="header-mapa">
      <button class="btn-volver" @click="volverAtras">
        <span class="flecha">⬅️</span> Volver Atrás
      </button>
      <h3>📍 Mapa del Campus y PIUs</h3>
    </div>

    <div class="control-panel">
      <div class="origen-selector" style="margin-bottom: 15px;">
        <label style="font-weight: bold; font-size: 0.9rem;">Punto de Origen:</label>
        <select v-model="piuSeleccionado" style="width: 100%; padding: 8px; margin-top: 5px; border-radius: 6px;">
          <option :value="null">Selecciona un PIU...</option>
          <option v-for="piu in pius" :key="'opt-'+piu.id" :value="piu">
            🖥️ {{ piu.codigo }} ({{ piu.ubicacion }})
          </option>
        </select>
      </div>

      <div class="search-box">
        <input 
          v-model="busqueda" 
          @keyup.enter="buscarLugar"
          placeholder="Buscar lugar o código de PIU..." 
        />
        <button @click="buscarLugar">Buscar</button>
      </div>
      
      <div class="quick-list">
        <span v-for="lugar in lugares" :key="'lug-'+lugar.id" @click="trazarRutaHacia(lugar)" class="pill">
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

        <l-polyline 
          v-if="rutaCoordenadas.length > 0" 
          :lat-lngs="rutaCoordenadas" 
          color="#3498db" 
          :weight="6" 
          :opacity="0.8">
        </l-polyline>

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
  height: 600px; /* Aumentado ligeramente para acomodar el nuevo botón */
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

/* ✨ ESTILOS DEL NUEVO ENCABEZADO Y BOTÓN */
.header-mapa {
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 2px solid #f0f2f5;
  padding-bottom: 15px;
  margin-bottom: 5px;
}

.header-mapa h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.4rem;
}

.btn-volver {
  background: white;
  color: #2c3e50;
  border: 1px solid #ccc;
  padding: 8px 15px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s ease;
}

.btn-volver:hover {
  background: #f8f9fa;
  border-color: #3498db;
  color: #3498db;
  transform: translateX(-2px); /* Pequeño efecto de moverse a la izquierda */
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
  flex: 1; 
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ddd;
  z-index: 1; 
}

.waze-link {
  display: block;
  margin-top: 5px;
  color: #3498db;
  text-decoration: none;
  font-weight: bold;
}
</style>