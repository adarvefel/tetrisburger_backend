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