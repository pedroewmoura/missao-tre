
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { type Usuario, usuarioService } from '@/services/usuarioService'

const form = ref<Usuario>({
  cpf: '',
  nome: '',
  email: '',
})

const usuarios = ref<Usuario[]>([])
const carregando = ref(false)
const erro = ref<string | null>(null)
const sucessoMsg = ref<string | null>(null)

const carregarUsuarios = async () => {
  carregando.value = true
  erro.value = null

  try {
    usuarios.value = await usuarioService.listarTodos()
  } catch (err: any) {
    erro.value = err.message || 'Erro ao carregar lista de usuários.'
  } finally {
    carregando.value = false
  }
}

const formatarCpf = (event: Event) => {
  const input = event.target as HTMLInputElement
  let valor = input.value.replace(/\D/g, '').slice(0, 11)

  if (valor.length > 9) {
    valor = valor.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2}).*/, '$1.$2.$3-$4')
  } else if (valor.length > 6) {
    valor = valor.replace(/^(\d{3})(\d{3})(\d{1,3}).*/, '$1.$2.$3')
  } else if (valor.length > 3) {
    valor = valor.replace(/^(\d{3})(\d{1,3}).*/, '$1.$2')
  }

  input.value = valor
  form.value.cpf = valor
}

const cadastrarUsuario = async () => {
  if (!form.value.cpf || !form.value.nome || !form.value.email) return

  carregando.value = true
  erro.value = null
  sucessoMsg.value = null

  try {
    await usuarioService.cadastrar(form.value)
    form.value = { cpf: '', nome: '', email: '' }
    sucessoMsg.value = 'Usuário cadastrado com sucesso!'
    await carregarUsuarios()
  } catch (err: any) {
    erro.value = err.message || 'Erro ao cadastrar usuário.'
  } finally {
    carregando.value = false
  }
}

onMounted(carregarUsuarios)
</script>

<template>
  <main class="page-container">
    <header class="header-section">
      <h1>Cadastro de Usuário</h1>
    </header>

    <!-- MENSAGENS DE FEEDBACK -->
    <div v-if="erro" class="alert error">{{ erro }}</div>
    <div v-if="sucessoMsg" class="alert success">{{ sucessoMsg }}</div>

    <!-- FORMULÁRIO DE CADASTRO -->
    <section class="card">
      <form @submit.prevent="cadastrarUsuario" class="form-grid">
        <div class="form-group col-cpf">
          <label for="cpf">CPF *</label>
          <input
            id="cpf"
            v-model="form.cpf"
            type="text"
            placeholder="000.000.000-00"
            required
            @input="formatarCpf"
          >
        </div>

        <div class="form-group col-nome">
          <label for="nome">Nome *</label>
          <input
            id="nome"
            v-model="form.nome"
            type="text"
            placeholder="Nome completo"
            required
          >
        </div>

        <div class="form-group col-email">
          <label for="email">Email *</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="usuario@exemplo.com"
            required
          >
        </div>

        <div class="col-btn">
          <button type="submit" class="btn-cadastrar" :disabled="carregando">
            {{ carregando ? 'Cadastrando...' : 'Cadastrar' }}
          </button>
        </div>
      </form>
    </section>

    <!-- LISTAGEM DOS USUÁRIOS -->
    <section class="card card-tabela">
      <h2 class="tabela-titulo">Usuários Cadastrados</h2>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>CPF</th>
              <th>Nome</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="carregando && usuarios.length === 0">
              <td colspan="3" class="text-center">A carregar usuários...</td>
            </tr>
            <tr v-else-if="usuarios.length === 0">
              <td colspan="3" class="text-center">[ Nenhum usuário cadastrado ]</td>
            </tr>
            <tr v-for="user in usuarios" :key="user.cpf">
              <td>{{ user.cpf }}</td>
              <td>{{ user.nome }}</td>
              <td>{{ user.email }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
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
  gap: 1.5rem;
  border-radius: 8px;
}

.header-section {
  text-align: center;
}

.header-section h1 {
  margin-bottom: 0.5rem;
  color: #1c2f50;
}

.alert {
  padding: 0.75rem 1rem;
  border-radius: 6px;
  font-weight: 600;
  text-align: center;
}

.alert.error {
  background-color: #fee2e2;
  color: #b91c1c;
}

.alert.success {
  background-color: #d1fae5;
  color: #065f46;
}

.card {
  background-color: #c5d7f5;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

/* GRID DO FORMULÁRIO */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
}

.col-cpf {
  grid-column: span 1;
}

.col-nome {
  grid-column: span 1;
}

.col-email {
  grid-column: span 2;
}

.col-btn {
  grid-column: span 2;
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

.form-group input {
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  background-color: #ffffff;
  color: #1c2f50;
}

.btn-cadastrar {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  background-color: #1c2f50;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.btn-cadastrar:hover {
  background-color: #122038;
}

.btn-cadastrar:disabled {
  background-color: #5c6b82;
  cursor: not-allowed;
}

/* TABELA */
.tabela-titulo {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: #1c2f50;
}

.card-tabela {
  padding: 1.5rem;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table th,
.data-table td {
  padding: 0.85rem 1rem;
  border-bottom: 1px solid rgba(28, 47, 80, 0.1);
  color: #1c2f50;
}

.data-table th {
  background-color: rgba(255, 255, 255, 0.4);
  font-weight: 700;
}

.text-center {
  text-align: center;
}

@media (max-width: 600px) {
  .page-container {
    padding: 1rem;
    margin-top: 2em;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .col-cpf,
  .col-nome,
  .col-email,
  .col-btn {
    grid-column: span 1;
  }

  .btn-cadastrar {
    width: 100%;
  }
}
</style>