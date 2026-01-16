import { createApp } from 'vue'
import './style.css' // Si tienes estilos base de Vite, si no, puedes borrar esta línea
import App from './App.vue'
import router from './router' // Importamos el router que creamos en el paso anterior

const app = createApp(App)

// IMPORTANTE: Decirle a la app que use el router antes de montarse
app.use(router)

app.mount('#app')