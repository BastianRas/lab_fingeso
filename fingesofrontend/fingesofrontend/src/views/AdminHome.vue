<script setup>
import { ref } from 'vue';

// Datos falsos para visualizar la tabla
const pius = ref([
  { id: 1, codigo: 'PIU-001', ubicacion: 'Edificio M - Piso 1', estado: 'Activo' },
  { id: 2, codigo: 'PIU-002', ubicacion: 'Biblioteca Central', estado: 'Inactivo' },
  { id: 3, codigo: 'PIU-003', ubicacion: 'Casino EAO', estado: 'Activo' },
]);
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <h1>Panel de Administración GPIU</h1>
      <button class="logout-btn" @click="$router.push('/')">Cerrar Sesión</button>
    </header>

    <main class="content">
      <div class="actions">
        <h2>Gestión de Puntos de Información</h2>
        <button class="add-btn">+ Nuevo PIU</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Código</th>
            <th>Ubicación Física</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="piu in pius" :key="piu.id">
            <td>{{ piu.codigo }}</td>
            <td>{{ piu.ubicacion }}</td>
            <td>
              <span :class="['badge', piu.estado === 'Activo' ? 'green' : 'red']">
                {{ piu.estado }}
              </span>
            </td>
            <td>
              <button class="edit-btn">Editar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; background-color: #f4f4f4; min-height: 100vh; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.actions { display: flex; justify-content: space-between; margin-bottom: 1rem; }

/* Botones y Tablas */
.add-btn { background: #ea7600; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.data-table { width: 100%; background: white; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #2c3e50; color: white; }
.badge { padding: 4px 8px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
.badge.green { background: #d4edda; color: #155724; }
.badge.red { background: #f8d7da; color: #721c24; }
</style>