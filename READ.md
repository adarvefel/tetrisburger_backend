# 🍔 TetrisBurger Backend

REST API para el sistema de gestión de restaurante **TetrisBurger**, construida con
**Spring Boot 3** y **Java 21**. Soporta pedidos, hamburguesas personalizadas, pagos,
facturación, inventario y notificaciones por correo.

---

## 🛠️ Stack Tecnológico

| Tecnología        | Versión       | Uso                                |
|-------------------|---------------|------------------------------------|
| Java              | 21            | Lenguaje principal                 |
| Spring Boot       | 3.5.6         | Framework base                     |
| MySQL             | 8+            | Base de datos relacional           |
| JWT (jjwt)        | 0.12.5        | Autenticación stateless            |
| AWS SDK v2 (S3)   | 2.31.78       | Almacenamiento de imágenes         |
| MapStruct         | 1.5.5.Final   | Mapeo DTO ↔ Entidad                |
| Lombok            | 1.18.34       | Reducción de boilerplate           |
| SpringDoc OpenAPI | 2.8.8         | Documentación Swagger UI           |
| Bucket4j          | 8.10.1        | Rate limiting                      |
| Apache Tika       | 2.9.1         | Validación de tipo MIME en uploads |
| Brevo (Sendinblue)| SDK oficial   | Envío de correos transaccionales   |

---

## ⚙️ Requisitos Previos

- **Java 21+**
- **Maven 3.8+**
- **MySQL 8+** corriendo en `localhost:3306`
- Cuenta de **AWS** con bucket S3 creado
- Credenciales de **Google OAuth 2.0** configuradas
- Cuenta en **Brevo** con API key activa
- Cuenta en **reCAPTCHA v3** (Google)

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git https://github.com/adarvefel/tetrisburger_backend.git
cd tetrisburger_backend
```

### 2. Inicializar la base de datos

```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < seed.sql
```

> Esto crea la base de datos `railway`, las 20 tablas y carga todos los datos de prueba.

### 3. Configurar variables de entorno

Copia el archivo de ejemplo y rellena tus valores:

```bash
cp .env.example .env
```

Contenido del `.env`:

```env
# ── Base de datos ──────────────────────────────────────────
DB_URL=jdbc:mysql://localhost:3306/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=tu_password

# ── JWT ────────────────────────────────────────────────────
JWT_SECRET=tu_jwt_secret_minimo_32_caracteres
JWT_EXPIRATION=3600000        # ms → 1 hora

# ── AWS S3 ─────────────────────────────────────────────────
AWS_ACCESS_KEY_ID=tu_access_key
AWS_SECRET_ACCESS_KEY=tu_secret_key
AWS_PROFILE=tetrisburger
AWS_REGION=us-east-2
AWS_S3_BUCKET=tetrisburger-image

# ── Carpetas S3 ────────────────────────────────────────────
S3_FOLDER_PRODUCTS=products
S3_FOLDER_USERS=users
S3_FOLDER_BURGERS_MENU=burgers
S3_FOLDER_MENUS=menus
S3_FOLDER_ADDITION=addition
S3_FOLDER_INVOICES=invoices

# ── Google OAuth ───────────────────────────────────────────
GOOGLE_OAUTH_CLIENT_ID=tu_google_client_id

# ── CORS / Frontend ────────────────────────────────────────
APP_FRONTEND_URL=http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000

# ── Servicios externos ─────────────────────────────────────
INVOICE_GENERATOR_API_KEY=tu_api_key
BREVO_API_KEY=tu_brevo_key

# ── reCAPTCHA v3 ───────────────────────────────────────────
RECAPTCHA_SECRET_KEY=tu_recaptcha_key
RECAPTCHA_VERIFY_URL=https://www.google.com/recaptcha/api/siteverify
RECAPTCHA_THRESHOLD=0.5
```


### 4. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

---

## 📚 Documentación de la API (Swagger UI)
http://localhost:8080/swagger-ui/index.html

text

Generada automáticamente con **SpringDoc OpenAPI 2.8.8**.

---

## 🗂️ Estructura del Proyecto
src/
├── main/
│ ├── java/com/tetris/tetrisburger_backend/
│ │ ├── config/ # Seguridad, CORS, AWS, Bucket4j
│ │ ├── controller/ # Controladores REST
│ │ ├── service/ # Lógica de negocio
│ │ ├── repository/ # Repositorios JPA
│ │ ├── model/ # Entidades JPA
│ │ ├── dto/ # Data Transfer Objects
│ │ └── mapper/ # Mappers MapStruct
│ └── resources/
│ └── application.properties
└── test/ # Tests con H2 en memoria

text

---

## 🗄️ Base de Datos

La base de datos se llama `railway` y usa **MySQL 8+** con charset `utf8mb4`. Todas las
tablas que aplican implementan **soft delete** mediante el campo `deleted_at` y
trazabilidad de auditoría (`created_by`, `updated_by`, `deleted_by`).

### Diagrama de módulos
[user] ──────────────── [cart] ──── [cart_item] ──── [burger]
│ │ (BURGER/ │
├── [favorite_burger] └── [order] ADDITION/ ├── [burger_ingredient] ── [product]
│ │ PRODUCT) │ │
└── [pqrs] [payment] [invoice] └── [addition] [product_category]
[supplier]
[menu] ── [menu_category]
└── [menu_item] ── [burger]

[burger_settings] [addition_settings]

text

### Tablas del esquema (20 tablas)

#### 👤 Usuarios y Acceso

| Tabla  | Descripción                                                                      |
|--------|----------------------------------------------------------------------------------|
| `user` | Usuarios del sistema. Roles: `ADMIN`, `CLIENT`, `EMPLOYEE`. Contraseñas BCrypt.  |

**Campos clave de `user`:**

| Campo        | Tipo            | Descripción                     |
|--------------|-----------------|---------------------------------|
| `id_user`    | INT PK AI       | Identificador único             |
| `name`       | VARCHAR(50)     | Nombre del usuario              |
| `email`      | VARCHAR(150) UQ | Email único, usado para login   |
| `password`   | VARCHAR(255)    | Hash BCrypt                     |
| `role`       | ENUM            | `ADMIN` / `CLIENT` / `EMPLOYEE` |
| `deleted_at` | DATETIME        | Soft delete                     |

---

#### 🍔 Hamburguesas y Menú

| Tabla               | Descripción                                                                                                                        |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------|
| `burger`            | Hamburguesas del menú oficial y personalizadas (`is_custom = 1`). Incluye `final_price`, `margin` y `selling_at_loss`.             |
| `burger_ingredient` | Ingredientes de cada hamburguesa. Guarda `price_at_time`, `product_name` y `subtotal` como snapshot histórico.                     |
| `burger_settings`   | Configuración global: mín/máx ingredientes, rango de precios y precio base. Registro único (`id_settings = 1`).                    |
| `menu`              | Menús agrupados por `menu_category`. Soporta imagen y soft delete.                                                                 |
| `menu_category`     | Categorías para organizar los menús.                                                                                               |
| `menu_item`         | Hamburguesas incluidas en un menú, con campo `position` para ordenamiento.                                                         |
| `favorite_burger`   | Hamburguesas marcadas como favoritas por un usuario.                                                                               |

**Campos clave de `burger`:**

| Campo             | Tipo          | Descripción                              |
|-------------------|---------------|------------------------------------------|
| `is_on_menu`      | TINYINT(1)    | `1` = forma parte del menú oficial       |
| `is_custom`       | TINYINT(1)    | `1` = creada por un cliente              |
| `is_featured`     | TINYINT(1)    | `1` = destacada en el menú               |
| `id_user`         | INT (FK)      | `NULL` si es del menú; ID si es custom   |
| `final_price`     | DECIMAL(10,2) | Precio final calculado con ingredientes  |
| `margin`          | DECIMAL(10,2) | Margen de ganancia absoluto              |
| `selling_at_loss` | TINYINT(1)    | `1` si se vende a pérdida                |
| `times_ordered`   | INT           | Contador de veces ordenada               |

**Campos clave de `burger_ingredient`:**

| Campo           | Tipo          | Descripción                                       |
|-----------------|---------------|---------------------------------------------------|
| `price_at_time` | DECIMAL(38,2) | Precio del producto al momento de armar la burger |
| `product_name`  | VARCHAR(255)  | Nombre del producto como snapshot histórico       |
| `subtotal`      | DECIMAL(38,2) | `quantity × price_at_time`                        |
| `is_optional`   | TINYINT(1)    | `1` si el ingrediente puede omitirse              |

**Campos clave de `burger_settings`:**

| Campo                     | Tipo          | Descripción                              |
|---------------------------|---------------|------------------------------------------|
| `min_ingredients`         | INT           | Mínimo de ingredientes por burger custom |
| `max_ingredients`         | INT           | Máximo de ingredientes por burger custom |
| `custom_burger_min_price` | DECIMAL(10,2) | Precio mínimo de una burger custom       |
| `custom_burger_max_price` | DECIMAL(10,2) | Precio máximo de una burger custom       |
| `custom_burgers_enabled`  | TINYINT(1)    | Habilita/deshabilita burgers custom      |

---

#### 📦 Productos e Inventario

| Tabla               | Descripción                                                                    |
|---------------------|--------------------------------------------------------------------------------|
| `product`           | Ingredientes e insumos. Incluye `stock`, proveedor y categoría.                |
| `product_category`  | Categorías de productos (panes, carnes, salsas, etc.). Nombre único indexado.  |
| `supplier`          | Proveedores de ingredientes, con email único e indexado.                       |
| `addition`          | Adiciones opcionales (extras) que se agregan a una orden.                      |
| `addition_settings` | Configuración global: máx adiciones por ítem y precio total máximo.            |

---

#### 🛒 Carrito y Pedidos

| Tabla        | Descripción                                                                                           |
|--------------|-------------------------------------------------------------------------------------------------------|
| `cart`       | Carrito de compras activo por usuario. Un carrito por usuario (`UNIQUE id_user`).                     |
| `cart_item`  | Ítem del carrito. El campo `item_type` indica si es `BURGER`, `ADDITION` o `PRODUCT`.                 |
| `order`      | Pedido generado desde el carrito.                                                                     |
| `order_item` | Detalle línea a línea del pedido (hamburguesa, cantidad, precio, notas).                              |

**Flujo de estados de una orden:**
PENDING → ACCEPTED → IN_PROGRESS → READY → COMPLETED
↘ CANCELLED_BY_EMPLOYEE

text

**Campos clave de `cart_item`:**

| Campo       | Tipo                                    | Descripción                        |
|-------------|-----------------------------------------|------------------------------------|
| `item_type` | ENUM(`BURGER`, `ADDITION`, `PRODUCT`)   | Tipo de ítem en el carrito         |
| `id_item`   | INT                                     | FK al id del tipo correspondiente  |
| `subtotal`  | DECIMAL(10,2)                           | `quantity × unit_price`            |

---

#### 💳 Pagos y Facturación

| Tabla     | Descripción                                                                                          |
|-----------|------------------------------------------------------------------------------------------------------|
| `payment` | Pago asociado a una orden. Métodos: `CASH`, `CARD`, `TRANSFER`.                                      |
| `invoice` | Factura generada. Estados: `PENDING`, `ISSUED`, `FAILED`. Guarda URL del PDF en S3.                  |

---

#### 📋 Otros módulos

| Tabla  | Descripción                                                                                       |
|--------|---------------------------------------------------------------------------------------------------|
| `pqrs` | Peticiones, Quejas, Reclamos y Sugerencias. Incluye `response`, `assigned_to` y `priority`.       |

---

## 🧪 Datos de Prueba (`seed.sql`)

El script `seed.sql` inicializa la base de datos con datos ficticios listos para
desarrollo y QA. **La contraseña de todos los usuarios es: `tetris2024`**

> ⚠️ El hash BCrypt fue generado con strength 10. Si necesitas regenerar los hashes
> en producción, ejecuta `passwordEncoder.encode("tetris2024")` en tu app y actualiza
> el script antes de importar.

### 👥 Usuarios

| ID | Nombre             | Email                          | Rol      | Teléfono   |
|----|--------------------|--------------------------------|----------|------------|
| 1  | Admin TetrisBurger | admintetrisburger@gmail.com    | ADMIN    | 3001111001 |
| 2  | Laura Gómez        | employeetetrisburger@gmail.com | EMPLOYEE | 3001111002 |
| 3  | Carlos Herrera     | usuariotetrisburger@gmail.com  | CLIENT   | 3001111003 |
| 4  | Valentina Torres   | valentina.torres@prueba.test   | CLIENT   | 3001111004 |
| 5  | Santiago Muñoz     | santiago.munoz@prueba.test     | CLIENT   | 3001111005 |
| 6  | Andrea Ospina      | andrea.ospina@prueba.test      | CLIENT   | 3001111006 |

### 🏭 Proveedores (5)

| ID | Nombre                    | Email                            | Ciudad   |
|----|---------------------------|----------------------------------|----------|
| 1  | Carnes del Valle SAS      | ventas@carnesvalle.test          | Medellín |
| 2  | Panadería La Miga         | pedidos@panlmiga.test            | Medellín |
| 3  | Vegetales y Verduras Ltda | compras@vegetalesyv.test         | Medellín |
| 4  | Salsas & Sabores SAS      | info@salsasabores.test           | Medellín |
| 5  | Bebidas Nacionales Ltda   | comercial@bebidasnacionales.test | Itagüí   |

### 🥩 Productos (30)

| Categoría          | Productos                                                                         |
|--------------------|-----------------------------------------------------------------------------------|
| Panes              | Pan Brioche ($2.500), Pan Tradicional ($2.000)                                    |
| Carnes y proteínas | Res 180g ($7.000), Pollo 180g ($6.000), Pollo Crispy ($6.000), Cerdo ($6.000)    |
| Quesos             | Cheddar ($2.000), Mozzarella ($2.000), Americano ($2.000)                        |
| Vegetales          | Lechuga, Tomate, C. Caramelizada, C. Morada, Pepinillos, Aguacate, Jalapeños     |
| Salsas             | BBQ, Mayonesa, Ketchup, Mostaza, Picante, Ajo                                    |
| Extras             | Tocineta ($3.000), Huevo Frito ($1.000), Jamón ($4.000)                          |
| Acompañantes       | Papas a la Francesa ($5.000) · tipo `SIDE`                                       |
|                    | Nuggets de Pollo ($5.000) · tipo `SIDE`                                          |
| Bebidas            | Coca-Cola 400ml ($4.500), Limonada de Coco ($6.500), Agua 500ml ($2.500)         |

> Tipos de producto: `INGREDIENT` (ingredientes para burgers), `SIDE` (acompañante), `BEVERAGE` (bebida).

### 🍔 Hamburguesas del Menú (7)

| Nombre           | Precio final | Margen   | Destacada |
|------------------|-------------|----------|-----------|
| Clásica Demo     | $19.000     | 43,94 %  | ✅        |
| BBQ Bacon        | $23.000     | 32,95 %  | ✅        |
| Crispy Chicken   | $20.000     | 57,48 %  | ❌        |
| Mega Doble       | $29.000     | 17,41 %  | ✅        |
| Mexicana Picante | $25.000     | 30,21 %  | ✅        |
| Hawaiana         | $27.000     | 31,07 %  | ❌        |
| La Reina         | $35.000     | 11,11 %  | ✅        |

### ➕ Adiciones (8)

| Nombre               | Precio   |
|----------------------|----------|
| Tocineta Crocante    | $3.000   |
| Huevo Frito          | $2.500   |
| Cebolla Caramelizada | $2.000   |
| Jalapeños            | $1.500   |
| Aguacate             | $2.000   |
| Queso Extra          | $2.500   |
| Papas a la Francesa  | $5.000   |
| Jamón Extra          | $4.000   |

### ⚙️ Configuración global (`burger_settings` / `addition_settings`)

| Parámetro                       | Valor    |
|---------------------------------|----------|
| Mín. ingredientes por burger    | 2        |
| Máx. ingredientes por burger    | 15       |
| Precio mín. burger custom       | $10.000  |
| Precio máx. burger custom       | $50.000  |
| Burgers custom habilitadas      | ✅       |
| Máx. adiciones por ítem         | 5        |
| Precio total máx. de adiciones  | $20.000  |
| Adiciones habilitadas           | ✅       |

### 📋 Menús y categorías

| Menú               | Categoría      | Ítems                                                 |
|--------------------|----------------|-------------------------------------------------------|
| Menú Hamburguesas  | Hamburguesas   | Clásica Demo, BBQ Bacon, Crispy Chicken, Mega Doble,  |
|                    |                | Mexicana Picante, Hawaiana, La Reina                  |
| Menú Acompañantes  | Acompañantes   | Papas a la Francesa, Nuggets de Pollo                 |
| Menú Bebidas       | Bebidas        | Coca-Cola 400ml, Limonada de Coco, Agua Mineral       |

### 📦 Órdenes de prueba (5)

| Nro. Orden    | Usuario   | Estado    | Total   | Método de pago | Factura        |
|---------------|-----------|-----------|---------|----------------|----------------|
| ORD-TEST-0001 | Carlos    | COMPLETED | $23.500 | CARD           | FV-DEMO-0001 ✅ |
| ORD-TEST-0002 | Valentina | COMPLETED | $29.500 | TRANSFER       | FV-DEMO-0002 ✅ |
| ORD-TEST-0003 | Santiago  | COMPLETED | $25.000 | CASH           | FV-DEMO-0003 ✅ |
| ORD-TEST-0004 | Andrea    | ACCEPTED  | $34.000 | CARD           | FV-DEMO-0004 ⏳ |
| ORD-TEST-0005 | Carlos    | PENDING   | $19.000 | —              | —              |

### 📋 PQRS de prueba (4)

| Tipo        | Estado      | Prioridad | Asunto                          | Asignado a  |
|-------------|-------------|-----------|----------------------------------|-------------|
| SUGGESTION  | ANSWERED    | MEDIUM    | Agregar combo familiar           | Laura (id 2)|
| CLAIM       | ANSWERED    | HIGH      | Pedido incompleto (sin queso)    | Laura (id 2)|
| QUESTION    | PENDING     | LOW       | ¿Cuáles burgers no tienen gluten?| Laura (id 2)|
| COMPLAINT   | IN_PROGRESS | HIGH      | Tiempo de entrega demasiado largo| Laura (id 2)|

### 🛒 Carritos activos (3)

| Usuario   | Ítems                                         | Subtotal     |
|-----------|-----------------------------------------------|--------------|
| Carlos    | Clásica Demo × 1 + Coca-Cola × 1              | $23.500      |
| Valentina | BBQ Bacon × 1 + Limonada de Coco × 1         | $29.500      |
| Santiago  | Crispy Chicken × 2                            | $40.000      |

---

## 🔐 Seguridad

- **JWT** con expiración configurable (`JWT_EXPIRATION` en ms).
- **Google OAuth 2.0** para login social.
- **reCAPTCHA v3** en formularios públicos (umbral configurable, por defecto `0.5`).
- **Rate limiting** con Bucket4j para prevenir abuso de endpoints.
- **Spring Security** protegiendo rutas por rol (`ADMIN`, `EMPLOYEE`, `CLIENT`).
- **Validación MIME** con Apache Tika para uploads de imágenes.

---

## ☁️ Almacenamiento en la Nube (AWS S3)

Las imágenes se almacenan en S3 organizadas en carpetas separadas:

| Variable de entorno      | Carpeta S3 | Contenido                |
|--------------------------|------------|--------------------------|
| `S3_FOLDER_PRODUCTS`     | `products` | Imágenes de ingredientes |
| `S3_FOLDER_USERS`        | `users`    | Fotos de perfil          |
| `S3_FOLDER_BURGERS_MENU` | `burgers`  | Imágenes de hamburguesas |
| `S3_FOLDER_MENUS`        | `menus`    | Imágenes de menús        |
| `S3_FOLDER_ADDITION`     | `addition` | Imágenes de adiciones    |
| `S3_FOLDER_INVOICES`     | `invoices` | PDFs de facturas         |

> Cada entidad almacena tanto `image_url` (URL pública) como `image_key`
> (clave S3 para operaciones de delete/update).

---

## 📧 Notificaciones por Correo

El envío de correos se gestiona con **Brevo (Sendinblue)** mediante su SDK oficial.
La API key se configura por variable de entorno (`BREVO_API_KEY`). El remitente
está configurado como:

```java
sender.setName("TetrisBurger");
sender.setEmail("tetrisburger8@gmail.com");
```

> ⚠️ Si despliega con su propia cuenta de Brevo, ese email debe estar verificado.
> Modifique esas dos líneas si usa otro remitente.

---

## 🧪 Testing

```bash
mvn test
```

Los tests usan una base de datos **H2 en memoria**, por lo que no requieren
MySQL en ejecución.

---

## 📝 Convenciones del Proyecto

- **Soft delete**: Las entidades nunca se eliminan físicamente. Se marca `deleted_at`
  con la fecha y `deleted_by` con el ID del usuario que eliminó.
- **Auditoría**: Todos los cambios registran `created_by`, `updated_by` y `deleted_by`.
- **Snapshots de precio**: `burger_ingredient` guarda `price_at_time`, `product_name`
  y `subtotal` para mantener el historial aunque el precio del producto cambie.
- **Imágenes**: Siempre se guarda tanto `image_url` como `image_key` para poder
  eliminar del S3 correctamente.
- **Settings globales**: `burger_settings` y `addition_settings` son tablas de
  configuración con un único registro (`id_settings = 1`).
- **Carrito único por usuario**: `cart` tiene constraint `UNIQUE` en `id_user`.

---



## 📝 Licencia

Este proyecto es privado y de uso interno del equipo TetrisBurger.