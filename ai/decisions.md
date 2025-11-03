# Decisiones de diseño y correcciones manuales

## 1. Decisiones de Arquitectura

### Arquitectura Hexagonal (Puertos y Adaptadores)
**Decisión:** Implementar arquitectura hexagonal completa
**Razón:**
- Desacopla la lógica de negocio de los frameworks
- Facilita el testing (se pueden mockear los puertos)
- Permite cambiar adaptadores sin afectar el dominio
- Alineado con Domain-Driven Design (DDD)

### Programación Reactiva (WebFlux + R2DBC)
**Decisión:** Usar Spring WebFlux en lugar de Spring MVC
**Razón:**
- Mayor escalabilidad con menos recursos (non-blocking I/O)
- Mejor manejo de alta concurrencia
- R2DBC para persistencia reactiva end-to-end
- Cumple con requisito "opcional" que se convirtió en mandatorio

---

## 2. Mapeo SOAP → REST

### Enriquecimiento de Campos
**SOAP (legacy):**
```xml
<debtorIban>ES9121...</debtorIban>
<creditorIban>ES7921...</creditorIban>
```

**REST (moderno):**
```json
{
  "debtorAccount": {
    "identification": "ES9121...",
    "name": "John Doe",
    "bankIdentifier": "BANKESMMXXX"
  }
}
```

**Decisión:** Agregar campos `name` y `bankIdentifier`
**Razón:**
- El WSDL solo tenía IBANs, insuficiente para un sistema real
- BIAN Payment Initiation requiere información completa de cuentas
- Mejora la trazabilidad y auditoría

### Estados del Sistema
**SOAP:** Solo 2 estados (ACCEPTED, SETTLED)
**REST:** 6 estados (PENDING, PROCESSING, COMPLETED, FAILED, REJECTED, CANCELLED)

**Decisión:** Expandir la máquina de estados
**Razón:**
- Mayor granularidad en el ciclo de vida del pago
- Permite mejor monitoreo y troubleshooting
- Alineado con estándares BIAN

---

## 3. Correcciones Manuales al Código Generado

### OpenAPI Generator - Modelos
**Problema:** Los modelos generados no tenían constructores completos
**Solución Manual:**
```gradle
additionalModelTypeAnnotations: '@lombok.Builder @lombok.AllArgsConstructor'
```
**Impacto:** Facilita la creación de objetos en tests y mappers

### MapStruct - Mapeo de Enums
**Problema:** MapStruct no mapeaba correctamente los enums generados por OpenAPI
**Solución Manual:** Creación de métodos custom en PaymentOrderRestMapper:
```java
default PaymentOrderResponse.StatusEnum toRestStatus(PaymentOrderStatus status) {
    return status != null ? PaymentOrderResponse.StatusEnum.fromValue(status.name()) : null;
}
```
**Impacto:** Conversión correcta entre enums de dominio y REST

### R2DBC - Schema Initialization
**Problema:** R2DBC no ejecuta schema.sql automáticamente como JPA
**Solución Manual:** Configuración en application.yaml:
```yaml
spring:
  sql:
    init:
      mode: always
```
**Impacto:** Base de datos inicializada correctamente al arrancar

---

## 4. Validaciones y Reglas de Negocio

### Idempotencia
**Decisión:** Usar `endToEndIdentification` para prevenir duplicados
**Implementación:**
```java
private Mono<Void> checkForDuplicates(String endToEndIdentification) {
    return repository.existsByEndToEndIdentification(endToEndIdentification)
        .flatMap(exists -> exists ? Mono.error(...) : Mono.empty());
}
```
**Razón:**
- Evita pagos duplicados en caso de retry del cliente
- Estándar en sistemas bancarios (ISO 20022)

### Validación de Transiciones de Estado
**Decisión:** Implementar state machine en el dominio
**Implementación:**
```java
private void validateStatusTransition(PaymentOrderStatus current, PaymentOrderStatus new) {
    boolean isValid = switch (current) {
        case PENDING -> new == PROCESSING || new == REJECTED || new == CANCELLED;
        case PROCESSING -> new == COMPLETED || new == FAILED;
        case COMPLETED, FAILED, REJECTED, CANCELLED -> false;
    };
    // throw exception if invalid
}
```
**Razón:**
- Previene transiciones ilógicas (ej: COMPLETED → PENDING)
- Mantiene la integridad del estado del pago

---

## 5. Testing

### Testcontainers vs H2
**Decisión:** Usar Testcontainers con PostgreSQL real
**Razón:**
- H2 no soporta todas las features de PostgreSQL
- Tests más confiables (test against production-like environment)
- Requerimiento del ejercicio

### Cobertura de Tests
**Objetivo:** ≥80% líneas de código
**Estrategia:**
- Tests unitarios para dominio (validaciones, lógica de negocio)
- Tests de servicio con mocks
- Tests E2E con Testcontainers
- Exclusión de código generado y configuración

---

## 6. Seguridad y Buenas Prácticas

### Usuario Non-Root en Docker
**Decisión:** Crear usuario `spring:spring` en Dockerfile
**Implementación:**
```dockerfile
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
```
**Razón:**
- Principio de least privilege
- Previene escalación de privilegios si el contenedor es comprometido

### Secrets Management
**Decisión:** Usar variables de entorno para credenciales
**Nota:** En producción se debe usar:
- Kubernetes Secrets
- AWS Secrets Manager
- HashiCorp Vault

---

## 7. Observabilidad

### Actuator + Prometheus
**Decisión:** Habilitar métricas con Micrometer
**Configuración:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```
**Razón:**
- Monitoreo de salud del servicio
- Métricas de performance (latencia, throughput)
- Integración con Grafana para dashboards

---

## 8. Manejo de Errores (RFC 7807)

### ProblemDetail
**Decisión:** Implementar RFC 7807 para todas las respuestas de error
**Estructura:**
```json
{
  "type": "https://api.bank.com/problems/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Payment order not found: PO-123",
  "timestamp": "2025-11-03T10:30:00Z"
}
```
**Razón:**
- Estándar de industria para APIs REST
- Información estructurada para debugging
- Content-Type: application/problem+json

---

## 9. Performance y Escalabilidad

### Connection Pooling
**Configuración R2DBC:**
```yaml
spring:
  r2dbc:
    pool:
      initial-size: 5
      max-size: 20
      max-idle-time: 30m
```
**Razón:**
- Reutilización de conexiones
- Mejor throughput bajo carga

### JVM Tuning
**Docker ENTRYPOINT:**
```dockerfile
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
```
**Razón:**
- G1GC para baja latencia
- Aprovecha memoria del contenedor sin OOM

---

## 10. Lecciones Aprendidas

### Lo que funcionó bien:
✅ OpenAPI Generator para contract-first
✅ MapStruct para mapeo automático
✅ Arquitectura hexagonal para testing
✅ R2DBC + Testcontainers

### Desafíos encontrados:
⚠️ MapStruct con enums generados requiere mappers custom
⚠️ R2DBC no auto-inicializa schema (a diferencia de JPA)
⚠️ WebFlux requiere pensamiento reactivo (Mono/Flux)

### Recomendaciones para futuro:
📝 Agregar cache (Redis) para consultas frecuentes
📝 Implementar circuit breaker (Resilience4j)
📝 API Gateway para rate limiting y autenticación
📝 Event sourcing para auditoría completa
