<script setup lang="ts">
import { ref } from 'vue'
import { poloService, type Polo } from '@/services/poloService'
import { municipioService, type Municipio } from '@/services/municipioService'

const numeroPolo = ref<number | null>(null)
const poloEncontrado = ref<Polo | null>(null)
const municipiosDoPolo = ref<Municipio[]>([])
const carregando = ref(false)
const erro = ref<string | null>(null)

const pesquisar = async () => {
  if (numeroPolo.value === null || numeroPolo.value === undefined) {
    erro.value = 'Insira um número de polo válido.'
    return
  }

  carregando.value = true
  erro.value = null
  poloEncontrado.value = null
  municipiosDoPolo.value = []

  try {
    const todosPolos = await poloService.listarTodos()
    const encontrado = todosPolos.find((p: Polo) => p.numero === Number(numeroPolo.value))

    if (encontrado) {
      poloEncontrado.value = encontrado

      // Busca todos os municípios associados a este polo
      const todosMunicipios = await municipioService.listarTodos()
      municipiosDoPolo.value = todosMunicipios.filter((m: Municipio) =>
          m.poloNumero === encontrado.numero ||
          (m.polo && m.polo.numero === encontrado.numero)
      )
    } else {
      erro.value = 'Polo não encontrado.'
    }
  } catch (err: any) {
    erro.value = 'Erro ao consultar polo: ' + (err.message || 'Erro desconhecido.')
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <main class="page-container">

    <header class="header-section">
      <h1>Consulta de Polos</h1>
    </header>

    <section class="card-entrada-usuario card">
      <div class="form-group">
        <label for="input-polo">Número do Polo</label>
        
        <div class="input-with-button">
          <input 
            id="input-polo" 
            v-model.number="numeroPolo" 
            type="number" 
            placeholder="Ex: 01"
            @keyup.enter="pesquisar"
          >
          <button type="button" class="btn-pesquisar" @click="pesquisar" :disabled="carregando">
            {{ carregando ? 'Buscando...' : 'Pesquisar' }}
          </button>
        </div>
      </div>
    </section>

    <section class="card-resultado-requisicao card">
      <h2>Resultado da Consulta</h2>
      <div class="box-resultado"> 
        <p v-if="carregando" class="text-info">Buscando...</p>
        <p v-else-if="erro" class="text-error" style="color: #b91c1c;">{{ erro }}</p>
        <div v-else-if="poloEncontrado">
          <p><strong>Número do Polo:</strong> {{ poloEncontrado.numero }}</p>
          <p><strong>Código Município Sede:</strong> {{ poloEncontrado.municipioSede }}</p>
        </div>
        <p v-else class="text-muted">[ Nenhum resultado ]</p>  
      </div>
    </section>

    <div class="cards-grid">
      <section class="card">
        <h2>Municípios do Polo</h2>
        <div class="box-conteudo">
          <p v-if="!poloEncontrado" class="text-muted">Pesquise um polo para ver os municípios.</p>
          <ul v-else-if="municipiosDoPolo.length > 0" class="lista-dados">
            <li v-for="mun in municipiosDoPolo" :key="mun.codTse">
              <strong>{{ mun.nome }}</strong> (Cód TSE: {{ mun.codTse }})
            </li>
          </ul>
          <p v-else class="text-muted">[ Nenhum município associado a este polo ]</p>
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
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  border-radius: 8px;
}

.header-section {
  text-align: center;
  margin-top: 5em;
}

.header-section h1 {
  margin-bottom: 0.5rem;
  color: #1c2f50;
}

.card {
  background-color: #c5d7f5;
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

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #1c2f50;
}

.input-with-button {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}

.input-with-button input {
  flex: 1;
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  -moz-appearance: textfield;
  appearance: textfield;
}

.input-with-button input::-webkit-outer-spin-button,
.input-with-button input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.btn-pesquisar {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  background-color: #1c2f50;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
  white-space: nowrap;
}

.btn-pesquisar:hover {
  background-color: #122038;
}

.btn-pesquisar:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.box-resultado {
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
}

.box-conteudo {
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  min-height: 80px;
  max-height: 300px;
  overflow-y: auto;
  color: #1c2f50;
}

.lista-dados {
  list-style-type: disc;
  padding-left: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.text-muted {
  color: #64748b;
  font-style: italic;
  text-align: center;
  margin: auto 0;
}

.cards-grid {
  display: grid;
  grid-template-columns:  1fr;
  gap: 1.5rem;
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

  .input-with-button {
    flex-direction: column;
  }
}
</style>
