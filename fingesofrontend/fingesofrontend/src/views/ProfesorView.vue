<script setup>
import CarteleraView from './CarteleraView.vue'; // Reutilizamos la cartelera
</script>

<template>
  <div class="profesor-layout">
    <h1>Panel Docente</h1>
    
    <div class="funciones-profe">
       <button>📅 Ver Horario y Sala</button>
       <button>✅ Registrar Asistencia</button>
    </div>

    <hr>
    
    <h3>Noticias USACH</h3>
    <CarteleraView /> 
  </div>
</template>