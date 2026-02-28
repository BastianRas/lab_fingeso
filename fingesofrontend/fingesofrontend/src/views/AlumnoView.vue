<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';
import matriculaService from '../services/matriculaService';

const router = useRouter();
const alumnoNombre = ref('');

const proximaClase = ref({
  materia: "Ingeniería de Software",
  sala: "Sala 202 - Depto Informática",
  hora: "14:30 PM"
});

// Datos matrícula
const matricula = ref(null);
const vistaActiva = ref(null); // 'malla' | 'estado' | null

const irAlMapa = () => router.push('/mapa');

const mostrar = (vista) => {
  vistaActiva.value = vistaActiva.value === vista ? null : vista;
};

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    alumnoNombre.value = user.nombre;
    try {
      matricula.value = await matriculaService.obtenerMatricula(user.usuarioId);
    } catch {
      matricula.value = null;
    }
  }
});
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, {{ alumnoNombre }} ✌️</h2>
        <p>Estudiante Regular</p>
      </div>
      <div class="avatar" style="background-color: #3498db;">🎓</div>
    </header>

    <section class="urgent-card student-card">
      <div class="card-header">
        <span class="badge-blue">Próxima Clase</span>
        <h3>{{ proximaClase.materia }}</h3>
      </div>
      <div class="card-body">
        <div class="info-row">
          <i class="fas fa-map-pin"></i> 
          <span class="highlight-location">{{ proximaClase.sala }}</span>
        </div>
        <div class="info-row">
          <i class="far fa-clock"></i> 
          <span>{{ proximaClase.hora }}</span>
        </div>
      </div>
      <button class="primary-btn" style="background-color: #3498db;">
        📚 Ver Material de Estudio
      </button>
    </section>

    <section class="quick-actions">
      <h3>Mis Accesos</h3>
      <div class="grid-buttons">
        <button class="action-btn" @click="irAlMapa">
          <span class="icon">🗺️</span> <span class="text">Ver Campus</span>
        </button>
        <button class="action-btn">
          <span class="icon">📊</span> <span class="text">Mis Notas</span>
        </button>
        <button class="action-btn">
          <span class="icon">📄</span> <span class="text">Certificados</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'malla' }" @click="mostrar('malla')">
          <span class="icon">📚</span> <span class="text">Malla Curricular</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'estado' }" @click="mostrar('estado')">
          <span class="icon">🎓</span> <span class="text">Estado Matrícula</span>
        </button>
      </div>

      <!-- Panel Malla Curricular -->
      <div v-if="vistaActiva === 'malla'" class="info-panel">
        <h4>📚 Malla Curricular — {{ matricula?.carrera?.nombre }}</h4>
        <p class="facultad">{{ matricula?.carrera?.facultad }}</p>
        <div v-if="matricula?.clases?.length" class="tabla-clases">
          <div class="clase-row header-row">
            <span>Asignatura</span><span>Código</span><span>Horario</span><span>Sala</span><span>Profesor</span>
          </div>
          <div v-for="clase in matricula.clases" :key="clase.id" class="clase-row">
            <span>{{ clase.nombre }}</span>
            <span>{{ clase.codigo }}</span>
            <span>{{ clase.horario }}</span>
            <span>{{ clase.sala }}</span>
            <span>{{ clase.profesor }}</span>
          </div>
        </div>
        <p v-else class="sin-datos">No hay clases registradas.</p>
      </div>

      <!-- Panel Estado Matrícula -->
      <div v-if="vistaActiva === 'estado'" class="info-panel">
        <h4>🎓 Estado de Matrícula</h4>
        <div v-if="matricula" class="estado-grid">
          <div class="estado-item">
            <span class="label">Carrera</span>
            <span class="value">{{ matricula.carrera?.nombre }}</span>
          </div>
          <div class="estado-item">
            <span class="label">Facultad</span>
            <span class="value">{{ matricula.carrera?.facultad }}</span>
          </div>
          <div class="estado-item">
            <span class="label">Estado</span>
            <span class="value badge-estado" :class="matricula.estadoMatricula?.toLowerCase()">
              {{ matricula.estadoMatricula }}
            </span>
          </div>
          <div class="estado-item">
            <span class="label">Ramos inscritos</span>
            <span class="value">{{ matricula.clases?.length ?? 0 }}</span>
          </div>
        </div>
        <p v-else class="sin-datos">No se encontró información de matrícula.</p>
      </div>
    </section>

    <section class="news-section">
      <h3>📢 Cartelera USACH</h3>
      <CarteleraList />
    </section>

  </div>
</template>

<style scoped>
/* Estilos Base */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.avatar { color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

/* Tarjeta Azul para Estudiante */
.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #3498db; }
.badge-blue { background: #e3f2fd; color: #1565c0; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.primary-btn { color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; }

.grid-buttons { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 15px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #e3f2fd; border-color: #3498db; }
.action-btn.active { background-color: #e3f2fd; border-color: #3498db; font-weight: bold; }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }

.info-panel { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-left: 5px solid #3498db; }
.info-panel h4 { margin: 0 0 5px; color: #2c3e50; }
.facultad { color: #7f8c8d; font-size: 0.9rem; margin-bottom: 15px; }
.sin-datos { color: #999; font-style: italic; }

.tabla-clases { overflow-x: auto; }
.clase-row { display: grid; grid-template-columns: 2fr 1fr 2fr 1fr 1.5fr; gap: 8px; padding: 8px 4px; border-bottom: 1px solid #f0f0f0; font-size: 0.85rem; }
.header-row { font-weight: bold; color: #555; background: #f8f9fa; border-radius: 6px; }

.estado-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.estado-item { background: #f8f9fa; border-radius: 8px; padding: 12px; }
.estado-item .label { display: block; font-size: 0.8rem; color: #7f8c8d; margin-bottom: 4px; }
.estado-item .value { font-weight: bold; color: #2c3e50; }
.badge-estado { padding: 3px 10px; border-radius: 12px; font-size: 0.85rem; }
.badge-estado.activa { background: #d4edda; color: #155724; }
.badge-estado.suspendida { background: #fff3cd; color: #856404; }
.badge-estado.egresado { background: #cce5ff; color: #004085; }
</style>