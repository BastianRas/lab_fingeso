<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import piuService from '../services/piuService';

const router = useRouter();
const operadorNombre = ref('');
const pius = ref([]);
const cargando = ref(true);
const vistaActiva = ref('vigilancia'); // 'vigilancia' | 'reubicacion' | 'reportes'

// Filtros para la vista
const filtroBusqueda = ref('');

const piusFiltrados = computed(() => {
  if (!filtroBusqueda.value) return pius.value;
  return pius.value.filter(p => 
    p.codigo.toLowerCase().includes(filtroBusqueda.value.toLowerCase()) || 
    p.ubicacion.toLowerCase().includes(filtroBusqueda.value.toLowerCase())
  );
});

const cargarDatos = async () => {
  try {
    const resPius = await piuService.obtenerTodos();
    pius.value = resPius.data;
  } catch (error) {
    console.error("Error al cargar PIUs:", error);
  } finally {
    cargando.value = false;
  }
};

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user && user.rol === 'OPERADOR') {
    operadorNombre.value = user.nombre;
    cargarDatos();
  } else {
    router.push('/'); 
  }
});

const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};

const irAlMapa = () => router.push('/mapa');

// --- FUNCIONES ESPECÍFICAS DEL OPERADOR (Según Informe 2) ---

// CU-008: Reportar falla de PIU
const reportarFalla = async (piu) => {
  const motivo = prompt(`¿Cuál es el problema reportado para el equipo ${piu.codigo}? (Ej: Pantalla rota, Sin conexión)`);
  if (motivo) {
    try {
      const piuActualizado = { ...piu, estado: 'Inactivo' };
      await piuService.actualizar(piu.id, piuActualizado);
      alert(`⚠️ Falla reportada exitosamente. El equipo ${piu.codigo} ha sido marcado como Inactivo y notificado al Gestor.`);
      await cargarDatos();
    } catch (error) {
      alert("Error al reportar la falla.");
    }
  }
};

// RF-021: Instalación/reubicación de PIUs
const reubicarPiu = async (piu) => {
  const nuevaUbicacion = prompt(`Ingrese la nueva ubicación física para el equipo ${piu.codigo}:`, piu.ubicacion);
  if (nuevaUbicacion && nuevaUbicacion !== piu.ubicacion) {
    try {
      const piuActualizado = { ...piu, ubicacion: nuevaUbicacion };
      await piuService.actualizar(piu.id, piuActualizado);
      alert(`📍 Equipo ${piu.codigo} reubicado exitosamente a: ${nuevaUbicacion}`);
      await cargarDatos();
    } catch (error) {
      alert("Error al registrar la reubicación.");
    }
  }
};

// CU-009: Generar reportes de operación
const generarReporte = () => {
  const total = pius.value.length;
  const activos = pius.value.filter(p => p.estado === 'Activo').length;
  const inactivos = total - activos;
  const porcentaje = total > 0 ? Math.round((activos / total) * 100) : 0;

  alert(`📄 GENERANDO REPORTE ESTADÍSTICO DE RED...\n\n- Total de equipos: ${total}\n- Equipos Operativos: ${activos}\n- Equipos con fallas: ${inactivos}\n- Salud de la red: ${porcentaje}%\n\n(Descargando PDF...)`);
};
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, Operador {{ operadorNombre }} 🛠️</h2>
        <p>Panel de Control y Mantenimiento de Red PIU</p>
      </div>
      <div class="header-actions">
        <button class="btn-logout" @click="cerrarSesion">Cerrar Sesión</button>
        <div class="avatar">👷‍♂️</div>
      </div>
    </header>

    <section class="quick-actions">
      <div class="grid-buttons">
        <button class="action-btn" :class="{ active: vistaActiva === 'vigilancia' }" @click="vistaActiva = 'vigilancia'">
          <span class="icon">🔍</span> <span class="text">Vigilar Red</span>
        </button>
        <button class="action-btn" :class="{ active: vistaActiva === 'reubicacion' }" @click="vistaActiva = 'reubicacion'">
          <span class="icon">📍</span> <span class="text">Reubicar Equipos</span>
        </button>
        <button class="action-btn" @click="generarReporte">
          <span class="icon">📊</span> <span class="text">Generar Reporte</span>
        </button>
        <button class="action-btn" @click="irAlMapa">
          <span class="icon">🗺️</span> <span class="text">Ver Mapa Físico</span>
        </button>
      </div>
    </section>

    <div v-if="cargando" class="cargando">Conectando con la infraestructura de red...</div>

    <div v-else class="contenido-operador">
      
      <section v-if="vistaActiva === 'vigilancia'" class="panel-lista animacion-panel">
        <div class="panel-header">
          <h3>🔍 Vigilancia Activa de Dispositivos</h3>
          <input type="text" v-model="filtroBusqueda" class="buscador" placeholder="Buscar por código o ubicación...">
        </div>
        <p class="instruccion">Supervisa el funcionamiento técnico. Si detectas anomalías físicas o de software, repórtalo inmediatamente.</p>

        <div class="tabla-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Código Equipo</th>
                <th>Ubicación Fija</th>
                <th>Estado Actual</th>
                <th>Acción de Operador</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="piu in piusFiltrados" :key="'vig-'+piu.id" :class="piu.estado.toLowerCase()">
                <td><strong>🖥️ {{ piu.codigo }}</strong></td>
                <td>{{ piu.ubicacion }}</td>
                <td><span :class="['badge-estado', piu.estado.toLowerCase()]">{{ piu.estado }}</span></td>
                <td>
                  <button v-if="piu.estado === 'Activo'" class="btn-accion danger" @click="reportarFalla(piu)">
                    ⚠️ Reportar Falla
                  </button>
                  <span v-else class="text-muted">Falla ya reportada al Gestor</span>
                </td>
              </tr>
              <tr v-if="piusFiltrados.length === 0">
                <td colspan="4" class="text-center">No se encontraron equipos.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section v-if="vistaActiva === 'reubicacion'" class="panel-lista animacion-panel">
        <div class="panel-header">
          <h3>📍 Registro de Reubicación Física</h3>
          <input type="text" v-model="filtroBusqueda" class="buscador" placeholder="Buscar equipo a mover...">
        </div>
        <p class="instruccion">Actualiza la ubicación en la base de datos cuando muevas físicamente un PIU a otro sector del campus.</p>

        <div class="tabla-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Código Equipo</th>
                <th>Ubicación Actual</th>
                <th>Estado</th>
                <th>Logística</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="piu in piusFiltrados" :key="'reu-'+piu.id">
                <td><strong>🖥️ {{ piu.codigo }}</strong></td>
                <td class="ubicacion-destacada">{{ piu.ubicacion }}</td>
                <td><span :class="['badge-estado', piu.estado.toLowerCase()]">{{ piu.estado }}</span></td>
                <td>
                  <button class="btn-accion info" @click="reubicarPiu(piu)">
                    🔄 Cambiar Ubicación
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </div>
  </div>
</template>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }

/* Header estilo Operador (Turquesa/Teal) */
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: white; padding: 20px 30px; border-radius: 12px; border-left: 6px solid #16a085; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.welcome-text h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.welcome-text p { margin: 5px 0 0; color: #7f8c8d; font-weight: 500; }
.header-actions { display: flex; align-items: center; gap: 15px; }
.btn-logout { background: transparent; color: #c0392b; border: 1px solid #c0392b; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-logout:hover { background: #c0392b; color: white; }
.avatar { background: #16a085; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

/* Menú de Botones */
.quick-actions { margin-bottom: 25px; }
.grid-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; }
.action-btn { background: white; border: 1px solid #e0e6ed; border-radius: 10px; padding: 15px; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 5px rgba(0,0,0,0.02); }
.action-btn:hover { background-color: #f0fbf9; border-color: #16a085; transform: translateY(-2px); }
.action-btn.active { background-color: #e8f8f5; border-color: #16a085; font-weight: bold; box-shadow: 0 4px 10px rgba(22, 160, 133, 0.2); }
.icon { font-size: 1.8rem; }
.text { font-size: 0.9rem; color: #333; }

/* Panel Principal */
.panel-lista { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border-top: 5px solid #16a085; }
.animacion-panel { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; flex-wrap: wrap; gap: 10px; }
.panel-header h3 { margin: 0; color: #2c3e50; font-size: 1.3rem; }
.buscador { padding: 8px 12px; border: 1px solid #ccc; border-radius: 6px; width: 250px; font-size: 0.9rem; }
.buscador:focus { outline: none; border-color: #16a085; }
.instruccion { color: #7f8c8d; margin-bottom: 20px; font-size: 0.95rem; }

/* Tablas */
.tabla-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.95rem; min-width: 600px; }
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
.data-table th { background-color: #f8f9fa; color: #555; font-weight: bold; }
.data-table tr:hover { background-color: #fdfdfd; }

.badge-estado { padding: 5px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; }
.badge-estado.activo { background-color: #d4edda; color: #155724; }
.badge-estado.inactivo { background-color: #f8d7da; color: #721c24; }
.badge-estado.mantenimiento { background-color: #fff3cd; color: #856404; }

.btn-accion { padding: 8px 12px; border: none; border-radius: 6px; font-weight: bold; cursor: pointer; font-size: 0.85rem; transition: 0.2s; }
.btn-accion.danger { background-color: #fff5f5; color: #c0392b; border: 1px solid #f5c6cb; }
.btn-accion.danger:hover { background-color: #e74c3c; color: white; }
.btn-accion.info { background-color: #e8f4f8; color: #2980b9; border: 1px solid #b3d4fc; }
.btn-accion.info:hover { background-color: #3498db; color: white; }

.text-muted { color: #95a5a6; font-style: italic; font-size: 0.85rem; }
.ubicacion-destacada { color: #16a085; font-weight: bold; }
.text-center { text-align: center; color: #7f8c8d; padding: 20px !important; }
.cargando { text-align: center; margin-top: 50px; color: #7f8c8d; font-style: italic; }
</style>