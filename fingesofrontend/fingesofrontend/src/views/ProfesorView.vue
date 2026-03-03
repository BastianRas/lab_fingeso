<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';
import claseService from '../services/claseService';
import usuarioService from '../services/usuarioService';
import notaService from '../services/notaService';

const router = useRouter();
const profesorNombre = ref('');
const clases = ref([]);
const alumnosInscritos = ref([]);
const vistaActiva = ref('horario'); // 'horario' | 'cursos' | 'asistencia' | 'notas'

// --- VARIABLES DE HORARIO (Grid Visual) ---
const diasSemana = [ { id: 'L', nombre: 'Lunes' }, { id: 'M', nombre: 'Martes' }, { id: 'W', nombre: 'Miércoles' }, { id: 'J', nombre: 'Jueves' }, { id: 'V', nombre: 'Viernes' } ];
const bloquesHorarios = [ { id: 1, hora: "08:15 - 09:35" }, { id: 2, hora: "09:50 - 11:10" }, { id: 3, hora: "11:25 - 12:45" }, { id: 4, hora: "13:45 - 15:05" }, { id: 5, hora: "15:20 - 16:40" }, { id: 6, hora: "16:55 - 18:15" }, { id: 7, hora: "18:45 - 20:05" }, { id: 8, hora: "20:05 - 21:25" }, { id: 9, hora: "21:25 - 22:45" } ];

const horarioGenerado = computed(() => {
  const grid = {};
  bloquesHorarios.forEach(b => {
    grid[b.id] = {};
    diasSemana.forEach(d => grid[b.id][d.id] = null);
  });
  if (clases.value.length > 0) {
    clases.value.forEach(clase => {
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

// --- VARIABLES DE ASISTENCIA ---
const cursoAsistenciaId = ref('');
const listaAsistencia = ref([]);

const cargarListaAsistencia = () => {
  if (cursoAsistenciaId.value) {
    // Simulamos la lista de curso asignándoles un estado "presente" por defecto
    listaAsistencia.value = alumnosInscritos.value.map(a => ({
      ...a,
      presente: true
    }));
  }
};

const guardarAsistencia = () => {
  alert("✅ Registro de asistencia guardado exitosamente en el sistema académico.");
  cursoAsistenciaId.value = '';
  listaAsistencia.value = [];
};

// --- VARIABLES DE NOTAS (Mantenidas) ---
const formNota = ref({ claseId: '', alumnoId: '', nombreEvaluacion: '', valor: '', ponderacion: '' });
const enviando = ref(false);
const mensaje = ref({ texto: '', tipo: '' });
const idEditando = ref(null);
const notasRegistradas = ref([]);

const mostrar = (vista) => vistaActiva.value = vistaActiva.value === vista ? null : vista;

const irAlMapaParaLugar = (ubicacion) => {
  alert(`📍 Calculando ruta en el campus hacia la sala: ${ubicacion}`);
  router.push('/mapa');
};

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user && user.rol === 'PROFESOR') {
    profesorNombre.value = user.nombre;
    try {
      // ✨ LEER CURSOS ASIGNADOS POR EL ADMINISTRADOR
      const profesAdmin = JSON.parse(localStorage.getItem('profesor_admin') || '{}');
      const misCursosAsignados = profesAdmin[user.numeroCredencial];

      if (misCursosAsignados && misCursosAsignados.length > 0) {
        clases.value = misCursosAsignados.map(c => ({
          ...c, semestre: "1-2026"
        }));
      } else {
        // Fallback al backend antiguo si no tiene asignación manual
        clases.value = await claseService.getClasesByProfesor(user.usuarioId);
      }

      const resUsuarios = await usuarioService.obtenerTodos();
      alumnosInscritos.value = resUsuarios.data.filter(u => u.rol === 'ALUMNO' || u.rol === 'VISITA');
    } catch (error) {
      console.error(error);
    }
  } else {
    router.push('/');
  }
});

const primerCurso = () => clases.value.length > 0 ? clases.value[0] : null;

// --- FUNCIONES DE NOTAS ---
const cargarNotasDeClase = async () => {
  if (formNota.value.claseId) {
    try {
      const res = await notaService.obtenerPorClase(formNota.value.claseId);
      notasRegistradas.value = res.data;
    } catch (e) { console.error("Error al cargar registro", e); }
  }
};

const enviarNota = async () => {
  mensaje.value = { texto: '', tipo: '' };
  if (!formNota.value.claseId || !formNota.value.alumnoId || !formNota.value.nombreEvaluacion || !formNota.value.valor || !formNota.value.ponderacion) {
    mensaje.value = { texto: '⚠️ Completa todos los campos.', tipo: 'error' };
    return;
  }
  enviando.value = true;
  const notaBD = {
    nombreEvaluacion: formNota.value.nombreEvaluacion,
    valor: parseFloat(formNota.value.valor),
    ponderacion: parseInt(formNota.value.ponderacion),
    alumno: { usuarioId: formNota.value.alumnoId },
    clase: { id: formNota.value.claseId }
  };
  try {
    if (idEditando.value) {
      await notaService.actualizarNota(idEditando.value, notaBD);
      mensaje.value = { texto: '✅ ¡Nota actualizada exitosamente!', tipo: 'exito' };
    } else {
      await notaService.crearNota(notaBD);
      mensaje.value = { texto: '✅ ¡Nota subida exitosamente!', tipo: 'exito' };
    }
    cancelarEdicion();
    await cargarNotasDeClase();
  } catch (error) {
    mensaje.value = { texto: '❌ Error de conexión al guardar.', tipo: 'error' };
  } finally {
    enviando.value = false;
  }
};

const editarNota = (nota) => {
  idEditando.value = nota.id;
  formNota.value.alumnoId = nota.alumno.usuarioId;
  formNota.value.nombreEvaluacion = nota.nombreEvaluacion;
  formNota.value.valor = nota.valor;
  formNota.value.ponderacion = nota.ponderacion;
  mensaje.value = { texto: '✏️ Modo edición activado. Modifica los datos arriba.', tipo: 'exito' };
};

const cancelarEdicion = () => {
  idEditando.value = null;
  formNota.value.nombreEvaluacion = '';
  formNota.value.valor = '';
  formNota.value.ponderacion = '';
  mensaje.value = { texto: '', tipo: '' };
};

const eliminarNota = async (id) => {
  if(confirm("¿Estás seguro de eliminar esta calificación del sistema?")) {
    try {
      await notaService.eliminarNota(id);
      await cargarNotasDeClase();
      mensaje.value = { texto: '🗑️ Nota eliminada exitosamente.', tipo: 'exito' };
    } catch (e) {
      alert("Error al eliminar la nota.");
    }
  }
};
</script>

<template>
  <div class="dashboard-container">

    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, Profesor {{ profesorNombre }} 👋</h2>
        <p>Panel Docente y Gestión de Cursos</p>
      </div>
      <div class="header-actions">
        <button class="btn-logout" @click="router.push('/')">Cerrar Sesión</button>
        <div class="avatar">👨‍🏫</div>
      </div>
    </header>

    <section v-if="primerCurso()" class="urgent-card">
      <div class="card-header">
        <span class="badge">Semestre {{ primerCurso().semestre }}</span>
        <h3>Próxima Clase: {{ primerCurso().nombre }}</h3>
      </div>
      <div class="card-body">
        <div class="info-row">
          <i class="fas fa-map-marker-alt"></i>
          <span class="highlight-location">Sala: {{ primerCurso().sala }}</span>
        </div>
        <div class="info-row">
          <i class="far fa-clock"></i>
          <span>Horario: {{ primerCurso().horario }}</span>
        </div>
      </div>
      <div class="btn-group-urgent">
        <button class="primary-btn map-btn" @click="irAlMapaParaLugar(primerCurso().sala)">
          🗺️ Trazar ruta a la sala
        </button>
        <button class="primary-btn" @click="mostrar('asistencia')">
          📋 Pasar Asistencia
        </button>
      </div>
    </section>

    <section class="quick-actions">
      <h3>Gestión Académica</h3>
      <div class="grid-buttons">
        <button class="action-btn" :class="{ active: vistaActiva === 'horario' }" @click="mostrar('horario')">
          <span class="icon">📅</span> <span class="text">Mi Horario</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'cursos' }" @click="mostrar('cursos')">
          <span class="icon">📚</span> <span class="text">Mis Cursos</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'asistencia' }" @click="mostrar('asistencia')">
          <span class="icon">✅</span> <span class="text">Asistencia</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'notas' }" @click="mostrar('notas')">
          <span class="icon">✍️</span> <span class="text">Subir Notas</span>
        </button>
        <button class="action-btn" @click="router.push('/mapa')">
          <span class="icon">📍</span> <span class="text">Mapa Campus</span>
        </button>
      </div>

      <div v-if="vistaActiva === 'horario'" class="info-panel animacion-panel">
        <h4>📅 Horario Docente</h4>
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

      <div v-if="vistaActiva === 'cursos'" class="info-panel animacion-panel">
        <h4>📚 Cursos Asignados</h4>
        <div class="cursos-grid">
          <div v-for="clase in clases" :key="clase.id" class="curso-card">
            <div class="curso-header">
              <span class="codigo-badge">{{ clase.codigo }}</span>
              <span class="semestre-txt">{{ clase.semestre }}</span>
            </div>
            <h5>{{ clase.nombre }}</h5>
            <p><strong>Horario:</strong> {{ clase.horario }}</p>
            <div class="curso-footer">
              <span class="sala-txt">📍 {{ clase.sala }}</span>
              <button class="btn-ruta-sm" @click="irAlMapaParaLugar(clase.sala)">Ruta</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="vistaActiva === 'asistencia'" class="info-panel animacion-panel">
        <h4>✅ Registro de Asistencia</h4>
        <p class="instruccion">Selecciona un curso para pasar la lista del día de hoy.</p>
        
        <div class="form-group mb-4">
          <label>Curso a pasar asistencia:</label>
          <select v-model="cursoAsistenciaId" @change="cargarListaAsistencia">
            <option value="" disabled>Seleccione un ramo...</option>
            <option v-for="clase in clases" :key="clase.id" :value="clase.id">{{ clase.codigo }} - {{ clase.nombre }}</option>
          </select>
        </div>

        <div v-if="listaAsistencia.length > 0" class="tabla-responsive asistencia-table-box">
          <table class="data-table">
            <thead>
              <tr>
                <th>RUT / Credencial</th>
                <th>Nombre del Alumno</th>
                <th class="text-center">Estado (Presente)</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="alumno in listaAsistencia" :key="alumno.usuarioId" :class="{'ausente': !alumno.presente}">
                <td>{{ alumno.numeroCredencial }}</td>
                <td>{{ alumno.nombre }} {{ alumno.apellido }}</td>
                <td class="text-center toggle-cell">
                  <label class="switch">
                    <input type="checkbox" v-model="alumno.presente">
                    <span class="slider round"></span>
                  </label>
                  <span class="estado-lbl">{{ alumno.presente ? 'Presente' : 'Ausente' }}</span>
                </td>
              </tr>
            </tbody>
          </table>
          <button class="btn-guardar-asistencia" @click="guardarAsistencia">💾 Confirmar y Guardar Asistencia</button>
        </div>
      </div>

      <div v-if="vistaActiva === 'notas'" class="info-panel animacion-panel">
        <h4>{{ idEditando ? '✏️ Editando Calificación' : '✍️ Ingresar Calificación' }}</h4>
        <p class="instruccion">Selecciona el curso, el alumno y asigna la calificación correspondiente.</p>
        
        <form @submit.prevent="enviarNota" class="form-notas">
          <div class="form-row">
            <div class="form-group">
              <label>Asignatura</label>
              <select v-model="formNota.claseId" @change="cargarNotasDeClase" required>
                <option value="" disabled>Seleccione un ramo...</option>
                <option v-for="clase in clases" :key="clase.id" :value="clase.id">{{ clase.codigo }} - {{ clase.nombre }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Estudiante</label>
              <select v-model="formNota.alumnoId" required>
                <option value="" disabled>Seleccione un estudiante...</option>
                <option v-for="alumno in alumnosInscritos" :key="alumno.usuarioId" :value="alumno.usuarioId">
                  {{ alumno.numeroCredencial }} | {{ alumno.nombre }} {{ alumno.apellido }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label>Nombre de la Evaluación (Ej: PEP 1)</label>
            <input type="text" v-model="formNota.nombreEvaluacion" required>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Nota Obtenida (1.0 a 7.0)</label>
              <input type="number" step="0.1" min="1.0" max="7.0" v-model="formNota.valor" required>
            </div>
            <div class="form-group">
              <label>Ponderación (%)</label>
              <input type="number" min="1" max="100" v-model="formNota.ponderacion" required>
            </div>
          </div>
          <div v-if="mensaje.texto" :class="['alerta', mensaje.tipo]">{{ mensaje.texto }}</div>
          <div class="botones-form">
            <button type="submit" class="btn-subir" :disabled="enviando">
              {{ enviando ? 'Procesando...' : (idEditando ? '💾 Guardar Cambios' : '📤 Subir Calificación') }}
            </button>
            <button v-if="idEditando" type="button" class="btn-cancelar" @click="cancelarEdicion">Cancelar Edición</button>
          </div>
        </form>

        <div v-if="notasRegistradas.length > 0" class="historial-notas">
          <h5>📑 Registro de Evaluaciones de la Asignatura</h5>
          <div class="tabla-responsive">
            <table class="data-table">
              <thead>
                <tr><th>Alumno</th><th>Evaluación</th><th>Nota</th><th>Peso</th><th>Acciones</th></tr>
              </thead>
              <tbody>
                <tr v-for="nota in notasRegistradas" :key="nota.id" :class="{ 'fila-editando': nota.id === idEditando }">
                  <td>{{ nota.alumno.nombre }} {{ nota.alumno.apellido }}</td>
                  <td>{{ nota.nombreEvaluacion }}</td>
                  <td class="nota-destacada">{{ nota.valor.toFixed(1) }}</td>
                  <td>{{ nota.ponderacion }}%</td>
                  <td>
                    <button type="button" class="icon-btn edit" @click="editarNota(nota)">✏️</button>
                    <button type="button" class="icon-btn delete" @click="eliminarNota(nota.id)">🗑️</button>
                  </td>
                </tr>
              </tbody>
            </table>
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
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: white; padding: 20px 30px; border-radius: 12px; border-left: 6px solid #e67e22; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.welcome-text h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.welcome-text p { margin: 5px 0 0; color: #7f8c8d; font-weight: 500; }
.header-actions { display: flex; align-items: center; gap: 15px; }
.btn-logout { background: transparent; color: #c0392b; border: 1px solid #c0392b; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-logout:hover { background: #c0392b; color: white; }
.avatar { background: #e67e22; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

/* Tarjeta Urgente (Clase) */
.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #e67e22; }
.card-header { display: flex; flex-direction: column; align-items: flex-start; gap: 8px; margin-bottom: 15px; }
.badge { background: #fdf2e9; color: #d35400; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.card-header h3 { margin: 0; color: #2c3e50; }
.info-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; color: #555; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.btn-group-urgent { display: flex; gap: 10px; margin-top: 15px; }
.primary-btn { flex: 1; background-color: #e67e22; color: white; padding: 10px; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { background: #d35400; }
.map-btn { background-color: #34495e; }
.map-btn:hover { background-color: #2c3e50; }

/* Grid Botones Accesos */
.quick-actions { margin-bottom: 25px; }
.grid-buttons { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 20px; }
.action-btn { flex: 1; min-width: 100px; background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover, .action-btn.active { background-color: #fdf2e9; border-color: #e67e22; font-weight: bold; }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }

/* Panel General */
.info-panel { background: white; border-radius: 12px; padding: 25px; border-top: 5px solid #e67e22; margin-bottom: 20px; }
.animacion-panel { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
h4 { margin: 0 0 15px; color: #2c3e50; font-size: 1.2rem; }
.instruccion { color: #7f8c8d; font-size: 0.95rem; margin-top: -10px; margin-bottom: 20px; }

/* Horario Grid Visual */
.tabla-horario-wrapper { overflow-x: auto; border: 1px solid #e0e6ed; border-radius: 8px; }
.tabla-horario { width: 100%; border-collapse: collapse; min-width: 600px; }
.tabla-horario th, .tabla-horario td { border: 1px solid #e0e6ed; padding: 10px; text-align: center; }
.tabla-horario th { background-color: #f1f3f5; color: #2c3e50; font-size: 0.9rem; padding: 12px; }
.col-hora { background-color: #f8f9fa; width: 100px; color: #2c3e50; }
.col-hora strong { font-size: 1.1rem; }
.hora-detalle { font-size: 0.75rem; color: #7f8c8d; margin-top: 4px; }
.celda-clase { vertical-align: middle; height: 60px; padding: 5px; width: 16%; }
.clase-bloque { background-color: #fdf2e9; border-left: 4px solid #e67e22; padding: 8px; border-radius: 4px; display: flex; flex-direction: column; justify-content: center; height: 100%; }
.clase-nombre { font-size: 0.8rem; font-weight: bold; color: #d35400; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.clase-sala { font-size: 0.75rem; color: #555; margin-top: 4px; }
.fila-descanso td { background-color: #fcfcfc; color: #7f8c8d; font-style: italic; font-size: 0.85rem; padding: 8px; }

/* Tarjetas de Cursos */
.cursos-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 15px; }
.curso-card { background: #fafbfc; border: 1px solid #eee; border-radius: 8px; padding: 20px; transition: 0.2s; }
.curso-card:hover { border-color: #e67e22; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.curso-header { display: flex; justify-content: space-between; margin-bottom: 10px; }
.codigo-badge { background: #2c3e50; color: white; padding: 3px 8px; border-radius: 4px; font-size: 0.8rem; font-family: monospace; }
.semestre-txt { font-size: 0.8rem; color: #7f8c8d; font-weight: bold; }
.curso-card h5 { margin: 0 0 10px 0; font-size: 1.1rem; color: #333; }
.curso-card p { margin: 0 0 15px 0; font-size: 0.9rem; color: #555; }
.curso-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 15px; border-top: 1px dashed #ddd; }
.sala-txt { font-weight: bold; color: #e67e22; font-size: 0.9rem; }
.btn-ruta-sm { background: white; border: 1px solid #e67e22; color: #e67e22; padding: 5px 10px; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: bold; transition: 0.2s; }
.btn-ruta-sm:hover { background: #e67e22; color: white; }

/* Asistencia Tabla & Toggles */
.mb-4 { margin-bottom: 20px; }
.asistencia-table-box { border: 1px solid #eee; border-radius: 8px; padding: 15px; background: #fafbfc; }
.ausente { background-color: #fdf5f6 !important; }
.ausente td { color: #c0392b; }
.toggle-cell { display: flex; align-items: center; justify-content: center; gap: 10px; }
.estado-lbl { font-size: 0.85rem; font-weight: bold; width: 60px; text-align: left; }
.btn-guardar-asistencia { width: 100%; background: #27ae60; color: white; border: none; padding: 12px; border-radius: 8px; font-size: 1rem; font-weight: bold; cursor: pointer; margin-top: 15px; }

/* Switch - Toggle */
.switch { position: relative; display: inline-block; width: 50px; height: 24px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #e74c3c; transition: .4s; }
.slider:before { position: absolute; content: ""; height: 16px; width: 16px; left: 4px; bottom: 4px; background-color: white; transition: .4s; }
input:checked + .slider { background-color: #2ecc71; }
input:checked + .slider:before { transform: translateX(26px); }
.slider.round { border-radius: 34px; }
.slider.round:before { border-radius: 50%; }

/* Formularios Notas */
.form-notas { display: flex; flex-direction: column; gap: 15px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.form-group { display: flex; flex-direction: column; gap: 5px; }
.form-group label { font-size: 0.85rem; font-weight: bold; color: #555; }
.form-group input, .form-group select { padding: 10px; border: 1px solid #ddd; border-radius: 8px; font-size: 0.9rem; transition: 0.2s; }
.form-group input:focus, .form-group select:focus { border-color: #e67e22; outline: none; }
.botones-form { display: flex; gap: 10px; margin-top: 5px; }
.btn-subir { flex: 1; background-color: #27ae60; color: white; border: none; padding: 12px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-subir:hover:not(:disabled) { background-color: #219653; }
.btn-subir:disabled { background-color: #95a5a6; cursor: not-allowed; }
.btn-cancelar { background-color: #95a5a6; color: white; border: none; padding: 12px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-cancelar:hover { background: #7f8c8d; }
.alerta { padding: 10px; border-radius: 8px; font-size: 0.9rem; font-weight: bold; text-align: center; margin-top: 10px; }
.exito { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
.error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

/* Tabla Base */
.tabla-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
.data-table th, .data-table td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #eee; }
.data-table th { background-color: #f8f9fa; color: #555; font-weight: bold; }
.text-center { text-align: center !important; }

/* Historial Notas */
.historial-notas { margin-top: 30px; border-top: 2px dashed #eee; padding-top: 20px; }
.historial-notas h5 { color: #2c3e50; font-size: 1.05rem; margin-bottom: 15px; }
.nota-destacada { font-weight: bold; color: #e67e22; }
.fila-editando { background-color: #fdf2e9; }
.icon-btn { background: transparent; border: none; font-size: 1.1rem; cursor: pointer; transition: 0.2s; margin-right: 8px; }
.icon-btn:hover { transform: scale(1.2); }

.news-section { margin-top: 30px; }
.news-section h3 { color: #2c3e50; margin-bottom: 15px; padding-left: 5px; border-left: 4px solid #e67e22; }
</style>