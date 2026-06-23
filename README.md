
<div align="center">

# 🔥 PARANOIA ENGINE

### *Chaos Engineering Platform for Spring Boot*

[![Java](https://img.shields.io/badge/Java-21-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Toxiproxy](https://img.shields.io/badge/Toxiproxy-2.8.0-%2300ADD8?style=for-the-badge&logo=probot&logoColor=white)](https://github.com/Shopify/toxiproxy)
[![Groq](https://img.shields.io/badge/Groq-Llama--3.3--70B-%23F55036?style=for-the-badge&logo=groq&logoColor=white)](https://groq.com)
[![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)](LICENSE)

<br>

```
██████╗  █████╗ ██████╗  █████╗ ███╗   ██╗ ██████╗ ██╗ █████╗
██╔══██╗██╔══██╗██╔══██╗██╔══██╗████╗  ██║██╔═══██╗██║██╔══██╗
██████╔╝███████║██████╔╝███████║██╔██╗ ██║██║   ██║██║███████║
██╔═══╝ ██╔══██║██╔══██╗██╔══██║██║╚██╗██║██║   ██║██║██╔══██║
██║     ██║  ██║██║  ██║██║  ██║██║ ╚████║╚██████╔╝██║██║  ██║
╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝╚═╝  ╚═╝
```

### *"Your code will break. Let's find out how before your users do."*

</div>

---

## 📋 Overview

**Paranoia Engine** is a cutting-edge Chaos Engineering platform that systematically scans Java/Spring Boot applications for fragility points, generates intelligent failure scenarios using AI (Groq/Llama), and executes them against live targets via Toxiproxy fault injection.

It answers the question every developer fears: **"What happens when everything goes wrong?"**

The engine targets **Finance Control** (`localhost:9000`), a full-featured financial transaction API secured with JWT authentication, demonstrating real-world chaos engineering in action.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        PARANOIA ENGINE                              │
│                                                                     │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────────────────┐  │
│  │  JavaParser   │   │  Spring AI   │   │   Chaos Executor       │  │
│  │  Static       │──▶│  (Groq/      │──▶│                        │  │
│  │  Analysis     │   │   Llama 3.3) │   │  ┌──────────────────┐  │  │
│  │               │   │              │   │  │  ToxiproxyManager │  │  │
│  │ • @Transactional│  │ Generates   │   │  │  (Fault Injection)│  │  │
│  │ • External APIs │  │ chaos       │   │  └──────────────────┘  │  │
│  │ • Shared State  │   │ scenarios   │   │  ┌──────────────────┐  │  │
│  └──────────────┘   └──────────────┘   │  │  Concurrency      │  │  │
│                                         │  │  (Multi-thread)   │  │  │
│                                         │  └──────────────────┘  │  │
│                                         └────────────────────────┘  │
│                                           │                         │
│                                           ▼                         │
│                               ┌──────────────────────┐              │
│                               │    Finance Control    │              │
│                               │   (Target App :9000)  │              │
│                               │   JWT Auth Required   │              │
│                               └──────────────────────┘              │
│                                           │                         │
│                                           ▼                         │
│                               ┌──────────────────────┐              │
│                               │   PostgreSQL (via     │              │
│                               │   Toxiproxy :5433)    │              │
│                               └──────────────────────┘              │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

### 🔬 Static Code Analysis (JavaParser)
- **Transaction Detection** — Identifies `@Transactional` methods and analyzes rollback behavior
- **External Call Detection** — Finds `RestTemplate`, `WebClient`, `JpaRepository` usage patterns
- **Shared State Detection** — Flags mutable fields modified in public methods (race condition hotspots)
- **Snippet Extraction** — Captures contextual code around each fragility point

### 🤖 AI-Powered Scenario Generation (Spring AI + Groq)
- Uses **Llama 3.3 70B** via Groq API to generate 3 chaos scenarios per fragility point
- Intelligent scenario types: `CONCORRENCIA`, `FALHA_REDE`, `FALHA_BANCO`, `TIMEOUT`
- Estimates severity: `CRITICA`, `ALTA`, `MEDIA`, `BAIXA`
- Structured JSON output parsed into domain objects

### 💥 Chaos Execution Engine
- **Concurrency Testing** — Multi-threaded race condition simulation (20 threads)
- **Fault Injection** — Toxiproxy-based: `disable()` (connection cut), `latency()` (30s delay)
- **REST Pipeline** — Calls real finance operations (`AlvoOperacao`) and checks system health (`VerificadorEstado`)
- **Resilient Result Saving** — In-memory fallback when DB itself is under attack

### 📊 Reporting & API
- `POST /api/analise` — Start static analysis
- `POST /api/executar-tudo/{id}` — Run full pipeline (async, returns 202)
- `GET /api/executar-tudo/{id}/status` — Poll progress
- `GET /api/relatorio/{id}` — Consolidated report with severity-ordered results
- `GET /swagger-ui.html` — OpenAPI/Swagger documentation

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Runtime** | Java 21, Spring Boot 3.2.5 |
| **Static Analysis** | JavaParser 3.25.10 |
| **AI** | Spring AI 1.0.0-M1, Groq API (Llama 3.3 70B) |
| **Fault Injection** | Toxiproxy 2.8.0, toxiproxy-java 2.1.7 |
| **Database** | PostgreSQL 16, HikariCP |
| **Persistence** | Spring Data JPA, Hibernate |
| **Documentation** | SpringDoc OpenAPI 2.5.0 |
| **Containers** | Docker Compose |
| **Testing** | JUnit 5, TestContainers, H2 |

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven
- OpenAI-compatible API key (Groq)

### 1. Clone & Build
```bash
git clone https://github.com/MartinesEmanuel/ParanoiaEngine.git
cd ParanoiaEngine

# Build Paranoia Engine
cd paranoia-engine
export OPENAI_API_KEY=gsk_your_groq_key_here
mvn clean package -DskipTests
```

### 2. Start Infrastructure
```bash
cd ..
docker compose up -d
```

This starts:
- `postgres:16-alpine` (port 5432, database `paranoia_engine`)
- `finance-postgres:16-alpine` (port 5434, database `finance_control`)
- `toxiproxy:2.8.0` (API on 8474, proxied ports 5433 and 5435)
- Init container (creates proxies `postgres:5433` and `finance-db:5435`)

### 3. Start Finance Control
```bash
cd finance-control
mvn spring-boot:run
# Starts on localhost:9000
# Default credentials: admin / admin123
```

### 4. Start Paranoia Engine
```bash
cd paranoia-engine
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Starts on localhost:8080
```

### 5. Run Chaos
```bash
# 1. Analyze Finance Control source code
curl -X POST http://localhost:8080/api/analise \
  -H "Content-Type: application/json" \
  -d '{"caminhoDiretorio": "/path/to/finance-control/src/main/java"}'

# 2. Execute full pipeline (note the analysis ID from step 1)
curl -X POST http://localhost:8080/api/executar-tudo/1

# 3. Poll progress
curl http://localhost:8080/api/executar-tudo/1/status

# 4. Get consolidated report
curl http://localhost:8080/api/relatorio/1
```

---

## 🧪 Testing

```bash
# Unit tests (excludes integration)
cd paranoia-engine
mvn test

# Integration tests (requires Docker for TestContainers)
mvn test -P integracao
```

### Test Results: **21/21 passed** ✅
- **16 Unit Tests** — Services, controllers, AI scenario generation, JavaParser analysis
- **5 Integration Tests** — Full Toxiproxy fault injection, concurrency, database resilience

---

## 🗂️ Project Structure

```
evo_fed/
├── docker-compose.yml           # Infrastructure: PostgreSQL x2 + Toxiproxy + init
├── paranoia-engine/             # 🔥 The Chaos Engine
│   ├── pom.xml                  # Spring Boot 3.2.5, TestContainers, Toxiproxy
│   └── src/
│       ├── main/java/com/paranoia/engine/
│       │   ├── ParanoiaEngineApplication.java
│       │   ├── config/          # AsyncConfig, RestPipelineConfig, ToxiproxyConfig
│       │   ├── controller/      # REST: Analise, Cenario, Execucao, Relatorio
│       │   ├── execution/       # CenarioExecutor, ToxiproxyManager, AlvoOperacao
│       │   ├── model/           # Entities, records, enums, DTOs
│       │   ├── repository/      # Spring Data JPA repositories
│       │   └── service/         # Orquestrador, JavaParser, CenarioIa (AI)
│       └── test/                # Unit + Integration tests
└── finance-control/             # 🎯 Target Application
    ├── pom.xml
    └── src/main/java/com/emanuel/finance_control/
        ├── controller/          # AuthController, TransactionController
        ├── service/             # AuthService, TransactionService
        ├── security/            # JWT filter + JwtService
        └── model/               # Transaction, AppUser, TransactionType
```

---

## 🔥 Chaos Scenarios

| Type | What It Does | How |
|------|-------------|-----|
| `CONCORRENCIA` | Spawns 20 threads hammering the same method | `ExecutorService` + `CountDownLatch` |
| `FALHA_REDE` | Cuts network to the database | `Toxiproxy.disable()` |
| `FALHA_BANCO` | Same as FALHA_REDE (DB connection drop) | `Toxiproxy.disable()` |
| `TIMEOUT` | Adds 30s latency to all DB queries | `Toxiproxy.toxics().latency(30000)` |

### What Gets Tested
- **Race conditions** in `ContaServiceExemplo.debitarSemLock()` and `creditar()`
- **Transaction rollback** behavior under DB failure
- **Connection pool resilience** (HikariCP with `softEvictConnections()`)
- **Application health** during and after fault injection via `/api/actuator/health`

---

## 📖 API Reference

### Paranoia Engine (`:8080`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/analise` | Start static code analysis |
| GET | `/api/cenarios/{analiseId}` | List generated scenarios |
| PUT | `/api/cenarios` | Update a scenario |
| DELETE | `/api/cenarios/{id}` | Delete a scenario |
| POST | `/api/executar-tudo/{analiseId}` | Run full pipeline (async) |
| GET | `/api/executar-tudo/{analiseId}/status` | Poll pipeline progress |
| GET | `/api/relatorio/{analiseId}` | Get consolidated report |
| GET | `/swagger-ui.html` | Swagger UI |

### Finance Control (`:9000`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Authenticate (returns JWT) |
| POST | `/api/auth/register` | Register new user |
| GET | `/api/transactions` | List transactions |
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transactions/balance` | Get current balance |

---

## 🧠 Key Design Decisions

- **REST-only integration** between Paranoia Engine and Finance Control (no shared JARs — loose coupling)
- **JWT per operation** — No token caching; each REST call authenticates separately (realistic scenario)
- **`@ConditionalOnProperty("toxiproxy.url")`** — Toxiproxy beans only activate when proxy is configured, enabling clean unit testing
- **`client.reset()` on restore** — Enables proxy and removes all toxics atomically
- **In-memory `ResultadoExecucao` fallback** — When DB is under attack, results still get saved
- **`softEvictConnections()` in test** — Clears HikariCP's broken connections between test cases
- **Token Management**: Uses `gh` CLI with browser-based OAuth for GitHub authentication

---

## 🛡️ Security

- No secrets in repository (API keys via environment variables)
- JWT-secured target application (Finance Control)
- `@ConditionalOnProperty` guards for Toxiproxy configuration
- `.gitignore` excludes builds, IDE files, and `node_modules`

---

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">

### *"Chaos Engineering isn't about breaking things. It's about learning what your system can survive."*

---

**Built with 🔥 by the Paranoia Team**

</div>
