-- ============================================================
--  TetrisBurger — Seed de datos de prueba (ficticio)
--  Usuarios de prueba provistos por el equipo
--  NOTA: Reemplaza los hashes de contraseña ejecutando
--        passwordEncoder.encode("tetris2024") en tu app
--        antes de importar en producción.
-- ============================================================

USE railway;

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- ============================================================
-- LIMPIEZA
-- ============================================================
DELETE FROM invoice;
DELETE FROM payment;
DELETE FROM orderitem;
DELETE FROM `order`;
DELETE FROM menuitem;
DELETE FROM menu;
DELETE FROM menucategory;
DELETE FROM favoriteburger;
DELETE FROM cartitem;
DELETE FROM cart;
DELETE FROM burgeringredient;
DELETE FROM burger;
DELETE FROM addition;
DELETE FROM additionsettings;
DELETE FROM burgersettings;
DELETE FROM product;
DELETE FROM productcategory;
DELETE FROM supplier;
DELETE FROM pqrs;
DELETE FROM `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- USERS
-- Contraseña de todos: tetris2024
-- Hash BCrypt generado con strength 10
-- ============================================================
INSERT INTO `user`
  (iduser, name, email, password, phone, role,
   userimage, imagekey, userimagekey,
   createdat, updatedat, deletedat,
   createdby, updatedby, deletedby)
VALUES
(1,  'Admin TetrisBurger',  'admintetrisburger@gmail.com',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111001', 'ADMIN',  NULL, NULL, NULL,
     '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, NULL, NULL, NULL),

(2,  'Laura Gómez',  'employeetetrisburger@gmail.com',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111002', 'EMPLOYEE', NULL, NULL, NULL,
     '2026-04-01 08:05:00', '2026-04-01 08:05:00', NULL, 1, 1, NULL),

(3,  'Carlos Herrera', 'usuariotetrisburger@gmail.com',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111003', 'CLIENT', NULL, NULL, NULL,
     '2026-04-01 09:00:00', '2026-04-01 09:00:00', NULL, 1, 1, NULL),

(4,  'Valentina Torres', 'valentina.torres@prueba.test',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111004', 'CLIENT', NULL, NULL, NULL,
     '2026-04-02 10:00:00', '2026-04-02 10:00:00', NULL, 1, 1, NULL),

(5,  'Santiago Muñoz', 'santiago.munoz@prueba.test',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111005', 'CLIENT', NULL, NULL, NULL,
     '2026-04-03 11:00:00', '2026-04-03 11:00:00', NULL, 1, 1, NULL),

(6,  'Andrea Ospina', 'andrea.ospina@prueba.test',
     '$2a$10$E6M3kQ9FXqBzUv5YtRn7deH4JlPswOaTXigC9UVZmKxoE3nQgQoTm',
     '3001111006', 'CLIENT', NULL, NULL, NULL,
     '2026-04-04 12:00:00', '2026-04-04 12:00:00', NULL, 1, 1, NULL);

-- ============================================================
-- SUPPLIERS
-- ============================================================
INSERT INTO supplier
  (idsupplier, name, email, phone, address,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby)
VALUES
(1, 'Carnes del Valle SAS',    'ventas@carnesvalle.test',       '6042200001', 'Calle 10 #20-30, Medellín',          '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(2, 'Panadería La Miga',       'pedidos@panlmiga.test',          '6042200002', 'Carrera 45 #50-12, Medellín',        '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(3, 'Vegetales y Verduras Ltda','compras@vegetalesyv.test',      '6042200003', 'Plaza Minorista Local 22, Medellín', '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(4, 'Salsas & Sabores SAS',    'info@salsasabores.test',         '6042200004', 'Calle 33 #65-14, Medellín',          '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(5, 'Bebidas Nacionales Ltda', 'comercial@bebidasnacionales.test','6042200005', 'Autopista Sur #12-90, Itagüí',      '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL);

-- ============================================================
-- PRODUCT CATEGORIES
-- ============================================================
INSERT INTO productcategory
  (idproductcategory, description, available, productcategoryname,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby)
VALUES
(1, 'Bebidas frías, gaseosas y naturales',     b'1', 'Bebidas',             '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(2, 'Tipos de pan para hamburguesa',           b'1', 'Panes',               '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(3, 'Proteínas principales',                   b'1', 'Carnes y proteínas',  '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(4, 'Quesos para preparación',                 b'1', 'Quesos',              '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(5, 'Vegetales y verduras frescas',            b'1', 'Vegetales',           '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(6, 'Salsas y aderezos',                       b'1', 'Salsas',              '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(7, 'Acompañantes y extras del pedido',        b'1', 'Extras',              '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL);

-- ============================================================
-- PRODUCTS
-- ============================================================
INSERT INTO product
  (idproduct, name, description, price, isavailable, isburgeringredient,
   producttype, imageurl, imagekey, idproductcategory, idsupplier,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby,
   availability, quantity, supplierid)
VALUES
-- Panes
(1,  'Pan Brioche',          'Pan suave y ligeramente dulce, ideal para premium.',  2500.00, 1, 1, 'INGREDIENT', NULL, NULL, 2, 2, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 100, NULL),
(2,  'Pan Tradicional',      'Pan de hamburguesa clásico y esponjoso.',              2000.00, 1, 1, 'INGREDIENT', NULL, NULL, 2, 2, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 120, NULL),
-- Carnes
(3,  'Carne de Res 180g',    'Medallón de carne de res molida 80/20.',               7000.00, 1, 1, 'INGREDIENT', NULL, NULL, 3, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 80,  NULL),
(4,  'Pechuga de Pollo 180g','Pechuga de pollo a la plancha.',                       6000.00, 1, 1, 'INGREDIENT', NULL, NULL, 3, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 70,  NULL),
(5,  'Pollo Crispy',         'Pechuga apanada y frita.',                             6000.00, 1, 1, 'INGREDIENT', NULL, NULL, 3, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 65,  NULL),
(6,  'Carne de Cerdo 180g',  'Medallón de cerdo sazonado.',                         6000.00, 1, 1, 'INGREDIENT', NULL, NULL, 3, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 60,  NULL),
-- Quesos
(7,  'Queso Cheddar',        'Queso cheddar tajado derretido.',                      2000.00, 1, 1, 'INGREDIENT', NULL, NULL, 4, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 90,  NULL),
(8,  'Queso Mozzarella',     'Mozzarella estirable y suave.',                        2000.00, 1, 1, 'INGREDIENT', NULL, NULL, 4, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 85,  NULL),
(9,  'Queso Americano',      'Queso americano clásico fundido.',                     2000.00, 1, 1, 'INGREDIENT', NULL, NULL, 4, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 80,  NULL),
-- Vegetales
(10, 'Lechuga',              'Hojas de lechuga fresca y crujiente.',                  800.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 110, NULL),
(11, 'Tomate',               'Rodajas de tomate fresco.',                             900.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 105, NULL),
(12, 'Cebolla Caramelizada', 'Cebolla cocinada lentamente con sabor dulce.',         1500.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 70,  NULL),
(13, 'Cebolla Morada',       'Cebolla morada cruda en rodajas.',                      800.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 95,  NULL),
(14, 'Pepinillos',           'Pepinillos encurtidos en rodajas.',                    1200.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 80,  NULL),
(15, 'Aguacate',             'Rodajas de aguacate fresco.',                          1500.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 75,  NULL),
(16, 'Jalapeños',            'Jalapeños en rodajas.',                               1500.00, 1, 1, 'INGREDIENT', NULL, NULL, 5, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 60,  NULL),
-- Salsas
(17, 'Salsa BBQ',            'Salsa dulce con toque ahumado.',                        800.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 100, NULL),
(18, 'Mayonesa',             'Mayonesa clásica casera.',                              500.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 100, NULL),
(19, 'Ketchup',              'Salsa de tomate dulce.',                                500.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 100, NULL),
(20, 'Mostaza',              'Mostaza amarilla clásica.',                             500.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 95,  NULL),
(21, 'Salsa Picante',        'Salsa de ají con intensidad media.',                    900.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 90,  NULL),
(22, 'Salsa de Ajo',         'Ajo asado en base cremosa.',                            800.00, 1, 1, 'INGREDIENT', NULL, NULL, 6, 4, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 85,  NULL),
-- Extras / Acompañantes
(23, 'Tocineta',             'Tiras de tocineta frita y crocante.',                  3000.00, 1, 1, 'INGREDIENT', NULL, NULL, 7, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 70,  NULL),
(24, 'Huevo Frito',          'Huevo fresco preparado al momento.',                   1000.00, 1, 1, 'INGREDIENT', NULL, NULL, 7, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 65,  NULL),
(25, 'Jamón',                'Rebanada de jamón premium.',                           4000.00, 1, 1, 'INGREDIENT', NULL, NULL, 7, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 60,  NULL),
(26, 'Papas a la Francesa',  'Porción de papas crocantes.',                          5000.00, 1, 0, 'SIDE',       NULL, NULL, 7, 5, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 80,  NULL),
(27, 'Nuggets de Pollo',     'Porción de 6 nuggets de pollo.',                       5000.00, 1, 0, 'SIDE',       NULL, NULL, 7, 5, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 55,  NULL),
-- Bebidas
(28, 'Coca-Cola 400ml',      'Gaseosa personal.',                                    4500.00, 1, 0, 'BEVERAGE',   NULL, NULL, 1, 5, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 150, NULL),
(29, 'Limonada de Coco',     'Bebida cremosa de coco con limón.',                    6500.00, 1, 0, 'BEVERAGE',   NULL, NULL, 1, 5, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 40,  NULL),
(30, 'Agua Mineral 500ml',   'Agua mineral sin gas.',                                2500.00, 1, 0, 'BEVERAGE',   NULL, NULL, 1, 5, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL, b'1', 100, NULL);

-- ============================================================
-- ADDITIONS
-- ============================================================
INSERT INTO additionsettings
  (idsettings, maxadditionsperitem, maxtotalprice, additionsenabled, updatedat)
VALUES
  (1, 5, 20000.00, 1, '2026-04-01 08:00:00');

INSERT INTO addition
  (idaddition, name, description, price, isavailable, imageurl,
   createdat, deletedat, imagekey, updatedat, createdby, deletedby, updatedby)
VALUES
(1, 'Tocineta Crocante',   'Tiras de tocineta frita y crujiente.',        3000.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(2, 'Huevo Frito',         'Huevo fresco preparado al momento.',           2500.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(3, 'Cebolla Caramelizada','Cebolla cocinada lentamente con sabor dulce.', 2000.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(4, 'Jalapeños',           'Jalapeños frescos en rodajas.',               1500.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(5, 'Aguacate',            'Rodajas de aguacate fresco.',                  2000.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(6, 'Queso Extra',         'Porción adicional de queso cheddar.',          2500.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(7, 'Papas a la Francesa', 'Porción de papas crocantes.',                  5000.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1),
(8, 'Jamón Extra',         'Rebanada adicional de jamón.',                 4000.00, 1, NULL, '2026-04-01 08:00:00', NULL, NULL, '2026-04-01 08:00:00', 1, NULL, 1);

-- ============================================================
-- BURGER SETTINGS
-- ============================================================
INSERT INTO burgersettings
  (idsettings, minprice, maxprice, miningredients, maxingredients,
   customburgersenabled, updatedat, customburgermaxprice, customburgerminprice)
VALUES
  (1, NULL, NULL, 2, 15, 1, '2026-04-01 08:00:00', 50000.00, 10000.00);

-- ============================================================
-- BURGERS (menú principal — iscustom=0)
-- ============================================================
INSERT INTO burger
  (idburger, name, description, baseprice, isavailable, isonmenu, iscustom,
   isfeatured, imageurl, imagekey, iduser,
   createdat, updatedat, deletedat, createdby, updatedby,
   finalprice, margin, marginpercentage, sellingatloss, deletedby, timesordered)
VALUES
(1, 'Clásica Demo',         'Pan brioche, carne, queso cheddar, lechuga, tomate, ketchup y mayonesa.',        13200.00, 1, 1, 0, 1, NULL, NULL, NULL, '2026-04-01 08:10:00', '2026-04-06 20:00:00', NULL, 1, 1, 19000.00, 5800.00, 43.94, 0, NULL, 12),
(2, 'BBQ Bacon',            'Pan brioche, carne, cheddar, tocineta crocante y salsa BBQ.',                    17300.00, 1, 1, 0, 1, NULL, NULL, NULL, '2026-04-01 08:15:00', '2026-04-06 20:00:00', NULL, 1, 1, 23000.00, 5700.00, 32.95, 0, NULL, 9),
(3, 'Crispy Chicken',       'Pan tradicional, pollo crispy, mozzarella, lechuga, tomate y mayonesa.',         12700.00, 1, 1, 0, 0, NULL, NULL, NULL, '2026-04-01 08:20:00', '2026-04-06 20:00:00', NULL, 1, 1, 20000.00, 7300.00, 57.48, 0, NULL, 7),
(4, 'Mega Doble',           'Pan brioche, doble carne, queso cheddar, tocineta, huevo frito y salsa BBQ.',   24700.00, 1, 1, 0, 1, NULL, NULL, NULL, '2026-04-01 08:25:00', '2026-04-07 00:59:00', NULL, 1, 1, 29000.00, 4300.00, 17.41, 0, NULL, 5),
(5, 'Mexicana Picante',     'Pan tradicional, carne, cheddar, jalapeños, salsa picante y aguacate.',          19200.00, 1, 1, 0, 1, NULL, NULL, NULL, '2026-04-01 08:30:00', '2026-04-06 20:04:00', NULL, 1, 1, 25000.00, 5800.00, 30.21, 0, NULL, 4),
(6, 'Hawaiana',             'Pan brioche, cerdo, mozzarella, piña caramelizada, cebolla y salsa BBQ.',       20600.00, 1, 1, 0, 0, NULL, NULL, NULL, '2026-04-01 08:35:00', '2026-04-06 20:03:00', NULL, 1, 1, 27000.00, 6400.00, 31.07, 0, NULL, 3),
(7, 'La Reina',             'Pan brioche, carne, cerdo, cheddar, tocineta, huevo frito y ketchup.',          31500.00, 1, 1, 0, 1, NULL, NULL, NULL, '2026-04-01 08:40:00', '2026-04-07 01:26:00', NULL, 1, 1, 35000.00, 3500.00, 11.11, 0, NULL, 2);

-- ============================================================
-- BURGER INGREDIENTS
-- ============================================================
INSERT INTO burgeringredient
  (idburgeringredient, idburger, idproduct, quantity, priceattime, productname, subtotal, isoptional, imageurl)
VALUES
-- Clásica Demo (id=1)
(1,  1,  1,  1, 2500.00, 'Pan Brioche',         2500.00, 0, NULL),
(2,  1,  3,  1, 7000.00, 'Carne de Res 180g',   7000.00, 0, NULL),
(3,  1,  7,  1, 2000.00, 'Queso Cheddar',        2000.00, 0, NULL),
(4,  1, 10,  1,  800.00, 'Lechuga',               800.00, 0, NULL),
(5,  1, 11,  1,  900.00, 'Tomate',                900.00, 0, NULL),
(6,  1, 19,  1,  500.00, 'Ketchup',               500.00, 0, NULL),
(7,  1, 18,  1,  500.00, 'Mayonesa',              500.00, 0, NULL),
-- BBQ Bacon (id=2)
(8,  2,  1,  1, 2500.00, 'Pan Brioche',         2500.00, 0, NULL),
(9,  2,  3,  1, 7000.00, 'Carne de Res 180g',   7000.00, 0, NULL),
(10, 2,  7,  1, 2000.00, 'Queso Cheddar',        2000.00, 0, NULL),
(11, 2, 17,  1,  800.00, 'Salsa BBQ',             800.00, 0, NULL),
(12, 2, 23,  2, 3000.00, 'Tocineta',             6000.00, 0, NULL),
-- Crispy Chicken (id=3)
(13, 3,  2,  1, 2000.00, 'Pan Tradicional',     2000.00, 0, NULL),
(14, 3,  5,  1, 6000.00, 'Pollo Crispy',        6000.00, 0, NULL),
(15, 3,  8,  1, 2000.00, 'Queso Mozzarella',    2000.00, 0, NULL),
(16, 3, 10,  1,  800.00, 'Lechuga',               800.00, 0, NULL),
(17, 3, 11,  1,  900.00, 'Tomate',                900.00, 0, NULL),
(18, 3, 18,  1,  500.00, 'Mayonesa',              500.00, 0, NULL),
-- Mega Doble (id=4)
(19, 4,  1,  1, 2500.00, 'Pan Brioche',         2500.00, 0, NULL),
(20, 4,  3,  2, 7000.00, 'Carne de Res 180g',  14000.00, 0, NULL),
(21, 4,  7,  1, 2000.00, 'Queso Cheddar',        2000.00, 0, NULL),
(22, 4, 23,  1, 3000.00, 'Tocineta',             3000.00, 0, NULL),
(23, 4, 24,  1, 1000.00, 'Huevo Frito',          1000.00, 0, NULL),
(24, 4, 17,  1,  800.00, 'Salsa BBQ',             800.00, 0, NULL),
(25, 4, 19,  1,  500.00, 'Ketchup',               500.00, 0, NULL),
-- Mexicana Picante (id=5)
(26, 5,  2,  1, 2000.00, 'Pan Tradicional',     2000.00, 0, NULL),
(27, 5,  3,  1, 7000.00, 'Carne de Res 180g',   7000.00, 0, NULL),
(28, 5,  7,  1, 2000.00, 'Queso Cheddar',        2000.00, 0, NULL),
(29, 5, 21,  2,  900.00, 'Salsa Picante',        1800.00, 0, NULL),
(30, 5, 16,  2, 1500.00, 'Jalapeños',           3000.00, 0, NULL),
(31, 5, 15,  1, 1500.00, 'Aguacate',             1500.00, 0, NULL),
(32, 5, 19,  1,  500.00, 'Ketchup',               500.00, 0, NULL),
-- Hawaiana (id=6)
(33, 6,  1,  1, 2500.00, 'Pan Brioche',         2500.00, 0, NULL),
(34, 6,  6,  1, 6000.00, 'Carne de Cerdo 180g', 6000.00, 0, NULL),
(35, 6,  8,  1, 2000.00, 'Queso Mozzarella',    2000.00, 0, NULL),
(36, 6, 12,  1, 1500.00, 'Cebolla Caramelizada',1500.00, 0, NULL),
(37, 6, 17,  1,  800.00, 'Salsa BBQ',             800.00, 0, NULL),
-- La Reina (id=7)
(38, 7,  1,  1, 2500.00, 'Pan Brioche',         2500.00, 0, NULL),
(39, 7,  3,  1, 7000.00, 'Carne de Res 180g',   7000.00, 0, NULL),
(40, 7,  6,  1, 6000.00, 'Carne de Cerdo 180g', 6000.00, 0, NULL),
(41, 7,  7,  1, 2000.00, 'Queso Cheddar',        2000.00, 0, NULL),
(42, 7, 23,  1, 3000.00, 'Tocineta',             3000.00, 0, NULL),
(43, 7, 24,  1, 1000.00, 'Huevo Frito',          1000.00, 0, NULL),
(44, 7, 19,  1,  500.00, 'Ketchup',               500.00, 0, NULL);

-- ============================================================
-- MENU CATEGORIES
-- ============================================================
INSERT INTO menucategory
  (idmenucategory, categoryname, description,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby)
VALUES
(1, 'Hamburguesas',          'Selección principal de hamburguesas.', '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(2, 'Acompañantes',          'Papas, nuggets y otros acompañantes.', '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(3, 'Bebidas',               'Gaseosas, limonadas y aguas.',         '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL);

-- ============================================================
-- MENUS
-- ============================================================
INSERT INTO menu
  (idmenu, name, description, isavailable, imageurl, imagekey, idmenucategory,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby)
VALUES
(1, 'Menú Hamburguesas',   'Hamburguesas de la carta principal.',   1, NULL, NULL, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(2, 'Menú Acompañantes',   'Acompañantes disponibles para pedir.', 1, NULL, NULL, 2, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL),
(3, 'Menú Bebidas',        'Bebidas disponibles para pedir.',      1, NULL, NULL, 3, '2026-04-01 08:00:00', '2026-04-01 08:00:00', NULL, 1, 1, NULL);

-- ============================================================
-- MENU ITEMS
-- ============================================================
INSERT INTO menuitem
  (idmenuitem, idmenu, itemtype, idburger, idproduct)
VALUES
(1,  1, 'BURGER',   1, NULL),
(2,  1, 'BURGER',   2, NULL),
(3,  1, 'BURGER',   3, NULL),
(4,  1, 'BURGER',   4, NULL),
(5,  1, 'BURGER',   5, NULL),
(6,  1, 'BURGER',   6, NULL),
(7,  1, 'BURGER',   7, NULL),
(8,  2, 'PRODUCT', NULL, 26),
(9,  2, 'PRODUCT', NULL, 27),
(10, 3, 'PRODUCT', NULL, 28),
(11, 3, 'PRODUCT', NULL, 29),
(12, 3, 'PRODUCT', NULL, 30);

-- ============================================================
-- CART
-- ============================================================
INSERT INTO cart
  (idcart, iduser, createdat, updatedat)
VALUES
(1, 3, '2026-04-06 20:00:00', '2026-04-06 20:00:00'),
(2, 4, '2026-04-06 21:00:00', '2026-04-06 21:00:00'),
(3, 5, '2026-04-07 10:00:00', '2026-04-07 10:00:00');

INSERT INTO cartitem
  (idcartitem, idcart, itemtype, quantity, unitprice, subtotal, createdat, iditem, imageurl, name)
VALUES
(1, 1, 'BURGER',  1, 19000.00, 19000.00, '2026-04-06 20:01:00', 1, NULL, 'Clásica Demo'),
(2, 1, 'PRODUCT', 1,  4500.00,  4500.00, '2026-04-06 20:01:00',28, NULL, 'Coca-Cola 400ml'),
(3, 2, 'BURGER',  1, 23000.00, 23000.00, '2026-04-06 21:02:00', 2, NULL, 'BBQ Bacon'),
(4, 2, 'PRODUCT', 1,  6500.00,  6500.00, '2026-04-06 21:02:00',29, NULL, 'Limonada de Coco'),
(5, 3, 'BURGER',  2, 20000.00, 40000.00, '2026-04-07 10:05:00', 3, NULL, 'Crispy Chicken');

-- ============================================================
-- FAVORITES
-- ============================================================
INSERT INTO favoriteburger
  (idfavorite, iduser, idburger, name, createdat)
VALUES
(1, 3, 1, 'Clásica Demo',   '2026-04-02 10:00:00'),
(2, 4, 2, 'BBQ Bacon',      '2026-04-03 11:00:00'),
(3, 5, 4, 'Mega Doble',     '2026-04-04 12:00:00'),
(4, 6, 5, 'Mexicana Picante','2026-04-05 09:00:00');

-- ============================================================
-- ORDERS
-- ============================================================
INSERT INTO `order`
  (idorder, iduser, ordernumber, status, totalamount, orderdate, updatedat, deletedat,
   createdby, updatedby, deletedby)
VALUES
(1, 3, 'ORD-TEST-0001', 'COMPLETED', 23500.00, '2026-04-02 12:00:00', '2026-04-02 12:30:00', NULL, 3, 2, NULL),
(2, 4, 'ORD-TEST-0002', 'COMPLETED', 29500.00, '2026-04-03 13:00:00', '2026-04-03 13:35:00', NULL, 4, 2, NULL),
(3, 5, 'ORD-TEST-0003', 'COMPLETED', 25000.00, '2026-04-04 14:00:00', '2026-04-04 14:25:00', NULL, 5, 3, NULL),
(4, 6, 'ORD-TEST-0004', 'ACCEPTED',  34000.00, '2026-04-05 15:00:00', '2026-04-05 15:10:00', NULL, 6, 2, NULL),
(5, 3, 'ORD-TEST-0005', 'PENDING',   19000.00, '2026-04-07 21:00:00', '2026-04-07 21:00:00', NULL, 3, NULL, NULL);

INSERT INTO orderitem
  (idorderitem, idorder, itemtype, idburger, idproduct,
   itemname, quantity, unitprice, subtotal)
VALUES
-- Orden 1
(1,  1, 'BURGER',  1, NULL, 'Clásica Demo',    1, 19000.00, 19000.00),
(2,  1, 'PRODUCT', NULL, 28,'Coca-Cola 400ml', 1,  4500.00,  4500.00),
-- Orden 2
(3,  2, 'BURGER',  2, NULL, 'BBQ Bacon',        1, 23000.00, 23000.00),
(4,  2, 'PRODUCT', NULL, 29,'Limonada de Coco', 1,  6500.00,  6500.00),
-- Orden 3
(5,  3, 'BURGER',  3, NULL, 'Crispy Chicken',   1, 20000.00, 20000.00),
(6,  3, 'PRODUCT', NULL, 30,'Agua Mineral',     2,  2500.00,  5000.00),
-- Orden 4
(7,  4, 'BURGER',  4, NULL, 'Mega Doble',       1, 29000.00, 29000.00),
(8,  4, 'PRODUCT', NULL, 28,'Coca-Cola 400ml',  1,  4500.00,  4500.00),
(9,  4, 'ADDITION',NULL, NULL,'Aguacate',       1,  2000.00,  2000.00),
-- Orden 5
(10, 5, 'BURGER',  1, NULL, 'Clásica Demo',     1, 19000.00, 19000.00);

-- ============================================================
-- PAYMENTS
-- ============================================================
INSERT INTO payment
  (idpayment, idorder, iduser, paymentmethod, amount, paidat)
VALUES
(1, 1, 3, 'CARD',     23500.00, '2026-04-02 12:05:00'),
(2, 2, 4, 'TRANSFER', 29500.00, '2026-04-03 13:05:00'),
(3, 3, 5, 'CASH',     25000.00, '2026-04-04 14:05:00'),
(4, 4, 6, 'CARD',     34000.00, '2026-04-05 15:05:00');

-- ============================================================
-- INVOICES
-- ============================================================
INSERT INTO invoice
  (idinvoice, idorder, idpayment, invoicenumber, externalinvoiceid,
   totalamount, status, invoicedate, pdfurl)
VALUES
(1, 1, 1, 'FV-DEMO-0001', 'EXT-DEMO-0001', 23500.00, 'ISSUED',  '2026-04-02 12:10:00', NULL),
(2, 2, 2, 'FV-DEMO-0002', 'EXT-DEMO-0002', 29500.00, 'ISSUED',  '2026-04-03 13:10:00', NULL),
(3, 3, 3, 'FV-DEMO-0003', 'EXT-DEMO-0003', 25000.00, 'ISSUED',  '2026-04-04 14:10:00', NULL),
(4, 4, 4, 'FV-DEMO-0004', 'EXT-DEMO-0004', 34000.00, 'PENDING', '2026-04-05 15:10:00', NULL);

-- ============================================================
-- PQRS
-- ============================================================
INSERT INTO pqrs
  (idpqrs, iduser, type, status, subject, description, response,
   createdat, updatedat, deletedat, createdby, updatedby, deletedby,
   assignedto, priority)
VALUES
(1, 3, 'SUGGESTION', 'ANSWERED',
 'Agregar combo familiar',
 'Sería genial tener un combo para 4 personas con descuento.',
 'Gracias por la sugerencia, estamos evaluando combos para grupos.',
 '2026-04-03 09:00:00', '2026-04-04 10:00:00', NULL, 3, 2, NULL, 2, 'MEDIUM'),

(2, 4, 'CLAIM', 'ANSWERED',
 'Pedido incompleto',
 'Me llegó la hamburguesa sin el queso extra que pedí.',
 'Ofrecemos disculpas. Le hemos generado un cupón de cortesía.',
 '2026-04-04 11:00:00', '2026-04-04 15:00:00', NULL, 4, 2, NULL, 2, 'HIGH'),

(3, 5, 'QUESTION', 'PENDING',
 '¿Cuáles hamburguesas no tienen gluten?',
 'Tengo intolerancia al gluten y quiero saber qué puedo pedir.',
 NULL,
 '2026-04-06 08:00:00', '2026-04-06 08:00:00', NULL, 5, NULL, NULL, 3, 'LOW'),

(4, 6, 'COMPLAINT', 'IN_PROGRESS',
 'Tiempo de entrega demasiado largo',
 'Esperé más de 1 hora por mi pedido en hora de almuerzo.',
 NULL,
 '2026-04-07 13:00:00', '2026-04-07 14:00:00', NULL, 6, 3, NULL, 3, 'HIGH');