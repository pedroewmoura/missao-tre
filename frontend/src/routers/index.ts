import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../visoes/HomeView.vue'
import ZonasView from '../visoes/ZonasView.vue'
import MunicipiosView from '../visoes/MunicipiosView.vue'
import SecoesView from '../visoes/SecoesView.vue'
import PolosView from '../visoes/PolosView.vue'
import UsuariosView from '../visoes/UsuariosView.vue'



const routes = [
  {
    path: '/',
    component: HomeView
  },
  {
    path: '/zonas',
    component: ZonasView
  },
  {
    path: '/municipios',
    component: MunicipiosView
  },
  {
    path: '/secoes',
    component: SecoesView
  },
  {
    path: '/polos',
    component: PolosView
  },
  {
    path: '/usuarios',
    component: UsuariosView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router