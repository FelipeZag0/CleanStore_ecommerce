# **API de Gerenciamento de Pedidos para E-commerce**

> **Resumo:** API RESTful robusta, construída em **Java 17** com **Spring Boot 3**, **arquitetura limpa** e princípios **SOLID**, projetada para gerenciar o ciclo de vida completo de pedidos em um e-commerce, com documentação interativa via **Swagger** e **testes automatizados**.

---

## 📋 **Descrição do Projeto**

Aplicação backend que implementa **CRUD de pedidos** com suporte a múltiplos itens, cálculo de valor total, atualização de status e cancelamento lógico.
Segue **boas práticas** de desenvolvimento, incluindo:

* Arquitetura modular e desacoplada.
* Padrões DTO e tratamento centralizado de exceções.
* Testes unitários e de integração.

---

## 🎯 **Contexto do Desafio**

Desenvolvido como resposta a um desafio técnico para **Desenvolvedor Backend Java**, atendendo aos requisitos funcionais e não-funcionais:

**Requisitos Funcionais:**

* \[RF01] Criar pedido com múltiplos itens.
* \[RF02] Listar todos os pedidos.
* \[RF03] Buscar pedido por ID.
* \[RF04] Atualizar status.
* \[RF05] Cancelamento lógico.

**Requisitos Não-Funcionais:**

* Arquitetura em camadas.
* Princípios SOLID e injeção de dependência.
* Persistência com Spring Data JPA.
* Documentação com Swagger UI.
* Testes com JUnit, Mockito e Testcontainers.

---

## 🛠 **Tecnologias Utilizadas**

| Categoria       | Tecnologia                       |
| --------------- | -------------------------------- |
| Linguagem       | Java 17                          |
| Framework       | Spring Boot 3.2.x                |
| Banco de Dados  | PostgreSQL                       |
| ORM             | Spring Data JPA                  |
| Documentação    | SpringDoc OpenAPI / Swagger UI   |
| Testes          | JUnit 5, Mockito, Testcontainers |
| Build           | Maven                            |
| Containerização | Docker Compose                   |
| Utilitários     | Lombok                           |

---

## 🏗 **Arquitetura e Padrões**

* **Controller:** Exposição dos endpoints REST.
* **Service:** Lógica de negócio e orquestração.
* **Repository:** Acesso a dados com Spring Data JPA.
* **Model:** Entidades JPA e enums.
* **DTOs:** Transferência de dados entre camadas.
* **Exception Handler:** Tratamento global de erros.

**Práticas aplicadas:**
✔ Princípios SOLID
✔ Injeção de dependências via construtor
✔ Validações no domínio
✔ Documentação automática da API

---

## ⚙ **Instalação e Configuração**

### Pré-requisitos

* Java 17+
* Maven 3.6+
* Docker e Docker Compose

### Passos

```bash
git clone https://github.com/FelipeZag0/desafio-CRUD_ecommerce.git
cd desafio-CRUD_ecommerce
```

```bash
docker-compose up -d  # Inicia PostgreSQL
```

```bash
mvn spring-boot:run
```

API disponível em:

* `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 📡 **Endpoints Principais**

| Método | Endpoint               | Função              |
| ------ | ---------------------- | ------------------- |
| POST   | `/pedidos`             | Criar novo pedido   |
| GET    | `/pedidos`             | Listar todos        |
| GET    | `/pedidos/{id}`        | Buscar por ID       |
| PUT    | `/pedidos/{id}/status` | Atualizar status    |
| DELETE | `/pedidos/{id}`        | Cancelamento lógico |

**Exemplo de Criação de Pedido:**

```json
{
  "clienteId": 1,
  "enderecoEntrega": "Rua das Flores, 123",
  "itens": [
    { "produtoId": 10, "descricaoProduto": "Smartphone XYZ", "quantidade": 2, "precoUnitario": 999.99 }
  ]
}
```

---

## 📁 **Estrutura de Pastas**

```
src/
├── main/java/com/ecommerce/pedidos_api/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   └── exception/
└── test/
    ├── controller/
    └── service/
```

---

## 🧪 **Testes**

* **Unitários:** Regras de negócio com JUnit e Mockito.
* **Integração:** Fluxos completos usando Testcontainers.

Executar:

```bash
mvn test
```
