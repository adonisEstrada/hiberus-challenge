# Payment Initiation - Payment Order API

## Contexto y Decisiones

Este proyecto implementa una migración de un servicio SOAP legado de órdenes de pago a una API REST alineada con el Service Domain BIAN Payment Initiation / PaymentOrder. Se utiliza Spring Boot 3+ con Java 17, arquitectura hexagonal, contract-first con OpenAPI 3.0, y enfoque en calidad y pruebas.

### Pasos de Migración
1. Análisis del WSDL: Identificación de operaciones (SubmitPaymentOrder, GetPaymentOrderStatus) y campos.
2. Mapeo a BIAN: Alineación de nomenclatura y estados (PENDING, EXECUTED, FAILED).
3. Diseño contract-first: Creación de openapi.yaml para especificar contratos.
4. Arquitectura hexagonal: Separación en domain, application, infrastructure.
5. Implementación: Use cases, adapters, controllers.
6. Pruebas: Unitarias e integración.
7. Calidad: JaCoCo, Checkstyle, SpotBugs.
8. Docker: Multi-stage build.

### Uso de IA
Ver carpeta `ai/` para prompts, respuestas y correcciones.

## Ejecución Local
1. Clona el repo.
2. Ejecuta `./gradlew build` (o usa IntelliJ).
3. Ejecuta `./gradlew bootRun`.
4. API disponible en `http://localhost:8080`.
5. Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Ejecución con Docker
1. `docker build -t payment-initiation .`
2. `docker run -p 8080:8080 payment-initiation`
3. O `docker-compose up`

## Pruebas
- Unitarias: `./gradlew test`
- Cobertura: `./gradlew jacocoTestReport` (reporte en build/reports/jacoco)
- Calidad: `./gradlew checkstyleMain` y `./gradlew spotbugsMain`

## Entregables
- openapi.yaml: Contrato REST.
- Tests: Cobertura >80%.
- Calidad: Sin fallos en Checkstyle/SpotBugs.
- Docker: Dockerfile y docker-compose.
- IA: Documentado en ai/.
