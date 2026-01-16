<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const usuario = ref(null);
const rol = ref('');

onMounted(() => {
  // Recuperamos el usuario guardado en el Login
  const userStored = localStorage.getItem('user');
  if (userStored) {
    usuario.value = JSON.parse(userStored);
    rol.value = usuario.value.rol; // 'ALUMNO', 'PROFESOR' o 'FUNCIONARIO'
  } else {
    router.push('/'); // Si no hay usuario, devolver al login
  }
});

const cerrarSesion = () => {
  localStorage.removeItem('user');
  router.push('/');
};
</script>

<template>
  <div class="user-home">
    <div class="hero">
      <h1>Hola, {{ usuario?.nombre }}</h1>
      <p class="subtitle">Perfil: <strong>{{ rol }}</strong></p>
    </div>

    <div class="menu-grid">
      
      <div class="menu-item">
        <span class="icon">🗺️</span>
        <h3>Mapa del Campus</h3>
        <p>Ubica salas y edificios</p>
      </div>
      
      <div class="menu-item">
        <span class="icon">📢</span>
        <h3>Eventos USACH</h3>
        <p>Noticias y actividades</p>
      </div>

      <template v-if="rol === 'ALUMNO'">
        <div class="menu-item highlight">
          <span class="icon">🎓</span>
          <h3>Malla Curricular</h3>
          <p>Avance y pre-requisitos</p>
        </div>
        <div class="menu-item">
          <span class="icon">📅</span>
          <h3>Mi Horario</h3>
          <p>Clases y laboratorios</p>
        </div>
        <div class="menu-item">
          <span class="icon">📝</span>
          <h3>Estado Matrícula</h3>
          <p>Certificados y pagos</p>
        </div>
      </template>

      <template v-if="rol === 'PROFESOR'">
        <div class="menu-item highlight">
          <span class="icon">📆</span>
          <h3>Mi Agenda</h3>
          <p>Reuniones y compromisos</p>
        </div>
        <div class="menu-item">
          <span class="icon">📚</span>
          <h3>Cursos Asignados</h3>
          <p>Listas de alumnos y actas</p>
        </div>
        <div class="menu-item">
          <span class="icon">📍</span>
          <h3>Ubicación de Salas</h3>
          <p>Buscar salas de clases</p>
        </div>
      </template>

      <template v-if="rol === 'FUNCIONARIO'">
        <div class="menu-item highlight">
          <span class="icon">📒</span>
          <h3>Agenda Corporativa</h3>
          <p>Reuniones de departamento</p>
        </div>
        <div class="menu-item">
          <span class="icon">💼</span>
          <h3>Mis Funciones</h3>
          <p>Tareas y procedimientos</p>
        </div>
      </template>

    </div>
    
    <button class="logout-btn" @click="cerrarSesion">Cerrar Sesión</button>
  </div>
</template>

<style scoped>
.user-home {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
}

.hero {
  text-align: center;
  margin-bottom: 3rem;
  background: linear-gradient(135deg, #ea7600 0%, #d35400 100%);
  color: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(234, 118, 0, 0.3);
}

.hero h1 { margin: 0; font-size: 2.5rem; }
.subtitle { margin-top: 0.5rem; opacity: 0.9; font-size: 1.1rem; }

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.menu-item {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.menu-item:hover {
  transform: translateY(-5px);
  border-color: #ea7600;
  box-shadow: 0 8px 15px rgba(0,0,0,0.1);
}

/* Estilo especial para las tarjetas principales de cada rol */
.menu-item.highlight {
  border-top: 4px solid #ea7600;
  background-color: #fffaf5;
}

.icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  display: block;
}

h3 { margin: 0; color: #333; font-size: 1.1rem; }
p { margin: 0.5rem 0 0; color: #666; font-size: 0.9rem; }

.logout-btn {
  display: block;
  margin: 0 auto;
  padding: 0.8rem 2rem;
  background: white;
  border: 1px solid #ccc;
  border-radius: 25px;
  color: #555;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: #f0f0f0;
  color: #ea7600;
  border-color: #ea7600;
}
</style>