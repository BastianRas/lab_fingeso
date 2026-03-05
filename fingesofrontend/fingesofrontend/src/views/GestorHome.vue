<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import piuService from '../services/piuService';

const router = useRouter();
const gestorNombre = ref('');
const pius = ref([]);
const cargando = ref(true);

// Variables para el Dashboard
const totalPius = computed(() => pius.value.length);
const piusActivos = computed(() => pius.value.filter(p => p.estado === 'Activo').length);
const piusInactivos = computed(() => pius.value.filter(p => p.estado === 'Inactivo').length);
const piusMantenimiento = computed(() => pius.value.filter(p => p.estado === 'Mantenimiento').length);

const irAlMapa = () => router.push('/mapa');
const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};

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
  if (user && user.rol === 'GESTOR') {
    gestorNombre.value = user.nombre;
    cargarDatos();
  } else {
    router.push('/'); 
  }
});

// Funciones simuladas de acción del Gestor según el informe
const enviarTecnico = (piuCodigo) => {
  alert(`👷‍♂️ Se ha generado un ticket para enviar un técnico al dispositivo: ${piuCodigo}.`);
};

const reiniciarRemoto = (piuCodigo) => {
  alert(`🔄 Ejecutando reinicio remoto del sistema operativo en el dispositivo: ${piuCodigo}...`);
};
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, Gestor {{ gestorNombre }} 🖥️</h2>
        <p>Centro de Monitoreo de Red PIU</p>
      </div>
      <div class="header-actions">
        <button class="btn-logout" @click="cerrarSesion">Cerrar Sesión</button>
        <div class="avatar">👨‍💻</div>
      </div>
    </header>

    <div v-if="cargando" class="cargando">Cargando estado de la red...</div>

    <div v-else class="contenido-gestor">
      
      <section class="metricas-grid">
        <div class="metrica-card total">
          <span class="metrica-valor">{{ totalPius }}</span>
          <span class="metrica-label">PIUs Registrados</span>
        </div>
        <div class="metrica-card activos">
          <span class="metrica-valor">{{ piusActivos }}</span>
          <span class="metrica-label">Operativos</span>
        </div>
        <div class="metrica-card mantenimiento">
          <span class="metrica-valor">{{ piusMantenimiento }}</span>
          <span class="metrica-label">En Mantenimiento</span>
        </div>
        <div class="metrica-card inactivos">
          <span class="metrica-valor">{{ piusInactivos }}</span>
          <span class="metrica-label">Caídos / Inactivos</span>
        </div>
      </section>

      <div class="grid-principal">
        
        <section class="panel-lista">
          <div class="panel-header">
            <h3>🔴 Alertas y Estado de la Red</h3>
            <button class="btn-mapa" @click="irAlMapa">📍 Ver PIUs en Mapa</button>
          </div>
          <p class="instruccion">Define acciones ante fallas o mal funcionamiento de los equipos.</p>

          <div class="tabla-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Código Equipo</th>
                  <th>Ubicación Física</th>
                  <th>Estado Actual</th>
                  <th>Acción Rápida</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="piu in pius" :key="piu.id" :class="piu.estado.toLowerCase()">
                  <td><strong>🖥️ {{ piu.codigo }}</strong></td>
                  <td>{{ piu.ubicacion }}</td>
                  <td>
                    <span :class="['badge-estado', piu.estado.toLowerCase()]">
                      {{ piu.estado }}
                    </span>
                  </td>
                  <td>
                    <button v-if="piu.estado === 'Inactivo'" class="btn-accion alert" @click="enviarTecnico(piu.codigo)">
                      🎫 Enviar Técnico
                    </button>
                    <button v-if="piu.estado === 'Mantenimiento'" class="btn-accion warn" @click="reiniciarRemoto(piu.codigo)">
                      🔄 Reiniciar Remoto
                    </button>
                    <span v-if="piu.estado === 'Activo'" class="ok-text">✅ Red estable</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section class="panel-lateral">
          <div class="info-card">
            <span class="icon-big">📈</span>
            <h4>Reportes de Operación</h4>
            <p>Genera reportes estadísticos del uso y fallos de la red para el departamento de DTI.</p>
            <button class="btn-outline">Generar Reporte PDF</button>
          </div>

          <div class="info-card">
            <span class="icon-big">⚙️</span>
            <h4>Políticas de Red</h4>
            <p>Ajusta las políticas de ahorro de energía y horarios de apagado de las pantallas.</p>
            <button class="btn-outline">Configurar Políticas</button>
          </div>
        </section>

      </div>

    </div>
  </div>
</template>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }

.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; background: white; padding: 20px 30px; border-radius: 12px; border-left: 6px solid #8e44ad; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.welcome-text h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.welcome-text p { margin: 5px 0 0; color: #7f8c8d; font-weight: 500; }
.header-actions { display: flex; align-items: center; gap: 15px; }
.btn-logout { background: transparent; color: #c0392b; border: 1px solid #c0392b; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.btn-logout:hover { background: #c0392b; color: white; }
.avatar { background: #8e44ad; color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }


.metricas-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 25px; }
.metrica-card { background: white; padding: 20px; border-radius: 12px; display: flex; flex-direction: column; align-items: center; justify-content: center; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.metrica-valor { font-size: 2.5rem; font-weight: bold; color: #2c3e50; }
.metrica-label { font-size: 0.9rem; color: #7f8c8d; text-transform: uppercase; letter-spacing: 1px; font-weight: bold; margin-top: 5px; }
.metrica-card.total { border-bottom: 4px solid #3498db; }
.metrica-card.activos { border-bottom: 4px solid #27ae60; }
.metrica-card.mantenimiento { border-bottom: 4px solid #f39c12; }
.metrica-card.inactivos { border-bottom: 4px solid #e74c3c; }


.grid-principal { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }


.panel-lista { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px; }
.panel-header h3 { margin: 0; color: #2c3e50; font-size: 1.3rem; }
.btn-mapa { background: #8e44ad; color: white; border: none; padding: 8px 15px; border-radius: 6px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-mapa:hover { background: #732d91; }
.instruccion { color: #7f8c8d; margin-bottom: 20px; font-size: 0.95rem; }

.tabla-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.95rem; }
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
.data-table th { background-color: #f8f9fa; color: #555; font-weight: bold; }
.data-table tr:hover { background-color: #fdfdfd; }

.badge-estado { padding: 5px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; }
.badge-estado.activo { background-color: #d4edda; color: #155724; }
.badge-estado.inactivo { background-color: #f8d7da; color: #721c24; }
.badge-estado.mantenimiento { background-color: #fff3cd; color: #856404; }

.btn-accion { padding: 6px 12px; border: none; border-radius: 6px; font-weight: bold; cursor: pointer; font-size: 0.85rem; }
.btn-accion.alert { background-color: #e74c3c; color: white; }
.btn-accion.warn { background-color: #f39c12; color: white; }
.btn-accion:hover { opacity: 0.9; }
.ok-text { color: #27ae60; font-weight: bold; font-size: 0.9rem; }


.panel-lateral { display: flex; flex-direction: column; gap: 20px; }
.info-card { background: white; padding: 25px; border-radius: 12px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.icon-big { font-size: 2.5rem; display: block; margin-bottom: 10px; }
.info-card h4 { margin: 0 0 10px; color: #2c3e50; font-size: 1.1rem; }
.info-card p { margin: 0 0 20px; color: #7f8c8d; font-size: 0.9rem; line-height: 1.4; }
.btn-outline { background: transparent; color: #8e44ad; border: 2px solid #8e44ad; padding: 8px 20px; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-outline:hover { background: #8e44ad; color: white; }


@media (max-width: 768px) {
  .grid-principal { grid-template-columns: 1fr; }
}
</style>