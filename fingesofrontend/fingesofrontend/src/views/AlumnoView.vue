<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const usuario = ref(null);

onMounted(() => {
  const userStored = localStorage.getItem('user');
  if (userStored) {
    usuario.value = JSON.parse(userStored);
    // Si NO es alumno, lo sacamos de aquí (seguridad básica)
    if (usuario.value.rol !== 'ALUMNO' && usuario.value.rol !== 'VISITA') {
       // Opcional: router.push('/login'); 
    }
  } else {
    router.push('/');
  }
});

const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};
</script>

<template>
  <div class="student-home">
    <header class="header">
      <div class="user-info">
        <h1>Hola, {{ usuario?.nombre }}</h1>
        <span class="badge">Estudiante</span>
      </div>
      <button class="logout-btn" @click="cerrarSesion">Salir</button>
    </header>

    <main class="grid-container">
      
      <div class="card info-card" @click="router.push('/cartelera')">
        <div class="icon-circle">📢</div>
        <h3>Cartelera USACH</h3>
        <p>Ver noticias, eventos y mapa</p>
      </div>

      <div class="card">
        <div class="icon-circle">📅</div>
        <h3>Mi Horario</h3>
        <p>Clases de hoy</p>
      </div>

      <div class="card">
        <div class="icon-circle">🎓</div>
        <h3>Mis Notas</h3>
        <p>Intranet Docente</p>
      </div>

      <div class="card">
        <div class="icon-circle">📝</div>
        <h3>Certificados</h3>
        <p>Alumno regular</p>
      </div>

    </main>
  </div>
</template>

<style scoped>
.student-home { min-height: 100vh; background-color: #f0f2f5; font-family: 'Segoe UI', sans-serif; }
.header { background: #ea7600; color: white; padding: 1.5rem 2rem; display: flex; justify-content: space-between; align-items: center; }
.user-info h1 { margin: 0; font-size: 1.5rem; }
.badge { background: rgba(255,255,255,0.2); padding: 4px 10px; border-radius: 15px; font-size: 0.8rem; }
.logout-btn { background: white; color: #ea7600; border: none; padding: 0.5rem 1.2rem; border-radius: 20px; font-weight: bold; cursor: pointer; }

.grid-container { max-width: 1000px; margin: 2rem auto; display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; padding: 0 1rem; }

.card { background: white; padding: 2rem; border-radius: 12px; text-align: center; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }

/* Destacar la tarjeta de información */
.info-card { border: 2px solid #3498db; background: #f0f8ff; }
.info-card .icon-circle { background: #3498db; }

.icon-circle { width: 60px; height: 60px; background: #ea7600; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.8rem; margin: 0 auto 1rem; }
h3 { margin: 0 0 0.5rem; color: #333; }
p { margin: 0; color: #666; font-size: 0.9rem; }
</style>