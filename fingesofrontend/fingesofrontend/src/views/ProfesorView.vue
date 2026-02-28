<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';
import claseService from '../services/claseService';

const router = useRouter();
const profesorNombre = ref('');
const clases = ref([]);
const vistaActiva = ref(null); // 'horario' | 'cursos' | null

const irAlMapa = () => router.push('/mapa');

const mostrar = (vista) => {
  vistaActiva.value = vistaActiva.value === vista ? null : vista;
};

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    profesorNombre.value = user.nombre;
    try {
      clases.value = await claseService.getClasesByProfesor(user.usuarioId);
    } catch {
      clases.value = [];
    }
  }
});

const primerCurso = () => clases.value.length > 0 ? clases.value[0] : null;
</script>

<template>
  <div class="dashboard-container">

    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, Profesor {{ profesorNombre }} 👋</h2>
        <p>Panel Docente</p>
      </div>
      <div class="avatar">👨‍🏫</div>
    </header>

    <section v-if="primerCurso()" class="urgent-card">
      <div class="card-header">
        <span class="badge">Semestre {{ primerCurso().semestre }}</span>
        <h3>{{ primerCurso().nombre }}</h3>
      </div>
      <div class="card-body">
        <div class="info-row">
          <i class="fas fa-map-marker-alt"></i>
          <span class="highlight-location">{{ primerCurso().sala }}</span>
        </div>
        <div class="info-row">
          <i class="far fa-clock"></i>
          <span>{{ primerCurso().horario }}</span>
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
        <button class="action-btn" :class="{ active: vistaActiva === 'horario' }" @click="mostrar('horario')">
          <span class="icon">📅</span> <span class="text">Mi Horario</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'cursos' }" @click="mostrar('cursos')">
          <span class="icon">📝</span> <span class="text">Cursos</span>
        </button>
      </div>

      <!-- Panel Mi Horario -->
      <div v-if="vistaActiva === 'horario'" class="info-panel">
        <h4>📅 Mi Horario — {{ profesorNombre }}</h4>
        <div v-if="clases.length" class="tabla-clases">
          <div class="clase-row header-row">
            <span>Asignatura</span><span>Código</span><span>Horario</span><span>Sala</span>
          </div>
          <div v-for="clase in clases" :key="clase.id" class="clase-row">
            <span>{{ clase.nombre }}</span>
            <span>{{ clase.codigo }}</span>
            <span>{{ clase.horario }}</span>
            <span>{{ clase.sala }}</span>
          </div>
        </div>
        <p v-else class="sin-datos">No hay clases registradas.</p>
      </div>

      <!-- Panel Cursos -->
      <div v-if="vistaActiva === 'cursos'" class="info-panel">
        <h4>📝 Mis Cursos</h4>
        <div v-if="clases.length" class="tabla-clases">
          <div class="clase-row header-row">
            <span>Asignatura</span><span>Código</span><span>Horario</span><span>Sala</span><span>Semestre</span>
          </div>
          <div v-for="clase in clases" :key="clase.id" class="clase-row">
            <span>{{ clase.nombre }}</span>
            <span>{{ clase.codigo }}</span>
            <span>{{ clase.horario }}</span>
            <span>{{ clase.sala }}</span>
            <span>{{ clase.semestre }}</span>
          </div>
        </div>
        <p v-else class="sin-datos">No hay cursos registrados.</p>
      </div>
    </section>

    <section class="news-section">
      <h3>📢 Cartelera USACH</h3>
      <CarteleraList />
    </section>

  </div>
</template>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.avatar { background: #ea7600; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #ea7600; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.badge { background: #ffe0b2; color: #e65100; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.primary-btn { background-color: #ea7600; color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; }

.grid-buttons { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 15px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #fff8f0; border-color: #ea7600; }
.action-btn.active { background-color: #fff8f0; border-color: #ea7600; font-weight: bold; }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }

.info-panel { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-left: 5px solid #ea7600; }
.info-panel h4 { margin: 0 0 15px; color: #2c3e50; }
.sin-datos { color: #999; font-style: italic; }

.tabla-clases { overflow-x: auto; }
.clase-row { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 1fr; gap: 8px; padding: 8px 4px; border-bottom: 1px solid #f0f0f0; font-size: 0.85rem; }
.header-row { font-weight: bold; color: #555; background: #f8f9fa; border-radius: 6px; }
</style>
