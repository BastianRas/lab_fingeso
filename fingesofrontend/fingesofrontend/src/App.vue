<script setup>
// En Vue 3 con <script setup>, no necesitamos importar RouterView explícitamente
// si el router está bien configurado en el main.js, pero el componente lo usa internamente.
</script>

<template>
  <!-- 
    El RouterView es dinámico. 
    Si la ruta es '/', cargará LoginView.vue.
  -->
  <RouterView />
</template>

<style>
/* Estilos Globales de la App 
  (Estos estilos aplicarán a todas las vistas)
*/

:root {
  font-family: Inter, system-ui, Avenir, Helvetica, Arial, sans-serif;
  line-height: 1.5;
  font-weight: 400;

  /* Esquema de colores base */
  --color-primary: #42b883;
  --color-bg: #f4f4f4;
  --color-text: #213547;
}

body {
  margin: 0;
  display: flex;
  place-items: center;
  min-width: 320px;
  min-height: 100vh;
  background-color: var(--color-bg);
  color: var(--color-text);
}

#app {
  width: 100%;
  margin: 0 auto;
  text-align: center;
}
</style>