<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};
</script>

<template>
  <div class="admin-lobby">
    <header>
      <h1>Panel de Administrador</h1>
      <p>Selecciona una opción para gestionar el sistema GPIU</p>
    </header>

    <div class="menu-grid">
      <div class="card" @click="router.push('/admin/pius')">
        <span class="icon">🖥️</span>
        <h3>Gestionar PIUs</h3>
        <p>Agregar, editar o eliminar puntos de información.</p>
      </div>

      <div class="card" @click="alert('Funcionalidad en construcción')">
        <span class="icon">📅</span>
        <h3>Gestionar Eventos</h3>
        <p>Publicar noticias y eventos en las pantallas.</p>
      </div>

      <div class="card" @click="alert('Funcionalidad en construcción')">
        <span class="icon">🗺️</span>
        <h3>Gestionar Mapa</h3>
        <p>Actualizar ubicaciones y rutas del campus.</p>
      </div>
    </div>

    <button class="logout-btn" @click="cerrarSesion">Cerrar Sesión</button>
  </div>
</template>

<style scoped>
.admin-lobby { max-width: 900px; margin: 0 auto; padding: 3rem; text-align: center; font-family: sans-serif; }
header { margin-bottom: 3rem; }
h1 { color: #2c3e50; font-size: 2.5rem; }

.menu-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; margin-bottom: 3rem; }

.card { 
  background: white; 
  padding: 2rem; 
  border-radius: 12px; 
  box-shadow: 0 4px 10px rgba(0,0,0,0.1); 
  cursor: pointer; 
  transition: transform 0.2s, border-color 0.2s; 
  border: 2px solid transparent;
}

.card:hover { transform: translateY(-5px); border-color: #ea7600; }
.icon { font-size: 3rem; display: block; margin-bottom: 1rem; }
h3 { color: #ea7600; margin-bottom: 0.5rem; }
p { color: #666; font-size: 0.9rem; }

.logout-btn { background: #c0392b; color: white; border: none; padding: 0.8rem 2rem; border-radius: 25px; cursor: pointer; font-weight: bold; }
.logout-btn:hover { background: #a93226; }
</style>