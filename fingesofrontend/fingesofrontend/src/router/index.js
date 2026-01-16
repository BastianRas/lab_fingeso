import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

// 1. IMPORTAMOS LAS NUEVAS VISTAS (Asegúrate de haber creado los archivos en /views)
import AdminHome from '../views/AdminHome.vue'
import GestorHome from '../views/GestorHome.vue'
import OperadorHome from '../views/OperadorHome.vue'
import UsuarioHome from '../views/UsuarioHome.vue'

const routes = [
  {
    path: '/',
    name: 'login',
    component: LoginView
  },
  // 2. AGREGAMOS LAS RUTAS PARA CADA ROL
  {
    path: '/admin',
    name: 'admin',
    component: AdminHome
  },
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
    component: UsuarioHome // Esta la usaremos para Alumnos, Profes y Funcionarios
  }
]

const router = createRouter({
  history: createWebHistory(), // O puedes usar: createWebHistory(import.meta.env.BASE_URL)
  routes
})

export default router