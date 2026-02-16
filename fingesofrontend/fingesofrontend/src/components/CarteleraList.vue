<script setup>
import { ref } from 'vue';

// Datos simulados (Mock) de noticias USACH
const noticias = ref([
  {
    id: 1,
    titulo: "Suspensión de actividades por Feriado",
    fecha: "15 Ago 2024",
    resumen: "Se informa a la comunidad universitaria que este viernes no habrá actividades académicas ni administrativas.",
    categoria: "Institucional"
  },
  {
    id: 2,
    titulo: "Nuevos horarios de Biblioteca Central",
    fecha: "14 Ago 2024",
    resumen: "La biblioteca extenderá su horario de atención hasta las 21:00 hrs durante periodo de exámenes.",
    categoria: "Servicios"
  },
  {
    id: 3,
    titulo: "Charla de Inteligencia Artificial",
    fecha: "12 Ago 2024",
    resumen: "El departamento de informática invita a la charla magistral sobre IA Generativa en el Aula Magna.",
    categoria: "Académico"
  }
]);
</script>

<template>
  <div class="cartelera-container">
    <div v-for="noticia in noticias" :key="noticia.id" class="noticia-card">
      <div class="noticia-header">
        <span class="categoria-badge">{{ noticia.categoria }}</span>
        <span class="fecha">{{ noticia.fecha }}</span>
      </div>
      <h4>{{ noticia.titulo }}</h4>
      <p>{{ noticia.resumen }}</p>
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