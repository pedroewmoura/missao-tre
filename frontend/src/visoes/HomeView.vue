<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { type Zona, zonaService } from "@/services/zonaService.ts"
import { type Municipio, municipioService } from "@/services/municipioService.ts"
import { type Secao, secaoService } from "@/services/secaoService.ts"
import { type Polo, poloService } from "@/services/poloService.ts"


const todasZonas = ref<Zona[]>([])
const todosMunicipios = ref<Municipio[]>([])
const todasSecoes = ref<Secao[]>([])
const todosPolos = ref<Polo[]>([])

const carregando = ref(true)
const erro = ref<string | null>(null)

// Carrega os 4 serviços em paralelo ao abrir a tela
onMounted(async () => {
  try {
    carregando.value = true
    erro.value = null

    const [zonas, municipios, secoes, polos] = await Promise.all([
      zonaService.listarTodos(),
      municipioService.listarTodos(),
      secaoService.listarTodos(),
      poloService.listarTodos(),
    ])

    todasZonas.value = zonas || []
    todosMunicipios.value = municipios || []
    todasSecoes.value = secoes || []
    todosPolos.value = polos || []
  } catch (err: any) {
    erro.value = "Erro ao carregar dados do painel: " + (err.message || "Erro desconhecido")
    console.error(err)
  } finally {
    carregando.value = false
  }
})






</script>

<template>
  <main class="page-container">
    <header class="header-section">
      <h1>Bem-vindos ao Sistema de Zonas Eleitorais</h1>
      <p> selecione uma das opções  
        do menu para consultar as informações
        das zonas, municípios, seções, polos ou cadastrar usuários.
     </p>
    </header>




    <div v-if="erro" class="alert-erro">{{ erro }}</div>

    <div class="cards-grid">
      <section class="card">
        <h2>Zonas</h2>
        <div class="card-conteudo">
          <span v-if="carregando" class="spinner-texto">Carregando...</span>
          <span v-else class="numero-destaque">{{ todasZonas.length }}</span>
          <p class="legenda">Zonas Eleitorais cadastradas</p>
        </div>
      </section>

      <section class="card">
        <h2>Municípios</h2>
        <div class="card-conteudo">
          <span v-if="carregando" class="spinner-texto">Carregando...</span>
          <span v-else class="numero-destaque">{{ todosMunicipios.length }}</span>
          <p class="legenda">Municípios atendidos</p>
        </div>
      </section>

      <section class="card">
        <h2>Seções</h2>
        <div class="card-conteudo">
          <span v-if="carregando" class="spinner-texto">Carregando...</span>
          <span v-else class="numero-destaque">{{ todasSecoes.length.toLocaleString('pt-BR') }}</span>
          <p class="legenda">Seções ativas</p>
        </div>
      </section>

      <section class="card">
        <h2>Polos</h2>
        <div class="card-conteudo">
          <span v-if="carregando" class="spinner-texto">Carregando...</span>
          <span v-else class="numero-destaque">{{ todosPolos.length }}</span>
          <p class="legenda">Polos administrativos</p>
        </div>
      </section>
    </div>





  </main>
</template>

<style scoped>
.page-container {
  background-color: #859dbb;   
  width: 100%;
  max-width: 900px;
  margin: 5em auto 0 auto;
  padding: 5rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  border-radius: 8px;
}

.header-section {
  text-align: center;
  background-color: aliceblue;
  border-radius: 8px;
  margin-top: 5em;
  margin-bottom: 5em;
}

.header-section h1 {
  margin-top: 0.5rem;  
  margin-bottom: 0.5rem;
  color: #1c2f50;
}

.header-section p {
  padding: 1.5em;  
  margin-bottom: 0.5rem;
  color: #1c2f50;
  font-weight: 500;
}

.card {
  background-color: aliceblue;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.card h2 {
  font-size: 1.25rem; 
  margin-bottom: 1rem;
  color: #1c2f50;
  text-align: center;
}


.cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.card-conteudo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 0;
}

.numero-destaque {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1c2f50;
  line-height: 1;
}

.legenda {
  margin: 0;
  font-size: 0.9rem;
  color: #556987;
  text-align: center;
}

.spinner-texto {
  font-size: 1rem;
  color: #556987;
  font-style: italic;
}

.alert-erro {
  background-color: #fee2e2;
  color: #b91c1c;
  padding: 1rem;
  border-radius: 6px;
  text-align: center;
  font-weight: 600;
}

@media (max-width: 600px) {
  .page-container {
    padding: 0; 
    margin-top: 4.5rem;
    margin-bottom: 0.5rem;
  
  }

  .card {
    padding: 1rem;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }

  
}
</style>