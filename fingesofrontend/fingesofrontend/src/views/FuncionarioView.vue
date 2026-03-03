<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue';

const router = useRouter();
const funcionarioNombre = ref('');
const vistaActiva = ref('agenda'); 
const agenda = ref([]); 
const diaSeleccionado = ref(15); 

// Tareas del día seleccionado
const agendaDelDia = computed(() => {
  return agenda.value.filter(t => t.dia === diaSeleccionado.value);
});

// ✨ MAGIA: Ordenar cronológicamente para encontrar la PRÓXIMA tarea real
const proximaTarea = computed(() => {
  const pendientes = agenda.value.filter(t => t.estado === 'Pendiente');
  if (pendientes.length === 0) return null;
  
  // Ordena de menor a mayor día (ej: Día 2 va antes que Día 15)
  pendientes.sort((a, b) => a.dia - b.dia);
  
  // Devuelve la primera tarea que encuentre después de ordenar
  return pendientes[0];
});

const tieneTareaPendiente = (dia) => {
  return agenda.value.some(t => t.dia === dia && t.estado === 'Pendiente');
};

const toggleEstadoTarea = (tarea) => {
  tarea.estado = tarea.estado === 'Pendiente' ? 'Completada' : 'Pendiente';
};

const procesos = ref([
  { id: 1, nombre: "Plazos de Matrícula 2026-2", fecha: "15 Nov - 20 Dic", descripcion: "Periodo oficial para el proceso de matrícula de estudiantes antiguos." },
  { id: 2, nombre: "Evaluación de Desempeño Funcionario", fecha: "Hasta 30 Nov", descripcion: "Completar formulario de autoevaluación en la intranet central." },
  { id: 3, nombre: "Solicitud de Feriados Legales", fecha: "Continuo", descripcion: "Ingresar solicitudes con al menos 15 días de anticipación mediante el portal RRHH." },
  { id: 4, nombre: "Fechas de Exámenes Finales", fecha: "05 Dic - 18 Dic", descripcion: "Periodo de toma de exámenes y cierre de actas académicas." }
]);

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user && user.rol === 'FUNCIONARIO') {
    funcionarioNombre.value = user.nombre;
    
    const tareasAdmin = JSON.parse(localStorage.getItem('tareas_admin') || '{}');
    const misTareasAsignadas = tareasAdmin[user.numeroCredencial];

    if (misTareasAsignadas && misTareasAsignadas.length > 0) {
      agenda.value = misTareasAsignadas.map((t, index) => ({
        id: index + 1,
        // ✨ Leemos y convertimos el día correctamente
        dia: parseInt(t.dia), 
        hora: t.hora, 
        titulo: t.titulo,
        ubicacion: t.ubicacion,
        estado: t.estado || "Pendiente"
      }));

      // Auto-seleccionar en el calendario el día de la próxima tarea
      const next = proximaTarea.value;
      if (next) {
        diaSeleccionado.value = next.dia;
      }

    } else {
      // Tareas de muestra si está vacío
      agenda.value = [
        { id: 1, dia: 15, hora: "09:00 AM", titulo: "Reunión de Coordinación", ubicacion: "Sala de Reuniones - EAO", estado: "Pendiente" },
        { id: 2, dia: 15, hora: "11:30 AM", titulo: "Revisión de Inventario PIU", ubicacion: "Biblioteca Central", estado: "Pendiente" }
      ];
    }
  } else {
    router.push('/');
  }
});

const cerrarSesion = () => { localStorage.removeItem('user'); router.push('/'); };
const mostrar = (vista) => { vistaActiva.value = vistaActiva.value === vista ? null : vista; };
const irAlMapaParaLugar = (ubicacion) => { alert(`📍 Redirigiendo al mapa para trazar ruta hacia: ${ubicacion}`); router.push('/mapa'); };
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, {{ funcionarioNombre }} 🏢</h2>
        <p>Portal Administrativo y de Servicios</p>
      </div>
      <div class="header-actions">
        <button class="btn-logout" @click="cerrarSesion">Cerrar Sesión</button>
        <div class="avatar">💼</div>
      </div>
    </header>

    <section v-if="proximaTarea" class="urgent-card">
      <div class="card-header">
        <span class="badge">Siguiente Actividad Pendiente</span>
        <h3>{{ proximaTarea.titulo }}</h3>
      </div>
      <div class="card-body">
        <div class="info-row">
          <i class="far fa-clock"></i>
          <span>Día {{ proximaTarea.dia }} - {{ proximaTarea.hora }}</span>
        </div>
        <div class="info-row">
          <i class="fas fa-map-marker-alt"></i>
          <span class="highlight-location">{{ proximaTarea.ubicacion }}</span>
        </div>
      </div>
      <button class="primary-btn" @click="irAlMapaParaLugar(proximaTarea.ubicacion)">
        🗺️ Trazar ruta al lugar
      </button>
    </section>

    <section class="quick-actions">
      <h3>Mi Espacio de Trabajo</h3>
      <div class="grid-buttons">
        <button class="action-btn" :class="{ active: vistaActiva === 'agenda' }" @click="mostrar('agenda')">
          <span class="icon">📅</span> <span class="text">Mi Agenda</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'procesos' }" @click="mostrar('procesos')">
          <span class="icon">📂</span> <span class="text">Procesos Admin.</span>
        </button>
        <button class="action-btn" @click="router.push('/mapa')">
          <span class="icon">📍</span> <span class="text">Mapa del Campus</span>
        </button>
      </div>

      <div v-if="vistaActiva === 'agenda'" class="info-panel animacion-panel panel-agenda-grid">
        
        <div class="calendario-box">
          <div class="mes-header">
            <button class="btn-mes">&lt;</button>
            <strong>Marzo 2026</strong>
            <button class="btn-mes">&gt;</button>
          </div>
          <div class="dias-semana">
            <span>Lu</span><span>Ma</span><span>Mi</span><span>Ju</span><span>Vi</span><span>Sa</span><span>Do</span>
          </div>
          <div class="dias-grid">
            <span class="dia-vacio"></span><span class="dia-vacio"></span><span class="dia-vacio"></span><span class="dia-vacio"></span><span class="dia-vacio"></span><span class="dia-vacio"></span>
            
            <button 
              v-for="dia in 31" :key="dia" 
              :class="['dia-btn', { 'seleccionado': dia === diaSeleccionado, 'con-tarea': tieneTareaPendiente(dia) }]"
              @click="diaSeleccionado = dia"
            >
              {{ dia }}
            </button>
          </div>
        </div>

        <div class="agenda-lista-box">
          <h4>Tareas del día {{ diaSeleccionado }} de Marzo</h4>
          
          <div v-if="agendaDelDia.length > 0" class="agenda-lista">
            <div v-for="tarea in agendaDelDia" :key="tarea.id" :class="['tarea-card', tarea.estado.toLowerCase()]">
              
              <div class="tarea-hora">{{ tarea.hora }}</div>
              
              <div class="tarea-info">
                <h5>{{ tarea.titulo }}</h5>
                <span class="tarea-ubicacion">📍 {{ tarea.ubicacion }}</span>
              </div>
              
              <div class="tarea-acciones">
                <span class="estado-badge">{{ tarea.estado }}</span>
                <div class="acciones-botones">
                  <button class="icon-btn-check" :title="tarea.estado === 'Pendiente' ? 'Marcar Completada' : 'Marcar Pendiente'" @click="toggleEstadoTarea(tarea)">
                    {{ tarea.estado === 'Pendiente' ? '✔️' : '🔄' }}
                  </button>
                  <button class="btn-ruta" title="Ver cómo llegar" @click="irAlMapaParaLugar(tarea.ubicacion)">
                    🗺️
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="sin-tareas">
            ☕ No hay tareas agendadas para este día.
          </div>
        </div>

      </div>

      <div v-if="vistaActiva === 'procesos'" class="info-panel animacion-panel">
        <h4>📂 Procesos Administrativos</h4>
        <p class="instruccion">Consulta normativas, plazos institucionales y fechas clave.</p>
        
        <div class="procesos-grid">
          <div v-for="proceso in procesos" :key="proceso.id" class="proceso-card">
            <div class="proceso-header">
              <span class="proceso-fecha">🗓️ {{ proceso.fecha }}</span>
            </div>
            <h5>{{ proceso.nombre }}</h5>
            <p>{{ proceso.descripcion }}</p>
            <button class="btn-detalle">Ver Detalle Oficial</button>
          </div>
        </div>
      </div>

    </section>

    <section class="news-section">
      <h3>📢 Eventos y Avisos Institucionales</h3>
      <CarteleraList />
    </section>

  </div>
</template>

<style scoped>
/* ESTILOS INTACTOS */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: white; padding: 20px 30px; border-radius: 12px; border-left: 6px solid #2980b9; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.welcome-text h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.welcome-text p { margin: 5px 0 0; color: #7f8c8d; font-weight: 500; }
.header-actions { display: flex; align-items: center; gap: 15px; }
.btn-logout { background: transparent; color: #c0392b; border: 1px solid #c0392b; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-logout:hover { background: #c0392b; color: white; }
.avatar { background: #2980b9; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #2980b9; }
.card-header { display: flex; flex-direction: column; align-items: flex-start; gap: 8px; margin-bottom: 15px; }
.badge { background: #e8f4f8; color: #2980b9; padding: 4px 10px; border-radius: 6px; font-size: 0.85rem; font-weight: bold; }
.card-header h3 { margin: 0; color: #2c3e50; font-size: 1.3rem; }
.info-row { display: flex; align-items: center; gap: 8px; color: #555; margin-bottom: 8px; font-size: 1rem; }
.highlight-location { font-weight: bold; color: #2c3e50; }
.primary-btn { background-color: #2980b9; color: white; width: 100%; padding: 12px; border: none; border-radius: 8px; font-size: 1rem; font-weight: bold; margin-top: 15px; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { background-color: #1f6391; }

.quick-actions { margin-bottom: 25px; }
.grid-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(130px, 1fr)); gap: 15px; margin-bottom: 20px; }
.action-btn { background: white; border: 1px solid #e0e6ed; border-radius: 10px; padding: 15px; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; transition: all 0.2s; }
.action-btn:hover { background-color: #e8f4f8; border-color: #2980b9; transform: translateY(-2px); }
.action-btn.active { background-color: #e8f4f8; border-color: #2980b9; font-weight: bold; box-shadow: 0 4px 10px rgba(41, 128, 185, 0.15); }
.icon { font-size: 1.8rem; }
.text { font-size: 0.9rem; color: #333; }

.info-panel { background: white; border-radius: 12px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-top: 5px solid #2980b9; }
.animacion-panel { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
.info-panel h4 { margin: 0 0 15px; color: #2c3e50; font-size: 1.2rem; }

.panel-agenda-grid { display: grid; grid-template-columns: 350px 1fr; gap: 30px; }
.calendario-box { background: #fdfdfd; border: 1px solid #e0e6ed; border-radius: 10px; padding: 20px; height: fit-content; }
.mes-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; color: #2c3e50; }
.btn-mes { background: none; border: none; font-size: 1.2rem; cursor: pointer; color: #2980b9; font-weight: bold; }
.dias-semana { display: grid; grid-template-columns: repeat(7, 1fr); text-align: center; font-size: 0.8rem; font-weight: bold; color: #7f8c8d; margin-bottom: 10px; }
.dias-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 5px; }
.dia-vacio { padding: 10px; }
.dia-btn { background: white; border: 1px solid #eee; border-radius: 6px; padding: 10px 0; text-align: center; cursor: pointer; font-size: 0.9rem; transition: 0.2s; position: relative; }
.dia-btn:hover { background: #f0f4f8; border-color: #2980b9; }
.dia-btn.seleccionado { background: #2980b9; color: white; font-weight: bold; box-shadow: 0 2px 5px rgba(41, 128, 185, 0.3); border-color: #2980b9; }
.dia-btn.con-tarea::after { content: ''; display: block; width: 6px; height: 6px; background-color: #e74c3c; border-radius: 50%; position: absolute; bottom: 2px; left: calc(50% - 3px); }

.agenda-lista-box h4 { margin-top: 0; color: #2980b9; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 15px; }
.agenda-lista { display: flex; flex-direction: column; gap: 12px; }
.tarea-card { display: flex; align-items: center; justify-content: space-between; padding: 15px; border: 1px solid #eee; border-radius: 8px; background: #fafbfc; transition: 0.2s; }
.tarea-card:hover { border-color: #2980b9; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.tarea-card.completada { opacity: 0.6; background: #f9f9f9; }
.tarea-card.completada h5 { text-decoration: line-through; color: #7f8c8d; }
.tarea-hora { font-weight: bold; color: #2c3e50; width: 90px; font-size: 0.95rem; border-right: 2px solid #eee; padding-right: 15px; margin-right: 15px; }
.tarea-info { flex: 1; }
.tarea-info h5 { margin: 0 0 5px 0; color: #333; font-size: 1.05rem; }
.tarea-ubicacion { font-size: 0.85rem; color: #666; }
.tarea-acciones { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
.estado-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: bold; text-transform: uppercase; }
.tarea-card.pendiente .estado-badge { background: #fff3cd; color: #856404; }
.tarea-card.completada .estado-badge { background: #d4edda; color: #155724; }
.acciones-botones { display: flex; gap: 8px; }
.btn-ruta, .icon-btn-check { background: white; border: 1px solid #ddd; padding: 6px 10px; border-radius: 6px; cursor: pointer; font-size: 1rem; transition: 0.2s; display: flex; align-items: center; justify-content: center; }
.btn-ruta:hover { background: #e8f4f8; border-color: #2980b9; }
.icon-btn-check:hover { background: #d4edda; border-color: #27ae60; }
.sin-tareas { padding: 30px; text-align: center; color: #7f8c8d; font-style: italic; background: #fafbfc; border-radius: 8px; border: 1px dashed #ddd; }

.procesos-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 15px; }
.proceso-card { border: 1px solid #e0e6ed; border-radius: 10px; padding: 20px; background: white; display: flex; flex-direction: column; transition: 0.2s; }
.proceso-card:hover { box-shadow: 0 5px 15px rgba(0,0,0,0.05); transform: translateY(-2px); border-color: #2980b9; }
.proceso-header { margin-bottom: 10px; }
.proceso-fecha { background: #f4f6f8; color: #555; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; }
.proceso-card h5 { margin: 0 0 10px 0; color: #2c3e50; font-size: 1.1rem; }
.proceso-card p { margin: 0 0 15px 0; color: #666; font-size: 0.9rem; line-height: 1.4; flex-grow: 1; }
.btn-detalle { background: transparent; color: #2980b9; border: 1px solid #2980b9; padding: 8px; border-radius: 6px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-detalle:hover { background: #2980b9; color: white; }

.news-section { margin-top: 35px; }
.news-section h3 { color: #2c3e50; margin-bottom: 15px; padding-left: 5px; border-left: 4px solid #ea7600; }

@media (max-width: 900px) {
  .panel-agenda-grid { grid-template-columns: 1fr; }
  .tarea-card { flex-direction: column; align-items: flex-start; gap: 10px; }
  .tarea-hora { border-right: none; border-bottom: 2px solid #eee; padding-bottom: 8px; margin-bottom: 5px; width: 100%; }
  .tarea-acciones { width: 100%; flex-direction: row; justify-content: space-between; margin-top: 10px; }
}
</style>