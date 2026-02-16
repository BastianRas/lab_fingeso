<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue'; 

const router = useRouter();
const profesorNombre = ref("Juan Pérez"); 

// Datos simulados
const claseActual = ref({
  asignatura: "Cálculo III",
  codigo: "MAT-301",
  sala: "Sala 304 - Pabellón Forma",
  horario: "10:00 - 11:30",
  estado: "En curso"
});

const irAlMapa = () => {
  router.push('/mapa');
};
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, Profesor {{ profesorNombre.split(' ')[0] }} 👋</h2>
        <p>Panel Docente</p>
      </div>
      <div class="avatar">👨‍🏫</div>
    </header>

    <section class="urgent-card">
      <div class="card-header">
        <span class="badge">En curso</span>
        <h3>{{ claseActual.asignatura }}</h3>
      </div>
      <div class="card-body">
        <div class="info-row">
          <i class="fas fa-map-marker-alt"></i> 
          <span class="highlight-location">{{ claseActual.sala }}</span>
        </div>
        <div class="info-row">
          <i class="far fa-clock"></i> 
          <span>{{ claseActual.horario }}</span>
        </div>
      </div>
      <button class="primary-btn">
        📋 Pasar Asistencia
      </button>
    </section>

    <section class="quick-actions">
      <h3>Gestión</h3>
      <div class="grid-buttons">
        <button class="action-btn" @click="irAlMapa">
          <span class="icon">🗺️</span> <span class="text">Ver Mapa</span>
        </button>
        <button class="action-btn">
          <span class="icon">📅</span> <span class="text">Mi Horario</span>
        </button>
        <button class="action-btn">
          <span class="icon">📝</span> <span class="text">Cursos</span>
        </button>
      </div>
    </section>

    <section class="news-section">
      <h3>📢 Cartelera USACH</h3>
      <CarteleraList />
    </section>

  </div>
</template>

<style scoped>
/* Estilos unificados */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.avatar { background: #ea7600; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #ea7600; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.badge { background: #ffe0b2; color: #e65100; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.primary-btn { background-color: #ea7600; color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; }

.grid-buttons { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 25px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #fff8f0; border-color: #ea7600; }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }
</style>