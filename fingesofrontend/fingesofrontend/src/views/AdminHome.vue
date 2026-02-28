<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};
</script>

<template>
  <div class="admin-home">
    <header class="header">
      <h1>Panel de Administración</h1>
      <button class="logout-btn" @click="cerrarSesion">Cerrar Sesión</button>
    </header>

    <div class="dashboard-grid">
      
      <div class="card action-card" @click="router.push('/admin/pius')">
        <span class="icon">⚙️</span>
        <h3>Gestionar PIUs</h3>
        <p>Administrar dispositivos</p>
      </div>

      <div class="card event-card" @click="router.push('/admin/eventos')">
        <span class="icon">📅</span>
        <h3>Gestionar Cartelera</h3>
        <p>Crear y editar avisos</p>
      </div>

      <div class="card info-card" @click="router.push('/cartelera')">
        <span class="icon">📢</span>
        <h3>Ver Cartelera</h3>
        <p>Vista de usuario común</p>
      </div>

      <div class="card user-card" @click="router.push('/admin/usuarios')">
        <span class="icon">👥</span>
        <h3>Usuarios</h3>
        <p>Gestionar cuentas</p>
      </div>

      <div class="card place-card" @click="router.push('/admin/lugares')">
        <span class="icon">🏢</span>
        <h3>Gestionar Lugares</h3>
        <p>Administrar edificios y zonas</p>
      </div>

      <div class="card map-card" @click="router.push('/mapa')">
        <span class="icon">🗺️</span>
        <h3>Ver Mapa Campus</h3>
        <p>Vista interactiva de PIUs</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-home { padding: 2rem; background: #f4f4f4; min-height: 100vh; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; margin-bottom: 3rem; align-items: center; }
.dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; max-width: 1000px; margin: 0 auto; }

.card { background: white; padding: 2rem; border-radius: 10px; text-align: center; cursor: pointer; transition: 0.2s; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
.card:hover { transform: translateY(-5px); }

.action-card { border-top: 5px solid #c0392b; } /* Rojo PIUs */
.event-card { border-top: 5px solid #27ae60; } /* Verde Eventos */
.info-card { border-top: 5px solid #3498db; background-color: #fbfdff; } /* Azul Info */
.user-card   { border-top: 5px solid #8e44ad; } /* Morado Usuarios */
.place-card { border-top: 5px solid #f39c12; } /* Naranja Lugares */
.map-card   { border-top: 5px solid #e67e22; background-color: #fffaf5; } /* Naranja claro Mapa */

.icon { font-size: 3rem; display: block; margin-bottom: 1rem; }
.logout-btn { padding: 0.5rem 1.5rem; background: #333; color: white; border: none; border-radius: 4px; cursor: pointer; }
</style>