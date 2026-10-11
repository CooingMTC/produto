# 🎥 Microsserviço de Produto (Livestream Catalog Service)

Microsserviço responsável pelo catálogo de transmissões ao vivo (lives), controle de ingressos/vagas e tratamento de concorrência com **Optimistic Locking**.

---

## 📌 Papel na Arquitetura

- **Gerenciamento de Lives:** Cadastro e consulta de salas de transmissão com capacidade máxima de participantes.
- **Validador de Vagas:** Atua como consumidor da fila `venda.solicitacao.fila`, verifica a disponibilidade de ingressos e rebate a resposta na fila `venda.resposta.fila`.
- **Tratamento de Concorrência:** Utiliza a anotação `@Version` do JPA para evitar reservas simultâneas acima do teto estipulado, além de múltiplos workers configurados no RabbitMQ.

---

## ⚙️ Pré-requisitos & Dependências

- **Java 21**
- **RabbitMQ:** Necessário para o fluxo assíncrono com o microsserviço de vendas. O serviço já está apontado para uma instância AMQP na nuvem (CloudAMQP).
- **Relacionamento com outros serviços:**
  - Pode subir de forma **independente** para cadastro e visualização de lives.
  - Para o fluxo completo de compra e reserva de vagas, deve rodar em conjunto com o [**venda-service**](https://github.com/CooingMTC/venda.git).

---

## 🚀 Como Executar

1. Navegue até a pasta do projeto:
   ```bash
   cd produto
   ```
2. Compile e execute com o Maven Wrapper:
   ```bash
   ./mvnw clean compile
   ./mvnw spring-boot:run
   ```
3. A aplicação subirá na porta **`8081`**

---

## 📡 Endpoints REST

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/lives` | Cria uma nova live com capacidade de vagas |
| `GET` | `/lives` | Lista todas as lives cadastradas |
| `GET` | `/lives/{id}` | Busca os detalhes de uma live específica |

### Exemplo de Criação de Live (`POST /lives`):
```json
{
  "titulo": "Workshop Prático de Microsserviços",
  "descricao": "Mentoria com vagas limitadas",
  "precoIngresso": 50.00,
  "capacidadeMaxima": 3
}
```

---

## 🔄 Filas do RabbitMQ Utilizadas

- **Consome:** `venda.solicitacao.fila` (Recebe pedidos de compra de ingressos)
- **Publica em:** `venda.exchange` com routing key `venda.evento.processado` (Responde se a compra foi autorizada ou se as vagas esgotaram)
