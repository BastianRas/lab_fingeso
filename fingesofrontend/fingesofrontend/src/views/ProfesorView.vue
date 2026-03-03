<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';
import claseService from '../services/claseService';
import usuarioService from '../services/usuarioService';
import notaService from '../services/notaService';

const router = useRouter();
const profesorNombre = ref('');
const clases = ref([]);
const alumnosInscritos = ref([]);
const vistaActiva = ref(null); // 'horario' | 'cursos' | 'notas' | null

// Datos del formulario de notas
const formNota = ref({
  claseId: '',
  alumnoId: '',
  nombreEvaluacion: '',
  valor: '',
  ponderacion: ''
});
const enviando = ref(false);
const mensaje = ref({ texto: '', tipo: '' });

// Nuevas variables para el registro y edición
const idEditando = ref(null);
const notasRegistradas = ref([]);

const irAlMapa = () => router.push('/mapa');

const mostrar = (vista) => {
  vistaActiva.value = vistaActiva.value === vista ? null : vista;
};

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    profesorNombre.value = user.nombre;
    try {
      // 1. Cargar las clases del profesor
      clases.value = await claseService.getClasesByProfesor(user.usuarioId);
      
      // 2. Cargar todos los alumnos para poder asignarles notas
      const resUsuarios = await usuarioService.obtenerTodos();
      alumnosInscritos.value = resUsuarios.data.filter(u => u.rol === 'ALUMNO' || u.rol === 'VISITA');
    } catch (error) {
      console.error("Error al cargar datos del profesor", error);
      clases.value = [];
    }
  }
});

const primerCurso = () => clases.value.length > 0 ? clases.value[0] : null;

// Cargar el historial de notas cuando se selecciona un curso
const cargarNotasDeClase = async () => {
  if (formNota.value.claseId) {
    try {
      const res = await notaService.obtenerPorClase(formNota.value.claseId);
      notasRegistradas.value = res.data;
    } catch (e) {
      console.error("Error al cargar registro", e);
    }
  }
};

const enviarNota = async () => {
  mensaje.value = { texto: '', tipo: '' };

  if (!formNota.value.claseId || !formNota.value.alumnoId || !formNota.value.nombreEvaluacion || !formNota.value.valor || !formNota.value.ponderacion) {
    mensaje.value = { texto: '⚠️ Por favor, completa todos los campos.', tipo: 'error' };
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
      // Si estamos editando
      await notaService.actualizarNota(idEditando.value, notaBD);
      mensaje.value = { texto: '✅ ¡Nota actualizada exitosamente!', tipo: 'exito' };
    } else {
      // Si es una nota nueva
      await notaService.crearNota(notaBD);
      mensaje.value = { texto: '✅ ¡Nota subida exitosamente al sistema!', tipo: 'exito' };
    }
    
    // Limpiamos los campos, cancelamos modo edición y recargamos la tabla
    cancelarEdicion();
    await cargarNotasDeClase();
  } catch (error) {
    console.error(error);
    mensaje.value = { texto: '❌ Error al subir la nota. Intenta nuevamente.', tipo: 'error' };
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
        <p>Panel Docente</p>
      </div>
      <div class="header-actions">
        <button class="btn-logout" @click="router.push('/')">Cerrar Sesión</button>
        <div class="avatar">👨‍🏫</div>
      </div>
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
        <button class="action-btn" :class="{ active: vistaActiva === 'notas' }" @click="mostrar('notas')">
          <span class="icon">✍️</span> <span class="text">Subir Notas</span>
        </button>
      </div>

      <div v-if="vistaActiva === 'horario'" class="info-panel animacion-panel">
        <h4>📅 Mi Horario — {{ profesorNombre }}</h4>
        <div v-if="clases.length" class="tabla-clases">
          <div class="clase-row header-row">
            <span>Asignatura</span><span>Código</span><span>Horario</span><span>Sala</span>
          </div>
          <div v-for="clase in clases" :key="clase.id" class="clase-row">
            <span>{{ clase.nombre }}</span>
            <span>{{ clase.codigo }}</span>
            <span class="texto-destacado">{{ clase.horario }}</span>
            <span>{{ clase.sala }}</span>
          </div>
        </div>
        <p v-else class="sin-datos">No hay clases registradas.</p>
      </div>

      <div v-if="vistaActiva === 'cursos'" class="info-panel animacion-panel">
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

      <div v-if="vistaActiva === 'notas'" class="info-panel animacion-panel">
        <h4>{{ idEditando ? '✏️ Editando Calificación' : '✍️ Ingresar Calificación' }}</h4>
        <p class="instruccion">Selecciona el curso, el alumno y asigna la calificación correspondiente.</p>
        
        <form @submit.prevent="enviarNota" class="form-notas">
          <div class="form-row">
            <div class="form-group">
              <label>Asignatura</label>
              <select v-model="formNota.claseId" @change="cargarNotasDeClase" required>
                <option value="" disabled>Seleccione un ramo...</option>
                <option v-for="clase in clases" :key="clase.id" :value="clase.id">
                  {{ clase.codigo }} - {{ clase.nombre }}
                </option>
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
            <label>Nombre de la Evaluación (Ej: PEP 1, Control...)</label>
            <input type="text" v-model="formNota.nombreEvaluacion" placeholder="Ej: PEP 1" required>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Nota Obtenida (1.0 a 7.0)</label>
              <input type="number" step="0.1" min="1.0" max="7.0" v-model="formNota.valor" placeholder="Ej: 6.5" required>
            </div>

            <div class="form-group">
              <label>Ponderación (%)</label>
              <input type="number" min="1" max="100" v-model="formNota.ponderacion" placeholder="Ej: 30" required>
            </div>
          </div>

          <div v-if="mensaje.texto" :class="['alerta', mensaje.tipo]">
            {{ mensaje.texto }}
          </div>

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
                <tr>
                  <th>Alumno</th>
                  <th>Evaluación</th>
                  <th>Nota</th>
                  <th>Peso</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="nota in notasRegistradas" :key="nota.id" :class="{ 'fila-editando': nota.id === idEditando }">
                  <td>{{ nota.alumno.nombre }} {{ nota.alumno.apellido }}</td>
                  <td>{{ nota.nombreEvaluacion }}</td>
                  <td class="nota-destacada">{{ nota.valor.toFixed(1) }}</td>
                  <td>{{ nota.ponderacion }}%</td>
                  <td>
                    <button type="button" class="icon-btn edit" @click="editarNota(nota)" title="Editar">✏️</button>
                    <button type="button" class="icon-btn delete" @click="eliminarNota(nota.id)" title="Eliminar">🗑️</button>
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
/* ESTILOS ORIGINALES MANTENIDOS */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-actions { display: flex; align-items: center; gap: 15px; }
.btn-logout { background: transparent; color: #c0392b; border: 1px solid #c0392b; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-logout:hover { background: #c0392b; color: white; }
.avatar { background: #ea7600; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #ea7600; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.badge { background: #ffe0b2; color: #e65100; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.highlight-location { font-weight: bold; font-size: 1.1rem; color: #2c3e50; }
.primary-btn { background-color: #ea7600; color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { background-color: #d35400; }

.grid-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(100px, 1fr)); gap: 10px; margin-bottom: 15px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #fff8f0; border-color: #ea7600; }
.action-btn.active { background-color: #fff8f0; border-color: #ea7600; font-weight: bold; box-shadow: 0 2px 8px rgba(234, 118, 0, 0.2); }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }

.info-panel { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-left: 5px solid #ea7600; }
.animacion-panel { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
.info-panel h4 { margin: 0 0 15px; color: #2c3e50; font-size: 1.2rem; }
.sin-datos { color: #999; font-style: italic; }
.texto-destacado { color: #ea7600; font-weight: bold; }

.tabla-clases { overflow-x: auto; }
.clase-row { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 1fr; gap: 8px; padding: 10px 4px; border-bottom: 1px solid #f0f0f0; font-size: 0.85rem; align-items: center; }
.header-row { font-weight: bold; color: #555; background: #f8f9fa; border-radius: 6px; }

/* ESTILOS DE FORMULARIO DE NOTAS */
.instruccion { color: #7f8c8d; font-size: 0.9rem; margin-top: -10px; margin-bottom: 20px; }
.form-notas { display: flex; flex-direction: column; gap: 15px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.form-group { display: flex; flex-direction: column; gap: 5px; }
.form-group label { font-size: 0.85rem; font-weight: bold; color: #555; }
.form-group input, .form-group select { padding: 10px; border: 1px solid #ddd; border-radius: 8px; font-size: 0.9rem; transition: 0.2s; }
.form-group input:focus, .form-group select:focus { border-color: #ea7600; outline: none; box-shadow: 0 0 0 2px rgba(234, 118, 0, 0.1); }

.botones-form { display: flex; gap: 10px; margin-top: 5px; }
.btn-subir { flex: 1; background-color: #27ae60; color: white; border: none; padding: 12px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-subir:hover:not(:disabled) { background-color: #219653; }
.btn-subir:disabled { background-color: #95a5a6; cursor: not-allowed; }
.btn-cancelar { background-color: #95a5a6; color: white; border: none; padding: 12px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-cancelar:hover { background-color: #7f8c8d; }

.alerta { padding: 10px; border-radius: 8px; font-size: 0.9rem; font-weight: bold; text-align: center; margin-top: 10px; }
.exito { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
.error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

/* ESTILOS DE LA TABLA DE HISTORIAL DE NOTAS */
.historial-notas { margin-top: 30px; border-top: 2px dashed #eee; padding-top: 20px; }
.historial-notas h5 { color: #2c3e50; font-size: 1.05rem; margin-bottom: 15px; }
.tabla-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
.data-table th, .data-table td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #eee; }
.data-table th { background-color: #f8f9fa; color: #555; font-weight: bold; }
.nota-destacada { font-weight: bold; color: #ea7600; }
.fila-editando { background-color: #fff8f0; }
.icon-btn { background: transparent; border: none; font-size: 1.1rem; cursor: pointer; transition: 0.2s; margin-right: 8px; }
.icon-btn:hover { transform: scale(1.2); }

.news-section { margin-top: 30px; }
.news-section h3 { color: #2c3e50; margin-bottom: 15px; }
</style>