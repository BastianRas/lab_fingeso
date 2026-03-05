<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import usuarioService from '../services/usuarioService';

const router = useRouter();
const usuarios = ref([]);
const mostrandoFormulario = ref(false);
const idEditando = ref(null);

const datosFormulario = ref({ 
  nombre: '', apellido: '', correo: '', numeroCredencial: '', contrasena: '', rol: 'ALUMNO' 
});


const carrerasDisponibles = ref([
  { id: 1, nombre: "Ingeniería en Informática" },
  { id: 2, nombre: "Ingeniería Civil Industrial" }
]);

const cursosDisponibles = ref([
  { id: 1, carreraId: 1, codigo: "CBM-1000", nombre: "Álgebra I", horario: "L1 M1 W1", sala: "A-101" },
  { id: 2, carreraId: 1, codigo: "CBM-1001", nombre: "Cálculo I", horario: "M2 J2 V1", sala: "A-102" },
  { id: 3, carreraId: 1, codigo: "INF-1000", nombre: "Intr. a la Ingeniería", horario: "W3 J3", sala: "A-103" },
  { id: 4, carreraId: 1, codigo: "CBM-1002", nombre: "Álgebra II", horario: "L2 M2", sala: "B-201" },
  { id: 5, carreraId: 1, codigo: "CBM-1003", nombre: "Cálculo II", horario: "W2 J2", sala: "B-202" },
  { id: 6, carreraId: 1, codigo: "INF-1100", nombre: "Programación Básica", horario: "L3 V3", sala: "LINF-1" },
  { id: 7, carreraId: 1, codigo: "FIS-1000", nombre: "Física I", horario: "M3 V2", sala: "C-301" },
  { id: 8, carreraId: 1, codigo: "INF-2200", nombre: "Estructuras de Datos", horario: "L4 W4", sala: "LINF-2" },
  { id: 9, carreraId: 1, codigo: "INF-2201", nombre: "Arquitectura de Computadores", horario: "M4 J4", sala: "D-101" },
  { id: 10, carreraId: 1, codigo: "INF-3321", nombre: "Sistemas Operativos", horario: "L5 W5", sala: "LINF-3" },
  { id: 11, carreraId: 1, codigo: "INF-3410", nombre: "Redes de Computadores", horario: "M5 J5", sala: "LINF-3" },
  { id: 12, carreraId: 1, codigo: "INF-3454", nombre: "Fundamentos de Ing. de Software", horario: "V4 V5", sala: "D-203" },
  { id: 13, carreraId: 2, codigo: "IND-1001", nombre: "Introducción a la Ind.", horario: "M2 J2", sala: "EAO-101" },
  { id: 14, carreraId: 2, codigo: "IND-1002", nombre: "Economía y Mercado", horario: "V3 V4", sala: "EAO-102" }
]);


const datosAlumno = ref({ carreraId: '', estadoMatricula: 'ACTIVA', cursosIds: [] });
const nuevaTarea = ref({ titulo: '', ubicacion: '', hora: '', dia: 15 });
const tareasFuncionario = ref([]);
const datosProfesor = ref({ cursosIds: [] }); 

const cursosFiltrados = computed(() => {
  if (!datosAlumno.value.carreraId) return [];
  return cursosDisponibles.value.filter(c => c.carreraId === datosAlumno.value.carreraId);
});

watch(() => datosAlumno.value.carreraId, (newVal, oldVal) => {
  if (oldVal !== '' && newVal !== oldVal) datosAlumno.value.cursosIds = [];
});

const cargarUsuarios = async () => {
  try {
    const respuesta = await usuarioService.obtenerTodos();
    usuarios.value = respuesta.data;
  } catch (error) { console.error(error); }
};

const toggleCursoAlumno = (cursoId) => {
  const index = datosAlumno.value.cursosIds.indexOf(cursoId);
  if (index > -1) datosAlumno.value.cursosIds.splice(index, 1);
  else datosAlumno.value.cursosIds.push(cursoId);
};


const toggleCursoProfesor = (cursoId) => {
  const index = datosProfesor.value.cursosIds.indexOf(cursoId);
  if (index > -1) datosProfesor.value.cursosIds.splice(index, 1);
  else datosProfesor.value.cursosIds.push(cursoId);
};

const convertirAMPM = (hora24) => {
  if (!hora24) return "";
  let [h, m] = hora24.split(':');
  h = parseInt(h);
  let ampm = h >= 12 ? 'PM' : 'AM';
  h = h % 12 || 12; 
  return `${h.toString().padStart(2, '0')}:${m} ${ampm}`;
};

const agregarTarea = () => {
  if (nuevaTarea.value.titulo && nuevaTarea.value.ubicacion && nuevaTarea.value.hora && nuevaTarea.value.dia) {
    const horaFormateada = convertirAMPM(nuevaTarea.value.hora);
    tareasFuncionario.value.push({ 
      titulo: nuevaTarea.value.titulo, ubicacion: nuevaTarea.value.ubicacion,
      dia: nuevaTarea.value.dia, hora: horaFormateada, estado: 'Pendiente' 
    });
    nuevaTarea.value = { titulo: '', ubicacion: '', hora: '', dia: nuevaTarea.value.dia };
  } else {
    alert("⚠️ Por favor completa todos los campos.");
  }
};

const eliminarTarea = (index) => tareasFuncionario.value.splice(index, 1);

const guardarUsuario = async () => {
  try {
    let rutUsuario = datosFormulario.value.numeroCredencial;
    if (idEditando.value) {
      await usuarioService.actualizar(idEditando.value, datosFormulario.value);
    } else {
      await usuarioService.crear(datosFormulario.value);
    }

    if (datosFormulario.value.rol === 'ALUMNO') {
       const matriculaData = {
           estado: datosAlumno.value.estadoMatricula,
           carreraNombre: carrerasDisponibles.value.find(c=>c.id===datosAlumno.value.carreraId)?.nombre,
           cursos: cursosDisponibles.value.filter(c => datosAlumno.value.cursosIds.includes(c.id))
       };
       const todas = JSON.parse(localStorage.getItem('matriculas_admin') || '{}');
       todas[rutUsuario] = matriculaData;
       localStorage.setItem('matriculas_admin', JSON.stringify(todas));
       alert(`✅ Alumno matriculado con ${matriculaData.cursos.length} ramos asignados.`);
    } 
    else if (datosFormulario.value.rol === 'FUNCIONARIO') {
       const todas = JSON.parse(localStorage.getItem('tareas_admin') || '{}');
       todas[rutUsuario] = tareasFuncionario.value;
       localStorage.setItem('tareas_admin', JSON.stringify(todas));
       alert(`✅ Agenda actualizada. Funcionario con ${tareasFuncionario.value.length} tareas.`);
    } 
    
    else if (datosFormulario.value.rol === 'PROFESOR') {
       const clasesAsignadas = cursosDisponibles.value.filter(c => datosProfesor.value.cursosIds.includes(c.id));
       const todas = JSON.parse(localStorage.getItem('profesor_admin') || '{}');
       todas[rutUsuario] = clasesAsignadas;
       localStorage.setItem('profesor_admin', JSON.stringify(todas));
       alert(`✅ Profesor asignado a ${clasesAsignadas.length} ramos universitarios.`);
    } else {
       alert("✅ Usuario guardado correctamente.");
    }

    limpiarFormulario();
    await cargarUsuarios();
  } catch (error) {
    alert("Error al guardar. Verifica que el correo o credencial no existan ya.");
  }
};

const cargarEdicion = (usuario) => {
  idEditando.value = usuario.usuarioId;
  mostrandoFormulario.value = true;
  
  datosFormulario.value = { 
    nombre: usuario.nombre, apellido: usuario.apellido, correo: usuario.correo, 
    numeroCredencial: usuario.numeroCredencial, contrasena: '', rol: usuario.rol 
  };

  if (usuario.rol === 'FUNCIONARIO') {
    const tareasGuardadas = JSON.parse(localStorage.getItem('tareas_admin') || '{}');
    tareasFuncionario.value = tareasGuardadas[usuario.numeroCredencial] || [];
  } 
  else if (usuario.rol === 'ALUMNO') {
    const matriculasGuardadas = JSON.parse(localStorage.getItem('matriculas_admin') || '{}');
    const m = matriculasGuardadas[usuario.numeroCredencial];
    if (m) {
      const carreraObj = carrerasDisponibles.value.find(c => c.nombre === m.carreraNombre);
      datosAlumno.value = { carreraId: carreraObj ? carreraObj.id : '', estadoMatricula: m.estado || 'ACTIVA', cursosIds: m.cursos ? m.cursos.map(c => c.id) : [] };
    }
  }
  
  else if (usuario.rol === 'PROFESOR') {
    const profesGuardados = JSON.parse(localStorage.getItem('profesor_admin') || '{}');
    const cursosAsignados = profesGuardados[usuario.numeroCredencial] || [];
    datosProfesor.value.cursosIds = cursosAsignados.map(c => c.id);
  }
};

const limpiarFormulario = () => {
  idEditando.value = null;
  datosFormulario.value = { nombre: '', apellido: '', correo: '', numeroCredencial: '', contrasena: '', rol: 'ALUMNO' };
  datosAlumno.value = { carreraId: '', estadoMatricula: 'ACTIVA', cursosIds: [] };
  tareasFuncionario.value = [];
  nuevaTarea.value = { titulo: '', ubicacion: '', hora: '', dia: 15 };
  datosProfesor.value = { cursosIds: [] };
  mostrandoFormulario.value = false;
};

const eliminarUsuario = async (id) => {
  if(confirm("¿Estás seguro de eliminar este usuario del sistema?")) {
    try { await usuarioService.eliminar(id); await cargarUsuarios(); } catch (error) { alert("Error al eliminar"); }
  }
};

onMounted(() => cargarUsuarios());
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Gestión de Usuarios e Identidades</h1>
      <button class="back-btn" @click="$router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="actions">
        <button class="add-btn" @click="mostrandoFormulario ? limpiarFormulario() : (mostrandoFormulario = true)" :style="{ backgroundColor: mostrandoFormulario ? '#7f8c8d' : '#8e44ad' }">
          {{ mostrandoFormulario ? 'Cancelar Registro' : '+ Nuevo Usuario' }}
        </button>
      </div>

      <div v-if="mostrandoFormulario" class="form-card">
        <h3>{{ idEditando ? 'Editar Perfil de Usuario' : 'Registrar Nuevo Usuario' }}</h3>
        
        <div class="form-inputs">
          <input v-model="datosFormulario.nombre" placeholder="Nombres" required />
          <input v-model="datosFormulario.apellido" placeholder="Apellidos" required />
        </div>
        <div class="form-inputs">
          <input v-model="datosFormulario.correo" type="email" placeholder="Correo institucional" required />
          <input v-model="datosFormulario.numeroCredencial" placeholder="Número de Credencial (RUT)" required />
        </div>
        <div class="form-inputs">
          <input v-model="datosFormulario.contrasena" type="password" placeholder="Contraseña temporal" :required="!idEditando" />
          <select v-model="datosFormulario.rol">
            <option value="ADMINISTRADOR">Administrador</option>
            <option value="GESTOR">Gestor</option>
            <option value="OPERADOR">Operador</option>
            <option value="PROFESOR">Profesor</option>
            <option value="FUNCIONARIO">Funcionario</option>
            <option value="ALUMNO">Alumno</option>
          </select>
        </div>

        <div v-if="datosFormulario.rol === 'ALUMNO'" class="modulo-extra modulo-alumno">
          <h4>🎓 Asignación Académica (Estudiante)</h4>
          <div class="form-inputs">
            <select v-model="datosAlumno.carreraId" required>
              <option value="" disabled>Seleccione la Carrera...</option>
              <option v-for="carrera in carrerasDisponibles" :key="carrera.id" :value="carrera.id">{{ carrera.nombre }}</option>
            </select>
            <select v-model="datosAlumno.estadoMatricula">
              <option value="ACTIVA">Estado: Matrícula Activa</option>
              <option value="SUSPENDIDA">Estado: Suspendida</option>
            </select>
          </div>
          
          <div v-if="datosAlumno.carreraId" class="lista-seleccion">
            <label>📚 Inscribir Cursos (Malla Curricular):</label>
            <div class="opciones-grid">
              <label v-for="curso in cursosFiltrados" :key="curso.id" class="check-card" :class="{'check-activo-alumno': datosAlumno.cursosIds.includes(curso.id)}">
                <input type="checkbox" :value="curso.id" @change="toggleCursoAlumno(curso.id)">
                <div class="curso-info"><span class="c-codigo">{{ curso.codigo }}</span><span class="c-nombre">{{ curso.nombre }}</span></div>
              </label>
            </div>
          </div>
          <p v-else class="text-muted mt-2">⚠️ Selecciona una carrera primero para ver los cursos disponibles.</p>
        </div>

        <div v-if="datosFormulario.rol === 'PROFESOR'" class="modulo-extra modulo-profesor">
          <h4>👨‍🏫 Asignación Docente (Cursos y Salas)</h4>
          <p class="text-muted">Selecciona los ramos que este profesor dictará durante el semestre. La sala y horario se conectarán automáticamente con los alumnos inscritos.</p>
          
          <div class="lista-seleccion">
            <div class="opciones-grid">
              <label v-for="curso in cursosDisponibles" :key="curso.id" class="check-card" :class="{'check-activo-profe': datosProfesor.cursosIds.includes(curso.id)}">
                <input type="checkbox" :value="curso.id" @change="toggleCursoProfesor(curso.id)">
                <div class="curso-info">
                  <span class="c-codigo">{{ curso.codigo }}</span>
                  <span class="c-nombre">{{ curso.nombre }}</span>
                  <span class="c-detalles">🕒 {{ curso.horario }} | 📍 Sala: {{ curso.sala }}</span>
                </div>
              </label>
            </div>
          </div>
        </div>

        <div v-if="datosFormulario.rol === 'FUNCIONARIO'" class="modulo-extra modulo-funcionario">
          <h4>💼 Asignación de Tareas en Agenda</h4>
          <div class="mini-calendario-container">
            <label class="instruccion-paso">1. Selecciona el día a agendar:</label>
            <div class="dias-grid-mini">
              <button type="button" v-for="dia in 31" :key="'dia-'+dia" :class="['dia-btn-mini', { 'dia-seleccionado': dia === nuevaTarea.dia }]" @click="nuevaTarea.dia = dia">{{ dia }}</button>
            </div>
          </div>
          <label class="instruccion-paso">2. Ingresa los detalles de la tarea para el día {{ nuevaTarea.dia }}:</label>
          <div class="form-inputs" style="align-items: center;">
            <input v-model="nuevaTarea.titulo" placeholder="Nombre de la Tarea" style="flex: 2;" />
            <input v-model="nuevaTarea.ubicacion" placeholder="Lugar (Ej: Sala EAO)" style="flex: 1.5;" />
            <input v-model="nuevaTarea.hora" type="time" style="flex: 1;" />
            <button type="button" class="btn-agregar-tarea" @click="agregarTarea">Añadir a lista</button>
          </div>
          <ul class="lista-tareas" v-if="tareasFuncionario.length > 0">
            <li v-for="(tarea, index) in tareasFuncionario" :key="index">
              <div class="tarea-detalle"><span class="tarea-dia-badge">Día {{ tarea.dia }}</span><span>🕒 {{ tarea.hora }} - <strong>{{ tarea.titulo }}</strong> (📍 {{ tarea.ubicacion }})</span></div>
              <button type="button" class="btn-quitar" @click="eliminarTarea(index)" title="Eliminar">❌</button>
            </li>
          </ul>
        </div>

        <button @click="guardarUsuario" class="save-btn">💾 {{ idEditando ? 'Guardar Cambios' : 'Guardar Usuario y Asignaciones' }}</button>
      </div>

      <table class="data-table">
        <thead><tr><th>Nombre Completo</th><th>Correo</th><th>Credencial</th><th>Rol</th><th>Acciones</th></tr></thead>
        <tbody>
          <tr v-for="user in usuarios" :key="user.usuarioId">
            <td><strong>{{ user.nombre }} {{ user.apellido }}</strong></td>
            <td>{{ user.correo }}</td>
            <td class="td-mono">{{ user.numeroCredencial }}</td>
            <td><span :class="['badge', user.rol.toLowerCase()]">{{ user.rol }}</span></td>
            <td class="action-buttons">
              <button class="edit-btn" @click="cargarEdicion(user)">Editar</button>
              <button class="delete-btn" @click="eliminarUsuario(user.usuarioId)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.header h1 { color: #2c3e50; margin: 0; font-size: 1.5rem; }
.back-btn { background: #34495e; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 6px; cursor: pointer; font-weight: bold; }
.actions { margin-bottom: 15px; }
.add-btn { color: white; border: none; padding: 0.8rem 1.5rem; border-radius: 6px; cursor: pointer; font-weight: bold; }
.form-card { background: white; padding: 2rem; margin-top: 1rem; margin-bottom: 2rem; border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.08); border-top: 5px solid #8e44ad; }
.form-inputs { display: flex; gap: 15px; margin: 15px 0; }
input, select { padding: 0.8rem; border: 1px solid #ced4da; border-radius: 6px; flex: 1; font-size: 0.95rem; }
input:focus, select:focus { outline: none; border-color: #8e44ad; }
.save-btn { background: #27ae60; color: white; border: none; padding: 1rem; border-radius: 6px; font-weight: bold; width: 100%; margin-top: 20px; cursor: pointer; transition: 0.2s; }
.save-btn:hover { background: #219653; }

.modulo-extra { padding: 20px; border-radius: 8px; margin: 20px 0; animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }

/* Alumno */
.modulo-alumno { background-color: #fdf5f6; border-left: 5px solid #c0392b; }
.modulo-alumno h4 { color: #c0392b; margin: 0 0 15px 0; }
.lista-seleccion { background: white; border: 1px solid #ddd; padding: 15px; border-radius: 8px; margin-top: 15px; }
.opciones-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 10px; max-height: 250px; overflow-y: auto; padding-right: 5px; }
.check-card { background: #fafbfc; border: 1px solid #ddd; padding: 10px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: 0.2s; }
.check-card:hover { background: #f4f6f8; }
.check-activo-alumno { border-color: #c0392b; background-color: #fce8ea; }
.curso-info { display: flex; flex-direction: column; }
.c-codigo { font-family: monospace; font-size: 0.75rem; color: #555; }
.c-nombre { font-size: 0.9rem; font-weight: 600; color: #333; }
.c-detalles { font-size: 0.75rem; color: #7f8c8d; margin-top: 3px; }

/* Profesor */
.modulo-profesor { background-color: #f4ecf8; border-left: 5px solid #8e44ad; }
.modulo-profesor h4 { color: #8e44ad; margin: 0 0 5px 0; }
.check-activo-profe { border-color: #8e44ad; background-color: #f0e4f5; }

/* Funcionario */
.modulo-funcionario { background-color: #e8f4f8; border-left: 5px solid #2980b9; }
.modulo-funcionario h4 { color: #2980b9; margin: 0 0 15px 0; }
.instruccion-paso { display: block; font-weight: bold; color: #2c3e50; font-size: 0.9rem; margin-bottom: 8px; margin-top: 15px; }
.mini-calendario-container { background: white; padding: 15px; border-radius: 8px; border: 1px solid #b3d4fc; margin-bottom: 20px; }
.dias-grid-mini { display: grid; grid-template-columns: repeat(10, 1fr); gap: 5px; }
.dia-btn-mini { background: #f4f6f8; border: 1px solid #ddd; border-radius: 4px; padding: 8px 0; cursor: pointer; font-size: 0.85rem; font-weight: 500; transition: 0.2s; }
.dia-btn-mini:hover { border-color: #2980b9; background: #e8f4f8; }
.dia-seleccionado { background: #2980b9; color: white; border-color: #2980b9; font-weight: bold; }
.btn-agregar-tarea { background: #2980b9; color: white; border: none; padding: 0 20px; height: 100%; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-agregar-tarea:hover { background: #1f6391; }
.lista-tareas { list-style: none; padding: 0; margin-top: 15px; }
.lista-tareas li { background: white; padding: 12px 15px; border-radius: 6px; margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center; border: 1px solid #b3d4fc; }
.tarea-detalle { display: flex; align-items: center; gap: 10px; }
.tarea-dia-badge { background: #2980b9; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: bold; }
.btn-quitar { background: none; border: none; cursor: pointer; transition: 0.2s; }
.btn-quitar:hover { transform: scale(1.2); }

.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 4px 10px rgba(0,0,0,0.05); border-radius: 10px; overflow: hidden; }
th, td { padding: 1.2rem 1rem; text-align: left; border-bottom: 1px solid #f0f0f0; }
th { background-color: #2c3e50; color: white; font-weight: 600; }
.badge { padding: 5px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: bold; color: white; }
.badge.administrador { background: #34495e; }
.badge.gestor { background: #d35400; }
.badge.operador { background: #27ae60; }
.badge.profesor { background: #8e44ad; }
.badge.funcionario { background: #2980b9; }
.badge.alumno { background: #c0392b; }
.action-buttons { display: flex; gap: 8px; }
.delete-btn { background: #fff0f0; color: #c0392b; border: 1px solid #f5c6cb; padding: 0.5rem; border-radius: 6px; cursor: pointer; }
.edit-btn { background: #fff8e1; color: #f39c12; border: 1px solid #ffeeba; padding: 0.5rem; border-radius: 6px; cursor: pointer; }

@media (max-width: 1024px) { .dias-grid-mini { grid-template-columns: repeat(7, 1fr); } }
@media (max-width: 768px) { .opciones-grid { grid-template-columns: 1fr; } }
</style>