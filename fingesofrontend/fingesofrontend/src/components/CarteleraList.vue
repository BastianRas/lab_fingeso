<script setup>
import { ref, onMounted } from 'vue';
import eventoService from '../services/eventoService';

// Variable vacía que se llenará con los datos de la base de datos
const noticias = ref([]);
const cargando = ref(true);

onMounted(async () => {
  try {
    // Llamamos al backend para traer los eventos creados por el administrador
    const respuesta = await eventoService.obtenerTodos();
    
    // Invertimos el arreglo para que los eventos más nuevos (los últimos creados) salgan arriba
    noticias.value = respuesta.data.reverse();
  } catch (error) {
    console.error("Error al cargar la cartelera:", error);
  } finally {
    cargando.value = false;
  }
});
</script>

<template>
  <div class="cartelera-container">
    
    <div v-if="cargando" class="mensaje-estado">
      ⏳ Cargando cartelera...
    </div>

    <div v-else-if="noticias.length === 0" class="mensaje-estado">
      📭 No hay noticias publicadas en este momento.
    </div>

    <div v-for="noticia in noticias" :key="noticia.id" class="noticia-card">
      <div class="noticia-header">
        <span class="categoria-badge">{{ noticia.tipo }}</span>
        <span class="fecha">{{ noticia.fecha }}</span>
      </div>
      <h4>{{ noticia.titulo }}</h4>
      <p>{{ noticia.descripcion }}</p>
      <button class="leer-mas">Leer más</button>
    </div>
    
  </div>
</template>

<style scoped>
.cartelera-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.mensaje-estado {
  text-align: center;
  padding: 20px;
  color: #7f8c8d;
  background: white;
  border-radius: 12px;
  border: 1px dashed #ccc;
  font-style: italic;
}

.noticia-card {
  background: white;
  padding: 15px;
  border-radius: 12px;
  border-left: 4px solid #ea7600; /* Color Naranja USACH */
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  margin-bottom: 10px;
}

.noticia-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.noticia-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  margin-bottom: 8px;
  color: #666;
}

.categoria-badge {
  background-color: #fff3e0;
  color: #d35400;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
  font-size: 0.7rem;
}

h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

p {
  font-size: 0.9rem;
  color: #555;
  line-height: 1.4;
  margin-bottom: 10px;
}

.leer-mas {
  background: none;
  border: none;
  color: #ea7600;
  font-weight: bold;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0;
}

.leer-mas:hover {
  text-decoration: underline;
}
</style>