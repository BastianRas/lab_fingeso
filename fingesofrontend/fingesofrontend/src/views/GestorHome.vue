<script setup>
import { ref } from 'vue';

const estadisticas = ref({
  total: 15,
  online: 12,
  offline: 3
});
</script>

<template>
  <div class="dashboard">
    <header>
      <h1>Centro de Monitoreo (Gestor)</h1>
      <p>Turno actual: Mañana</p>
    </header>

    <div class="stats-grid">
      <div class="card">
        <h3>Total PIUs</h3>
        <p class="number">{{ estadisticas.total }}</p>
      </div>
      <div class="card success">
        <h3>En Línea</h3>
        <p class="number">{{ estadisticas.online }}</p>
      </div>
      <div class="card danger">
        <h3>Desconectados</h3>
        <p class="number">{{ estadisticas.offline }}</p>
      </div>
    </div>
    
    <div class="alert-box" v-if="estadisticas.offline > 0">
      ⚠️ Atención: Hay {{ estadisticas.offline }} dispositivos que requieren revisión técnica.
    </div>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; font-family: sans-serif; }
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-top: 2rem; }
.card { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); text-align: center; border-left: 5px solid #2c3e50; }
.card.success { border-left-color: #27ae60; }
.card.danger { border-left-color: #c0392b; }
.number { font-size: 3rem; font-weight: bold; margin: 0.5rem 0; color: #333; }
.alert-box { margin-top: 2rem; background: #fff3cd; color: #856404; padding: 1rem; border-radius: 4px; border: 1px solid #ffeeba; }
</style>