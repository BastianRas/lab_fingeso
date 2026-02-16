import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

// Vistas de Roles
import AlumnoView from '../views/AlumnoView.vue'
import ProfesorView from '../views/ProfesorView.vue'
import FuncionarioView from '../views/FuncionarioView.vue'

// Vistas de Admin y otros
import AdminHome from '../views/AdminHome.vue'
import AdminPius from '../views/AdminPius.vue'
import GestorHome from '../views/GestorHome.vue'
import OperadorHome from '../views/OperadorHome.vue'

// Componentes funcionales (Mapa y Cartelera)
// Nota: MapaCampus lo importamos como vista para que tenga su propia página
import MapaCampus from '../components/MapaCampus.vue' 
import CarteleraView from '../views/CarteleraView.vue'

const routes = [
  { path: '/', name: 'login', component: LoginView },

  // --- RUTAS DE ROLES ---
  { path: '/alumno', name: 'alumno', component: AlumnoView },
  { path: '/profesor', name: 'profesor', component: ProfesorView },
  { path: '/funcionario', name: 'funcionario', component: FuncionarioView },

  // --- HERRAMIENTAS COMUNES ---
  { path: '/mapa', name: 'mapa', component: MapaCampus }, // Nueva ruta del mapa
  { path: '/cartelera', name: 'cartelera', component: CarteleraView },

  // --- RUTAS ADMINISTRATIVAS ---
  { path: '/admin', name: 'admin', component: AdminHome },
  { path: '/admin/pius', name: 'admin-pius', component: AdminPius },
  { path: '/gestor', name: 'gestor', component: GestorHome },
  { path: '/operador', name: 'operador', component: OperadorHome },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router