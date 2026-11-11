<script setup lang="ts">
import { ref } from 'vue'
import { type Municipio, municipioService } from "@/services/municipioService"
import { zonaService, type Zona } from "@/services/zonaService"
import { poloService, type Polo } from "@/services/poloService"

const codigoMunicipio = ref<number | null>(null)
const municipioEncontrado = ref<Municipio | null>(null)
const zonasMunicipio = ref<Zona[]>([])
const poloMunicipio = ref<Polo | null>(null)

const carregando = ref(false)
const erro = ref<string | null>(null)

const pesquisar = async () => {
  if (!codigoMunicipio.value) {
    erro.value = "Insira um código de município válido."
    return
  }

  carregando.value = true
  erro.value = null
  municipioEncontrado.value = null
  zonasMunicipio.value = []
  poloMunicipio.value = null

  // 1. Busca o município na API
  try {

    const municipios = await municipioService.listarTodos()
    const encontrado = municipios.find((m: Municipio) => m.codTse === codigoMunicipio.value)

    if (encontrado) {
      municipioEncontrado.value = encontrado

      // 2. Busca e filtra as zonas associadas
      const todasZonas = await zonaService.listarTodos()
      console.log("Todas as zonas:", todasZonas) // Log para depuração
      zonasMunicipio.value = todasZonas.filter((z: any) =>
          (z.municipiosCodTse && z.municipiosCodTse.includes(codigoMunicipio.value)) ||
          (z.municipios && z.municipios.some((m: any) => String(m.codTse) === String(codigoMunicipio.value))) ||
          String(z.municipioSede) === String(codigoMunicipio.value) ||
          String(z.municipioCodTse) === String(codigoMunicipio.value) ||
          String(z.codMunicipio) === String(codigoMunicipio.value) ||
          String(z.municipioId) === String(codigoMunicipio.value)
      )

      // 3. Busca e filtra o polo associado
      const todosPolos = await poloService.listarTodos()
      console.log("Todos os polos:", todosPolos) // Log para depuração

      const numPolo = encontrado.poloNumero || (encontrado.polo && encontrado.polo.numero)
      if (numPolo) {
        poloMunicipio.value = todosPolos.find((p: any) => p.numero === numPolo) || encontrado.polo || null
      } else {
        poloMunicipio.value = todosPolos.find((p: any) =>
            String(p.municipioSede) === String(codigoMunicipio.value) ||
            String(p.municipioCodTse) === String(codigoMunicipio.value) ||
            String(p.codMunicipio) === String(codigoMunicipio.value)
        ) || null
      }

    } else {
      erro.value = "Município não encontrado."
    }
  } catch (err: any) {
    erro.value = "Erro ao comunicar com o servidor: " + (err.message || "Erro desconhecido.")
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <main class="page-container">
    <header class="header-section">
      <h1>Consulta de Municípios</h1>
    </header>

    <section class="card-entrada-usuario card">
      <div class="form-group">
        <label for="input-municipio">Código do Município</label>
        <div class="input-with-button">
          <input
              id="input-municipio"
              v-model.number="codigoMunicipio"
              type="number"
              placeholder="Ex: 00000"
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
        <div v-else-if="municipioEncontrado">
          <p><strong>Código TSE:</strong> {{ municipioEncontrado.codTse }}</p>
          <p><strong>Nome:</strong> {{ municipioEncontrado.nome }}</p>
        </div>
        <p v-else class="text-muted">[ Nenhum resultado ]</p>
      </div>
    </section>

    <div class="cards-grid">
      <!-- CARD DE ZONAS -->
      <section class="card">
        <h2>Zonas do Município</h2>
        <div class="box-conteudo">
          <p v-if="!municipioEncontrado" class="text-muted">Pesquise um município para ver as zonas.</p>
          <ul v-else-if="zonasMunicipio.length > 0" class="lista-dados">
            <li v-for="zona in zonasMunicipio" :key="zona.numero">
              <strong>Zona {{ zona.numero }}</strong>
              <span v-if="zona.municipioSede"> - Sede: {{ zona.municipioSede }}</span>
            </li>
          </ul>
          <p v-else class="text-muted">[ Nenhuma zona encontrada para este município ]</p>
        </div>
      </section>

      <!-- CARD DE POLO -->
      <section class="card">
        <h2>Polo</h2>
        <div class="box-conteudo">
          <p v-if="!municipioEncontrado" class="text-muted">Pesquise um município para ver o polo.</p>
          <div v-else-if="poloMunicipio">
            <p><strong>Número do Polo:</strong> {{ poloMunicipio.numero }}</p>
            <p v-if="poloMunicipio.municipioSede"><strong>Município Sede:</strong> {{ poloMunicipio.municipioSede }}</p>
          </div>
          <p v-else class="text-muted">[ Nenhum polo associado ]</p>
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
  grid-template-columns: 1fr 1fr;
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
