# ms-inventory

Microservicio de Inventario. Gestiona la disponibilidad por producto y se integra con ms-products para validar existencia y obtener información del producto. Los endpoints están protegidos mediante API Key y la comunicación entre microservicios también utiliza API Key.

---

## Descripción

Recurso principal: `inventories`

Campos:

* `productId` (UUID)
* `quantity` (Integer)

Funcionalidad:

* Consultar la cantidad disponible de un producto por `productId`.
* Actualizar la cantidad disponible tras una compra/ajuste.
* Emitir un evento simple cuando el inventario cambia (log en consola).
* Consultar a ms-products vía HTTP para validar el producto y obtener datos básicos.

Las respuestas siguen el estándar JSON:API.

---

## Stack tecnológico

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA (Hibernate)
* Spring Security (API Key)
* PostgreSQL
* RestClient (Spring) para comunicación con ms-products
* OpenAPI/Swagger (springdoc)
* WireMock (tests de integración)
* Maven

---

## Configuración

### Base de datos

Variables de entorno requeridas:

* `DB_URL`
  Ejemplo local: `jdbc:postgresql://localhost:5433/inventory_db`
* `DB_USER`
  Ejemplo: `inventory_user`
* `DB_PASS`
  Ejemplo: `inventory_pass`

Configuración JPA relevante:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

### Seguridad (API Key)

Variables de entorno:

* `API_KEY_HEADER`
  Ejemplo: `X-API-KEY`
* `API_KEY_VALUE`
  Ejemplo: `inventory-secret-key`

Ejemplo de uso:

```bash
curl http://localhost:8082/inventory/{productId} \
  -H "X-API-KEY: inventory-secret-key"
```

Comportamiento esperado:

* Sin header: 401 Unauthorized
* Header inválido: 401 Unauthorized
* Header válido: 200 OK (o error funcional del endpoint, pero no 401)

---

### Integración con ms-products

Variables de entorno:

* `PRODUCTS_BASE_URL`
  Ejemplo local: `http://localhost:8081`
  Ejemplo en Docker Compose: `http://ms-products:8081`
* `PRODUCTS_API_KEY_HEADER`
  Ejemplo: `X-API-KEY`
* `PRODUCTS_API_KEY_VALUE`
  Ejemplo: `product-secret-key`
* `PRODUCTS_TIMEOUT_MS`
  Ejemplo: `2000`
* `PRODUCTS_RETRY_MAX`
  Ejemplo: `3`

Notas:

* Dentro de Docker, no se debe usar `localhost` para llamar a otro servicio.
* Para comunicación entre contenedores, usar el nombre del servicio definido en `docker-compose.yml`.

---

## Ejecutar local (sin Docker)

1. Asegurar PostgreSQL activo para inventory.
2. Definir variables (PowerShell):

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5433/inventory_db"
$env:DB_USER="inventory_user"
$env:DB_PASS="inventory_pass"

$env:API_KEY_HEADER="X-API-KEY"
$env:API_KEY_VALUE="inventory-secret-key"

$env:PRODUCTS_BASE_URL="http://localhost:8081"
$env:PRODUCTS_API_KEY_HEADER="X-API-KEY"
$env:PRODUCTS_API_KEY_VALUE="product-secret-key"
$env:PRODUCTS_TIMEOUT_MS="2000"
$env:PRODUCTS_RETRY_MAX="3"
```

3. Ejecutar:

```bash
mvn clean test
mvn spring-boot:run
```

---

## Ejecutar con Docker Compose

Si el repositorio incluye `docker-compose.yml` para ambos servicios:

```bash
docker compose up --build
```

Dentro del entorno Docker, ms-inventory debe apuntar a:

```text
http://ms-products:8081
```

---

## Endpoints

Los endpoints expuestos siguen el estándar JSON:API. Los paths pueden variar según la implementación, pero típicamente:

* `GET /inventory/{productId}`
  Obtiene la cantidad disponible de un producto.
* `PUT /inventory/{productId}`
  Actualiza la cantidad (ej: tras compra o ajuste).
* `POST /inventory/{productId}`
  Crea o ajusta inventario (según diseño del controller).

---

## OpenAPI / Swagger

Documentación disponible en:

* `/swagger-ui.html` (o `/swagger-ui/index.html` según versión)
* `/v3/api-docs`

---

## Tests

Ejecutar la suite completa:

```bash
mvn test
```

Incluye:

* Pruebas unitarias del service.
* Manejo de errores (inventario no encontrado, producto no encontrado).
* Pruebas de integración del ProductsClient usando WireMock (sin depender de ms-products real).

---

## Notas técnicas

* La integración con ms-products es síncrona (HTTP).
* El cliente HTTP implementa timeout y reintentos básicos.
* El evento de cambio de inventario se implementa como un log informativo.
* El esquema de base de datos se gestiona con Hibernate.

---
