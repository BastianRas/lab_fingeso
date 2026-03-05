import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'


import AlumnoView from '../views/AlumnoView.vue'
import ProfesorView from '../views/ProfesorView.vue'
import FuncionarioView from '../views/FuncionarioView.vue'


import AdminHome from '../views/AdminHome.vue'
import AdminPius from '../views/AdminPius.vue'
import AdminEventos from '../views/AdminEventos.vue'
import AdminUsuarios from '../views/AdminUsuarios.vue'
import AdminLugares from '../views/AdminLugares.vue'
import AdminSalas from '../views/AdminSalas.vue' 
import GestorHome from '../views/GestorHome.vue'
import OperadorHome from '../views/OperadorHome.vue'



import MapaCampus from '../components/MapaCampus.vue' 
import CarteleraView from '../views/CarteleraView.vue'

const routes = [
  { path: '/', name: 'Login', component: LoginView },

  
  { path: '/alumno', name: 'alumno', component: AlumnoView },
  { path: '/profesor', name: 'profesor', component: ProfesorView },
  { path: '/funcionario', name: 'funcionario', component: FuncionarioView },

  
  { path: '/mapa', name: 'mapa', component: MapaCampus }, 
  { path: '/cartelera', name: 'cartelera', component: CarteleraView },


  { path: '/admin', name: 'admin', component: AdminHome },
  { path: '/admin/pius', name: 'admin-pius', component: AdminPius },
  { path: '/admin/eventos', name: 'admin-eventos', component: AdminEventos },
  { path: '/admin/usuarios', name: 'admin-usuarios', component: AdminUsuarios },
  { path: '/admin/salas', name: 'admin-salas', component: AdminSalas }, 
  { path: '/admin/lugares', name: 'admin-lugares', component: AdminLugares },
  { path: '/gestor', name: 'gestor', component: GestorHome },
  { path: '/operador', name: 'operador', component: OperadorHome },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router