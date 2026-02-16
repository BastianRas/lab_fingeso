import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

// 1. IMPORTAMOS LAS VISTAS ESPECÍFICAS
// Nota: Asegúrate de crear los archivos ProfesorView.vue y FuncionarioView.vue 
// en la carpeta views para que esto no te de error.
import AlumnoView from '../views/AlumnoView.vue'      // Antes le llamabas UsuarioHome
import ProfesorView from '../views/ProfesorView.vue'  // NUEVA
import FuncionarioView from '../views/FuncionarioView.vue' // NUEVA

import AdminHome from '../views/AdminHome.vue'
import AdminPius from '../views/AdminPius.vue'
import GestorHome from '../views/GestorHome.vue'
import OperadorHome from '../views/OperadorHome.vue'
import CarteleraView from '../views/CarteleraView.vue' 

const routes = [
  { path: '/', name: 'login', component: LoginView },
  
  // --- RUTAS DE ROLES DE USUARIO ---
  
  // 2. Ruta para Alumnos (Antes era /home)
  { 
    path: '/alumno', 
    name: 'alumno', 
    component: AlumnoView 
  },

  // 3. Ruta para Profesores (NUEVA)
  { 
    path: '/profesor', 
    name: 'profesor', 
    component: ProfesorView 
  },

  // 4. Ruta para Funcionarios (NUEVA)
  { 
    path: '/funcionario', 
    name: 'funcionario', 
    component: FuncionarioView 
  },

  // --- OTRAS RUTAS ---

  // Ruta solo para ver la cartelera (opcional, si quieres verla pantalla completa)
  { path: '/cartelera', name: 'cartelera', component: CarteleraView },

  // Rutas de Admin
  { path: '/admin', name: 'admin', component: AdminHome },
  { path: '/admin/pius', name: 'admin-pius', component: AdminPius },

  // Rutas de otros roles
  { path: '/gestor', name: 'gestor', component: GestorHome },
  { path: '/operador', name: 'operador', component: OperadorHome },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router