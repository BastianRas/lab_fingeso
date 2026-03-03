<script setup>
import { ref, onMounted, computed } from 'vue';
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
const vistaActiva = ref(null); // 'malla' | 'estado' | 'notas' | 'certificados' | null

// --- DATOS SIMULADOS PARA NOTAS ---
const asignaturasNotas = ref([
  {
    nombre: "Fundamentos de Ingeniería de Software",
    evaluaciones: [
      { nombre: "PEP 1", nota: 6.5, ponderacion: 30 },
      { nombre: "PEP 2", nota: 5.2, ponderacion: 30 },
      { nombre: "Laboratorio", nota: 6.8, ponderacion: 40 }
    ]
  },
  {
    nombre: "Sistemas Operativos",
    evaluaciones: [
      { nombre: "PEP 1", nota: 4.8, ponderacion: 40 },
      { nombre: "Proyecto Final", nota: 6.0, ponderacion: 60 }
    ]
  }
]);

// --- DATOS SIMULADOS PARA CERTIFICADOS ---
const tiposCertificados = ref([
  { id: 'regular', nombre: 'Certificado de Alumno Regular', icono: '🎓', descripcion: 'Válido para AFP, Asignación Familiar, Pase Escolar, Caja de Compensación, etc.' },
  { id: 'notas', nombre: 'Certificado de Concentración de Notas', icono: '📊', descripcion: 'Historial académico y calificaciones históricas obtenidas.' },
  { id: 'seguro', nombre: 'Certificado de Seguro Escolar', icono: '🏥', descripcion: 'Acredita cobertura médica para accidentes estudiantiles.' },
  { id: 'docencia', nombre: 'Solicitud de Docencia', icono: '👨‍🏫', descripcion: 'Documento oficial para postulación a ayudantías o docencia.' }
]);

const irAlMapa = () => router.push('/mapa');

const mostrar = (vista) => {
  vistaActiva.value = vistaActiva.value === vista ? null : vista;
};

const descargarCertificado = (nombre) => {
  alert(`Generando y descargando: ${nombre}...\n(Esta es una simulación visual)`);
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
        <button class="action-btn" :class="{ active: vistaActiva === 'notas' }" @click="mostrar('notas')">
          <span class="icon">📊</span> <span class="text">Mis Notas</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'certificados' }" @click="mostrar('certificados')">
          <span class="icon">📄</span> <span class="text">Certificados</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'malla' }" @click="mostrar('malla')">
          <span class="icon">📚</span> <span class="text">Malla Curricular</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'estado' }" @click="mostrar('estado')">
          <span class="icon">🎓</span> <span class="text">Estado Matrícula</span>
        </button>
      </div>

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

      <div v-if="vistaActiva === 'notas'" class="info-panel panel-animado">
        <h4>📝 Mis Calificaciones</h4>
        <p class="facultad">Semestre Actual: 2026-1</p>
        
        <div class="notas-container">
          <div v-for="(asignatura, index) in asignaturasNotas" :key="index" class="asignatura-card">
            <h5 class="asignatura-titulo">{{ asignatura.nombre }}</h5>
            
            <div class="tabla-responsive">
                <table class="notas-tabla">
                <thead>
                    <tr>
                    <th>Evaluación</th>
                    <th>Ponderación</th>
                    <th>Nota Obtenida</th>
                    <th>Puntaje Ponderado</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(evaluacion, i) in asignatura.evaluaciones" :key="i">
                    <td>{{ evaluacion.nombre }}</td>
                    <td>{{ evaluacion.ponderacion }}%</td>
                    <td class="nota-destacada">{{ evaluacion.nota.toFixed(1) }}</td>
                    <td>{{ (evaluacion.nota * (evaluacion.ponderacion / 100)).toFixed(2) }}</td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                    <td colspan="3" class="text-right"><strong>Promedio Parcial:</strong></td>
                    <td class="promedio-final">
                        {{ asignatura.evaluaciones.reduce((acc, ev) => acc + (ev.nota * (ev.ponderacion / 100)), 0).toFixed(1) }}
                    </td>
                    </tr>
                </tfoot>
                </table>
            </div>
          </div>
        </div>
      </div>

      <div v-if="vistaActiva === 'certificados'" class="info-panel panel-animado">
        <h4>📄 Emisión de Certificados</h4>
        <p class="facultad">Selecciona el documento oficial que necesitas descargar (Formato PDF).</p>
        
        <div class="certificados-grid">
          <div v-for="cert in tiposCertificados" :key="cert.id" class="certificado-card">
            <div class="cert-icon">{{ cert.icono }}</div>
            <div class="cert-info">
              <h5>{{ cert.nombre }}</h5>
              <p>{{ cert.descripcion }}</p>
            </div>
            <button class="btn-descargar" @click="descargarCertificado(cert.nombre)">
              📥 Descargar
            </button>
          </div>
        </div>
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
.welcome-text h2 { margin: 0; color: #2c3e50; }
.welcome-text p { margin: 0; color: #7f8c8d; }
.avatar { color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

/* Tarjeta Azul para Estudiante */
.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #3498db; }
.card-header { display: flex; flex-direction: column; align-items: flex-start; gap: 8px; margin-bottom: 15px; }
.card-header h3 { margin: 0; color: #2c3e50; }
.badge-blue { background: #e3f2fd; color: #1565c0; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.info-row { margin-bottom: 8px; color: #555; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.primary-btn { color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { opacity: 0.9; }

/* Botones de Acceso */
.quick-actions h3 { color: #2c3e50; margin-bottom: 15px; }
.grid-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(100px, 1fr)); gap: 10px; margin-bottom: 20px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #e3f2fd; border-color: #3498db; }
.action-btn.active { background-color: #e3f2fd; border-color: #3498db; font-weight: bold; transform: translateY(-2px); box-shadow: 0 4px 8px rgba(52,152,219,0.2); }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; text-align: center; }

/* Paneles de Información */
.info-panel { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-left: 5px solid #3498db; }
.panel-animado { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
.info-panel h4 { margin: 0 0 5px; color: #2c3e50; font-size: 1.3rem; }
.facultad { color: #7f8c8d; font-size: 0.9rem; margin-bottom: 15px; }
.sin-datos { color: #999; font-style: italic; }

/* Tabla Malla */
.tabla-clases { overflow-x: auto; margin-top: 10px; }
.clase-row { display: grid; grid-template-columns: 2fr 1fr 2fr 1fr 1.5fr; gap: 8px; padding: 10px 8px; border-bottom: 1px solid #eee; font-size: 0.85rem; align-items: center; }
.header-row { font-weight: bold; color: #555; background: #f8f9fa; border-radius: 6px; border-bottom: none; }

/* Grid Estado Matrícula */
.estado-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 12px; }
.estado-item { background: #f8f9fa; border-radius: 8px; padding: 12px; border: 1px solid #eee; }
.estado-item .label { display: block; font-size: 0.8rem; color: #7f8c8d; margin-bottom: 6px; }
.estado-item .value { font-weight: bold; color: #2c3e50; font-size: 1rem; }
.badge-estado { padding: 4px 10px; border-radius: 12px; font-size: 0.85rem; display: inline-block; }
.badge-estado.activa { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
.badge-estado.suspendida { background: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
.badge-estado.egresado { background: #cce5ff; color: #004085; border: 1px solid #b8daff; }

/* Estilos de Notas */
.notas-container { display: flex; flex-direction: column; gap: 20px; }
.asignatura-card { border: 1px solid #eee; border-radius: 8px; padding: 15px; background: #fdfdfd; }
.asignatura-titulo { margin: 0 0 15px 0; color: #3498db; font-size: 1.1rem; border-bottom: 2px solid #3498db; padding-bottom: 8px; display: inline-block; }
.tabla-responsive { overflow-x: auto; }
.notas-tabla { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); font-size: 0.9rem; min-width: 400px; }
.notas-tabla th, .notas-tabla td { padding: 10px 15px; text-align: left; border-bottom: 1px solid #eee; }
.notas-tabla th { background-color: #f8f9fa; color: #2c3e50; font-weight: bold; }
.nota-destacada { font-weight: bold; color: #2980b9; }
.text-right { text-align: right; }
.promedio-final { font-weight: bold; color: #27ae60; font-size: 1.05rem; }

/* Estilos de Certificados */
.certificados-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 15px; }
.certificado-card { display: flex; flex-direction: column; gap: 12px; padding: 15px; border: 1px solid #e0e6ed; border-radius: 10px; background: white; transition: all 0.2s ease; }
.certificado-card:hover { border-color: #3498db; box-shadow: 0 4px 12px rgba(52,152,219,0.1); transform: translateY(-2px); }
.cert-header { display: flex; align-items: center; gap: 10px; }
.cert-icon { font-size: 2rem; background: #f0f4f8; padding: 8px; border-radius: 10px; width: fit-content; }
.cert-info h5 { margin: 0; color: #2c3e50; font-size: 1rem; }
.cert-info p { margin: 5px 0 0; color: #666; font-size: 0.8rem; line-height: 1.4; flex-grow: 1; }
.btn-descargar { background: #ecf0f1; color: #2c3e50; border: 1px solid #bdc3c7; padding: 8px 15px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; align-self: flex-start; margin-top: auto; }
.btn-descargar:hover { background: #3498db; color: white; border-color: #3498db; }

/* Cartelera */
.news-section { margin-top: 30px; }
.news-section h3 { color: #2c3e50; margin-bottom: 15px; }
</style>