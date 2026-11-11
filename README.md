# 🏛️ Missão TRE - Sistema de Consulta Eleitoral

Sistema full stack desenvolvido para consulta eleitoral (Polos, Zonas, Municípios e Seções Eleitorais) e gerenciamento de usuários.

---

## 🛠️ Tecnologias Utilizadas

### Backend
* **Java 21**
* **Spring Boot 3.3.0** (Spring Web, Spring Data JPA, Validation)
* **PostgreSQL**
* **Lombok**
* **Maven**

### Frontend
* **Vue 3** (Composition API / `<script setup>`)
* **TypeScript**
* **Vite**
* **Vue Router**

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java 21 JDK instalado
* PostgreSQL instalado e rodando
* Node.js (v18+) e npm

---

### 1. Configurando o Banco de Dados
Crie um banco de dados no PostgreSQL chamado `missao_tre_bd`:

CREATE DATABASE missao_tre_bd;
```

Crie o arquivo `backend/.env` (ou configure as variáveis de ambiente):
```env
DB_URL=jdbc:postgresql://localhost:5432/missao_tre_bd
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
```

*Nota: Na primeira inicialização, o sistema realiza a importação automática dos dados a partir dos arquivos CSV em `resources/csv`.*

---

### 2. Executando o Backend (Spring Boot)
No diretório `backend`:
```bash
./mvnw spring-boot:run
```
O servidor iniciará em `http://localhost:8080`.

---

### 3. Executando o Frontend (Vue.js)
No diretório `frontend`:
```bash
npm install
npm run dev
```
Acesse a aplicação no navegador em `http://localhost:5173`.

---

## 📋 Funcionalidades
* **Painel Geral (Home):** Dashboard com totalizadores em tempo real de Zonas, Municípios, Seções e Polos.
* **Consulta de Zonas:** Busca por número da zona com paginação de municípios e seções associadas.
* **Consulta de Municípios:** Busca por código TSE com indicação do polo e zonas vinculadas.
* **Consulta de Seções:** Filtro cruzado inteligente por Município e Zona com paginação e busca detalhada.
* **Consulta de Polos:** Busca por número do polo e listagem de cidades da regional.
* **Cadastro de Usuários:** Criação e listagem de operadores com validação de CPF e formulários.