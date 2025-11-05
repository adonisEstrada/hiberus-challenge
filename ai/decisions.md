# Decisiones tomadas en el desarrollo

## Arquitectura Hexagonal
- Elegí arquitectura hexagonal para separar responsabilidades: domain (entidades), application (use cases), infrastructure (adapters).
- Usé ports (interfaces) en application para desacoplar de infraestructura.

## Contract-first
- Creé openapi.yaml manualmente ya que openapi-generator no estaba disponible.
- Implementé interfaz PaymentOrderApi con annotations para contract-first.

## Almacenamiento
- Usé in-memory repository para simplicidad, como no se requiere persistencia avanzada.

## Validaciones
- Agregué @Valid en requests, pero no implementé custom validators por simplicidad.

## Tests
- Usé WebMvcTest para integración, Mockito para unitarios.
- Cobertura objetivo: >=80% con JaCoCo.

## Docker
- Multi-stage build para optimizar imagen.

## Calidad
- Checkstyle y SpotBugs configurados básicamente.

## Uso de IA
- IA ayudó en generación de YAML, estructura hexagonal, tests.
- Correcciones manuales: Ajustes a mappings, imports, lógica de negocio.
