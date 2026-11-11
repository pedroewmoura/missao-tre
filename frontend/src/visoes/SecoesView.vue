<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { type Zona, zonaService } from "@/services/zonaService.ts"
import { type Municipio, municipioService } from "@/services/municipioService.ts"
import { type Secao, secaoService } from "@/services/secaoService.ts"

// Estados dos Filtros
const municipioSelecionado = ref<any | null>("")
const zonaSelecionada = ref<any | null>("")

// Listas de Opções dos Selects
const municipiosList = ref<Municipio[]>([])
const zonasList = ref<Zona[]>([])
const secoes = ref<Secao[]>([])

// Estados de Controle
const carregando = ref(false)
const erro = ref<string | null>(null)
const pesquisou = ref(false)

// Paginação
const paginaAtual = ref(1)
const itensPorPagina = ref(10)
const opcoesPaginacao = [5, 10, 20, 50]

// 1. Carregar Municípios ao montar o componente
onMounted(async () => {
  try {
    carregando.value = true
    const resposta = await municipioService.listarTodos()
    municipiosList.value = Array.isArray(resposta) ? resposta : []
  } catch (err: any) {
    erro.value = "Erro ao carregar os municípios."
    console.error(err)
  } finally {
    carregando.value = false
  }
})

// Municípios ordenados em Ordem Alfabética
const municipiosOptions = computed(() => {
  const nomes = municipiosList.value.map((m: any) => {
    return typeof m === 'object' && m !== null ? (m.nome || m.nomeMunicipio) : m
  }).filter(Boolean)

  // Remove duplicados e ordena alfabeticamente
  return Array.from(new Set(nomes)).sort((a: any, b: any) => a.localeCompare(b, 'pt-BR'))
})
// 2. Quando o Município mudar, buscar as Zonas referentes a esse município
watch(municipioSelecionado, async (novoMunicipio) => {
  zonaSelecionada.value = ""
  zonasList.value = []

  if (!novoMunicipio) return

  try {
    carregando.value = true
    const resposta = await zonaService.listarTodos()
    const todasZonas = Array.isArray(resposta) ? resposta : []

    // Encontra o código TSE do município selecionado
    const muniObj = municipiosList.value.find((m: any) => m.nome === novoMunicipio)
    const codTse = muniObj ? muniObj.codTse : null

    // Filtra zonas que contêm esse município
    zonasList.value = todasZonas.filter((z: any) => {
      if (codTse && Array.isArray(z.municipiosCodTse)) {
        return z.municipiosCodTse.includes(codTse)
      }
      return String(z.municipioSede).trim().toLowerCase() === String(novoMunicipio).trim().toLowerCase()
    })
  } catch (err: any) {
    erro.value = "Erro ao carregar zonas do município."
    console.error(err)
  } finally {
    carregando.value = false
  }
})

// Zonas ordenadas em Ordem Numérica Crescente
const zonasOptions = computed(() => {
  const numeros = zonasList.value.map((z: any) => {
    const val = typeof z === 'object' && z !== null ? (z.numero ?? z.id ?? z.nome) : z
    return val !== undefined && val !== null ? Number(String(val).replace(/\D/g, '')) : null
  }).filter((num): num is number => num !== null && !isNaN(num))

  // Remove duplicados e ordena numericamente (1, 2, 10...)
  return Array.from(new Set(numeros)).sort((a, b) => a - b)
})

// 3. Ação do botão Pesquisar
const pesquisar = async () => {
  try {
    carregando.value = true
    erro.value = null
    pesquisou.value = true
    paginaAtual.value = 1

    const resposta: any = await secaoService.listarTodos()
    const todasSecoes = Array.isArray(resposta) ? resposta : []

    secoes.value = todasSecoes.filter((s: any) => {
      let matchMunicipio = true
      let matchZona = true

      // Filtro de Município
      if (municipioSelecionado.value) {
        const nomeSecao = String(s.municipioNome || '').trim().toLowerCase()
        const nomeFiltro = String(municipioSelecionado.value).trim().toLowerCase()
        matchMunicipio = nomeSecao === nomeFiltro
      }

      // Filtro de Zona
      if (zonaSelecionada.value) {
        matchZona = Number(s.zonaNumero) === Number(zonaSelecionada.value)
      }

      return matchMunicipio && matchZona
    })

  } catch (err: any) {
    erro.value = "Erro ao buscar as seções."
    console.error(err)
  } finally {
    carregando.value = false
  }
}
// 4. Computed properties de paginação
const totalPaginas = computed(() => {
  return Math.ceil(secoes.value.length / itensPorPagina.value) || 1
})

const secoesPaginadas = computed(() => {
  const inicio = (paginaAtual.value - 1) * itensPorPagina.value
  const fim = inicio + itensPorPagina.value
  return secoes.value.slice(inicio, fim)
})
</script>

<template>
  <main class="page-container">
    <header class="header-section">
      <h1>Lista de Seções</h1>
    </header>

    <!-- Card de Filtros -->
    <section class="card">
      <div class="filtros-grid">
        <div class="form-group">
          <label for="select-municipio">Município</label>
          <select id="select-municipio" v-model="municipioSelecionado">
            <option value="" disabled>Selecione um município</option>
            <option v-for="muni in municipiosOptions" :key="muni" :value="muni">
              {{ muni }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="select-zona">Zona</label>
          <select id="select-zona" v-model="zonaSelecionada" :disabled="!municipioSelecionado">
            <option value="" disabled>Selecione uma zona</option>
            <option v-for="zona in zonasOptions" :key="zona" :value="zona">
              Zona {{ zona }}
            </option>
          </select>
        </div>

        <button type="button" class="btn-pesquisar" @click="pesquisar">
          Pesquisar
        </button>
      </div>
    </section>

    <!-- Card da Tabela -->
    <section class="card">
      <div class="controles-topo" v-if="pesquisou">
        <span class="info-total">
          Registros encontrados: {{ secoes.length }}
        </span>

        <div class="seletor-paginacao-grupo" v-if="secoes.length > 0">
          <label for="itens-pagina">Exibir:</label>
          <select id="itens-pagina" v-model.number="itensPorPagina" @change="paginaAtual = 1" class="select-itens">
            <option v-for="opcao in opcoesPaginacao" :key="opcao" :value="opcao">
              {{ opcao }} / pág
            </option>
          </select>
        </div>
      </div>

      <div class="table-responsive">
        <div v-if="erro" class="alert-erro">{{ erro }}</div>
        <div v-else-if="carregando" class="alert-carregando">A carregar dados...</div>

        <!-- Estado inicial antes de clicar em pesquisar -->
        <div v-else-if="!pesquisou" class="estado-vazio">
          Selecione os filtros acima e clique em <strong>Pesquisar</strong> para exibir as seções.
        </div>

        <table v-else class="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Número</th>
            <th>Zona</th>
            <th>Município</th>
            <th>Polo</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="secoesPaginadas.length === 0">
            <td colspan="5" class="sem-registros">Nenhuma seção encontrada para os filtros selecionados.</td>
          </tr>

          <tr v-for="(item, index) in secoesPaginadas" :key="item?.id || index">
            <td>{{ item?.id }}</td>
            <td><a href="#" class="link-secao">{{ item?.numero }}</a></td>
            <td>Zona {{ item?.zonaNumero }}</td>
            <td>{{ item?.municipioNome || item?.municipioCodTse }}</td>
            <td>{{ item?.poloNumero ? `Polo ${item.poloNumero}` : '-' }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginação rodapé -->
      <div class="pagination-container" v-if="pesquisou && secoes.length > 0">
        <span class="info-pagina-atual">
          Página {{ paginaAtual }} de {{ totalPaginas || 1 }}
        </span>

        <div class="botoes-paginacao">
          <button type="button" class="btn-page" :disabled="paginaAtual === 1" @click="paginaAtual--">&lt;</button>
          <button type="button" v-for="pagina in totalPaginas" :key="pagina" class="btn-page" :class="{ active: paginaAtual === pagina }" @click="paginaAtual = pagina">{{ pagina }}</button>
          <button type="button" class="btn-page" :disabled="paginaAtual >= totalPaginas" @click="paginaAtual++">&gt;</button>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page-container {
  background-color: #859dbb;
  border-radius: 8px;
  width: 100%;
  max-width: 950px;
  margin: 5em auto 1rem auto;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  box-sizing: border-box;
}

.header-section {
  text-align: center;
}

.header-section h1 {
  margin: 0;
  margin-top: 4rem;
  color: #1c2f50;
  font-size: 2rem;
}

.card {
  background-color: #c5d7f5;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  width: 100%;
}

.filtros-grid {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 1rem;
  align-items: end;
  width: 100%;
  box-sizing: border-box;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  min-width: 0;
}

.form-group label {
  font-weight: 600;
  color: #1c2f50;
  font-size: 0.95rem;
}

.form-group select {
  padding: 0.65rem;
  font-size: 0.95rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  background-color: #ffffff;
  color: #1c2f50;
  width: 100%;
  box-sizing: border-box;
}

.form-group select:disabled {
  background-color: #e9ecef;
  cursor: not-allowed;
}

.btn-pesquisar {
  padding: 0.65rem 1.5rem;
  font-size: 0.95rem;
  font-weight: 600;
  color: #ffffff;
  background-color: #1c2f50;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
  white-space: nowrap;
  height: 42px;
}

.btn-pesquisar:hover {
  background-color: #122038;
}

.controles-topo {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.info-total {
  font-size: 0.9rem;
  color: #1c2f50;
  font-weight: 600;
}

.seletor-paginacao-grupo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.select-itens {
  padding: 0.3rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: #fff;
  color: #1c2f50;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
  box-sizing: border-box;
}

.data-type {
  width: 100%;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  min-width: 500px;
}

.data-table th,
.data-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid rgba(28, 47, 80, 0.1);
  color: #1c2f50;
  font-size: 0.95rem;
}

.data-table th {
  background-color: rgba(255, 255, 255, 0.4);
  font-weight: 700;
}

.link-secao {
  color: #1c2f50;
  text-decoration: underline;
  font-weight: 600;
}

.alert-erro, .alert-carregando, .estado-vazio, .sem-registros {
  text-align: center;
  padding: 2rem 1rem;
  color: #1c2f50;
  font-weight: 600;
}

.alert-erro {
  color: #a82020;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  padding: 0.75rem 1rem;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.info-pagina-atual {
  font-size: 0.85rem;
  color: #1c2f50;
  font-weight: 600;
}

.botoes-paginacao {
  display: flex;
  gap: 0.25rem;
  flex-wrap: wrap;
}

.btn-page {
  gap: 0.25rem;
  padding: 0.35rem 0.65rem;
  font-size: 0.85rem;
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
    padding: 0.75rem;
    margin-top: 4em;
  }

  .filtros-grid {
    grid-template-columns: 1fr;
  }

  .btn-pesquisar {
    width: 100%;
  }

  .pagination-container, .controles-topo {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>