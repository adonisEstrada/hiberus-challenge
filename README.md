# Payment Initiation Microservice

Microservicio REST para gestión de órdenes de pago, resultado de la migración del servicio SOAP legacy a arquitectura moderna alineada con BIAN (Banking Industry Architecture Network).

## 📋 Índice

- [Contexto del Proyecto](#-contexto-del-proyecto)
- [Arquitectura](#-arquitectura)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Testing](#-testing)
- [Calidad de Código](#-calidad-de-código)
- [API Documentation](#-api-documentation)
- [Uso de Inteligencia Artificial](#-uso-de-inteligencia-artificial)
- [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🎯 Contexto del Proyecto

### Migración SOAP → REST

La entidad bancaria de la localidad norte está modernizando sus servicios core, migrando de SOAP a REST con arquitectura alineada a BIAN. Este proyecto migra el servicio legacy de **Payment Order Service** a un microservicio REST moderno.

### Service Domain BIAN: Payment Initiation

- **Service Domain:** Payment Initiation
- **Behavior Qualifier (BQ):** PaymentOrder
- **Operaciones:**
  - **Initiate:** Crear una nueva orden de pago
  - **Retrieve:** Obtener información completa de una orden
  - **Retrieve Status:** Consultar solo el estado de una orden

### Mapeo SOAP → REST

| SOAP Operation | REST Endpoint | HTTP Method |
|----------------|---------------|-------------|
| SubmitPaymentOrder | `/payment-initiation/payment-orders` | POST |
| GetPaymentOrderStatus | `/payment-initiation/payment-orders/{id}` | GET |
| - | `/payment-initiation/payment-orders/{id}/status` | GET |

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Puertos y Adaptadores)

```
┌─────────────────────────────────────────────────────────────┐
│                     INFRASTRUCTURE LAYER                     │
│  ┌─────────────────────┐         ┌────────────────────────┐ │
│  │   REST Controller   │         │   R2DBC Repository     │ │
│  │  (Input Adapter)    │         │  (Output Adapter)      │ │
│  └──────────┬──────────┘         └──────────▲─────────────┘ │
│             │                               │               │
└─────────────┼───────────────────────────────┼───────────────┘
              │                               │
┌─────────────┼───────────────────────────────┼───────────────┐
│             ▼                               │               │
│  ┌──────────────────────────────────────────┴─────────────┐ │
│  │           APPLICATION SERVICE LAYER                    │ │
│  │       (PaymentOrderService - Use Cases)                │ │
│  └──────────────────┬───────────────────▲──────────────────┘ │
│                     │                   │                    │
│  ┌──────────────────▼───────────────────┴──────────────────┐ │
│  │                  DOMAIN LAYER                           │ │
│  │  ┌─────────────┐  ┌──────────┐  ┌──────────────────┐  │ │
│  │  │PaymentOrder │  │Ports (In)│  │  Ports (Out)     │  │ │
│  │  │AccountInfo  │  │Use Cases │  │  Repositories    │  │ │
│  │  │MonetaryAmt  │  └──────────┘  └──────────────────┘  │ │
│  │  └─────────────┘                                       │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Capas

1. **Domain (Dominio):** Lógica de negocio pura, sin dependencias externas
   - Entidades: `PaymentOrder`, `AccountInfo`, `MonetaryAmount`
   - Puertos: Interfaces que definen contratos (use cases, repositories)

2. **Application (Aplicación):** Orquestación de casos de uso
   - `PaymentOrderService`: Implementa los use cases
   - Validaciones de negocio, manejo de transacciones

3. **Infrastructure (Infraestructura):** Adaptadores a tecnologías específicas
   - **Input Adapters:** REST Controller, mappers
   - **Output Adapters:** R2DBC repositories, ID generators

---

## 🛠️ Tecnologías Utilizadas

### Stack Principal (Mandatorio)

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework principal
- **Spring WebFlux** - Programación reactiva (no-blocking)
- **R2DBC (PostgreSQL)** - Persistencia reactiva
- **OpenAPI 3.0** - Especificación de la API (contract-first)
- **OpenAPI Generator** - Generación de código desde OpenAPI
- **MapStruct** - Mapeo automático entre DTOs y dominio
- **Lombok** - Reducción de boilerplate code

### Testing

- **JUnit 5** - Framework de testing
- **AssertJ** - Aserciones fluidas
- **Mockito** - Mocking framework
- **Reactor Test** - Testing reactivo
- **Testcontainers** - Contenedores para tests de integración
- **WebTestClient** - Testing de endpoints REST

### Calidad de Código

- **JaCoCo** - Cobertura de código (≥80%)
- **Checkstyle** - Estilo de código
- **SpotBugs** - Análisis estático de bugs

### DevOps

- **Docker** - Contenedorización (multi-stage)
- **Docker Compose** - Orquestación local
- **Gradle 8.11** - Build tool

### Observabilidad (Opcional)

- **Spring Actuator** - Health checks, métricas
- **Micrometer** - Métricas
- **Prometheus** - Recolección de métricas
- **Grafana** - Visualización

---

## 📦 Requisitos Previos

- **Java 21** o superior
- **Docker** y **Docker Compose**
- **Gradle 8.x** (incluido via wrapper)
- **Git**

---

## 🚀 Instalación y Ejecución

### Opción 1: Ejecución Local (con PostgreSQL en Docker)

#### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd hiberus-challenge
```

#### 2. Iniciar PostgreSQL
```bash
docker-compose up -d postgres
```

#### 3. Compilar y ejecutar
```bash
# Compilar (incluye generación de código OpenAPI)
./gradlew clean build

# Ejecutar
./gradlew bootRun
```

#### 4. Verificar
```bash
curl http://localhost:8080/actuator/health
```

---

### Opción 2: Ejecución con Docker Compose (Recomendado)

#### 1. Construir y levantar todos los servicios
```bash
docker-compose up --build
```

Esto inicia:
- **PostgreSQL** (puerto 5432)
- **Payment Service** (puerto 8080)

#### 2. Verificar logs
```bash
docker-compose logs -f payment-service
```

#### 3. Health check
```bash
curl http://localhost:8080/actuator/health
```

---

### Opción 3: Con Monitoring (Prometheus + Grafana)

```bash
docker-compose --profile monitoring up --build
```

Acceder a:
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000 (admin/admin)

---

## 🧪 Testing

### Ejecutar todos los tests
```bash
./gradlew test
```

### Tests unitarios
```bash
./gradlew test --tests "*Test"
```

### Tests de integración
```bash
./gradlew test --tests "*IT"
```

### Reporte de cobertura
```bash
./gradlew jacocoTestReport

# Ver reporte HTML
open build/reports/jacoco/test/html/index.html
```

---

## ✅ Calidad de Código

### Verificar todo (checkstyle, spotbugs, jacoco)
```bash
./gradlew verify
```

### Checkstyle
```bash
./gradlew checkstyleMain checkstyleTest
```

### SpotBugs
```bash
./gradlew spotbugsMain

# Ver reporte
open build/reports/spotbugs/main/spotbugs.html
```

### Cobertura de código
```bash
./gradlew jacocoTestCoverageVerification
```

Requisito: **≥80% de cobertura** (excluyendo código generado y configuración)

---

## 📚 API Documentation

### Swagger UI (interactivo)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec (JSON)
```
http://localhost:8080/api-docs
```

### Ejemplos de uso

#### 1. Crear una orden de pago
```bash
curl -X POST http://localhost:8080/payment-initiation/payment-orders \
  -H "Content-Type: application/json" \
  -d '{
    "debtorAccount": {
      "identification": "ES9121000418450200051332",
      "name": "John Doe"
    },
    "creditorAccount": {
      "identification": "ES7921000813610123456789",
      "name": "Jane Smith"
    },
    "amount": {
      "value": 1500.50,
      "currency": "EUR"
    },
    "executionDate": "2025-11-05",
    "remittanceInformation": "Invoice payment"
  }'
```

**Respuesta (201 Created):**
```json
{
  "paymentOrderId": "PO-2025110312345600001",
  "status": "PENDING",
  "debtorAccount": {...},
  "creditorAccount": {...},
  "amount": {...},
  "executionDate": "2025-11-05",
  "createdAt": "2025-11-03T12:34:56Z",
  "updatedAt": "2025-11-03T12:34:56Z"
}
```

#### 2. Consultar una orden de pago
```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-2025110312345600001
```

#### 3. Consultar solo el estado
```bash
curl http://localhost:8080/payment-initiation/payment-orders/PO-2025110312345600001/status
```

**Respuesta (200 OK):**
```json
{
  "paymentOrderId": "PO-2025110312345600001",
  "status": "PENDING",
  "updatedAt": "2025-11-03T12:34:56Z"
}
```

---

## 🤖 Uso de Inteligencia Artificial

Este proyecto fue desarrollado con asistencia de **Claude AI (Anthropic)**. La IA fue utilizada estratégicamente en las siguientes áreas:

### Prompts Utilizados

Ver archivo completo: [`ai/prompts.md`](./ai/prompts.md)

**Ejemplos:**

1. **Análisis del WSDL:**
   ```
   Analiza el WSDL del servicio SOAP legacy. Identifica operaciones,
   campos, tipos de datos y estados del sistema.
   ```

2. **Generación de OpenAPI:**
   ```
   Genera una especificación OpenAPI 3.0 alineada con BIAN Payment Initiation,
   incluyendo validaciones robustas y RFC 7807 para errores.
   ```

3. **Arquitectura Hexagonal:**
   ```
   Diseña la estructura de paquetes para arquitectura hexagonal con
   separación clara de dominio, aplicación e infraestructura.
   ```

4. **Generación de Tests:**
   ```
   Genera tests unitarios con JUnit 5, AssertJ y Mockito cubriendo
   happy path y edge cases, validaciones y excepciones.
   ```

### Correcciones Manuales

Ver archivo completo: [`ai/decisions.md`](./ai/decisions.md)

**Principales correcciones:**

1. **MapStruct + OpenAPI Enums:** IA generó mappers básicos, se agregaron métodos custom para mapeo de enums generados.

2. **R2DBC Schema Init:** IA asumió auto-inicialización como JPA. Se configuró manualmente `spring.sql.init.mode=always`.

3. **Testcontainers Config:** IA generó configuración estática. Se ajustó a `@DynamicPropertySource` para mayor flexibilidad.

4. **Validación de Estados:** IA generó validaciones simples. Se implementó state machine completo manualmente.

### Validación Humana

- ✅ **Revisión de lógica de negocio:** Todas las reglas de validación fueron verificadas manualmente
- ✅ **Testing:** Tests ejecutados y ajustados para cubrir casos edge
- ✅ **Seguridad:** Revisión de configuraciones de seguridad (non-root user, secrets)
- ✅ **Performance:** JVM tuning y connection pooling configurado manualmente

---

## 📁 Estructura del Proyecto

```
hiberus-challenge/
├── ai/                                    # Documentación de uso de IA
│   ├── prompts.md                         # Prompts utilizados
│   └── decisions.md                       # Decisiones y correcciones
├── config/                                # Configuraciones de calidad
│   ├── checkstyle/
│   │   └── checkstyle.xml
│   └── spotbugs/
│       └── excludeFilter.xml
├── legacy/                                # Servicio SOAP legacy (referencia)
│   ├── PaymentOrderService.wsdl
│   └── samples/
│       ├── SubmitPaymentOrderRequest.xml
│       ├── SubmitPaymentOrderResponse.xml
│       ├── GetPaymentOrderStatusRequest.xml
│       └── GetPaymentOrderStatusResponse.xml
├── monitoring/
│   └── prometheus.yml
├── src/
│   ├── main/
│   │   ├── java/com/hiberus/challenge/
│   │   │   ├── PaymentInitiationApplication.java
│   │   │   ├── domain/                   # CAPA DE DOMINIO
│   │   │   │   ├── model/
│   │   │   │   │   ├── PaymentOrder.java
│   │   │   │   │   ├── AccountInfo.java
│   │   │   │   │   ├── MonetaryAmount.java
│   │   │   │   │   ├── PaymentOrderStatus.java
│   │   │   │   │   └── PaymentPriority.java
│   │   │   │   └── port/
│   │   │   │       ├── in/               # Puertos de entrada (use cases)
│   │   │   │       │   ├── InitiatePaymentOrderUseCase.java
│   │   │   │       │   ├── InitiatePaymentOrderCommand.java
│   │   │   │       │   ├── RetrievePaymentOrderUseCase.java
│   │   │   │       │   └── RetrievePaymentOrderStatusUseCase.java
│   │   │   │       └── out/              # Puertos de salida (repositories)
│   │   │   │           ├── PaymentOrderRepository.java
│   │   │   │           └── PaymentOrderIdGenerator.java
│   │   │   ├── application/              # CAPA DE APLICACIÓN
│   │   │   │   ├── service/
│   │   │   │   │   └── PaymentOrderService.java
│   │   │   │   └── exception/
│   │   │   │       ├── PaymentOrderNotFoundException.java
│   │   │   │       └── DuplicatePaymentOrderException.java
│   │   │   └── infrastructure/           # CAPA DE INFRAESTRUCTURA
│   │   │       └── adapter/
│   │   │           ├── in/rest/          # Adaptador REST (entrada)
│   │   │           │   ├── PaymentOrderController.java
│   │   │           │   ├── mapper/
│   │   │           │   │   └── PaymentOrderRestMapper.java
│   │   │           │   └── exception/
│   │   │           │       └── GlobalExceptionHandler.java
│   │   │           └── out/
│   │   │               ├── persistence/  # Adaptador R2DBC (salida)
│   │   │               │   ├── entity/
│   │   │               │   │   └── PaymentOrderEntity.java
│   │   │               │   ├── R2dbcPaymentOrderRepository.java
│   │   │               │   ├── PaymentOrderRepositoryAdapter.java
│   │   │               │   └── mapper/
│   │   │               │       └── PaymentOrderPersistenceMapper.java
│   │   │               └── id/
│   │   │                   └── SequentialPaymentOrderIdGenerator.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── schema.sql
│   └── test/
│       ├── java/com/hiberus/challenge/
│       │   ├── domain/model/
│       │   │   ├── PaymentOrderTest.java
│       │   │   ├── AccountInfoTest.java
│       │   │   └── MonetaryAmountTest.java
│       │   ├── application/service/
│       │   │   └── PaymentOrderServiceTest.java
│       │   └── infrastructure/adapter/in/rest/
│       │       └── PaymentOrderControllerIT.java
│       └── resources/
│           └── application-test.yaml
├── openapi_specification.yaml            # Contrato OpenAPI 3.0
├── build.gradle                          # Configuración Gradle
├── Dockerfile                            # Multi-stage Docker image
├── docker-compose.yml                    # Orquestación
├── postman_collection.json               # Colección Postman para testing
└── README.md                             # Este archivo
```

---

## 🔍 Características Destacadas

### ✅ Cumplimiento de Requisitos

**Mandatorios:**
- ✅ Java 21
- ✅ Spring Boot 3.5.7
- ✅ Contract-first con OpenAPI 3.0 y openapi-generator
- ✅ Arquitectura hexagonal completa
- ✅ Tests unitarios (JUnit 5, AssertJ, Mockito)
- ✅ Tests de integración (WebTestClient, Testcontainers)
- ✅ JaCoCo ≥80% cobertura
- ✅ Checkstyle + SpotBugs sin errores
- ✅ Dockerfile multi-stage + docker-compose
- ✅ Documentación de uso de IA (prompts, decisiones)

**Opcionales (Implementados):**
- ✅ Spring WebFlux (reactivo)
- ✅ R2DBC con PostgreSQL
- ✅ RFC 7807 (application/problem+json)
- ✅ Micrometer/Actuator para observabilidad
- ✅ Validaciones robustas con Bean Validation
- ✅ Idempotencia (endToEndIdentification)

### 🎯 Alineación BIAN

- **Service Domain:** Payment Initiation
- **Behavior Qualifier:** PaymentOrder
- **Nomenclatura de recursos:** `/payment-initiation/payment-orders`
- **Estados alineados:** PENDING → PROCESSING → COMPLETED/FAILED

### 🛡️ Seguridad

- Usuario non-root en Docker
- Health checks en servicios
- Secrets via environment variables
- Validaciones exhaustivas de entrada

### 📈 Calidad

- **Cobertura de tests:** ≥80%
- **Checkstyle:** 0 violaciones
- **SpotBugs:** 0 bugs
- **Documentación:** Completa (código, API, decisiones)

---

## 📞 Contacto

**Autor:** Hiberus Challenge
**Email:** api-support@bank.com
**Fecha:** Noviembre 2025

---

## 📄 Licencia

Este proyecto es parte de una prueba técnica para Hiberus.
