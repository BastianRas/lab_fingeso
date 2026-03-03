<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';
import matriculaService from '../services/matriculaService';
import notaService from '../services/notaService'; // <-- IMPORTAMOS EL NUEVO SERVICIO

const router = useRouter();
const alumnoNombre = ref('');
const matricula = ref(null);
const vistaActiva = ref(null);

// --- VARIABLE VACÍA QUE SE LLENARÁ DESDE LA BASE DE DATOS ---
const asignaturasNotas = ref([]);

// --- CERTIFICADOS Y MALLA (Estos se mantienen estáticos) ---
const tiposCertificados = ref([
  { id: 'regular', nombre: 'Certificado de Alumno Regular', icono: '🎓', descripcion: 'Válido para AFP, Asignación Familiar, Pase Escolar, etc.' },
  { id: 'notas', nombre: 'Certificado de Concentración de Notas', icono: '📊', descripcion: 'Historial académico.' },
  { id: 'seguro', nombre: 'Certificado de Seguro Escolar', icono: '🏥', descripcion: 'Acredita cobertura médica para accidentes.' },
  { id: 'docencia', nombre: 'Solicitud de Docencia', icono: '👨‍🏫', descripcion: 'Postulación a ayudantías.' }
]);

const mallaInformatica = ref([
  { semestre: 1, ramos: [{ codigo: "CBM-1000", nombre: "Álgebra I" }, { codigo: "CBM-1001", nombre: "Cálculo I" }, { codigo: "INF-1000", nombre: "Intr. a la Ingeniería" }] },
  { semestre: 2, ramos: [{ codigo: "CBM-1002", nombre: "Álgebra II" }, { codigo: "CBM-1003", nombre: "Cálculo II" }, { codigo: "INF-1100", nombre: "Programación Básica" }] },
  { semestre: 3, ramos: [{ codigo: "FIS-1000", nombre: "Física I" }, { codigo: "INF-2200", nombre: "Estructuras de Datos" }, { codigo: "INF-2201", nombre: "Arquitectura de Computadores" }] },
  { semestre: 4, ramos: [{ codigo: "INF-3321", nombre: "Sistemas Operativos" }, { codigo: "INF-3410", nombre: "Redes de Computadores" }, { codigo: "INF-3454", nombre: "Fundamentos de Ing. de Software" }] }
]);

// --- HORARIOS ---
const diasSemana = [ { id: 'L', nombre: 'Lunes' }, { id: 'M', nombre: 'Martes' }, { id: 'W', nombre: 'Miércoles' }, { id: 'J', nombre: 'Jueves' }, { id: 'V', nombre: 'Viernes' } ];
const bloquesHorarios = [ { id: 1, hora: "08:15 - 09:35" }, { id: 2, hora: "09:50 - 11:10" }, { id: 3, hora: "11:25 - 12:45" }, { id: 4, hora: "13:45 - 15:05" }, { id: 5, hora: "15:20 - 16:40" }, { id: 6, hora: "16:55 - 18:15" }, { id: 7, hora: "18:45 - 20:05" }, { id: 8, hora: "20:05 - 21:25" }, { id: 9, hora: "21:25 - 22:45" } ];

const horarioGenerado = computed(() => {
  const grid = {};
  bloquesHorarios.forEach(b => {
    grid[b.id] = {};
    diasSemana.forEach(d => grid[b.id][d.id] = null);
  });
  if (matricula.value && matricula.value.clases) {
    matricula.value.clases.forEach(clase => {
      if(clase.horario) {
        clase.horario.trim().split(/\s+/).forEach(mod => {
          const dia = mod.charAt(0).toUpperCase(); 
          const bloque = parseInt(mod.substring(1));
          if (!isNaN(bloque) && grid[bloque] && grid[bloque][dia] !== undefined) grid[bloque][dia] = clase;
        });
      }
    });
  }
  return grid;
});

const irAlMapa = () => router.push('/mapa');
const mostrar = (vista) => vistaActiva.value = vistaActiva.value === vista ? null : vista;
const descargarCertificado = (nombre) => alert(`Generando y descargando: ${nombre}...`);

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    alumnoNombre.value = user.nombre;
    try {
      // 1. Cargar Matrícula
      matricula.value = await matriculaService.obtenerMatricula(user.usuarioId);
      
      // 2. Cargar Notas Reales desde Base de Datos
      const resNotas = await notaService.obtenerPorAlumno(user.usuarioId);
      const notasPlanas = resNotas.data;

      // 3. Agrupar las notas por nombre de la asignatura
      const agrupadas = {};
      notasPlanas.forEach(nota => {
        const claseNombre = nota.clase.nombre;
        if (!agrupadas[claseNombre]) {
          agrupadas[claseNombre] = { nombre: claseNombre, evaluaciones: [] };
        }
        agrupadas[claseNombre].evaluaciones.push({
          nombre: nota.nombreEvaluacion,
          nota: nota.valor,
          ponderacion: nota.ponderacion
        });
      });
      // Convertimos el objeto en un Array para que Vue lo dibuje
      asignaturasNotas.value = Object.values(agrupadas);

    } catch (error) {
      console.error("Error al cargar datos:", error);
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

    <section class="quick-actions">
      <h3>Mis Accesos</h3>
      <div class="grid-buttons">
        <button class="action-btn" @click="irAlMapa">
          <span class="icon">🗺️</span> <span class="text">Ver Campus</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'horario' }" @click="mostrar('horario')">
          <span class="icon">📅</span> <span class="text">Mi Horario</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'malla' }" @click="mostrar('malla')">
          <span class="icon">📚</span> <span class="text">Malla Curricular</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'notas' }" @click="mostrar('notas')">
          <span class="icon">📊</span> <span class="text">Mis Notas</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'certificados' }" @click="mostrar('certificados')">
          <span class="icon">📄</span> <span class="text">Certificados</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'estado' }" @click="mostrar('estado')">
          <span class="icon">🎓</span> <span class="text">Estado Matrícula</span>
        </button>
      </div>

      <div v-if="vistaActiva === 'horario'" class="info-panel panel-animado">
        <h4>📅 Mi Horario</h4>
        <p class="facultad">Semestre Actual — {{ matricula?.carrera?.nombre || 'Carrera' }}</p>
        <div class="tabla-horario-wrapper">
          <table class="tabla-horario">
            <thead>
              <tr><th class="col-hora">Módulo</th><th v-for="dia in diasSemana" :key="dia.id">{{ dia.nombre }}</th></tr>
            </thead>
            <tbody>
              <template v-for="bloque in bloquesHorarios" :key="bloque.id">
                <tr>
                  <td class="col-hora"><strong>{{ bloque.id }}</strong><div class="hora-detalle">{{ bloque.hora }}</div></td>
                  <td v-for="dia in diasSemana" :key="dia.id" class="celda-clase">
                    <div v-if="horarioGenerado[bloque.id][dia.id]" class="clase-bloque">
                      <div class="clase-nombre">{{ horarioGenerado[bloque.id][dia.id].nombre }}</div>
                      <div class="clase-sala">{{ horarioGenerado[bloque.id][dia.id].sala }}</div>
                    </div>
                  </td>
                </tr>
                <tr v-if="bloque.id === 3" class="fila-descanso"><td colspan="6">🍽️ Horario de Almuerzo (12:45 - 13:45)</td></tr>
                <tr v-if="bloque.id === 6" class="fila-descanso"><td colspan="6">☕ Descanso / Break (18:15 - 18:45)</td></tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="vistaActiva === 'malla'" class="info-panel panel-animado">
        <div v-if="matricula?.carrera?.nombre === 'Ingeniería en Informática'">
          <h4>📚 Malla Curricular</h4>
          <p class="facultad">{{ matricula?.carrera?.nombre }} — Plan Vigente</p>
          <div class="malla-grid">
            <div v-for="semestre in mallaInformatica" :key="semestre.semestre" class="semestre-columna">
              <div class="semestre-header">Semestre {{ semestre.semestre }}</div>
              <div class="ramos-lista">
                <div v-for="(ramo, idx) in semestre.ramos" :key="idx" class="ramo-card">
                  <span class="ramo-codigo">{{ ramo.codigo }}</span>
                  <span class="ramo-nombre">{{ ramo.nombre }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
           <p class="sin-datos">Malla curricular no disponible para tu carrera actual.</p>
        </div>
      </div>

      <div v-if="vistaActiva === 'estado'" class="info-panel panel-animado">
        <h4>🎓 Estado de Matrícula</h4>
        <div v-if="matricula" class="estado-grid">
          <div class="estado-item"><span class="label">Carrera</span><span class="value">{{ matricula.carrera?.nombre }}</span></div>
          <div class="estado-item"><span class="label">Facultad</span><span class="value">{{ matricula.carrera?.facultad }}</span></div>
          <div class="estado-item">
            <span class="label">Estado</span>
            <span class="value badge-estado" :class="matricula.estadoMatricula?.toLowerCase()">{{ matricula.estadoMatricula }}</span>
          </div>
          <div class="estado-item"><span class="label">Ramos inscritos</span><span class="value">{{ matricula.clases?.length ?? 0 }}</span></div>
        </div>
        <p v-else class="sin-datos">No se encontró información de matrícula.</p>
      </div>

      <div v-if="vistaActiva === 'notas'" class="info-panel panel-animado">
        <h4>📝 Mis Calificaciones</h4>
        <p class="facultad">Semestre Actual</p>
        
        <div v-if="asignaturasNotas.length > 0" class="notas-container">
          <div v-for="(asignatura, index) in asignaturasNotas" :key="index" class="asignatura-card">
            <h5 class="asignatura-titulo">{{ asignatura.nombre }}</h5>
            <div class="tabla-responsive">
                <table class="notas-tabla">
                <thead>
                    <tr><th>Evaluación</th><th>Ponderación</th><th>Nota Obtenida</th><th>Puntaje Ponderado</th></tr>
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
        <p v-else class="sin-datos">Aún no tienes notas registradas en el sistema.</p>
      </div>

      <div v-if="vistaActiva === 'certificados'" class="info-panel panel-animado">
        <h4>📄 Emisión de Certificados</h4>
        <p class="facultad">Selecciona el documento oficial que necesitas descargar.</p>
        <div class="certificados-grid">
          <div v-for="cert in tiposCertificados" :key="cert.id" class="certificado-card">
            <div class="cert-icon">{{ cert.icono }}</div>
            <div class="cert-info"><h5>{{ cert.nombre }}</h5><p>{{ cert.descripcion }}</p></div>
            <button class="btn-descargar" @click="descargarCertificado(cert.nombre)">📥 Descargar</button>
          </div>
        </div>
      </div>

    </section>
    <section class="news-section"><h3>📢 Cartelera USACH</h3><CarteleraList /></section>
  </div>
</template>

<style scoped>
/* Estilos Base y Animaciones */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.welcome-text h2 { margin: 0; color: #2c3e50; }
.welcome-text p { margin: 0; color: #7f8c8d; }
.avatar { color: white; width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

/* Botones Accesos Rápidos */
.quick-actions h3 { color: #2c3e50; margin-bottom: 15px; }
.grid-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(110px, 1fr)); gap: 10px; margin-bottom: 20px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #e3f2fd; border-color: #3498db; }
.action-btn.active { background-color: #e3f2fd; border-color: #3498db; font-weight: bold; transform: translateY(-2px); box-shadow: 0 4px 8px rgba(52,152,219,0.2); }
.icon { font-size: 1.8rem; }
.text { font-size: 0.85rem; font-weight: 500; text-align: center; color: #333; }

/* Panel de Información General */
.info-panel { background: white; border-radius: 12px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-top: 5px solid #3498db; }
.panel-animado { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
.info-panel h4 { margin: 0 0 5px; color: #2c3e50; font-size: 1.4rem; }
.facultad { color: #7f8c8d; font-size: 0.95rem; margin-bottom: 20px; }
.sin-datos { color: #999; font-style: italic; }

/* Horario Grid */
.tabla-horario-wrapper { overflow-x: auto; border: 1px solid #e0e6ed; border-radius: 8px; }
.tabla-horario { width: 100%; border-collapse: collapse; background: white; min-width: 600px; }
.tabla-horario th, .tabla-horario td { border: 1px solid #e0e6ed; padding: 10px; text-align: center; }
.tabla-horario th { background-color: #f1f3f5; color: #2c3e50; font-size: 0.9rem; padding: 12px; }
.col-hora { background-color: #f8f9fa; width: 100px; color: #2c3e50; }
.col-hora strong { font-size: 1.1rem; }
.hora-detalle { font-size: 0.75rem; color: #7f8c8d; margin-top: 4px; }
.celda-clase { vertical-align: middle; height: 60px; padding: 5px; width: 16%; }
.clase-bloque { background-color: #e3f2fd; border-left: 4px solid #3498db; padding: 8px; border-radius: 4px; display: flex; flex-direction: column; justify-content: center; height: 100%; }
.clase-nombre { font-size: 0.8rem; font-weight: bold; color: #1565c0; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.clase-sala { font-size: 0.75rem; color: #555; margin-top: 4px; }
.fila-descanso td { background-color: #fcfcfc; color: #7f8c8d; font-style: italic; font-size: 0.85rem; padding: 8px; letter-spacing: 1px; }

/* Malla Curricular Grid */
.malla-grid { display: flex; gap: 15px; overflow-x: auto; padding-bottom: 15px; }
.semestre-columna { min-width: 250px; background: #f8f9fa; border-radius: 8px; border: 1px solid #e0e6ed; display: flex; flex-direction: column; }
.semestre-header { background: #2c3e50; color: white; padding: 10px; text-align: center; font-weight: bold; border-radius: 8px 8px 0 0; }
.ramos-lista { padding: 10px; display: flex; flex-direction: column; gap: 10px; }
.ramo-card { background: white; border: 1px solid #dcdde1; border-radius: 6px; padding: 12px; box-shadow: 0 2px 4px rgba(0,0,0,0.02); display: flex; flex-direction: column; gap: 5px; transition: transform 0.2s; border-left: 4px solid #ea7600; }
.ramo-card:hover { transform: scale(1.02); border-left-color: #3498db; }
.ramo-codigo { font-size: 0.75rem; font-family: monospace; color: #7f8c8d; }
.ramo-nombre { font-size: 0.95rem; font-weight: bold; color: #2c3e50; line-height: 1.2; }

/* Estado Matrícula */
.estado-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 15px; }
.estado-item { background: #f8f9fa; border-radius: 8px; padding: 15px; border: 1px solid #eee; }
.estado-item .label { display: block; font-size: 0.85rem; color: #7f8c8d; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
.estado-item .value { font-weight: bold; color: #2c3e50; font-size: 1.1rem; }
.badge-estado { padding: 4px 12px; border-radius: 15px; font-size: 0.85rem; display: inline-block; }
.badge-estado.activa { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }

/* Notas y Evaluaciones */
.notas-container { display: flex; flex-direction: column; gap: 20px; }
.asignatura-card { border: 1px solid #e0e6ed; border-radius: 8px; padding: 20px; background: #fafbfc; }
.asignatura-titulo { margin: 0 0 15px 0; color: #3498db; font-size: 1.15rem; border-bottom: 2px solid #3498db; padding-bottom: 8px; display: inline-block; }
.tabla-responsive { overflow-x: auto; }
.notas-tabla { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); font-size: 0.95rem; min-width: 450px; }
.notas-tabla th, .notas-tabla td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
.notas-tabla th { background-color: #f1f3f5; color: #2c3e50; font-weight: bold; border-bottom: 2px solid #dee2e6; }
.nota-destacada { font-weight: bold; color: #ea7600; font-size: 1.05rem; }
.text-right { text-align: right; }
.promedio-final { font-weight: bold; color: #27ae60; font-size: 1.1rem; }

/* Certificados */
.certificados-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 15px; }
.certificado-card { display: flex; flex-direction: column; gap: 12px; padding: 20px; border: 1px solid #e0e6ed; border-radius: 10px; background: white; transition: all 0.2s ease; }
.certificado-card:hover { border-color: #3498db; box-shadow: 0 6px 15px rgba(52,152,219,0.1); transform: translateY(-3px); }
.cert-icon { font-size: 2.2rem; background: #f4f6f8; padding: 10px; border-radius: 12px; width: fit-content; }
.cert-info h5 { margin: 0; color: #2c3e50; font-size: 1.1rem; }
.cert-info p { margin: 8px 0 0; color: #666; font-size: 0.85rem; line-height: 1.5; flex-grow: 1; }
.btn-descargar { background: #ecf0f1; color: #2c3e50; border: 1px solid #bdc3c7; padding: 10px 15px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; align-self: flex-start; margin-top: auto; }
.btn-descargar:hover { background: #ea7600; color: white; border-color: #ea7600; }

.news-section { margin-top: 35px; }
.news-section h3 { color: #2c3e50; margin-bottom: 15px; padding-left: 5px; border-left: 4px solid #ea7600; }
</style>