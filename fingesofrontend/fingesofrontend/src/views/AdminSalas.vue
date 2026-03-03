<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Lista maestra por defecto
const cursos = ref([
  { id: 1, carreraId: 1, codigo: "CBM-1000", nombre: "Álgebra I", horario: "L1 M1 W1", sala: "A-101" },
  { id: 2, carreraId: 1, codigo: "CBM-1001", nombre: "Cálculo I", horario: "M2 J2 V1", sala: "A-102" },
  { id: 3, carreraId: 1, codigo: "INF-1000", nombre: "Intr. a la Ingeniería", horario: "W3 J3", sala: "A-103" },
  { id: 4, carreraId: 1, codigo: "CBM-1002", nombre: "Álgebra II", horario: "L2 M2", sala: "B-201" },
  { id: 5, carreraId: 1, codigo: "CBM-1003", nombre: "Cálculo II", horario: "W2 J2", sala: "B-202" },
  { id: 6, carreraId: 1, codigo: "INF-1100", nombre: "Programación Básica", horario: "L3 V3", sala: "LINF-1" },
  { id: 7, carreraId: 1, codigo: "FIS-1000", nombre: "Física I", horario: "M3 V2", sala: "C-301" },
  { id: 8, carreraId: 1, codigo: "INF-2200", nombre: "Estructuras de Datos", horario: "L4 W4", sala: "LINF-2" },
  { id: 9, carreraId: 1, codigo: "INF-2201", nombre: "Arquitectura de Computadores", horario: "M4 J4", sala: "D-101" },
  { id: 10, carreraId: 1, codigo: "INF-3321", nombre: "Sistemas Operativos", horario: "L5 W5", sala: "LINF-3" },
  { id: 11, carreraId: 1, codigo: "INF-3410", nombre: "Redes de Computadores", horario: "M5 J5", sala: "LINF-3" },
  { id: 12, carreraId: 1, codigo: "INF-3454", nombre: "Fundamentos de Ing. de Software", horario: "V4 V5", sala: "D-203" },
  { id: 13, carreraId: 2, codigo: "IND-1001", nombre: "Introducción a la Ind.", horario: "M2 J2", sala: "EAO-101" },
  { id: 14, carreraId: 2, codigo: "IND-1002", nombre: "Economía y Mercado", horario: "V3 V4", sala: "EAO-102" }
]);

onMounted(() => {
  // Cargamos si ya existen cambios previos guardados
  const storedCursos = JSON.parse(localStorage.getItem('cursos_globales'));
  if (storedCursos && storedCursos.length > 0) {
    cursos.value = storedCursos;
  }
});

const guardarAsignaciones = () => {
  // 1. Guardamos la matriz principal
  localStorage.setItem('cursos_globales', JSON.stringify(cursos.value));

  // 2. Propagamos los cambios a los ALUMNOS matriculados
  const matriculas = JSON.parse(localStorage.getItem('matriculas_admin') || '{}');
  for (let rut in matriculas) {
    if (matriculas[rut].cursos) {
      matriculas[rut].cursos = matriculas[rut].cursos.map(cAsignado => {
        const cursoActualizado = cursos.value.find(c => c.id === cAsignado.id);
        return cursoActualizado 
          ? { ...cAsignado, sala: cursoActualizado.sala, horario: cursoActualizado.horario } 
          : cAsignado;
      });
    }
  }
  localStorage.setItem('matriculas_admin', JSON.stringify(matriculas));

  // 3. Propagamos los cambios a los PROFESORES
  const profes = JSON.parse(localStorage.getItem('profesor_admin') || '{}');
  for (let rut in profes) {
    profes[rut] = profes[rut].map(cAsignado => {
      const cursoActualizado = cursos.value.find(c => c.id === cAsignado.id);
      return cursoActualizado 
        ? { ...cAsignado, sala: cursoActualizado.sala, horario: cursoActualizado.horario } 
        : cAsignado;
    });
  }
  localStorage.setItem('profesor_admin', JSON.stringify(profes));

  alert("🏫 ¡Salas y horarios actualizados! Los cambios ya se reflejan en las cuentas de alumnos y profesores.");
};
</script>

<template>
  <div class="dashboard">
    <header class="header">
      <div>
        <h1>Gestión de Infraestructura Académica</h1>
        <p class="subtitle">Asigna salas y horarios a las asignaturas impartidas.</p>
      </div>
      <button class="back-btn" @click="router.push('/admin')">⬅ Volver al Panel</button>
    </header>

    <main class="content">
      <div class="info-banner">
        💡 <strong>Nota del sistema:</strong> Al modificar y guardar, las grillas de horario de profesores y estudiantes se actualizarán automáticamente. Utiliza el formato de módulos (Ej: L1 M1) para los horarios.
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Asignatura</th>
              <th>Horario (Módulos)</th>
              <th>Sala Asignada</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="curso in cursos" :key="curso.id">
              <td><span class="badge-codigo">{{ curso.codigo }}</span></td>
              <td><strong>{{ curso.nombre }}</strong></td>
              <td>
                <input type="text" v-model="curso.horario" class="input-field" placeholder="Ej: L1 M2" />
              </td>
              <td>
                <input type="text" v-model="curso.sala" class="input-field sala" placeholder="Ej: EAO-101" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="actions-footer">
        <button @click="guardarAsignaciones" class="save-btn">
          💾 Guardar Asignaciones Globales
        </button>
      </div>
    </main>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; background-color: #f4f6f8; min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }

.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; background: white; padding: 20px 30px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border-left: 6px solid #16a085; }
.header h1 { color: #2c3e50; margin: 0; font-size: 1.6rem; }
.subtitle { margin: 5px 0 0 0; color: #7f8c8d; font-size: 0.95rem; }
.back-btn { background: #34495e; color: white; border: none; padding: 0.8rem 1.5rem; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; }
.back-btn:hover { background: #2c3e50; }

.info-banner { background-color: #e8f8f5; border: 1px solid #a3e4d7; color: #117a65; padding: 15px; border-radius: 8px; margin-bottom: 20px; font-size: 0.95rem; }

.table-container { background: white; border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
.data-table th { background-color: #2c3e50; color: white; font-weight: 600; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 0.5px; }
.data-table tr:hover { background-color: #fdfdfd; }

.badge-codigo { background: #f0f2f5; color: #34495e; padding: 4px 8px; border-radius: 4px; font-family: monospace; font-weight: bold; font-size: 0.85rem; border: 1px solid #ddd; }
.input-field { padding: 8px 12px; border: 1px solid #ced4da; border-radius: 6px; font-size: 0.95rem; transition: 0.2s; width: 100%; max-width: 180px; }
.input-field:focus { outline: none; border-color: #16a085; box-shadow: 0 0 0 2px rgba(22, 160, 133, 0.2); }
.input-field.sala { font-weight: bold; color: #16a085; text-align: center; }

.actions-footer { margin-top: 25px; display: flex; justify-content: flex-end; }
.save-btn { background: #16a085; color: white; border: none; padding: 1rem 2rem; border-radius: 8px; font-weight: bold; font-size: 1.1rem; cursor: pointer; transition: 0.2s; box-shadow: 0 4px 10px rgba(22, 160, 133, 0.3); }
.save-btn:hover { background: #117a65; transform: translateY(-2px); }
</style>