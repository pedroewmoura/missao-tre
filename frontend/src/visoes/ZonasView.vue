<script setup lang="ts">
import {computed, ref} from 'vue'
import { type Zona, zonaService } from "@/services/zonaService"
import { type Municipio, municipioService } from "@/services/municipioService"
import { type Secao, secaoService} from "@/services/secaoService";

const numeroZona = ref<number | null>(null)
const zonaEncontrada = ref<(Zona | null)>(null)
const municipiosZona = ref<(Municipio[])>([])
const secoesZona = ref<(Secao[])>([])

const carregando = ref(false)
const erro = ref<string | null>(null)

// --- Paginação de Municípios ---
const paginaAtualMunicipios = ref(1)
const itensPorPaginaMunicipios = ref(5)
const opcoesPaginacao = [5, 10, 20, 50]

const totalPaginasMunicipios = computed(() => {
  return Math.ceil(municipiosZona.value.length / itensPorPaginaMunicipios.value) || 1
})

const municipiosPaginados = computed(() => {
  const inicio = (paginaAtualMunicipios.value - 1) * itensPorPaginaMunicipios.value
  const fim = inicio + itensPorPaginaMunicipios.value
  return municipiosZona.value.slice(inicio, fim)
})

// --- Paginação de Seções ---
const paginaAtualSecoes = ref(1)
const itensPorPaginaSecoes = ref(10)

const totalPaginasSecoes = computed(() => {
  return Math.ceil(secoesZona.value.length / itensPorPaginaSecoes.value) || 1
})

const secoesPaginadas = computed(() => {
  const inicio = (paginaAtualSecoes.value - 1) * itensPorPaginaSecoes.value
  const fim = inicio + itensPorPaginaSecoes.value
  return secoesZona.value.slice(inicio, fim)
})

const pesquisar = async () => {
  if (!numeroZona.value){
    erro.value = "Insira um número de zona válido."
    return
  }
  carregando.value = true
  erro.value = null
  zonaEncontrada.value= null
  municipiosZona.value=[]
  secoesZona.value=[]

  // Reseta as páginas ao realizar uma nova pesquisa
  paginaAtualMunicipios.value = 1
  paginaAtualSecoes.value = 1

  try {
    const zonas = await zonaService.listarTodos()
    const encontrada = zonas.find((z: Zona) => z.numero === numeroZona.value)

    if (encontrada){
      zonaEncontrada.value = encontrada

      const todosMunicipios = await municipioService.listarTodos()
      const codigosMunicipiosZona = encontrada.municipiosCodTse || []

      if (codigosMunicipiosZona && codigosMunicipiosZona.length > 0) {
        municipiosZona.value = todosMunicipios.filter((m: Municipio) => codigosMunicipiosZona.includes(m.codTse))
      } else {
        municipiosZona.value = todosMunicipios.filter((m: any) =>
            String(m.codTse) === String(encontrada.municipioSede) ||
            String(m.zonaNumero) == String(numeroZona.value)
        )
      }

      const todasSecoes = await secaoService.listarTodos()
      secoesZona.value = todasSecoes.filter((s: any) =>
          String(s.zonaNumero) === String(numeroZona.value) ||
          String(s.zonaNumero) === String(encontrada.numero)
      )

    } else {
      erro.value = "Zona não encontrada."
    }
  } catch (err: any) {
    erro.value = "Erro ao buscar zona:" + (err.message || "Erro desconhecido")
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <main class="page-container">

    <header class="header-section">
      <h1>Consulta de Zonas</h1>
    </header>

    <section class="card">
      <div class="form-group">
        <label for="input-zona">Número da zona</label>

        <div class="input-with-button">
          <input
              id="input-zona"
              v-model="numeroZona"
              type="number"
              placeholder="Ex: 000"
          >
          <button type="button" class="btn-pesquisar" @click="pesquisar">
            Pesquisar
          </button>
        </div>
      </div>
    </section>

    <section class="card">
      <h2>Resultado da Consulta</h2>
      <div class="box-resultado">
        <p v-if="carregando" class="text-info">Buscando zona: {{ numeroZona }} ...</p>
        <p v-else-if="erro" class="text-info" style="color: #b91c1c;" >{{erro}}</p>
        <div v-else-if="zonaEncontrada">
          <p><strong>Número:</strong> {{zonaEncontrada.numero}}</p>
          <p><strong>Município Sede:</strong> {{zonaEncontrada.municipioSede}}</p>
        </div>
        <p v-else>[ Nenhum resultado ]</p>
      </div>
    </section>

    <div class="cards-grid">
      <!-- Card de Municípios com Tabela e Paginação -->
      <section class="card">
        <h2>Municípios da Zona</h2>
        <div class="box-resultado">
          <p v-if="!zonaEncontrada" class="text-muted">Pesquise uma zona para ver os municípios associados.</p>

          <template v-else-if="municipiosZona.length > 0">
            <div class="controles-topo">
              <span class="info-total">Total: {{ municipiosZona.length }}</span>
              <div class="seletor-paginacao-grupo">
                <label>Exibir:</label>
                <select v-model.number="itensPorPaginaMunicipios" @change="paginaAtualMunicipios = 1" class="select-itens">
                  <option v-for="op in opcoesPaginacao" :key="op" :value="op">{{ op }} / pág</option>
                </select>
              </div>
            </div>

            <table class="data-table">
              <thead>
              <tr>
                <th>Município</th>
                <th>Código TSE</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="municipio in municipiosPaginados" :key="municipio.codTse">
                <td>{{ municipio.nome }}</td>
                <td>{{ municipio.codTse }}</td>
              </tr>
              </tbody>
            </table>

            <div class="pagination-container">
              <span class="info-pagina-atual">Página {{ paginaAtualMunicipios }} de {{ totalPaginasMunicipios }}</span>
              <div class="botoes-paginacao">
                <button type="button" class="btn-page" :disabled="paginaAtualMunicipios === 1" @click="paginaAtualMunicipios--">&lt;</button>
                <button type="button" v-for="p in totalPaginasMunicipios" :key="p" class="btn-page" :class="{ active: paginaAtualMunicipios === p }" @click="paginaAtualMunicipios = p">{{ p }}</button>
                <button type="button" class="btn-page" :disabled="paginaAtualMunicipios >= totalPaginasMunicipios" @click="paginaAtualMunicipios++">&gt;</button>
              </div>
            </div>
          </template>

          <p v-else class="text-muted">[ Nenhum município encontrado para esta zona ]</p>
        </div>
      </section>

      <!-- Card de Seções com Tabela e Paginação -->
      <section class="card">
        <h2>Seções da Zona</h2>
        <div class="box-resultado">
          <p v-if="!zonaEncontrada" class="text-muted">Pesquise uma zona para ver as seções associadas.</p>

          <template v-else-if="secoesZona.length > 0">
            <div class="controles-topo">
              <span class="info-total">Total: {{ secoesZona.length }}</span>
              <div class="seletor-paginacao-grupo">
                <label>Exibir:</label>
                <select v-model.number="itensPorPaginaSecoes" @change="paginaAtualSecoes = 1" class="select-itens">
                  <option v-for="op in opcoesPaginacao" :key="op" :value="op">{{ op }} / pág</option>
                </select>
              </div>
            </div>

            <table class="data-table">
              <thead>
              <tr>
                <th>Número da Seção</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="secao in secoesPaginadas" :key="secao.numero">
                <td>Seção {{ secao.numero }}</td>
              </tr>
              </tbody>
            </table>

            <div class="pagination-container">
              <span class="info-pagina-atual">Página {{ paginaAtualSecoes }} de {{ totalPaginasSecoes }}</span>
              <div class="botoes-paginacao">
                <button type="button" class="btn-page" :disabled="paginaAtualSecoes === 1" @click="paginaAtualSecoes--">&lt;</button>
                <button type="button" v-for="p in totalPaginasSecoes" :key="p" class="btn-page" :class="{ active: paginaAtualSecoes === p }" @click="paginaAtualSecoes = p">{{ p }}</button>
                <button type="button" class="btn-page" :disabled="paginaAtualSecoes >= totalPaginasSecoes" @click="paginaAtualSecoes++">&gt;</button>
              </div>
            </div>
          </template>

          <p v-else class="text-muted">[ Nenhuma seção encontrada para esta zona ]</p>
        </div>
      </section>
    </div>
  </main>
</template>

<style scoped>

.page-container {
  background-color: #859dbb;
  width: 100%;
  max-width: 950px;
  margin: 5em auto 0 auto;
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  border-radius: 8px;
  box-sizing: border-box;
}

.header-section {
  text-align: center;
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
  box-sizing: border-box;
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

.box-resultado {
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
}

.cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.controles-topo {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
  color: #1c2f50;
  font-weight: 600;
}

.seletor-paginacao-grupo {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.select-itens {
  padding: 0.25rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: #fff;
  color: #1c2f50;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table th,
.data-table td {
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid rgba(28, 47, 80, 0.1);
  color: #1c2f50;
  font-size: 0.9rem;
}

.data-table th {
  background-color: #f1f5f9;
  font-weight: 700;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
  padding-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.info-pagina-atual {
  font-size: 0.8rem;
  color: #1c2f50;
  font-weight: 600;
}

.botoes-paginacao {
  display: flex;
  gap: 0.2rem;
  flex-wrap: wrap;
}

.btn-page {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  border: 1px solid #1c2f50;
  background-color: #ffffff;
  color: #1c2f50;
  border-radius: 4px;
  cursor: pointer;
}

.btn-page.active {
  background-color: #1c2f50;
  color: #ffffff;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .page-container {
    padding: 0;
    margin-top: 4.5rem;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }

  .input-with-button {
    flex-direction: column;
  }
}
</style>