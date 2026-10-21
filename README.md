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

## Requisitos previos

- JDK 21
- Docker Desktop y la infraestructura local levantada: ver [infraestructure](https://github.com/tareas-practicas-wipay/infraestructure)
- El starter de DynamoDB instalado en el repositorio Maven local: clonar [dynamodb-spring-boot-starter](https://github.com/tareas-practicas-wipay/dynamodb-spring-boot-starter) y ejecutar `./mvnw clean install`

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

El email debe tener un formato válido (`usuario@dominio.ext`); si no, se devuelve un 400.

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
├── controller   → Controladores REST
├── service      → Interfaces e implementaciones de la lógica de negocio
├── model        → Modelos generales, de entrada y de salida
├── repository   → Acceso a DynamoDB
└── mappers      → Mappers de MapStruct
```

## Seguimiento del estado del proyecto

- [x] Creación del microservicio
- [x] Dependencias: MapStruct, DynamoDB y Swagger