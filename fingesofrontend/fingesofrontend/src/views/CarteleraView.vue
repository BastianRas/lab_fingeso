<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// Importación del servicio (Verifica que la ruta ../services/ sea correcta)
import eventoService from '../services/eventoService'; 

const router = useRouter();
const eventos = ref([]); 
const cargando = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    console.log("Cargando eventos...");
    const respuesta = await eventoService.obtenerTodos();
    console.log("Eventos recibidos:", respuesta.data);
    eventos.value = respuesta.data;
  } catch (err) {
    console.error("Error al conectar con backend:", err);
    error.value = "No se pudieron cargar los eventos. Revisa que el Backend esté encendido.";
  } finally {
    cargando.value = false;
  }
});
</script>

<template>
  <div class="cartelera-page">
    <header class="header">
      <div class="header-content">
        <h1>📢 Cartelera USACH</h1>
        <p>Noticias, Avisos y Eventos Oficiales</p>
      </div>
      <button class="back-btn" @click="router.push('/home')">⬅ Volver</button>
    </header>

    <div class="content">
      
      <div v-if="cargando" class="status-box loading">
        <p>⏳ Cargando información...</p>
      </div>

      <div v-else-if="error" class="status-box error">
        <p>⚠️ {{ error }}</p>
      </div>

      <div v-else-if="eventos.length === 0" class="status-box empty">
        <p>📭 No hay noticias publicadas en este momento.</p>
      </div>

      <div v-else class="news-grid">
        <div v-for="evento in eventos" :key="evento.id" class="news-card">
          <div class="card-header">
            <span v-if="evento.tipo === 'Aviso'" class="tag red">AVISO</span>
            <span v-else-if="evento.tipo === 'Académico'" class="tag blue">ACADÉMICO</span>
            <span v-else class="tag green">CULTURAL</span>
            
            <span class="date">{{ evento.fecha }}</span>
          </div>
          <h3>{{ evento.titulo }}</h3>
          <p>{{ evento.descripcion }}</p>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.cartelera-page { font-family: 'Segoe UI', sans-serif; min-height: 100vh; background-color: #f4f6f9; }

/* HEADER */
.header { background: #2c3e50; color: white; padding: 1.5rem 2rem; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.header h1 { margin: 0; font-size: 1.8rem; }
.header p { margin: 0; opacity: 0.8; font-size: 0.9rem; }
.back-btn { background: white; color: #2c3e50; border: none; padding: 0.5rem 1.2rem; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.back-btn:hover { background: #ea7600; color: white; }

.content { max-width: 1000px; margin: 2rem auto; padding: 0 1rem; }

/* ESTADOS */
.status-box { text-align: center; padding: 3rem; background: white; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); margin-top: 2rem; font-weight: bold; color: #777; }
.error { color: #c0392b; background: #fff5f5; border: 1px solid #ffcccc; }

/* GRILLA */
.news-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; }

.news-card { background: white; padding: 1.5rem; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); transition: transform 0.2s; border: 1px solid #eee; }
.news-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); }

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.date { font-size: 0.85rem; color: #999; font-weight: bold; }

/* ETIQUETAS */
.tag { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; color: white; font-weight: bold; letter-spacing: 0.5px; }
.red { background-color: #e74c3c; }
.blue { background-color: #3498db; }
.green { background-color: #27ae60; }

h3 { margin: 0 0 0.8rem 0; color: #2c3e50; font-size: 1.25rem; }
p { color: #555; line-height: 1.5; margin: 0; }
</style>