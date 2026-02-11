import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

// Importamos las vistas existentes
import AdminHome from '../views/AdminHome.vue'
import GestorHome from '../views/GestorHome.vue'
import OperadorHome from '../views/OperadorHome.vue'
import UsuarioHome from '../views/UsuarioHome.vue'

// 1. IMPORTAMOS LA NUEVA VISTA DE GESTIÓN DE PIUS
import AdminPius from '../views/AdminPius.vue' // <-- NUEVO

const routes = [
  {
    path: '/',
    name: 'login',
    component: LoginView
  },
  // --- Rutas de Administrador ---
  {
    path: '/admin',
    name: 'admin',
    component: AdminHome // Este es el "Lobby" o Menú Principal
  },
  {
    path: '/admin/pius',   // <-- NUEVO: La URL para gestionar dispositivos
    name: 'admin-pius',
    component: AdminPius   // <-- NUEVO: El componente con la tabla
  },
  // -----------------------------
  {
    path: '/gestor',
    name: 'gestor',
    component: GestorHome
  },
  {
    path: '/operador',
    name: 'operador',
    component: OperadorHome
  },
  {
    path: '/home',
    name: 'home',
    component: UsuarioHome 
  }
]

const router = createRouter({
  history: createWebHistory(), 
  routes
})

export default router