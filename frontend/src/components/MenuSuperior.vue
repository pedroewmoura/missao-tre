<script setup lang="ts">


// Interface para garantir tipagem forte no TypeScript
interface NavItem {
  label: string
  path: string
}

// Lista de rotas centralizada
const navItems: NavItem[] = [
  { label: 'Home', path: '/' },
  { label: 'Zonas', path: '/zonas' },
  { label: 'Municípios', path: '/municipios' },
  { label: 'Polos', path: '/polos' },
  { label: 'Seções', path: '/secoes' },
  { label: 'Usuários', path: '/usuarios' }
]
</script>

<template>
  <header class="menu-superior">
    <nav aria-label="Navegação Principal" class="menu-nav">
      <RouterLink
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-link"
        active-class="nav-link--active"
      >
        {{ item.label }}
      </RouterLink>
    </nav>
  </header>
</template>

<style scoped>
.menu-superior {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background-color: #1c2f50; /* Azul moderno com alto contraste */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15); /* Sombra suave */
  backdrop-filter: blur(8px); /* Efeito de desfoque estilo Glassmorphism */

}


.menu-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
  padding: 0.85rem 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
  overflow-x: auto; /* Permite rolagem horizontal em telas pequenas sem quebrar o layout */
}

/* O estilo do link deve ir direto na classe do RouterLink (que gera a tag <a>) */
.nav-link {
  color: #ffffff;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.95rem;
  padding: 0.5rem 0.85rem;
  border-radius: 6px;
  transition: all 0.2s ease-in-out;
  white-space: nowrap; /* Evita quebra de linha do texto no mobile */
}

/* Hover e Foco para Acessibilidade */
.nav-link:hover,
.nav-link:focus-visible {
  background-color: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  outline: none;
}

/* Estado de Link Ativo (Página Atual) */
.nav-link--active {
  background-color: rgba(255, 255, 255, 0.25);
  font-weight: 600;
}

@media (max-width: 600px) {     /*Mantem a responsividade no mobile */
  .menu-nav {
    justify-content: flex-start;
    padding: 0.75rem 1rem;
    gap: 0.75rem;
  }

}  
</style>