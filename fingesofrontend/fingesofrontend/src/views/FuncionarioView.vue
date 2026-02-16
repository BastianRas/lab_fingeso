<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import CarteleraList from '../components/CarteleraList.vue'; 

const router = useRouter();
const funcionarioNombre = ref("Admin Pañol");
const solicitudesPendientes = ref(5);

const irAlMapa = () => {
  router.push('/mapa');
};
</script>

<template>
  <div class="dashboard-container">
    
    <header class="user-header">
      <div class="welcome-text">
        <h2>Hola, {{ funcionarioNombre }} 👋</h2>
        <p>Gestión de Recursos</p>
      </div>
      <div class="avatar" style="background-color: #2c3e50;">🛠️</div>
    </header>

    <section class="urgent-card status-card">
      <div class="card-header">
        <h3>Solicitudes Pendientes</h3>
      </div>
      <div class="status-display">
        <span class="big-number">{{ solicitudesPendientes }}</span>
        <span class="label">Items por entregar</span>
      </div>
      <button class="primary-btn" style="background-color: #2c3e50;">
        📦 Gestionar Entregas
      </button>
    </section>

    <section class="quick-actions">
      <h3>Acciones</h3>
      <div class="grid-buttons">
        <button class="action-btn" @click="irAlMapa">
          <span class="icon">🗺️</span> <span class="text">Ver Campus</span>
        </button>
        <button class="action-btn">
          <span class="icon">🔍</span> <span class="text">Buscar Item</span>
        </button>
        <button class="action-btn">
          <span class="icon">📥</span> <span class="text">Devolución</span>
        </button>
      </div>
    </section>

    <section class="news-section">
      <h3>📢 Cartelera USACH</h3>
      <CarteleraList />
    </section>

  </div>
</template>

<style scoped>
/* Reutilizamos estilos base */
.dashboard-container { padding: 20px; background-color: #f4f6f8; min-height: 100vh; font-family: sans-serif; }
.user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.avatar { color: white; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }

.urgent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 25px; border-left: 5px solid #27ae60; } /* Verde para funcionario */
.status-display { text-align: center; margin: 10px 0; }
.big-number { font-size: 2.5rem; font-weight: bold; color: #27ae60; display: block; }
.primary-btn { color: white; width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; margin-top: 15px; cursor: pointer; }

.grid-buttons { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 25px; }
.action-btn { background: white; border: 1px solid #ddd; border-radius: 10px; padding: 15px 5px; display: flex; flex-direction: column; align-items: center; gap: 5px; cursor: pointer; transition: 0.2s; }
.action-btn:hover { background-color: #e8f5e9; border-color: #27ae60; }
.icon { font-size: 1.5rem; }
.text { font-size: 0.85rem; font-weight: 500; }
</style>