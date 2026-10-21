# client-microservice

Microservicio de **gestión de clientes** de Wipay.

Permite crear, consultar y modificar clientes. Forma parte de un sistema de dos microservicios junto a [merchant-microservice](https://github.com/tareas-practicas-wipay/merchant-microservice), que es el que gestiona los merchants de cada cliente.
## Stack

- Java: 21
- Spring Boot: 4.1.1
- Maven: Wrapper incluido (`mvnw`)
- Lombok: Gestionado por Spring Boot
- MapStruct: 1.6.3
- DynamoDB: wipay-dynamodb-spring-boot-starter 0.2.0-SNAPSHOT (AWS SDK v2 2.55.16)
- Swagger / OpenAPI: springdoc-openapi 3.1.1
- Spring Cloud: 2025.1.2 (OpenFeign 5.0.2), para las llamadas a merchant-microservice

## Requisitos previos

- JDK 21
- Docker Desktop y la infraestructura local levantada: ver [infraestructure](https://github.com/tareas-practicas-wipay/infraestructure)
- El starter de DynamoDB instalado en el repositorio Maven local: clonar [dynamodb-spring-boot-starter](https://github.com/tareas-practicas-wipay/dynamodb-spring-boot-starter) y ejecutar `./mvnw clean install`
- Para comprobar si existe un merchant, merchant-microservice debe estar arrancado en `http://localhost:8082` (se configura con `merchant.service.url`)

## Arranque

El servicio arranca en **http://localhost:8081**.

## Endpoints

Documentación interactiva en **http://localhost:8081/swagger-ui.html**.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/clients` | Crear un cliente |
| `GET` | `/clients/{id}` | Obtener un cliente por id. Con `?simpleOutput=true` devuelve solo el id |
| `GET` | `/clients/search/by-name?name=` | Buscar clientes cuyo nombre contiene el texto, sin distinguir mayúsculas |
| `GET` | `/clients/search/by-email?email=` | Buscar un cliente por email, sin distinguir mayúsculas |
| `PUT` | `/clients/{id}` | Modificar un cliente. Se envían todos los campos; el email no puede pertenecer a otro cliente |
| `GET` | `/merchants/{merchantId}/exists` | Comprobar si existe un merchant, consultando merchant-microservice mediante Feign |

El email debe tener un formato válido (`usuario@dominio.ext`); si no, se devuelve un 400.

## Errores

Los errores se devuelven con el formato estándar `ProblemDetail` (RFC 9457):

```json
{
  "type": "about:blank",
  "title": "Recurso no encontrado",
  "status": 404,
  "detail": "No existe ningún cliente con id abc",
  "instance": "/clients/abc"
}
```

| Código | Cuándo |
|---|---|
| `400` | Datos de entrada no válidos o JSON mal formado. En los errores de validación, `errors` indica el motivo de cada campo |
| `404` | El cliente no existe |
| `409` | El email ya pertenece a otro cliente |
| `500` | Error inesperado. El detalle se registra en el log, no en la respuesta |
| `503` | merchant-microservice no está disponible o no ha respondido a tiempo |

## Comunicación con merchant-microservice

Se hace con Spring Cloud OpenFeign. La interfaz `MerchantFeignClient` (paquete `integration`) llama a `GET /merchants/{id}?simpleOutput=true`.

| Respuesta de merchant-microservice | Respuesta de este servicio |
|---|---|
| `200` | `200` con `"exists": true` |
| `404` | `200` con `"exists": false` |
| Apagado, sin respuesta a tiempo u otro error | `503` |

Tiempos de espera (en `application.properties`):

```properties
spring.cloud.openfeign.client.config.merchant-service.connect-timeout=2000
spring.cloud.openfeign.client.config.merchant-service.read-timeout=3000
```

Sin ellos, Feign esperaría hasta 60 segundos a que merchant-microservice responda.

## Modelo de datos

Todas las entidades extienden de `MainTable`, que viene del starter de DynamoDB (diseño de tabla única).

| Entidad | Atributos |
|---|---|
| `MainTable` | `PK`, `SK`, `id`, `status`, `gIndex2Pk`, `gIndex3Pk`, `createdDate` |
| `ClientEntity` | `name`, `surname`, `cifNifNie`, `phone`, `email`, `keywordsSearch` |

`keywordsSearch` no está en el guion: guarda el nombre en minúsculas para buscar sin distinguir mayúsculas.

## Claves en DynamoDB

| Atributo | Valor |
|---|---|
| `PK` | `CLIENT#{id}` |
| `SK` | `CLIENTDATA` |
| `gIndex2Pk` | `CLIENTEMAIL#{email en minúsculas}` |
| `gIndex3Pk` | `ENTITY#CLIENT` |

| Consulta | Operación |
|---|---|
| Cliente por id | GetItem con `PK` y `SK` |
| Cliente por email | Query sobre `gIndex2` |
| Clientes por nombre | Query sobre `gIndex3` (`ENTITY#CLIENT`) con filtro `contains` sobre `keywordsSearch` |

La búsqueda por nombre lee todos los clientes y después filtra, porque DynamoDB no permite buscar texto parcial con una condición de clave. Gracias a `gIndex3`, no lee los merchants ni el resto de la tabla.

## Estructura del proyecto

```
src/main/java/com/wipay/client
├── config       → Configuración (Swagger, tabla de DynamoDB)
├── controller   → Controladores REST
├── exception    → Excepciones y manejo global de errores
├── integration  → Clientes Feign hacia otros microservicios
├── mappers      → Mappers de MapStruct
├── model        → Modelos generales, de entrada y de salida
├── repository   → Acceso a DynamoDB
└── service      → Interfaces e implementaciones de la lógica de negocio
```

## Seguimiento del estado del proyecto

- [x] Ejercicio 1: creación del microservicio y dependencias (MapStruct, DynamoDB y Swagger)
- [x] Ejercicio 2: modelos, mappers, repositorio, servicio y endpoints
    - [x] Crear cliente
    - [x] Buscar por id, con salida completa o simple (`simpleOutput`)
    - [x] Buscar por nombre (coincidencia parcial, sin distinguir mayúsculas)
    - [x] Buscar por email, validado con `@Pattern`
    - [x] Modificar cliente
    - [x] Manejo de errores con `ProblemDetail`
- [ ] Ejercicio 3: comunicación con merchant-microservice mediante Feign
- [ ] Ejercicio 4: interceptor con validación de JWT