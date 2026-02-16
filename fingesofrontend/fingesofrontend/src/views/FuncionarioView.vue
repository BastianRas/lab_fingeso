<script setup>
import CarteleraView from './CarteleraView.vue'; 
</script>

<template>
  <div class="funcionario-layout">
    <h1>Panel Administrativo</h1>
    
    <div class="funciones-funcionario">
       <button>📦 Solicitudes de Material</button>
       <button>📋 Inventario</button>
    </div>

    <hr>

    <h3>Noticias USACH</h3>
    <CarteleraView />
  </div>
</template>