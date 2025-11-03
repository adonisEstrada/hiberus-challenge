# Prompts utilizados en la generación del proyecto

## 1. Análisis del WSDL
**Prompt:**
```
Analiza los archivos WSDL del servicio SOAP legacy de órdenes de pago.
Identifica:
- Las operaciones disponibles
- Los campos y tipos de datos
- Los estados del sistema
- Las validaciones implícitas
```

**Respuesta (resumen):**
El servicio SOAP legacy tiene 2 operaciones principales:
1. `SubmitPaymentOrder` - Crea una nueva orden de pago
2. `GetPaymentOrderStatus` - Consulta el estado de una orden

Campos identificados:
- externalId, debtorIban, creditorIban, amount, currency
- remittanceInfo, requestedExecutionDate
- Estados: ACCEPTED, SETTLED

---

## 2. Diseño de la especificación OpenAPI
**Prompt:**
```
Basándote en el análisis del WSDL, genera una especificación OpenAPI 3.0 que:
- Esté alineada con BIAN Service Domain Payment Initiation
- Utilice el BQ (Behavior Qualifier) PaymentOrder
- Incluya validaciones robustas (patrones regex, enums, límites)
- Implemente RFC 7807 para manejo de errores
- Siga las mejores prácticas REST (nombres de recursos, verbos HTTP)
```

**Respuesta (resumen):**
Se generó `openapi_specification.yaml` con:
- 3 endpoints REST mapeados desde las 2 operaciones SOAP
- POST /payment-initiation/payment-orders (Initiate)
- GET /payment-initiation/payment-orders/{id} (Retrieve)
- GET /payment-initiation/payment-orders/{id}/status (Retrieve Status)
- Enriquecimiento de campos: AccountInfo con name y BIC, timestamps, priority
- Estados enumerados: PENDING, PROCESSING, COMPLETED, FAILED, REJECTED, CANCELLED
- ProblemDetail para errores siguiendo RFC 7807

---

## 3. Arquitectura Hexagonal
**Prompt:**
```
Diseña la estructura de paquetes para una arquitectura hexagonal (puertos y adaptadores) que incluya:
- Domain: modelos de dominio (PaymentOrder, AccountInfo, MonetaryAmount), puertos in/out
- Application: servicios de aplicación que implementan los casos de uso
- Infrastructure: adaptadores REST (controller), persistencia (R2DBC), generadores de ID
- Separación clara entre capas sin dependencias invertidas
```

**Respuesta (resumen):**
Estructura generada:
```
domain/
  model/ (PaymentOrder, AccountInfo, MonetaryAmount, enums)
  port/
    in/ (use cases: InitiatePaymentOrderUseCase, RetrievePaymentOrderUseCase)
    out/ (repositories: PaymentOrderRepository, PaymentOrderIdGenerator)
application/
  service/ (PaymentOrderService - implementa los use cases)
  exception/ (excepciones de negocio)
infrastructure/
  adapter/
    in/rest/ (PaymentOrderController, mappers, exception handlers)
    out/persistence/ (repositorios R2DBC, entities, mappers)
    out/id/ (SequentialPaymentOrderIdGenerator)
```

---

## 4. Generación del código de dominio
**Prompt:**
```
Genera el modelo de dominio PaymentOrder con:
- Inmutabilidad (usando Lombok @Value y @Builder)
- Factory method `create()` con validaciones de reglas de negocio
- Método `updateStatus()` que valide transiciones de estado permitidas
- Validaciones: deudor ≠ acreedor, monto > 0, fecha no pasada
```

**Respuesta (resumen):**
Se generó PaymentOrder.java con:
- Patrón Builder inmutable
- Validaciones en el factory method create()
- Lógica de transición de estados (state machine simplificado)
- Value objects AccountInfo y MonetaryAmount con sus propias validaciones

---

## 5. Servicios de aplicación
**Prompt:**
```
Implementa PaymentOrderService que:
- Implemente los 3 use cases (iniciar, recuperar, recuperar estado)
- Use inyección de dependencias para repository e idGenerator
- Valide datos de entrada llamando a validate() de los value objects
- Maneje idempotencia verificando endToEndIdentification duplicados
- Use programación reactiva (Mono, Flux) con Project Reactor
```

**Respuesta (resumen):**
Se generó PaymentOrderService con:
- Lógica de validación e idempotencia
- Manejo de errores con excepciones personalizadas
- Logging estratégico
- Métodos reactivos usando Mono<T>

---

## 6. Configuración de OpenAPI Generator
**Prompt:**
```
Configura el plugin openapi-generator en build.gradle para:
- Generar interfaces (no implementaciones) desde openapi_specification.yaml
- Usar generatorName = 'spring' con opciones reactive y useSpringBoot3
- Colocar interfaces en paquete infrastructure.adapter.in.rest.api
- Colocar modelos en infrastructure.adapter.in.rest.model
- Agregar anotaciones Lombok (@Builder, @AllArgsConstructor) a los modelos
```

**Respuesta (resumen):**
Configurado en build.gradle:
- Plugin org.openapi.generator version 7.10.0
- Generación contract-first automática
- Interfaces generadas que el controller implementa
- Integración con el proceso de compilación

---

## 7. Tests unitarios
**Prompt:**
```
Genera tests unitarios usando JUnit 5, AssertJ y Mockito para:
- Modelos de dominio (PaymentOrderTest, AccountInfoTest, MonetaryAmountTest)
- Servicios de aplicación (PaymentOrderServiceTest)
- Cubrir casos happy path y edge cases
- Verificar validaciones y excepciones
```

**Respuesta (resumen):**
Se generaron tests con:
- @DisplayName descriptivos
- AssertJ para aserciones fluidas
- Mockito para mocks de dependencias
- Cobertura de validaciones del dominio
- Tests de transiciones de estado

---

## 8. Tests de integración
**Prompt:**
```
Genera tests de integración E2E con:
- WebTestClient para pruebas reactivas
- Testcontainers para PostgreSQL real
- @SpringBootTest con perfil "test"
- Pruebas del flujo completo: crear → recuperar → consultar estado
- Verificación de respuestas HTTP y contenido JSON
```

**Respuesta (resumen):**
Se generó PaymentOrderControllerIT con:
- Testcontainers PostgreSQL
- Configuración dinámica de propiedades
- Tests E2E completos
- Verificación de error handling (404, 400)

---

## 9. Configuración de calidad (Checkstyle, SpotBugs, JaCoCo)
**Prompt:**
```
Configura herramientas de calidad en build.gradle:
- Checkstyle con reglas estándar (líneas max 140, sin tabs)
- SpotBugs con excludeFilter para código generado
- JaCoCo con cobertura mínima del 80%, excluyendo código generado y config
- Integrar con `./gradlew verify`
```

**Respuesta (resumen):**
Configurado en build.gradle:
- Checkstyle 10.20.2 con checkstyle.xml
- SpotBugs 6.0.26 con excludeFilter.xml
- JaCoCo 0.8.12 con verificación de cobertura 80%
- Exclusiones: model, api, config, Application

---

## 10. Docker y docker-compose
**Prompt:**
```
Crea Dockerfile multi-stage y docker-compose.yml:
- Stage 1: builder con Gradle para compilar
- Stage 2: runtime con JRE21 alpine, usuario non-root, healthcheck
- docker-compose: PostgreSQL + servicio + Prometheus/Grafana (opcional)
- Variables de entorno para configuración
```

**Respuesta (resumen):**
Se generaron:
- Dockerfile multi-stage optimizado (imagen final ~200MB)
- docker-compose.yml con networking y health checks
- Configuración de Prometheus para métricas
- Perfiles para servicios opcionales (monitoring)
