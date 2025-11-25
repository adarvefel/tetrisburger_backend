Pruebas en postman para los usuarios
Al final estan los cURLS:

Autenticación
Login
Autentica un usuario y retorna un token JWT.

Endpoint: POST /api/auth/login

json
{
"email": "pipe58@gmail.com",
"password": "pipe1234"
}
Respuesta (200):

json
{
"token": "eyJhbGciOiJIUzI1NiJ9...",
"tokenType": "Bearer",
"expiresIn": 3600000,
"user": {
"idUser": 2,
"userName": "felipeSA",
"email": "pipe58@gmail.com",
"role": "ADMIN"
},
"timestamp": "2025-11-05T17:04:37.037933"
}
Registro
Crea una nueva cuenta de usuario.

Endpoint: POST /api/auth/register

json
{
"userName": "felipeSa",
"email": "pipe58@gmail.com",
"password": "pipe1234"
}
Respuesta (201):

json
{
"idUser": 6,
"userName": "rompecucas",
"email": "rompecucas12@gmail.com",
"userImage": null,
"phone": null,
"createdAt": "2025-11-05T15:00:45.2081821"
}
Recuperar Contraseña
Envía un enlace de recuperación al correo del usuario.

Endpoint: POST /api/auth/forgot-password

json
{
"email": "adarvefelipe58@gmail.com"
}
Respuesta (200):

json
{
"message": "Correo de recuperación enviado",
"success": true,
"timestamp": 1762381426062
}
Reset de Contraseña
Actualiza la contraseña con el token enviado al correo.

Endpoint: POST /api/auth/reset-password

json
{
"token": "eyJhbGciOiJIUzI1NiJ9...",
"newPassword": "felipe12345"
}
Respuesta (200):

json
{
"message": "Contraseña actualizada",
"success": true,
"timestamp": 1762381521207
}
Perfil de Usuario
Obtener Perfil
Retorna los datos del usuario autenticado.

Endpoint: GET /api/profile

Header requerido:

text
Authorization: Bearer {token}
Respuesta (200):

json
{
"idUser": 6,
"userName": "rompecucas",
"email": "rompecucas12@gmail.com",
"userImage": null,
"role": "CLIENT",
"phone": null,
"createdAt": "2025-11-05T15:00:45.208182"
}
Actualizar Perfil
Actualiza los datos del perfil del usuario autenticado.

Endpoint: PATCH /api/profile

Header requerido:

text
Authorization: Bearer {token}
Content-Type: application/json
Body:

json
{
"userName": "queRicolacuca",
"userImage": null,
"phone": "3017342342"
}
Respuesta (200):

json
{
"idUser": 6,
"userName": "queRicolacuca",
"email": "rompecucas12@gmail.com",
"userImage": "aaaaaa",
"phone": "3017342342",
"createdAt": "2025-11-05T15:00:45.208182",
"updatedAt": "2025-11-05T16:56:48.054809",
"createdBy": null,
"updatedBy": 6
}
Eliminar Perfil
Realiza soft delete del usuario autenticado.

Endpoint: DELETE /api/profile

Header requerido:

text
Authorization: Bearer {token}
Respuesta (200):

json
{
"idUser": 6,
"userName": "queRicolacuca",
"email": "rompecucas12@gmail.com",
"userImage": "aaaaaa",
"phone": "3017342342",
"createdAt": "2025-11-05T15:00:45.208182",
"updatedAt": "2025-11-05T16:56:48.054809",
"createdBy": null,
"updatedBy": 6
}
Administración de Usuarios (Solo ADMIN)
Crear Usuario
Crea un nuevo usuario con rol específico.

Endpoint: POST /api/admin/users

Header requerido:

text
Authorization: Bearer {token}
Content-Type: application/json
Body:

json
{
"userName": "joseComeGordas",
"email": "jose56@gmail.com",
"password": "amogorda1234",
"userImage": "ttgj",
"role": "EMPLOYEE",
"phone": null
}
Respuesta (201):

json
{
"idUser": 7,
"userName": "joseComeGordas",
"email": "jose56@gmail.com",
"userImage": "ttgj",
"role": "EMPLOYEE",
"phone": null,
"createdAt": "2025-11-05T17:05:39.4572149",
"updatedAt": "2025-11-05T17:05:39.4572149",
"createdBy": 2,
"updatedBy": 2
}
Listar Usuarios
Retorna lista de todos los usuarios con paginación.

Endpoint: GET /api/admin/users

Header requerido:

text
Authorization: Bearer {token}
Respuesta (200):

json
{
"users": [
{
"idUser": 2,
"userName": "felipeSA",
"email": "adarvefelipe58@gmail.com",
"userImage": "3017342342",
"role": "ADMIN",
"phone": "askfkdflkdsfkl",
"createdAt": "2025-11-04T10:20:30.088329",
"updatedAt": "2025-11-04T20:52:18.37493",
"deleteAt": null,
"createdBy": null,
"updatedBy": null,
"deletedBy": null
}
],
"totalElements": 5,
"totalPages": 1,
"timestamp": "2025-11-05T17:06:13.6351691"
}
Actualizar Usuario
Actualiza datos de un usuario específico.

Endpoint: PUT /api/admin/users/{idUser}

Header requerido:

text
Authorization: Bearer {token}
Content-Type: application/json
Body:

json
{
"userName": "Saralegustachimbo238",
"phone": "41421"
}
Respuesta (200):

json
{
"idUser": 3,
"userName": "Saralegustachimbo238",
"email": "saracomepenes69@gmail.com",
"userImage": null,
"role": "EMPLOYEE",
"phone": "41421",
"createdAt": "2025-11-04T10:36:33.958751",
"updatedAt": "2025-11-05T14:56:13.812869",
"createdBy": 2,
"updatedBy": 2
}
Obtener Usuario por ID
Retorna los datos de un usuario específico.

Endpoint: GET /api/admin/users/{idUser}

Header requerido:

text
Authorization: Bearer {token}
Respuesta (200):

json
{
"idUser": 3,
"userName": "Saralegustachimbo08",
"email": "saracomepenes69@gmail.com",
"userImage": null,
"role": "EMPLOYEE",
"phone": null,
"createdAt": "2025-11-04T10:36:33.958751",
"updatedAt": "2025-11-05T14:56:13.812869",
"deleteAt": null,
"createdBy": 2,
"updatedBy": 2,
"deletedBy": null
}
Eliminar Usuario
Realiza soft delete de un usuario.

Endpoint: DELETE /api/admin/users/{idUser}

Header requerido:

text
Authorization: Bearer {token}
Respuesta (200):

json
{
"message": "Usuario eliminado correctamente",
"idUser": 4
}
Notas Importantes
Autenticación: Todos los endpoints excepto /auth/login, /auth/register y /auth/forgot-password requieren un token JWT
válido en el header Authorization: Bearer {token}

Roles: ADMIN, EMPLOYEE, CLIENT

Soft Delete: Los usuarios eliminados no se borran completamente, solo se marcan con deletedAt

Auditoría: Los campos createdBy, updatedBy y deletedBy registran qué usuario realizó cada acción



AUTH - Todos los Curls
---------------------------------------------------
Login
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "pipe58@gmail.com",
"password": "pipe1234"
}'

---------------------------------------------------------------
Register

curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
"userName": "felipeSa",
"email": "pipe58@gmail.com",
"password": "pipe1234"
}'

---------------------------------------------------------------------------
Forgot Password
curl --location 'http://localhost:8080/api/auth/forgot-password' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "adarvefelipe58@gmail.com"
}'

---------------------------------------------------------------------
Reset Password

curl --location 'http://localhost:8080/api/auth/reset-password' \
--header 'Content-Type: application/json' \
--data '{
"token":"
eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicGFzc3dvcmRfcmVzZXQiLCJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzMDczODQsImV4cCI6MTc2MjMxMDk4NH0.ZxjGefem6_
-I9ccwVIQUfqsmxpYLZnTcuP4HaJu3aIQ",
"newPassword":"felipe12345"
}'

--------------------
USER - Todos los Curls
--------------------------
Get Profile
curl --location 'http://localhost:8080/api/profile' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb21wZWN1Y2FzMTJAZ21haWwuY29tIiwiaWF0IjoxNzYyMzc4MTQ3LCJleHAiOjE3NjIzODE3NDd9.W8i1sBQmwD01wy2plew1qkahdMVxoTdkhXic2CJxRkw'
---------------------------------------------------------------------
Update Profile (PATCH)
curl --location --request PATCH 'http://localhost:8080/api/profile' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb21wZWN1Y2FzMTJAZ21haWwuY29tIiwiaWF0IjoxNzYyMzc5NzIxLCJleHAiOjE3NjIzODMzMjF9.CIm4wYtvOucM4Ib_thBLpOOBZCxzy4tS60HACDVB8SQ' \
--header 'Content-Type: application/json' \
--data '{
"userName": "queRicolacuca",
"userImage": null,
"phone": "3017342342"
}'
-------------------------------------------------------
Delete Profile
curl --location --request DELETE 'http://localhost:8080/api/profile' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjE1MDM5MzIsImV4cCI6MTc2MTUwNzUzMn0.QpolO523GCrrVSm8qTh3Tjmyp-LhaSc7C-H4KSDddzc'

----------------------------------------
ADMIN - Todos los Curls
----------------------------------------
Create User (POST)

curl --location 'http://localhost:8080/api/admin/users' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzODAyNzYsImV4cCI6MTc2MjM4Mzg3Nn0.kDz72LU2nRceGunhxsjkRjnTUxvF69oYXlcv6ht_Ylc' \
--header 'Content-Type: application/json' \
--data-raw '{
"userName": "joseComeGordas",
"email": "jose56@gmail.com",
"password":"amogorda1234",
"userImage": "ttgj",
"role": "EMPLOYEE",
"phone": null
}'
------------------------------------------------------
Get All Users (GET)
curl --location --request GET 'http://localhost:8080/api/admin/users' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzODAyNzYsImV4cCI6MTc2MjM4Mzg3Nn0.kDz72LU2nRceGunhxsjkRjnTUxvF69oYXlcv6ht_Ylc' \
--header 'Content-Type: application/json'
-------------------------------------------------------------
Update User (PUT)
curl --location --request PUT 'http://localhost:8080/api/admin/users/3' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzODAyNzYsImV4cCI6MTc2MjM4Mzg3Nn0.kDz72LU2nRceGunhxsjkRjnTUxvF69oYXlcv6ht_Ylc' \
--header 'Content-Type: application/json' \
--data '{
"userName": "Saralegustachimbo238",
"phone": "41421"
}'
---------------------------------
Get User by ID
curl --location 'http://localhost:8080/api/admin/users/3' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzODAyNzYsImV4cCI6MTc2MjM4Mzg3Nn0.kDz72LU2nRceGunhxsjkRjnTUxvF69oYXlcv6ht_Ylc'
-------------------------
Delete User
curl --location --request DELETE 'http://localhost:8080/api/admin/users/4' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFydmVmZWxpcGU1OEBnbWFpbC5jb20iLCJpYXQiOjE3NjIzNjg0MDEsImV4cCI6MTc2MjM3MjAwMX0.cgh4fFx_tnIe-UaGwHjq0zGdwRMetB3ODK0iVTgivLE'

# Módulo de Productos - TetrisBurger API

## Introducción

El módulo de **Productos** de **TetrisBurger** permite la gestión completa de los productos ofrecidos por el
restaurante.  
Todos los endpoints requieren **rol ADMIN**, ya que las operaciones incluyen creación, actualización, eliminación,
ajuste de stock y disponibilidad, además de la consulta de productos.

La API se expone en:  
`http://localhost:8080/api/products`

Cada producto contiene:

- `id`: Identificador único.
- `name`: Nombre del producto.
- `description`: Descripción breve.
- `quantity`: Cantidad disponible.
- `price`: Precio del producto.
- `availability`: Estado de disponibilidad (`true` o `false`).
- `productType`: Tipo de producto (FOOD, DRINK, etc.).
- `ingredientType`: Tipo de ingrediente (BURGER, SAUCE, etc.).
- `burgerIngredient`: Indica si es ingrediente de hamburguesa.
- `productCategoryId`: Categoría del producto.
- `supplierId`: Proveedor del producto.

Ejemplo de producto:

json
{
"id": 16,
"name": "Burger King XXXL",
"description": "Mista",
"quantity": 25,
"price": 250000.00,
"availability": true,
"productType": "FOOD",
"ingredientType": "BURGER",
"burgerIngredient": false,
"productCategoryId": 1,
"supplierId": 2
}

Crear un producto
Método: POST
URL: /api/products
Descripción: Crea un nuevo producto.
Cuerpo del Request:

{
"name": "Burger King XXXL",
"description": "Mista",
"quantity": 25,
"price": 250000.00,
"availability": true,
"productType": "FOOD",
"ingredientType": "BURGER",
"burgerIngredient": false,
"productCategoryId": 1,
"supplierId": 2
}

Rol requerido: ADMIN
Ejemplo de Response: Devuelve el producto creado con id.
curl --location 'http://localhost:8080/api/products' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzkyMjk2LCJleHAiOjE3NjIzOTU4OTZ9.8FPvuCXMoOGFsL19_ncGQS8gXLhpX4W5BOra4b52P0Y' \
--header 'Content-Type: application/json' \
--data '{
"name": "Burger King",
"description": "Mista",
"quantity": 25,
"price": 10000.00,
"availability": true,
"productType": "FOOD",
"ingredientType": "BURGER",
"burgerIngredient": false,
"productCategoryId": 1,
"supplierId": 2
}'

Obtener producto por ID
Método: GET
URL: /api/products/{id}
Descripción: Obtiene un producto específico por su ID.
Parámetros:
id (path) – Identificador del producto
Rol requerido: ADMIN
Ejemplo de Request: GET http://localhost:8080/api/products/{id}
curl --location 'http://localhost:8080/api/products/16' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzkyMjk2LCJleHAiOjE3NjIzOTU4OTZ9.8FPvuCXMoOGFsL19_ncGQS8gXLhpX4W5BOra4b52P0Y'

Listar productos
Método: GET
URL: /api/products
Descripción: Lista todos los productos con paginación y filtros opcionales.
Parámetros opcionales:
productCategoryId – Filtra por categoría
availability – Filtra por disponibilidad (true o false)
page – Número de página (default 0)
size – Cantidad de productos por página (default 12)
sortBy – Campo de orden
direction – ASC o DESC (default ASC)
Rol requerido: ADMIN
Ejemplo de Request:http://localhost:8080/api/products
curl --location 'http://localhost:8080/api/products' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzQ3Mjc5LCJleHAiOjE3NjIzNTA4Nzl9.okRVhK8V0BIHtbWXm-j7AQ9uVz7BhZmLJTEXfZvM-1M'

Buscar productos por texto
Método: GET
URL: /api/products/search
Descripción: Permite buscar productos por nombre o descripción.
Parámetros:
q – Término de búsqueda (obligatorio)
productCategoryId – Filtra por categoría
availability – Filtra por disponibilidad
page, size, sortBy, direction – Igual que listar productos
Rol requerido: ADMIN
Ejemplo de Request:http://localhost:8080/api/products/search?q=pulga
curl --location 'http://localhost:8080/api/products/search?q=pulga' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzQ3Mjc5LCJleHAiOjE3NjIzNTA4Nzl9.okRVhK8V0BIHtbWXm-j7AQ9uVz7BhZmLJTEXfZvM-1M'

Actualizar un producto
Método: PUT
URL: /api/products/{id}
Descripción: Actualiza un producto existente.
Parámetros: id (path)
Cuerpo del Request:
{
"name": "Burger King Mega",
"description": "Mista XXL",
"quantity": 30,
"price": 270000.00,
"availability": true,
"productType": "FOOD",
"ingredientType": "BURGER",
"burgerIngredient": false,
"productCategoryId": 1,
"supplierId": 2
}
Rol requerido: ADMIN
Ejemplo de Response: Producto actualizado.
curl --location --request PUT 'http://localhost:8080/api/products/16' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzkyMjk2LCJleHAiOjE3NjIzOTU4OTZ9.8FPvuCXMoOGFsL19_ncGQS8gXLhpX4W5BOra4b52P0Y' \
--header 'Content-Type: application/json' \
--data '{
"name": "Burger King XXXL",
"quantity": 25,
"price": 250000.00,
"availability": true,
"productType": "FOOD",
"ingredientType": "BURGER",
"burgerIngredient": false,
"productCategoryId": 1,
"supplierId": 2
}'

Eliminar un producto
Método: DELETE
URL: /api/products/{id}
Descripción: Elimina un producto del sistema.
Parámetros: id (path)
Rol requerido: ADMIN
Ejemplo de Response: Código 204 No Content.
http://localhost:8080/api/products/{id}
curl --location --request DELETE 'http://localhost:8080/api/products/15' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyMzQ3Mjc5LCJleHAiOjE3NjIzNTA4Nzl9.okRVhK8V0BIHtbWXm-j7AQ9uVz7BhZmLJTEXfZvM-1M'

Cambiar disponibilidad
Método: PATCH
URL: /api/products/{id}/availability
Descripción: Cambia el estado de disponibilidad de un producto.
Parámetros:
id (path)
availability (query) – true o false
Rol requerido: ADMIN
Ejemplo de Request: PATCH http://localhost:8080/api/products/16/availability?availability=false
curl --location --request PATCH 'http://localhost:8080/api/products/10/availability?availability=false' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGVAZ21haWwuY29tIiwiaWF0IjoxNzYyMzA2MTQ1LCJleHAiOjE3NjIzMDk3NDV9.KBUTqaP5NY_Sc_2iJ_X-VWQV3Uhs57x9HoELtfPxEb4'

Ajustar stock
Método: PATCH
URL: /api/products/{id}/stock
Descripción: Ajusta la cantidad disponible de un producto.
Parámetros:
id (path)
delta (query) – Positivo para aumentar, negativo para disminuir
Rol requerido: ADMIN
Ejemplo de Request: PATCH http://localhost:8080/api/products/{id}/availability?availability=false
curl --location --request PATCH 'http://localhost:8080/api/products/10/stock?delta=10' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGVAZ21haWwuY29tIiwiaWF0IjoxNzYyMzA2MTQ1LCJleHAiOjE3NjIzMDk3NDV9.KBUTqaP5NY_Sc_2iJ_X-VWQV3Uhs57x9HoELtfPxEb4'

# Módulo de Categorías de Producto - TetrisBurger API

## Introducción

El módulo de **Categorías de Producto** de **TetrisBurger** gestiona las distintas categorías a las que pueden
pertenecer los productos del restaurante.  
Este módulo permite **crear, actualizar, eliminar, listar y consultar categorías**, garantizando una organización clara
dentro del catálogo de productos.

Todos los endpoints requieren **rol ADMIN**, ya que las operaciones afectan directamente la estructura de clasificación
del inventario.

La API se expone en:  
`http://localhost:8080/api/product-categories`

Cada categoría de producto contiene los siguientes atributos:

- `id`: Identificador único de la categoría.
- `name`: Nombre de la categoría.
- `description`: Descripción breve de la categoría.
- `available`: Estado de disponibilidad (`true` o `false`).

Ejemplo de categoría:
json
{
"name": "Snacks",
"description": "Acompañamientos",
"available": true
}

GET públicos
Listar categorías
URL: http://localhost:8080/api/product-categories (sin Authorization)
Respuesta 200 ejemplo:

Obtener por id
URL: http://localhost:8080/api/product-categories/{id} (sin Authorization)
Respuesta 200 ejemplo:
{ "id": 1, "name": "Burgers", "description": "Clásicas", "available": true }

POST crear (ADMIN)
URL: http://localhost:8080/api/product-categories
Headers:
Content-Type: application/json
Authorization: Bearer <JWT_ADMIN> (en Postman Auth Type = Bearer Token, pega el JWT)
Body JSON:
{
"name": "Sides",
"description": "Acompañamientos",
"available": true
}
Respuesta esperada: 201 Created con el recurso creado en el body.
curl --location 'http://localhost:8080/api/product-categories' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyNTMxNjA1LCJleHAiOjE3NjI1MzUyMDV9.rkEaEE0wOSqItPW4FOiUI_XM-GG0hjhGXc1XsBPY-rw' \
--data '
{
"name": "Snacks",
"description": "Acompañamientos",
"available": true
}'

PUT actualizar (ADMIN)
URL: http://localhost:8080/api/product-categories/{id}
Headers: Content-Type: application/json, Authorization: Bearer <JWT_ADMIN>
Body JSON:
{
"name": "Sides & Snacks",
"description": "Acompañamientos y bocados",
"available": true
}
Respuesta esperada: 200 OK con la categoría actualizada.
curl --location --request PUT 'http://localhost:8080/api/product-categories/4' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyNTMxNjA1LCJleHAiOjE3NjI1MzUyMDV9.rkEaEE0wOSqItPW4FOiUI_XM-GG0hjhGXc1XsBPY-rw' \
--data '{
"name": "Sides & Snacks",
"description": "Acompañamientos y bocados",
"available": true
}'

DELETE eliminar (ADMIN)
URL: http://localhost:8080/api/product-categories/{id}
Headers: Authorization: Bearer <JWT_ADMIN>
Respuesta esperada: 204 No Content sin body.
curl --location --request DELETE 'http://localhost:8080/api/product-categories/2' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyNTMxNjA1LCJleHAiOjE3NjI1MzUyMDV9.rkEaEE0wOSqItPW4FOiUI_XM-GG0hjhGXc1XsBPY-rw'

## Módulo de Proveedores - TetrisBurger API

# Introducción

El módulo de Proveedores (Suppliers) de TetrisBurger gestiona la información de las empresas o personas que suministran
insumos al restaurante.
Este módulo permite listar, consultar, crear, actualizar y eliminar proveedores, facilitando una administración
organizada de los contactos comerciales.

Los endpoints GET son públicos, es decir, no requieren autenticación, mientras que las operaciones POST, PUT y DELETE
requieren el rol ADMIN, ya que modifican la información del sistema.

La API se expone en:
`http://localhost:8080/api/suppliers`

Cada proveedor contiene los siguientes atributos:

id: Identificador único del proveedor.
`name`: Nombre del proveedor o empresa.
`phone`: Número de teléfono de contacto.
`email`: Correo electrónico del proveedor.
`address`: Dirección física.
`registrationDate`: Fecha de registro del proveedor.

Endpoints
🔹 Listar proveedores (GET público)

URL:
http://localhost:8080/api/suppliers?q=juan&page=0&size=12&sortBy=name&direction=ASC
Descripción:
Permite listar los proveedores registrados con soporte de búsqueda, paginación y ordenamiento.
No requiere autenticación.
Parámetros opcionales:
q: Palabra clave para buscar por nombre.
page: Número de página (por defecto 0).
size: Cantidad de registros por página (por defecto 12).
sortBy: Campo por el cual ordenar (por defecto name).
direction: Dirección de orden (ASC o DESC).
curl --location 'http://localhost:8080/api/suppliers'

Obtener proveedor por ID (GET público)
URL:
http://localhost:8080/api/suppliers/3
Descripción:
Consulta la información de un proveedor específico por su identificador.
No requiere autenticación.
Respuesta 200 (ejemplo):
curl --location 'http://localhost:8080/api/suppliers/1'

Crear proveedor (POST - ADMIN)
Authorization: Bearer <JWT_ADMIN>
URL:
http://localhost:8080/api/suppliers
{
"name": "Proveedor S.A.",
"phone": "3120001111",
"email": "contacto@proveedor.com",
"address": "Zona Industrial 45",
"registrationDate": "2025-01-10"
}
Respuesta esperada:
201 Created con el proveedor creado en el body.
curl --location 'http://localhost:8080/api/suppliers' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyNzQyMDA1LCJleHAiOjE3NjI3NDU2MDV9.kVV3Ql5f7JzHMhMQX1gYAXfNlQaTqxkymuizTMG_tQg' \
--data-raw '{
"name": "Mac Pollo",
"phone": "3120001111",
"email": "mpollo@proveedor.com",
"address": "Zona Industrial 04",
"registrationDate": "2025-01-10"
}'

Actualizar proveedor (PUT - ADMIN)
URL:
http://localhost:8080/api/suppliers/3
Headers:
Authorization: Bearer <JWT_ADMIN>
{
"name": "Proveedor S.A. Actualizado",
"phone": "3120002222",
"email": "contacto@proveedor.com",
"address": "Zona Industrial 50",
"registrationDate": "2025-01-10"
}
Respuesta esperada:
200 OK con el proveedor actualizado en el body.
curl --location --request PUT 'http://localhost:8080/api/suppliers/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZWZmcmV5bWcxMjNAZ21haWwuY29tIiwiaWF0IjoxNzYyNzQyMDA1LCJleHAiOjE3NjI3NDU2MDV9.kVV3Ql5f7JzHMhMQX1gYAXfNlQaTqxkymuizTMG_tQg' \
--data-raw '{
"name": "Proveedor S.A. Actualizado",
"phone": "3120002222",
"email": "contacto@proveedor.com",
"address": "Zona Industrial 50",
"registrationDate": "2025-01-10"
}'

Eliminar proveedor (DELETE - ADMIN)
URL:
http://localhost:8080/api/suppliers/3
Headers:
Authorization: Bearer <JWT_ADMIN>
Respuesta esperada:
204 No Content sin body.


SECCION DE HAMBURGUESA

1) Crear hamburguesa personalizada (cliente)
   Método: POST
   URL: http://localhost:8080/api/burgers/custom

Headers en Postman:

Key: Content-Type → application/json

Key: Authorization → Bearer <token_cliente>

Body (tab Body → raw → JSON):

json
{
"name": "come cuca 200ks",
"description": "Hamburguesa test",
"imageUrl": "https://example.com/images/burger-cliente.png",
"ingredients": [
{
"idProduct": 9,
"quantity": 2,
"isOptional": false
},
{
"idProduct": 7,
"quantity": 2,
"isOptional": false
},
{
"idProduct": 8,
"quantity": 1,
"isOptional": false
},
{
"idProduct": 12,
"quantity": 1,
"isOptional": false
}
]
}
Respuesta ejemplo:

json
{
"idBurger": 25,
"name": "come cuca 200ks",
"description": "Hamburguesa test",
"basePrice": 0,
"finalPrice": 7200.00,
"isOnMenu": false,
"isFavorite": false,
"isCustom": true,
"availability": true,
"imageUrl": "https://example.com/images/burger-cliente.png",
"idUser": 6,
"timesOrdered": 0,
"createdAt": "2025-11-20T16:06:05.056567800Z",
"updatedAt": "2025-11-20T16:06:05.056567800Z",
"deletedAt": null,
"createdBy": 6,
"updatedBy": 6,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 52,
"idProduct": 9,
"priceAtTime": 500.00,
"quantity": 2,
"isOptional": false
},
{
"idBurgerIngredient": 53,
"idProduct": 7,
"priceAtTime": 1200.00,
"quantity": 2,
"isOptional": false
},
{
"idBurgerIngredient": 54,
"idProduct": 8,
"priceAtTime": 1800.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 55,
"idProduct": 12,
"priceAtTime": 2000.00,
"quantity": 1,
"isOptional": false
}
]
}
2) Crear hamburguesa de menú (admin)
   Método: POST
   URL: http://localhost:8080/api/admin/burgers/menu

Headers:

Content-Type: application/json

Authorization: Bearer <token_admin>

Body:

json
{
"name": "Burger Deluxe",
"description": "Hamburguesa gourmet con queso cheddar, tocino y salsa especial",
"imageUrl": "https://cdn.tetrisburger.com/menu/burger-deluxe.png",
"favorite": false,
"ingredients": [
{
"idProduct": 6,
"quantity": 1,
"isOptional": false
},
{
"idProduct": 7,
"quantity": 1,
"isOptional": false
},
{
"idProduct": 8,
"quantity": 1,
"isOptional": true
},
{
"idProduct": 15,
"quantity": 1,
"isOptional": true
}
]
}
Respuesta ejemplo:

json
{
"idBurger": 27,
"name": "Burger Deluxe",
"description": "Hamburguesa gourmet con queso cheddar, tocino y salsa especial",
"basePrice": 9500.00,
"finalPrice": 9500.00,
"isOnMenu": true,
"isFavorite": false,
"isCustom": false,
"availability": true,
"imageUrl": "https://cdn.tetrisburger.com/menu/burger-deluxe.png",
"timesOrdered": 0,
"createdAt": "2025-11-20T17:56:04.6926153",
"updatedAt": "2025-11-20T17:56:04.6926153",
"deletedAt": null,
"updatedBy": 2,
"createdBy": 2,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 60,
"idProduct": 6,
"priceAtTime": 4500.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 61,
"idProduct": 7,
"priceAtTime": 1200.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 62,
"idProduct": 8,
"priceAtTime": 1800.00,
"quantity": 1,
"isOptional": true
},
{
"idBurgerIngredient": 63,
"idProduct": 15,
"priceAtTime": 2000.00,
"quantity": 1,
"isOptional": true
}
]
}
3) Obtener burger por ID (pública/autenticada)
   Método: GET
   URL: http://localhost:8080/api/burgers/15

Headers:

Authorization: Bearer <token_cliente>

Body: (no se envía body en Postman, déjalo vacío)

Respuesta ejemplo (similar a tus datos):

json
{
"idBurger": 15,
"name": "Cheeseburger Clásica",
"description": "Hamburguesa de la casa con queso cheddar y salsa especial",
"basePrice": 4500.00,
"finalPrice": 12000.00,
"isOnMenu": true,
"isFavorite": true,
"isCustom": false,
"availability": true,
"imageUrl": "https://cdn.tetrisburger.com/menu/cheeseburger-clasica.png",
"timesOrdered": 0,
"createdAt": "2025-11-16T14:47:46.313016",
"updatedAt": "2025-11-20T14:47:46.313016",
"deletedAt": null,
"createdBy": 2,
"updatedBy": 2,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 2,
"idProduct": 6,
"priceAtTime": 4500.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 3,
"idProduct": 7,
"priceAtTime": 1200.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 4,
"idProduct": 8,
"priceAtTime": 1800.00,
"quantity": 1,
"isOptional": true
}
]
}
4) Listar hamburguesas de menú (admin, paginado)
   Método: GET
   URL: http://localhost:8080/api/admin/burgers/menu

Puedes usar query params page y size si quieres (ej: ?page=0&size=10).

Headers:

Authorization: Bearer <token_admin>

Body: vacío.

Respuesta ejemplo:

json
{
"content": [
{
"idBurger": 2,
"name": "Cheeseburger Clásica",
"description": "Hamburguesa de la casa con queso cheddar y salsa especial",
"basePrice": 0.00,
"finalPrice": 0,
"isOnMenu": true,
"isFavorite": true,
"isCustom": false,
"availability": true,
"imageUrl": "https://cdn.tetrisburger.com/menu/cheeseburger-clasica.png",
"timesOrdered": 0,
"createdAt": "2025-11-16T14:47:46.313016",
"updatedAt": "2025-11-16T14:47:46.313016",
"deletedAt": null,
"updatedBy": 2,
"createdBy": 2,
"deletedBy": null,
"ingredients": []
},
{
"idBurger": 9,
"name": "Cheeseburger Clásica",
"description": "Hamburguesa de la casa con queso cheddar y salsa especial",
"basePrice": 0.00,
"finalPrice": 0,
"isOnMenu": true,
"isFavorite": true,
"isCustom": false,
"availability": true,
"imageUrl": "https://cdn.tetrisburger.com/menu/cheeseburger-clasica.png",
"timesOrdered": 0,
"createdAt": "2025-11-17T12:16:29.892058",
"updatedAt": "2025-11-17T12:16:29.892058",
"deletedAt": null,
"updatedBy": 2,
"createdBy": 2,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 2,
"idProduct": 6,
"priceAtTime": 4500.00,
"quantity": 1,
"isOptional": false
}
]
}
// ...
],
"page": 0,
"size": 10,
"totalElements": 10,
"totalPages": 1,
"first": true,
"last": true
}
5) Listar hamburguesas custom del usuario (cliente)
   Método: GET
   URL: http://localhost:8080/api/burgers/custom/mine

Headers:

Authorization: Bearer <token_cliente>

Body: vacío.

Respuesta ejemplo:

json
{
"content": [
{
"idBurger": 25,
"name": "come cuca 200ks",
"description": "Hamburguesa test",
"basePrice": 0.00,
"finalPrice": 7200.00,
"isOnMenu": false,
"isFavorite": false,
"isCustom": true,
"availability": true,
"imageUrl": "https://example.com/images/burger-cliente.png",
"idUser": 6,
"timesOrdered": 0,
"createdAt": "2025-11-20T16:06:05.056568Z",
"updatedAt": "2025-11-20T16:06:05.056568Z",
"deletedAt": null,
"createdBy": 6,
"updatedBy": 6,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 52,
"idProduct": 9,
"priceAtTime": 500.00,
"quantity": 2,
"isOptional": false
}
]
}
// ...
],
"page": 0,
"size": 10,
"totalElements": 3,
"totalPages": 1,
"first": true,
"last": true
}
6) Actualizar hamburguesa custom (cliente)
   Método: PUT
   URL: http://localhost:8080/api/burgers/custom/25

Headers:

Content-Type: application/json

Authorization: Bearer <token_cliente>

Body:

json
{
"name": "Me encanta la cuca burger",
"description": "hamburguesa de cuca ",
"imageUrl": "https://cdn.miapp.com/images/custom-burger-123.png",
"ingredients": [
{
"idProduct": 6,
"quantity": 1,
"isOptional": false
},
{
"idProduct": 9,
"quantity": 1,
"isOptional": true
}
]
}
Respuesta ejemplo:

json
{
"idBurger": 25,
"name": "Me encanta la cuca burger",
"description": "hamburguesa de cuca",
"basePrice": 0.00,
"finalPrice": 5000.00,
"isOnMenu": false,
"isFavorite": false,
"isCustom": true,
"availability": true,
"imageUrl": "https://cdn.miapp.com/images/custom-burger-123.png",
"idUser": 6,
"timesOrdered": 0,
"createdAt": "2025-11-20T16:06:05.056568Z",
"updatedAt": "2025-11-20T18:31:19.029026100Z",
"deletedAt": null,
"createdBy": 6,
"updatedBy": 6,
"deletedBy": null,
"ingredients": [
{
"idBurgerIngredient": 64,
"idProduct": 6,
"priceAtTime": 4500.00,
"quantity": 1,
"isOptional": false
},
{
"idBurgerIngredient": 65,
"idProduct": 9,
"priceAtTime": 500.00,
"quantity": 1,
"isOptional": true
}
]
}
7) Actualizar hamburguesa de menú (admin)
   Método: PUT
   URL: http://localhost:8080/api/admin/burgers/menu/17

Headers:

Content-Type: application/json

Authorization: Bearer <token_admin>

Body:

json
{
"name": "Clásica doble editada",
"description": "Doble carne, queso cheddar y bacon",
"imageUrl": "https://example.com/images/menu-burger-3.png",
"ingredients": [
{
"idProduct": 9,
"quantity": 1,
"isOptional": false
},
{
"idProduct": 13,
"quantity": 2,
"isOptional": false
}
],
"availability": true,
"favorite": true,
"onMenu": true
}
Respuesta: (depende de tu implementación, similar al create de menú con datos actualizados).

8) Marcar hamburguesa custom como favorita (cliente)
   Método: POST
   URL: http://localhost:8080/api/burgers/custom/19/favorite

Headers:

Authorization: Bearer <token_cliente>

Body: vacío.

Respuesta ejemplo (ideal):

json
{
"idBurger": 19,
"isFavorite": true,
"message": "Hamburguesa marcada como favorita"
}
9) Actualizar precio de hamburguesa de menú (admin)
   Método: PATCH
   URL: http://localhost:8080/api/admin/burgers/menu/18/price

Headers:

Content-Type: application/json

Authorization: Bearer <token_admin>

Body:

json
{
"newPrice": 20000.00
}
Respuesta ejemplo:

json
{
"idBurger": 18,
"name": "Cheeseburger Clásica",
"basePrice": 20000.00,
"finalPrice": 20000.00,
"isOnMenu": true,
"availability": true,
"updatedAt": "2025-11-20T16:08:53.296522",
"updatedBy": 2
}
10) Buscar hamburguesas de menú por nombre (admin)
    Método: GET
    URL:
    http://localhost:8080/api/admin/burgers/menu/search?name=burger
    (o con paginación: ?name=burger&page=0&size=10)

Headers:

Authorization: Bearer <token_admin>

Body: vacío.

Respuesta: es un listado paginado como el de “listar menú”, filtrado por name.

11) Buscar hamburguesas custom del usuario por nombre (cliente)
    Método: GET
    URL:
    http://localhost:8080/api/burgers/custom/search?name=come
    (opcional: &page=0&size=10)

Headers:

Authorization: Bearer <token_cliente>

Body: vacío (no envíes JSON en GET).

DELETE admin – borrar burger de menú (soft delete)
Endpoint:
DELETE http://localhost:8080/api/admin/burgers/menu/{idBurger}

cURL:

bash
curl --location --request DELETE 'http://localhost:8080/api/admin/burgers/menu/27' \
--header 'Authorization: Bearer <token_admin>'
JSON de respuesta (200 OK con MessageResponseDTO):

json
{
"message": "Hamburguesa de menú eliminada correctamente",
"success": true,
"timestamp": 1732137600000
}
DELETE cliente – borrar burger personalizada (soft delete)
Endpoint:
DELETE http://localhost:8080/api/burgers/custom/{idBurger}

cURL:

bash
curl --location --request DELETE 'http://localhost:8080/api/burgers/custom/25' \
--header 'Authorization: Bearer <token_cliente>'
JSON de respuesta (200 OK con MessageResponseDTO):

json
{
"message": "Hamburguesa personalizada eliminada correctamente",
"success": true,
"timestamp": 1732137600000
}

---

# Módulo de PQRS 📬

---

## Crear una PQRS

Endpoint para registrar una nueva PQRS en el sistema.

### Endpoint

- Método: `POST`
- URL: `http://localhost:8080/api/pqrs`
- Ruta: `/api/pqrs`
- Autenticación: Requiere usuario autenticado

### Cuerpo de la petición (JSON)

```
{
  "type": "CLAIM",
  "subject": "Pedido retardado",
  "description": "El pedido llegó 2 horas tarde."
}
```

### Campos de la petición

- `type`: Tipo de PQRS, por ejemplo `CLAIM`, `REQUEST`, etc.
- `subject`: Asunto o título corto de la PQRS.
- `description`: Descripción detallada del caso reportado.

### Respuesta exitosa (201 Created)

```
{
  "idPqrs": 16,
  "type": "CLAIM",
  "status": "RECEIVED",
  "priority": "MEDIUM",
  "subject": "Pedido retardado",
  "description": "El pedido llegó 2 horas tarde.",
  "response": null,
  "idUser": 5,
  "assignedTo": null
}
```

### Campos de la respuesta

- `idPqrs`: Identificador único de la PQRS creada.
- `type`: Tipo de PQRS registrado.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.

### Ejemplo con cURL

```
curl --location 'http://localhost:8080/api/pqrs' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer <TOKEN>' \
  --data '{
    "type": "CLAIM",
    "subject": "Pedido retardado",
    "description": "El pedido llegó 2 horas tarde."
  }'
```

---

## Actualizar una PQRS

Endpoint para actualizar una PQRS existente en el sistema.
### Endpoint

- Método: `PATCH`
- URL: `http://localhost:8080/api/pqrs/{idPqrs}`
- Ruta: `/api/pqrs/{idPqrs}`
- Autenticación: Requiere usuario autenticado

### Parámetros de ruta

- `idPqrs`: Identificador único de la PQRS a actualizar.

### Cuerpo de la petición (JSON)

```
{
  "type": "CLAIM",
  "subject": "Pedido retardado_actualizado",
  "description": "El malparido rappi llego 2 horas tarde_acutualizado"
}
```

### Campos de la petición

- `type`: Tipo de PQRS, por ejemplo `CLAIM`, `REQUEST`, etc.
- `subject`: Asunto o título corto de la PQRS.
- `description`: Descripción detallada del caso reportado.

### Respuesta exitosa (200 OK)

```
{
    "idPqrs": 16,
    "type": "CLAIM",
    "status": "RECEIVED",
    "priority": "MEDIUM",
    "subject": "Pedido retardado_actualizado",
    "description": "El malparido rappi llego 2 horas tarde_acutualizado",
    "response": null,
    "idUser": 5,
    "assignedTo": null
}
```
### Campos de la respuesta

- `idPqrs`: Identificador único de la PQRS creada.
- `type`: Tipo de PQRS registrado.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.

### Ejemplo con cURL

```
curl --location --request PATCH 'http://localhost:8080/api/pqrs/16' \
--header 'Content-Type: application/json' \
--header 'Authorization: ••••••' \
--data '{
  "type": "CLAIM",
  "subject": "Pedido retardado_actualizado",
  "description": "El malparido rappi llego 2 horas tarde_acutualizado"
}
'
```
----

## Eliminar una PQRS

Endpoint para eliminar una PQRS existente en el sistema.
### Endpoint

- Método: `DELETE`
- URL: `http://localhost:8080/api/pqrs/{idPqrs}`
- Ruta: `/api/pqrs/{idPqrs}`
- Autenticación: Requiere usuario autenticado

### Parámetros de ruta

- `idPqrs`: Identificador único de la PQRS a eliminar.


### Respuesta exitosa (200 OK)

```
{
    "message": "PQRS eliminada corretamente.",
    "idPqrs": 16
}
```
### Campos de la respuesta

- `message`: Mensaje sobre la accion realizada.
- `idPqrs`: Identificador único de la PQRS eliminada.

### Ejemplo con cURL

```
curl --location --request DELETE 'http://localhost:8080/api/pqrs/16' \
--header 'Authorization: ••••••'
```
----

## Listar mis PQRS

Endpoint para obtener todas las PQRS asociadas al usuario autenticado, con soporte de paginación.

### Endpoint

- Método: `GET`
- URL: `http://localhost:8080/api/pqrs/me`
- Ruta: `/api/pqrs/me`
- Autenticación: Requiere usuario autenticado

### Parámetros de consulta (opcional)

- `page`: Número de página a retornar. Por defecto `0`.
- `size`: Cantidad de elementos por página. Por defecto `10`.



### Respuesta exitosa (200 OK)

```
{
    "pqrs": [
        {
            "idPqrs": 13,
            "type": "CLAIM",
            "status": "ANSWERED",
            "priority": "CRITICAL",
            "subject": "Burger desastroza",
            "description": "Me llego una hamburguesa con cucarachas.",
            "response": "CRITICAL",
            "idUser": 5,
            "assignedTo": 3
        },
        {
            "idPqrs": 14,
            "type": "CLAIM",
            "status": "ANSWERED",
            "priority": "CRITICAL",
            "subject": "Burger desastroza",
            "description": "Me llego una hamburguesa con cucarachas.",
            "response": "Lo sentimos en el culo, su dinero sera devolvido",
            "idUser": 5,
            "assignedTo": 3
        }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1
}
```
### Campos de la respuesta (paginación)

- `content`: Lista de PQRS correspondientes a la página actual.
- `page`: Número de página actual (basado en índice 0).
- `size`: Cantidad de elementos incluidos en la página actual.
- `totalElements`: Número total de PQRS que cumplen el criterio.
- `totalPages`: Número total de páginas disponibles.

### Campos de cada PQRS en `content`

- `idPqrs`: Identificador único de la PQRS.
- `type`: Tipo de PQRS, por ejemplo `CLAIM`, `REQUEST`, etc.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`, `IN_PROGRESS`, `ANSWERED`, `CLOSED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.
### Ejemplo con cURL

```
curl --location 'http://localhost:8080/api/pqrs/me' \
--header 'Authorization: ••••••'
```
----

## Responder una PQRS

Endpoint para que un administrador responda una PQRS existente en el sistema.
### Endpoint

- Método: `PATCH`
- URL: `http://localhost:8080/api/pqrs/admin/{idPqrs}`
- Ruta: `/api/pqrs/admin/{idPqrs}`
- Autenticación: Requiere usuario con rol administrador o empleado autenticado

### Parámetros de ruta

- `idPqrs`: Identificador único de la PQRS a responder.

### Cuerpo de la petición (JSON)

```
{
    "status":"ANSWERED",
    "priority":"CRITICAL",
    "response":"Lo sentimos en el culo, el veneco domiciliaro fue despedido."
}
```

### Campos de la petición

- `status`: Nuevo estado de la PQRS, por ejemplo `ANSWERED`.
- `priority`: Prioridad asignada tras la respuesta, por ejemplo `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- `response`: Texto de la respuesta que se envía al usuario.
### Respuesta exitosa (200 OK)

```
{
    "idPqrs": 15,
    "type": "PETITION",
    "status": "ANSWERED",
    "priority": "CRITICAL",
    "subject": "Prueba de peticion15_actualizada",
    "description": "Esta es la descripcion de  la prueba de peticion15_actualizada",
    "response": "Lo sentimos en el culo, el veneco domiciliaro fue despedido.",
    "idUser": 7,
    "assignedTo": 3
}
```
### Campos de la respuesta

- `idPqrs`: Identificador único de la PQRS creada.
- `type`: Tipo de PQRS registrado.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.

### Ejemplo con cURL

```
curl --location --request PATCH 'http://localhost:8080/api/pqrs/admin/15' \
--header 'Content-Type: application/json' \
--header 'Authorization: ••••••' \
--data '{
    "status":"ANSWERED",
    "priority":"CRITICAL",
    "response":"Lo sentimos en el culo, el veneco domiciliaro fue despedido."
}'
```
----

## Obtener una PQRS por ID

Endpoint para obtener una PQRS por ID existente en el sistema.
### Endpoint

- Método: `GET`
- URL: `http://localhost:8080/api/pqrs/{idPqrs}`
- Ruta: `/api/pqrs/{idPqrs}`
- Autenticación: Requiere usuario con rol Admin o Employee autenticado

### Parámetros de ruta

- `idPqrs`: Identificador único de la PQRS a consultar.


### Respuesta exitosa (200 OK)

```
{
    "idPqrs": 15,
    "type": "PETITION",
    "status": "ANSWERED",
    "priority": "CRITICAL",
    "subject": "Prueba de peticion15_actualizada",
    "description": "Esta es la descripcion de  la prueba de peticion15_actualizada",
    "response": "Lo sentimos en el culo, el veneco domiciliaro fue despedido.",
    "idUser": 7,
    "assignedTo": 3
}
```
### Campos de la respuesta
- `idPqrs`: Identificador único de la PQRS creada.
- `type`: Tipo de PQRS registrado.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.

### Ejemplo con cURL

```
curl --location 'http://localhost:8080/api/pqrs/15' \
--header 'Authorization: ••••••'

```
----

## Listar todas las PQRS

Endpoint para obtener todas las PQRS con soporte de paginación.

### Endpoint

- Método: `GET`
- URL: `http://localhost:8080/api/pqrs`
- Ruta: `/api/pqrs`
- Autenticación: Requiere usuario con rol ADMIN o Employee autenticado

### Parámetros de consulta (opcional)

- `page`: Número de página a retornar. Por defecto `0`.
- `size`: Cantidad de elementos por página. Por defecto `10`.
- `status`: Filtra las PQRS por estado, por ejemplo `RECEIVED`, `ANSWERED`, etc. (opcional).


### Respuesta exitosa (200 OK)

```
{
    "pqrs": [
        {
            "idPqrs": 3,
            "type": "PETITION",
            "status": "RECEIVED",
            "priority": "MEDIUM",
            "subject": "Prueba de peticion2",
            "description": "Esta es la descripcion de  la prueba de peticion2",
            "response": null,
            "idUser": 3,
            "assignedTo": null
        },
        {
            "idPqrs": 4,
            "type": "PETITION",
            "status": "RECEIVED",
            "priority": "MEDIUM",
            "subject": "Prueba de peticion3",
            "description": "Esta es la descripcion de  la prueba de peticion3",
            "response": null,
            "idUser": 3,
            "assignedTo": null
        },
        {
            "idPqrs": 5,
            "type": "PETITION",
            "status": "RECEIVED",
            "priority": "MEDIUM",
            "subject": "Prueba de peticion5_actualizada",
            "description": "Esta es la descripcion de  la prueba de peticion5_actualizada",
            "response": null,
            "idUser": 3,
            "assignedTo": null
        },
        {
            "idPqrs": 6,
            "type": "PETITION",
            "status": "RECEIVED",
            "priority": "MEDIUM",
            "subject": "Prueba de peticion5_actualizada",
            "description": "Esta es la descripcion de  la prueba de peticion5_actualizada",
            "response": null,
            "idUser": 3,
            "assignedTo": null
        },
        {
            "idPqrs": 7,
            "type": "PETITION",
            "status": "RECEIVED",
            "priority": "MEDIUM",
            "subject": "Prueba de peticion7",
            "description": "Esta es la descripcion de  la prueba de peticion6",
            "response": null,
            "idUser": 3,
            "assignedTo": null
        }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1
}
```
### Campos de la respuesta (paginación)

- `content`: Lista de PQRS correspondientes a la página actual.
- `page`: Número de página actual (basado en índice 0).
- `size`: Cantidad de elementos incluidos en la página actual.
- `totalElements`: Número total de PQRS que cumplen el criterio.
- `totalPages`: Número total de páginas disponibles.

### Campos de cada PQRS en `content`

- `idPqrs`: Identificador único de la PQRS.
- `type`: Tipo de PQRS, por ejemplo `CLAIM`, `REQUEST`, etc.
- `status`: Estado actual de la PQRS, por ejemplo `RECEIVED`, `IN_PROGRESS`, `ANSWERED`, `CLOSED`.
- `priority`: Prioridad asignada, por ejemplo `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- `subject`: Asunto de la PQRS.
- `description`: Descripción de la PQRS.
- `response`: Respuesta asociada a la PQRS (si existe), o `null` si aún no se ha respondido.
- `idUser`: Identificador del usuario que creó la PQRS.
- `assignedTo`: Identificador del agente o responsable asignado, o `null` si aún no tiene asignación.
### Ejemplo con cURL

```
curl --location 'http://localhost:8080/api/pqrs?page=0&size=10&status=RECEIVED' \
--header 'Authorization: ••••••'
```
----

