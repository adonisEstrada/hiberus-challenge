# Prompts usados en el desarrollo

## Prompt 1: Generar openapi.yaml alineado a BIAN Payment Initiation
"Genera un archivo openapi.yaml 3.0 para una API REST de Payment Initiation alineada con BIAN Service Domain Payment Initiation / PaymentOrder. Los endpoints deben ser:
- POST /payment-initiation/payment-orders: Initiate Payment Order
- GET /payment-initiation/payment-orders/{id}: Retrieve Payment Order
- GET /payment-initiation/payment-orders/{id}/status: Retrieve Payment Order Status
Incluye esquemas para InitiatePaymentOrderRequest, PaymentOrderResponse, PaymentOrderStatusResponse con campos como externalReference, debtorAccount, creditorAccount, instructedAmount, etc."

Respuesta: [El YAML generado]

Correcciones: Agregué enums para status y ajusté formatos de fecha.

## Prompt 2: Esqueleto de arquitectura hexagonal
"Proporciona un esqueleto de código Java Spring Boot para arquitectura hexagonal con capas domain, application, infrastructure. Para un servicio de payment orders con use cases initiate, retrieve, retrieve status."

Respuesta: [Estructura de paquetes y clases base]

Correcciones: Adapté a los DTOs y agregué mappers manuales en lugar de generados.

## Prompt 3: Generar tests con AssertJ y Mockito
"Escribe tests unitarios para PaymentOrderServiceImpl usando AssertJ y Mockito."

Respuesta: [Código de test]

Correcciones: Agregué assertions adicionales y mocks correctos.
