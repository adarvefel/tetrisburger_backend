CREATE DATABASE  IF NOT EXISTS `railway` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `railway`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: gondola.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addition`
--

DROP TABLE IF EXISTS `addition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addition` (
                            `id_addition` int NOT NULL AUTO_INCREMENT,
                            `name` varchar(150) NOT NULL,
                            `description` varchar(255) DEFAULT NULL,
                            `price` decimal(10,2) NOT NULL,
                            `is_available` tinyint(1) NOT NULL DEFAULT '1',
                            `image_url` varchar(255) DEFAULT NULL,
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `deleted_at` datetime(6) DEFAULT NULL,
                            `image_key` varchar(255) DEFAULT NULL,
                            `updated_at` datetime(6) DEFAULT NULL,
                            `created_by` int NOT NULL,
                            `deleted_by` int DEFAULT NULL,
                            `updated_by` int DEFAULT NULL,
                            PRIMARY KEY (`id_addition`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addition`
--

LOCK TABLES `addition` WRITE;
/*!40000 ALTER TABLE `addition` DISABLE KEYS */;
INSERT INTO `addition` VALUES (1,'Tocineta Crocante','Tiras de tocineta frita y crujiente',3000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775460660861-52cfad1f-gemini_generated_image_x7r1ijx7r1ijx7r1.png','2026-04-06 01:25:46',NULL,'addition/1775460660861-52cfad1f-gemini_generated_image_x7r1ijx7r1ijx7r1.png','2026-04-06 02:31:01.193296',1,NULL,1),(2,'Huevo Frito','Huevo fresco preparado al momento',2500.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775460872847-0a335359-gemini_generated_image_lc999zlc999zlc99.png','2026-04-06 01:26:10',NULL,'addition/1775460872847-0a335359-gemini_generated_image_lc999zlc999zlc99.png','2026-04-06 02:34:33.129698',1,NULL,1),(3,'Cebolla Caramelizada','Cebolla cocinada lentamente con sabor dulce',2000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775460671137-7b86a960-gemini_generated_image_xa47i5xa47i5xa47.png','2026-04-06 01:26:36',NULL,'addition/1775460671137-7b86a960-gemini_generated_image_xa47i5xa47i5xa47.png','2026-04-06 02:31:11.358514',1,NULL,1),(4,'Jalapeños','Jalapeños',1500.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775460937837-24a3c16f-gemini_generated_image_tnaqe6tnaqe6tnaq.png','2026-04-06 01:27:22',NULL,'addition/1775460937837-24a3c16f-gemini_generated_image_tnaqe6tnaqe6tnaq.png','2026-04-06 02:35:38.228966',1,NULL,1),(5,'Aguacate','Rodajas de aguacate fresco',2000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461161173-065f0865-gemini_generated_image_a3rxuda3rxuda3rx.png','2026-04-06 01:28:17',NULL,'addition/1775461161173-065f0865-gemini_generated_image_a3rxuda3rxuda3rx.png','2026-04-06 02:39:21.421484',1,NULL,1),(6,'Piña a la Parrilla','Rodaja de piña dulce caramelizada',2500.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461308504-7e92d135-gemini_generated_image_yzpfc6yzpfc6yzpf.png','2026-04-06 01:29:30',NULL,'addition/1775461308504-7e92d135-gemini_generated_image_yzpfc6yzpfc6yzpf.png','2026-04-06 02:41:48.773600',1,NULL,1),(7,'Papas a la francesa','papa crujientes',5000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461364795-435d38b7-gemini_generated_image_odixnkodixnkodix.png','2026-04-06 01:37:12',NULL,'addition/1775461364795-435d38b7-gemini_generated_image_odixnkodixnkodix.png','2026-04-06 02:42:45.141464',1,NULL,1),(8,'Queso Fundido','Queso Fundido',3000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461460077-a684352c-gemini_generated_image_idecm6idecm6idec.png','2026-04-06 01:40:26',NULL,'addition/1775461460077-a684352c-gemini_generated_image_idecm6idecm6idec.png','2026-04-06 02:44:20.417601',1,NULL,1),(9,'Nuggets de Pollo','Nuggets de Pollo',5000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461613842-65eb6baa-gemini_generated_image_7rilgs7rilgs7ril.png','2026-04-06 01:42:58',NULL,'addition/1775461613842-65eb6baa-gemini_generated_image_7rilgs7rilgs7ril.png','2026-04-06 02:46:54.241672',1,NULL,1),(10,'Jamón Extra','Rebanada adicional de jamón',4000.00,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/addition/1775461703339-fab81412-gemini_generated_image_46pj8g46pj8g46pj.png','2026-04-06 01:44:28',NULL,'addition/1775461703339-fab81412-gemini_generated_image_46pj8g46pj8g46pj.png','2026-04-06 02:48:23.746137',1,NULL,1);
/*!40000 ALTER TABLE `addition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `addition_settings`
--

DROP TABLE IF EXISTS `addition_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addition_settings` (
                                     `id_settings` int NOT NULL DEFAULT '1',
                                     `max_additions_per_item` int DEFAULT NULL,
                                     `max_total_price` decimal(10,2) DEFAULT NULL,
                                     `additions_enabled` tinyint(1) NOT NULL DEFAULT '1',
                                     `updated_at` datetime DEFAULT NULL,
                                     PRIMARY KEY (`id_settings`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addition_settings`
--

LOCK TABLES `addition_settings` WRITE;
/*!40000 ALTER TABLE `addition_settings` DISABLE KEYS */;
INSERT INTO `addition_settings` VALUES (1,5,20000.00,1,'2026-04-07 22:00:36');
/*!40000 ALTER TABLE `addition_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `burger`
--

DROP TABLE IF EXISTS `burger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `burger` (
                          `id_burger` int NOT NULL AUTO_INCREMENT,
                          `name` varchar(150) NOT NULL,
                          `description` varchar(255) DEFAULT NULL,
                          `base_price` decimal(10,2) NOT NULL,
                          `is_available` tinyint(1) NOT NULL DEFAULT '1',
                          `is_on_menu` tinyint(1) NOT NULL,
                          `is_custom` tinyint(1) NOT NULL DEFAULT '0',
                          `is_featured` tinyint(1) DEFAULT '0',
                          `image_url` varchar(255) DEFAULT NULL,
                          `image_key` varchar(255) DEFAULT NULL,
                          `id_user` int DEFAULT NULL COMMENT 'NULL si es del menú',
                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` datetime DEFAULT NULL,
                          `deleted_at` datetime DEFAULT NULL,
                          `created_by` int DEFAULT NULL,
                          `updated_by` int DEFAULT NULL,
                          `final_price` decimal(10,2) DEFAULT NULL,
                          `margin` decimal(10,2) DEFAULT NULL,
                          `margin_percentage` decimal(10,2) DEFAULT NULL,
                          `selling_at_loss` tinyint(1) DEFAULT '0',
                          `deleted_by` int DEFAULT NULL,
                          `times_ordered` int DEFAULT NULL,
                          PRIMARY KEY (`id_burger`),
                          KEY `idx_burger_id_user` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `burger`
--

LOCK TABLES `burger` WRITE;
/*!40000 ALTER TABLE `burger` DISABLE KEYS */;
INSERT INTO `burger` VALUES (1,'Clásica Tetris','Hamburguesa tradicional con ingredientes frescos y balance perfecto de sabor',14199.00,1,1,0,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775461989572-0f8889ba-gemini_generated_image_csanudcsanudcsan.png','burgers/1775461989572-0f8889ba-gemini_generated_image_csanudcsanudcsan.png',NULL,'2026-04-06 01:51:13','2026-04-06 20:44:50',NULL,1,1,20000.00,5801.00,40.85,0,NULL,3),(2,'Hawaiana Burger','Combinación dulce y salada con piña caramelizada y jamón.',20601.00,1,1,0,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775522947185-79a825cc-gemini_generated_image_qqdjvwqqdjvwqqdj.png','burgers/1775522947185-79a825cc-gemini_generated_image_qqdjvwqqdjvwqqdj.png',NULL,'2026-04-06 19:28:49','2026-04-06 20:43:54',NULL,1,1,22000.00,1399.00,6.79,0,NULL,3),(3,'Crispy Chicken','Hamburguesa de pollo crujiente con textura dorada y sabor suave.',12699.00,1,1,0,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523294687-be9e0376-gemini_generated_image_e6ct15e6ct15e6ct.png','burgers/1775523294687-be9e0376-gemini_generated_image_e6ct15e6ct15e6ct.png',NULL,'2026-04-06 19:41:21','2026-04-06 20:44:23',NULL,1,1,22000.00,9301.00,73.24,0,NULL,1),(4,'Mega Doble','Hamburguesa contundente con doble carne, huevo y tocineta para los más exigentes.',24700.00,1,1,0,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523550674-15415f10-gemini_generated_image_w7dx5vw7dx5vw7dx.png','burgers/1775523550674-15415f10-gemini_generated_image_w7dx5vw7dx5vw7dx.png',NULL,'2026-04-06 19:44:17','2026-04-07 00:59:11',NULL,1,1,29000.00,4300.00,17.41,0,NULL,1),(5,'?? Mexicana Picante','Hamburguesa con sabores intensos y picantes inspirados en México.',19200.00,1,1,0,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523769049-f62cc639-gemini_generated_image_cyfuwncyfuwncyfu.png','burgers/1775523769049-f62cc639-gemini_generated_image_cyfuwncyfuwncyfu.png',NULL,'2026-04-06 19:52:25','2026-04-06 20:44:32',NULL,1,1,25000.00,5800.00,30.21,0,NULL,2),(6,'?? Americana','Hamburguesa al estilo estadounidense con sabores clásicos y contundentes.',20300.00,1,1,0,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523915438-568cdd78-gemini_generated_image_cgm1ztcgm1ztcgm1.png','burgers/1775523915438-568cdd78-gemini_generated_image_cgm1ztcgm1ztcgm1.png',NULL,'2026-04-06 19:57:14','2026-04-06 20:44:40',NULL,1,1,25000.00,4700.00,23.15,0,NULL,2),(7,'Mix Bacon','Ideal para los amantes de la tocineta, con un sabor ahumado y dulce irresistible.',24801.00,1,1,0,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775524106614-5bc9df14-gemini_generated_image_xspirsxspirsxspi.png','burgers/1775524106614-5bc9df14-gemini_generated_image_xspirsxspirsxspi.png',NULL,'2026-04-06 20:03:23','2026-04-07 01:08:27',NULL,1,1,25000.00,199.00,0.80,0,NULL,0),(8,'Pollo Burger','El sabor del mejor pollo ',19799.00,1,1,0,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775524713276-5978888b-gemini_generated_image_bm581tbm581tbm58.png','burgers/1775524713276-5978888b-gemini_generated_image_bm581tbm581tbm58.png',NULL,'2026-04-06 20:10:22','2026-04-07 01:18:34',NULL,1,1,29000.00,9201.00,46.47,0,NULL,1),(9,'La Reina','Carga máxima de proteína',31501.00,1,1,0,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775525190019-d7b9c180-gemini_generated_image_25pie25pie25pie2.png','burgers/1775525190019-d7b9c180-gemini_generated_image_25pie25pie25pie2.png',NULL,'2026-04-06 20:20:37','2026-04-07 01:26:30',NULL,1,1,35000.00,3499.00,11.11,0,NULL,16),(10,'Monster','Hamburguesa extrema con triple porción de proteína, ideal para los más exigentes.',34601.00,1,1,0,1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775527731178-931ab765-gemini_generated_image_6xnl6t6xnl6t6xnl.png','burgers/1775527731178-931ab765-gemini_generated_image_6xnl6t6xnl6t6xnl.png',NULL,'2026-04-06 20:30:48','2026-04-07 02:08:52',NULL,1,1,40000.00,5399.00,15.60,0,NULL,17),(11,'DevOps Burguer',NULL,19700.00,1,0,1,0,NULL,NULL,7,'2026-04-06 21:14:34','2026-04-06 21:14:34',NULL,7,7,19700.00,0.00,0.00,0,NULL,0),(12,'WCG',NULL,43299.00,1,0,1,0,NULL,NULL,11,'2026-04-07 06:35:15','2026-04-07 06:35:15',NULL,11,11,43299.00,0.00,0.00,0,NULL,1),(13,'????????????????????‍??‍??‍??‍♂️?????????????????',NULL,43200.00,1,0,0,0,NULL,NULL,12,'2026-04-07 06:46:33',NULL,NULL,12,NULL,43200.00,0.00,0.00,0,NULL,NULL),(14,'Yisus burger',NULL,39300.00,1,0,1,0,NULL,NULL,13,'2026-04-07 07:11:08','2026-04-07 07:11:08',NULL,13,13,39300.00,0.00,0.00,0,NULL,1),(15,'My Special Burger',NULL,38000.00,1,0,1,0,NULL,NULL,17,'2026-04-07 21:40:54','2026-04-07 21:40:55',NULL,17,17,38000.00,0.00,0.00,0,NULL,1),(16,'Rayo McQueen Burger',NULL,23002.00,1,0,1,0,NULL,NULL,3,'2026-04-08 09:45:39','2026-04-08 09:45:40',NULL,3,3,23002.00,0.00,0.00,0,NULL,2),(17,'Burger super Che ',NULL,12500.00,1,0,0,0,NULL,NULL,20,'2026-04-08 10:48:15',NULL,NULL,20,NULL,12500.00,0.00,0.00,0,NULL,NULL),(18,'The Yahu special',NULL,41699.00,1,0,1,0,NULL,NULL,23,'2026-04-08 10:48:33','2026-04-08 10:48:33',NULL,23,23,41699.00,0.00,0.00,0,NULL,0),(19,'La verger ',NULL,28000.00,1,0,1,0,NULL,NULL,19,'2026-04-08 10:50:11','2026-04-08 10:50:11',NULL,19,19,28000.00,0.00,0.00,0,NULL,0),(20,'Aaaaaaaaaaaaa',NULL,23993.00,1,0,1,0,NULL,NULL,22,'2026-04-08 10:50:45','2026-04-08 10:50:45',NULL,22,22,23993.00,0.00,0.00,0,NULL,0),(21,'Aaaaaaaaaaaaa',NULL,23993.00,1,0,1,0,NULL,NULL,22,'2026-04-08 10:50:45','2026-04-08 10:50:45',NULL,22,22,23993.00,0.00,0.00,0,NULL,0),(22,'la super',NULL,37101.00,1,0,1,0,NULL,NULL,5,'2026-04-08 11:48:13','2026-04-08 11:56:06',NULL,5,5,37101.00,0.00,0.00,0,NULL,0),(23,'My burger',NULL,18800.00,1,0,1,0,NULL,NULL,24,'2026-04-08 21:04:13','2026-04-08 21:04:14',NULL,24,24,18800.00,0.00,0.00,0,NULL,0);
/*!40000 ALTER TABLE `burger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `burger_ingredient`
--

DROP TABLE IF EXISTS `burger_ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `burger_ingredient` (
                                     `id_burger_ingredient` int NOT NULL AUTO_INCREMENT,
                                     `id_burger` int NOT NULL,
                                     `id_product` int NOT NULL,
                                     `quantity` int NOT NULL DEFAULT '1',
                                     `price_at_time` decimal(38,2) NOT NULL,
                                     `product_name` varchar(255) NOT NULL,
                                     `subtotal` decimal(38,2) NOT NULL,
                                     `is_optional` tinyint(1) NOT NULL DEFAULT '0',
                                     `image_url` varchar(500) DEFAULT NULL,
                                     PRIMARY KEY (`id_burger_ingredient`),
                                     KEY `FKmjeklnasd7aj9vfjq5rmw586c` (`id_burger`),
                                     KEY `FK3j1pxkq2w5vetubq63o41oo3o` (`id_product`),
                                     CONSTRAINT `FK3j1pxkq2w5vetubq63o41oo3o` FOREIGN KEY (`id_product`) REFERENCES `product` (`id_product`),
                                     CONSTRAINT `FKmjeklnasd7aj9vfjq5rmw586c` FOREIGN KEY (`id_burger`) REFERENCES `burger` (`id_burger`)
) ENGINE=InnoDB AUTO_INCREMENT=510 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `burger_ingredient`
--

LOCK TABLES `burger_ingredient` WRITE;
/*!40000 ALTER TABLE `burger_ingredient` DISABLE KEYS */;
INSERT INTO `burger_ingredient` VALUES (175,4,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(176,4,7,2,7000.00,'Carne de Res 180g',14000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(177,4,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(178,4,25,1,3000.00,'Tocineta Crocante',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(179,4,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(180,4,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(181,4,23,1,900.00,'Salsa Picante',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460149525-c93c25aa-gemini_generated_image_apgik1apgik1apgi.png'),(182,4,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(202,7,25,4,3000.00,'Tocineta Crocante',12000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(203,7,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(204,7,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(205,7,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(206,7,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(207,7,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(262,8,9,1,6000.00,'Pechuga de Pollo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458278520-e122299f-gemini_generated_image_3d2jys3d2jys3d2j.png'),(263,8,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(264,8,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(265,8,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(266,8,5,1,899.00,'Tomate',899.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(267,8,2,1,2500.00,'Pan Tradicional',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png'),(268,8,44,1,5000.00,'Nuggets de Pollo',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522418534-91dc363f-gemini_generated_image_7rilgs7rilgs7ril.png'),(269,8,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(270,8,17,1,2000.00,'Queso Americano',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png'),(297,9,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(298,9,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(299,9,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(300,9,25,1,3000.00,'Tocineta Crocante',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(301,9,12,1,6000.00,'Pollo Crispy',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png'),(302,9,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(303,9,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(304,9,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(305,9,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(306,9,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(307,9,10,1,1200.00,'Pepinillos',1200.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png'),(319,2,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(320,2,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(321,2,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(322,2,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(323,2,19,1,500.00,'Mostaza',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459381749-25d6fb80-gemini_generated_image_s5nuyds5nuyds5nu.png'),(324,2,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(325,2,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(326,2,41,1,1500.00,'Piña a la Parrilla',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775521866584-4935869c-gemini_generated_image_yzpfc6yzpfc6yzpf.png'),(327,2,18,1,2000.00,'Queso Mozzarella',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459933936-8f991b57-gemini_generated_image_insfkdinsfkdinsf.png'),(328,2,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(329,2,43,1,4000.00,'Jamón',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(330,3,2,1,2500.00,'Pan Tradicional',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png'),(331,3,12,1,6000.00,'Pollo Crispy',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png'),(332,3,18,1,2000.00,'Queso Mozzarella',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459933936-8f991b57-gemini_generated_image_insfkdinsfkdinsf.png'),(333,3,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(334,3,5,1,899.00,'Tomate',899.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(335,3,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(336,5,2,1,2500.00,'Pan Tradicional',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png'),(337,5,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(338,5,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(339,5,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(340,5,23,3,900.00,'Salsa Picante',2700.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460149525-c93c25aa-gemini_generated_image_apgik1apgik1apgi.png'),(341,5,46,2,1500.00,'Jalapeños',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png'),(342,5,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(343,6,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(344,6,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(345,6,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(346,6,25,2,3000.00,'Tocineta Crocante',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(347,6,10,1,1200.00,'Pepinillos',1200.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png'),(348,6,14,2,800.00,'Salsa BBQ',1600.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(349,1,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(350,1,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(351,1,5,1,899.00,'Tomate',899.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(352,1,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(353,1,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(354,1,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(355,1,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(356,10,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(357,10,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(358,10,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(359,10,12,1,6000.00,'Pollo Crispy',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png'),(360,10,25,1,3000.00,'Tocineta Crocante',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(361,10,18,1,2000.00,'Queso Mozzarella',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459933936-8f991b57-gemini_generated_image_insfkdinsfkdinsf.png'),(362,10,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(363,10,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(364,10,43,1,4000.00,'Jamón',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(365,10,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(366,10,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(367,11,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(368,11,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(369,11,6,1,699.00,'Cebolla Blanca',699.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png'),(370,11,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(371,11,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(372,11,11,1,900.00,'Espinaca',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png'),(373,11,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(374,11,46,1,1500.00,'Jalapeños',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png'),(375,11,43,1,4000.00,'Jamón',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(376,11,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(377,11,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(378,11,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(379,12,7,2,7000.00,'Carne de Res 180g',14000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(380,12,47,2,1500.00,'Aguacate',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(381,12,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(382,12,11,1,900.00,'Espinaca',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png'),(383,12,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(384,12,6,1,699.00,'Cebolla Blanca',699.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png'),(385,12,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(386,12,5,1,899.00,'Tomate',899.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(387,12,10,1,1200.00,'Pepinillos',1200.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png'),(388,12,25,3,3000.00,'Tocineta Crocante',9000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(389,12,43,2,4000.00,'Jamón',8000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(390,12,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(391,13,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(392,13,25,1,3000.00,'Tocineta Crocante',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(393,13,7,2,7000.00,'Carne de Res 180g',14000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(394,13,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(395,13,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(396,13,43,1,4000.00,'Jamón',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(397,13,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(398,13,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(399,13,42,1,5000.00,'Papas a la francesa',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png'),(400,13,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(401,13,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(402,13,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(403,13,21,1,800.00,'Salsa de Ajo',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460053426-00e97e00-gemini_generated_image_u1xuhu1xuhu1xuhu.png'),(404,13,26,1,1500.00,'Salsa Maíz Dulce',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460624208-1d4aa786-gemini_generated_image_y1p3j7y1p3j7y1p3.png'),(405,14,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(406,14,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(407,14,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(408,14,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(409,14,43,1,4000.00,'Jamón',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(410,14,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(411,14,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(412,14,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(413,14,9,1,6000.00,'Pechuga de Pollo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458278520-e122299f-gemini_generated_image_3d2jys3d2jys3d2j.png'),(414,14,42,1,5000.00,'Papas a la francesa',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png'),(415,14,17,1,2000.00,'Queso Americano',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png'),(416,14,25,1,3000.00,'Tocineta Crocante',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(417,15,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(418,15,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(419,15,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(420,15,6,1,699.00,'Cebolla Blanca',699.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png'),(421,15,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(422,15,8,3,800.00,'Cebolla Morada',2400.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(423,15,11,1,900.00,'Espinaca',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png'),(424,15,12,3,6000.00,'Pollo Crispy',18000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png'),(425,16,47,2,1500.00,'Aguacate',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(426,16,13,2,6000.00,'Carne de Cerdo 180g',12000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(427,16,24,2,1501.00,'Cebolla Caramelizada',3002.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(428,16,1,2,2500.00,'Pan brioche',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(429,17,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(430,17,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(431,17,44,1,5000.00,'Nuggets de Pollo',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522418534-91dc363f-gemini_generated_image_7rilgs7rilgs7ril.png'),(432,18,7,2,7000.00,'Carne de Res 180g',14000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(433,18,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(434,18,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(435,18,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(436,18,19,1,500.00,'Mostaza',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459381749-25d6fb80-gemini_generated_image_s5nuyds5nuyds5nu.png'),(437,18,2,1,2500.00,'Pan Tradicional',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png'),(438,18,42,2,5000.00,'Papas a la francesa',10000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png'),(439,18,17,2,2000.00,'Queso Americano',4000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png'),(440,18,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(441,18,23,1,900.00,'Salsa Picante',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460149525-c93c25aa-gemini_generated_image_apgik1apgik1apgi.png'),(442,18,25,2,3000.00,'Tocineta Crocante',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png'),(443,18,5,1,899.00,'Tomate',899.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(444,19,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(445,19,7,1,7000.00,'Carne de Res 180g',7000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(446,19,6,1,699.00,'Cebolla Blanca',699.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png'),(447,19,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(448,19,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(449,19,46,1,1500.00,'Jalapeños',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png'),(450,19,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(451,19,3,1,800.00,'Lechuga',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png'),(452,19,16,1,500.00,'Mayonesa',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png'),(453,19,19,1,500.00,'Mostaza',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459381749-25d6fb80-gemini_generated_image_s5nuyds5nuyds5nu.png'),(454,19,42,1,5000.00,'Papas a la francesa',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png'),(455,19,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(456,19,10,1,1200.00,'Pepinillos',1200.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png'),(457,19,17,1,2000.00,'Queso Americano',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png'),(458,19,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(459,20,47,6,1500.00,'Aguacate',9000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(460,20,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(461,20,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(462,20,11,1,900.00,'Espinaca',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png'),(463,20,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(464,20,5,7,899.00,'Tomate',6293.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(465,21,47,6,1500.00,'Aguacate',9000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(466,21,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(467,21,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(468,21,11,1,900.00,'Espinaca',900.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png'),(469,21,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(470,21,5,7,899.00,'Tomate',6293.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png'),(493,22,1,1,2500.00,'Pan brioche',2500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(494,22,7,2,7000.00,'Carne de Res 180g',14000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png'),(495,22,15,1,2000.00,'Queso Cheddar',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png'),(496,22,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png'),(497,22,46,1,1500.00,'Jalapeños',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png'),(498,22,47,1,1500.00,'Aguacate',1500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(499,22,20,1,500.00,'Ketchup',500.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png'),(500,22,14,1,800.00,'Salsa BBQ',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png'),(501,22,24,1,1501.00,'Cebolla Caramelizada',1501.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png'),(502,22,45,1,1000.00,'Huevo Frito',1000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(503,22,42,1,5000.00,'Papas a la francesa',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png'),(504,22,13,1,6000.00,'Carne de Cerdo 180g',6000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png'),(505,23,47,2,1500.00,'Aguacate',3000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png'),(506,23,1,2,2500.00,'Pan brioche',5000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png'),(507,23,43,2,4000.00,'Jamón',8000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png'),(508,23,45,2,1000.00,'Huevo Frito',2000.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png'),(509,23,8,1,800.00,'Cebolla Morada',800.00,0,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png');
/*!40000 ALTER TABLE `burger_ingredient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `burger_settings`
--

DROP TABLE IF EXISTS `burger_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `burger_settings` (
                                   `id_settings` int NOT NULL DEFAULT '1',
                                   `min_price` decimal(10,2) DEFAULT NULL,
                                   `max_price` decimal(10,2) DEFAULT NULL,
                                   `min_ingredients` int DEFAULT NULL,
                                   `max_ingredients` int DEFAULT NULL,
                                   `custom_burgers_enabled` tinyint(1) NOT NULL DEFAULT '1',
                                   `updated_at` datetime DEFAULT NULL,
                                   `custom_burger_max_price` decimal(10,2) NOT NULL,
                                   `custom_burger_min_price` decimal(10,2) NOT NULL,
                                   PRIMARY KEY (`id_settings`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `burger_settings`
--

LOCK TABLES `burger_settings` WRITE;
/*!40000 ALTER TABLE `burger_settings` DISABLE KEYS */;
INSERT INTO `burger_settings` VALUES (1,NULL,NULL,2,15,1,'2026-04-06 21:14:34',50000.00,10000.00);
/*!40000 ALTER TABLE `burger_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
                        `id_cart` int NOT NULL AUTO_INCREMENT,
                        `id_user` int NOT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL,
                        PRIMARY KEY (`id_cart`),
                        UNIQUE KEY `id_user` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,4,'2026-04-06 02:08:43','2026-04-06 02:08:43'),(2,5,'2026-04-06 17:49:10','2026-04-06 17:49:10'),(3,3,'2026-04-06 20:24:01','2026-04-06 20:24:01'),(4,7,'2026-04-06 21:14:34','2026-04-06 21:14:34'),(5,12,'2026-04-07 06:29:38','2026-04-07 06:29:38'),(6,11,'2026-04-07 06:35:15','2026-04-07 06:35:15'),(8,13,'2026-04-07 07:11:08','2026-04-07 07:11:08'),(10,6,'2026-04-07 11:08:31','2026-04-07 11:08:31'),(11,14,'2026-04-07 11:11:54','2026-04-07 11:11:54'),(12,15,'2026-04-07 17:11:25','2026-04-07 17:11:25'),(13,16,'2026-04-07 17:38:45','2026-04-07 17:38:45'),(14,17,'2026-04-07 21:40:55','2026-04-07 21:40:55'),(16,18,'2026-04-08 10:43:09','2026-04-08 10:43:09'),(17,19,'2026-04-08 10:44:48','2026-04-08 10:44:48'),(18,20,'2026-04-08 10:44:54','2026-04-08 10:44:54'),(19,22,'2026-04-08 10:46:25','2026-04-08 10:46:25'),(20,23,'2026-04-08 10:48:33','2026-04-08 10:48:33'),(22,21,'2026-04-08 10:48:59','2026-04-08 10:48:59'),(23,24,'2026-04-08 21:04:13','2026-04-08 21:04:13');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
                             `id_cart_item` int NOT NULL AUTO_INCREMENT,
                             `id_cart` int NOT NULL,
                             `item_type` enum('ADDITION','BURGER','PRODUCT') NOT NULL,
                             `quantity` int NOT NULL DEFAULT '1',
                             `unit_price` decimal(10,2) NOT NULL,
                             `subtotal` decimal(10,2) NOT NULL,
                             `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `id_item` int NOT NULL,
                             `image_url` varchar(255) DEFAULT NULL,
                             `name` varchar(150) NOT NULL,
                             PRIMARY KEY (`id_cart_item`),
                             KEY `FKs3p4c1u0wxi7wg88j5gxpxiqe` (`id_cart`),
                             CONSTRAINT `FKs3p4c1u0wxi7wg88j5gxpxiqe` FOREIGN KEY (`id_cart`) REFERENCES `cart` (`id_cart`)
) ENGINE=InnoDB AUTO_INCREMENT=1027 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (43,4,'BURGER',1,19700.00,19700.00,'2026-04-06 21:14:34',11,'/assets/burgerCustom-CHEdnGg6.png','DevOps Burguer'),(353,11,'BURGER',1,25000.00,25000.00,'2026-04-07 11:11:56',6,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523915438-568cdd78-gemini_generated_image_cgm1ztcgm1ztcgm1.png','?? Americana'),(354,11,'BURGER',1,25000.00,25000.00,'2026-04-07 11:11:56',5,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523769049-f62cc639-gemini_generated_image_cyfuwncyfuwncyfu.png','?? Mexicana Picante'),(942,19,'BURGER',20,40000.00,800000.00,'2026-04-08 10:50:45',10,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775527731178-931ab765-gemini_generated_image_6xnl6t6xnl6t6xnl.png','Monster'),(943,19,'BURGER',13,35000.00,455000.00,'2026-04-08 10:50:45',9,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775525190019-d7b9c180-gemini_generated_image_25pie25pie25pie2.png','La Reina'),(944,19,'BURGER',5,25000.00,125000.00,'2026-04-08 10:50:45',6,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523915438-568cdd78-gemini_generated_image_cgm1ztcgm1ztcgm1.png','?? Americana'),(945,19,'BURGER',28,25000.00,700000.00,'2026-04-08 10:50:45',5,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523769049-f62cc639-gemini_generated_image_cyfuwncyfuwncyfu.png','?? Mexicana Picante'),(946,19,'BURGER',12,29000.00,348000.00,'2026-04-08 10:50:45',4,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/burgers/1775523550674-15415f10-gemini_generated_image_w7dx5vw7dx5vw7dx.png','Mega Doble'),(947,19,'BURGER',1,23993.00,23993.00,'2026-04-08 10:50:45',20,'/assets/burgerCustom-CHEdnGg6.png','Aaaaaaaaaaaaa'),(948,19,'BURGER',1,23993.00,23993.00,'2026-04-08 10:50:45',21,'/assets/burgerCustom-CHEdnGg6.png','Aaaaaaaaaaaaa');
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite_burger`
--

DROP TABLE IF EXISTS `favorite_burger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite_burger` (
                                   `id_favorite` int NOT NULL AUTO_INCREMENT,
                                   `id_user` int NOT NULL,
                                   `id_burger` int NOT NULL,
                                   `name` varchar(50) NOT NULL,
                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id_favorite`),
                                   UNIQUE KEY `UK4do14xdgmy4mfjsvf5hivck8r` (`id_user`,`id_burger`),
                                   KEY `FKtjm6anxnmslb8mn1qc6pofwmw` (`id_burger`),
                                   CONSTRAINT `FK1t0vokxvidvwog0q8aldngm6f` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
                                   CONSTRAINT `FKtjm6anxnmslb8mn1qc6pofwmw` FOREIGN KEY (`id_burger`) REFERENCES `burger` (`id_burger`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite_burger`
--

LOCK TABLES `favorite_burger` WRITE;
/*!40000 ALTER TABLE `favorite_burger` DISABLE KEYS */;
INSERT INTO `favorite_burger` VALUES (1,7,11,'DevOps Burguer','2026-04-06 21:14:34'),(2,11,12,'WCG','2026-04-07 06:35:15'),(3,13,14,'Yisus burger','2026-04-07 07:11:08'),(4,17,15,'My Special Burger','2026-04-07 21:40:55'),(5,3,16,'Rayo McQueen Burger','2026-04-08 09:45:40'),(6,23,18,'The Yahu special','2026-04-08 10:48:33'),(7,19,19,'La verger','2026-04-08 10:50:11'),(8,22,20,'Aaaaaaaaaaaaa','2026-04-08 10:50:45'),(9,22,21,'Aaaaaaaaaaaaa','2026-04-08 10:50:45'),(10,5,22,'la super','2026-04-08 11:48:14'),(11,24,23,'My burger','2026-04-08 21:04:14');
/*!40000 ALTER TABLE `favorite_burger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
                           `id_invoice` int NOT NULL AUTO_INCREMENT,
                           `id_order` int NOT NULL,
                           `id_payment` int NOT NULL,
                           `invoice_number` varchar(50) DEFAULT NULL,
                           `external_invoice_id` varchar(100) DEFAULT NULL,
                           `total_amount` decimal(38,2) DEFAULT NULL,
                           `status` enum('PENDING','ISSUED','FAILED') NOT NULL DEFAULT 'PENDING',
                           `invoice_date` datetime(6) DEFAULT NULL,
                           `pdf_url` varchar(500) DEFAULT NULL,
                           PRIMARY KEY (`id_invoice`),
                           UNIQUE KEY `id_order` (`id_order`),
                           UNIQUE KEY `invoice_number` (`invoice_number`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,1,1,'FV-ORD-2026-04-06-A3C6','ORD-2026-04-06-A3C6',30499.00,'ISSUED','2026-04-06 02:11:03.859443','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775459465384.pdf'),(2,2,2,'FV-ORD-2026-04-06-3D7B','ORD-2026-04-06-3D7B',28000.00,'ISSUED','2026-04-06 17:50:40.925609','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775515841971.pdf'),(3,3,3,'FV-ORD-2026-04-07-B5DE','ORD-2026-04-07-B5DE',43000.00,'ISSUED','2026-04-07 06:23:17.925480','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775560999007.pdf'),(4,5,4,'FV-ORD-2026-04-07-36BC','ORD-2026-04-07-36BC',43299.00,'ISSUED','2026-04-07 06:37:16.200669','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775561837064.pdf'),(5,4,5,'FV-ORD-2026-04-07-AA7A','ORD-2026-04-07-AA7A',40000.00,'ISSUED','2026-04-07 06:39:04.587593','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775561945993.pdf'),(6,6,6,'FV-ORD-2026-04-07-F615','ORD-2026-04-07-F615',40000.00,'ISSUED','2026-04-07 06:39:12.900139','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775561954303.pdf'),(7,8,7,'FV-ORD-2026-04-07-77C2','ORD-2026-04-07-77C2',39300.00,'ISSUED','2026-04-07 07:16:48.597957','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775564209549.pdf'),(8,9,8,'FV-ORD-2026-04-07-5F9B','ORD-2026-04-07-5F9B',75000.00,'ISSUED','2026-04-07 17:15:27.664759','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775600129001.pdf'),(9,10,9,'FV-ORD-2026-04-07-A48D','ORD-2026-04-07-A48D',39500.00,'ISSUED','2026-04-07 17:41:59.971397','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775601721716.pdf'),(10,11,10,'FV-ORD-2026-04-07-270D','ORD-2026-04-07-270D',43000.00,'ISSUED','2026-04-07 20:27:42.594687','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775611663682.pdf'),(11,12,11,'FV-ORD-2026-04-07-5114','ORD-2026-04-07-5114',58500.00,'ISSUED','2026-04-07 21:46:12.565786','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775616373638.pdf'),(12,13,12,'FV-ORD-2026-04-07-6D95','ORD-2026-04-07-6D95',59500.00,'ISSUED','2026-04-07 22:00:31.152908','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775617232217.pdf'),(13,14,13,'ERR-14',NULL,136504.00,'FAILED','2026-04-08 09:48:09.669525',NULL),(14,15,14,'ERR-15',NULL,39500.00,'FAILED','2026-04-08 09:50:41.670380',NULL),(15,16,15,'ERR-16',NULL,39500.00,'FAILED','2026-04-08 09:51:38.196003',NULL),(16,17,16,'ERR-17',NULL,125000.00,'FAILED','2026-04-08 10:04:35.422144',NULL),(17,49,17,'ERR-49',NULL,750000.00,'FAILED','2026-04-08 10:48:38.135670',NULL),(18,81,18,'ERR-81',NULL,183000.00,'FAILED','2026-04-08 10:50:29.468424',NULL),(19,104,20,'FV-ORD-2026-04-09-96C4','ORD-2026-04-09-96C4',31000.00,'ISSUED','2026-04-09 11:16:55.873060','https://tetrisburger-image.s3.us-east-2.amazonaws.com/invoices/factura-1775751417622.pdf');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
                        `id_menu` int NOT NULL AUTO_INCREMENT,
                        `name` varchar(150) NOT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `is_available` tinyint(1) NOT NULL DEFAULT '1',
                        `image_url` varchar(255) DEFAULT NULL,
                        `image_key` varchar(255) DEFAULT NULL,
                        `id_menu_category` int DEFAULT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL,
                        `deleted_at` datetime DEFAULT NULL,
                        `created_by` int DEFAULT NULL,
                        `updated_by` int DEFAULT NULL,
                        `deleted_by` int DEFAULT NULL,
                        PRIMARY KEY (`id_menu`),
                        KEY `FKkmgvkdj11q4rp4jpx5dr3kvvi` (`id_menu_category`),
                        CONSTRAINT `FKkmgvkdj11q4rp4jpx5dr3kvvi` FOREIGN KEY (`id_menu_category`) REFERENCES `menu_category` (`id_menu_category`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'Hamburguesas','Deliciosas hamburguesas preparadas con ingredientes frescos y combinaciones únicas. Perfectas para disfrutar de un sabor auténtico en cada bocado.',1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/menus/1775530827710-b733b888-gemini_generated_image_acyi7aacyi7aacyi.png','menus/1775530827710-b733b888-gemini_generated_image_acyi7aacyi7aacyi.png',1,'2026-04-06 21:40:25','2026-04-06 22:00:29',NULL,1,1,NULL),(2,'Bebidas','Refrescantes bebidas ideales para acompañar tus comidas y calmar la sed en cualquier momento.',1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/menus/1775605619482-357c23be-whatsapp_image_2026-04-07_at_6.45.09_pm.jpeg','menus/1775605619482-357c23be-whatsapp_image_2026-04-07_at_6.45.09_pm.jpeg',2,'2026-04-06 21:46:36','2026-04-07 18:47:00',NULL,1,1,NULL),(3,'Bebidas Especiales','Bebidas preparadas al momento con sabores frutales y refrescantes que le dan un toque diferente a tu experiencia.',1,'https://tetrisburger-image.s3.us-east-2.amazonaws.com/menus/1775605629956-7a85674f-whatsapp_image_2026-04-07_at_6.43.28_pm.jpeg','menus/1775605629956-7a85674f-whatsapp_image_2026-04-07_at_6.43.28_pm.jpeg',2,'2026-04-06 21:47:22','2026-04-07 18:47:10',NULL,1,1,NULL);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_category`
--

DROP TABLE IF EXISTS `menu_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_category` (
                                 `id_menu_category` int NOT NULL AUTO_INCREMENT,
                                 `category_name` varchar(100) NOT NULL,
                                 `description` varchar(255) DEFAULT NULL,
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime DEFAULT NULL,
                                 `deleted_at` datetime DEFAULT NULL,
                                 `created_by` int DEFAULT NULL,
                                 `deleted_by` int DEFAULT NULL,
                                 `updated_by` int DEFAULT NULL,
                                 PRIMARY KEY (`id_menu_category`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_category`
--

LOCK TABLES `menu_category` WRITE;
/*!40000 ALTER TABLE `menu_category` DISABLE KEYS */;
INSERT INTO `menu_category` VALUES (1,'Hamburguesas','Deliciosas hamburguesas preparadas con ingredientes frescos y combinaciones únicas. Perfectas para disfrutar de un sabor auténtico en cada bocado.','2026-04-06 21:39:02',NULL,NULL,1,NULL,NULL),(2,'Bebidas','Refrescantes bebidas ideales para acompañar tus comidas y calmar la sed en cualquier momento.','2026-04-06 21:45:13',NULL,NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `menu_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_item`
--

DROP TABLE IF EXISTS `menu_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_item` (
                             `id_menu_item` int NOT NULL AUTO_INCREMENT,
                             `id_menu` int NOT NULL,
                             `item_type` varchar(20) NOT NULL,
                             `id_burger` int DEFAULT NULL,
                             `id_product` int DEFAULT NULL,
                             PRIMARY KEY (`id_menu_item`),
                             KEY `FKoqjyo2tvxaisnkxvrahxdjf57` (`id_menu`),
                             KEY `FKpfvg400o0fuuduq1jbyod7uyw` (`id_burger`),
                             KEY `FK3nnxn46xsa38ay2h4e10mslcr` (`id_product`),
                             CONSTRAINT `FK3nnxn46xsa38ay2h4e10mslcr` FOREIGN KEY (`id_product`) REFERENCES `product` (`id_product`),
                             CONSTRAINT `FKoqjyo2tvxaisnkxvrahxdjf57` FOREIGN KEY (`id_menu`) REFERENCES `menu` (`id_menu`),
                             CONSTRAINT `FKpfvg400o0fuuduq1jbyod7uyw` FOREIGN KEY (`id_burger`) REFERENCES `burger` (`id_burger`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_item`
--

LOCK TABLES `menu_item` WRITE;
/*!40000 ALTER TABLE `menu_item` DISABLE KEYS */;
INSERT INTO `menu_item` VALUES (23,1,'BURGER',1,NULL),(24,1,'BURGER',2,NULL),(25,1,'BURGER',3,NULL),(26,1,'BURGER',4,NULL),(27,1,'BURGER',5,NULL),(28,1,'BURGER',6,NULL),(29,1,'BURGER',7,NULL),(30,1,'BURGER',8,NULL),(31,1,'BURGER',9,NULL),(32,1,'BURGER',10,NULL),(33,2,'PRODUCT',NULL,27),(34,2,'PRODUCT',NULL,28),(35,2,'PRODUCT',NULL,29),(36,2,'PRODUCT',NULL,30),(37,2,'PRODUCT',NULL,31),(38,2,'PRODUCT',NULL,34),(39,2,'PRODUCT',NULL,33),(40,2,'PRODUCT',NULL,32),(41,3,'PRODUCT',NULL,37),(42,3,'PRODUCT',NULL,38),(43,3,'PRODUCT',NULL,39),(44,3,'PRODUCT',NULL,40);
/*!40000 ALTER TABLE `menu_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
                         `id_order` int NOT NULL AUTO_INCREMENT,
                         `id_user` int NOT NULL,
                         `order_number` varchar(50) NOT NULL,
                         `status` enum('PENDING','ACCEPTED','IN_PROGRESS','READY','COMPLETED','CANCELLED_BY_EMPLOYEE') NOT NULL DEFAULT 'PENDING',
                         `total_amount` decimal(38,2) DEFAULT NULL,
                         `order_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime DEFAULT NULL,
                         `deleted_at` datetime DEFAULT NULL,
                         `created_by` int DEFAULT NULL,
                         `updated_by` int DEFAULT NULL,
                         `deleted_by` int DEFAULT NULL,
                         PRIMARY KEY (`id_order`),
                         UNIQUE KEY `order_number` (`order_number`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,4,'ORD-2026-04-06-A3C6','COMPLETED',30499.00,'2026-04-06 02:09:33','2026-04-06 02:11:51',NULL,4,1,NULL),(2,5,'ORD-2026-04-06-3D7B','COMPLETED',28000.00,'2026-04-06 17:50:02','2026-04-06 20:41:12',NULL,5,1,NULL),(3,5,'ORD-2026-04-07-B5DE','COMPLETED',43000.00,'2026-04-07 06:17:58','2026-04-07 06:40:40',NULL,5,2,NULL),(4,12,'ORD-2026-04-07-AA7A','ACCEPTED',40000.00,'2026-04-07 06:34:09','2026-04-07 06:39:05',NULL,12,2,NULL),(5,11,'ORD-2026-04-07-36BC','COMPLETED',43299.00,'2026-04-07 06:36:13','2026-04-07 06:40:11',NULL,11,2,NULL),(6,12,'ORD-2026-04-07-F615','ACCEPTED',40000.00,'2026-04-07 06:37:56','2026-04-07 06:39:13',NULL,12,2,NULL),(7,12,'ORD-2026-04-07-B815','PENDING',43200.00,'2026-04-07 06:46:43',NULL,NULL,12,NULL,NULL),(8,13,'ORD-2026-04-07-77C2','COMPLETED',39300.00,'2026-04-07 07:12:16','2026-04-07 07:18:04',NULL,13,6,NULL),(9,15,'ORD-2026-04-07-5F9B','ACCEPTED',75000.00,'2026-04-07 17:11:53','2026-04-07 17:15:28',NULL,15,6,NULL),(10,16,'ORD-2026-04-07-A48D','ACCEPTED',39500.00,'2026-04-07 17:39:18','2026-04-07 17:42:00',NULL,16,6,NULL),(11,5,'ORD-2026-04-07-270D','COMPLETED',43000.00,'2026-04-07 20:24:10','2026-04-07 20:30:27',NULL,5,1,NULL),(12,17,'ORD-2026-04-07-5114','COMPLETED',58500.00,'2026-04-07 21:43:09','2026-04-07 21:48:43',NULL,17,1,NULL),(13,5,'ORD-2026-04-07-6D95','CANCELLED_BY_EMPLOYEE',59500.00,'2026-04-07 21:59:55','2026-04-07 22:01:24',NULL,5,3,NULL),(14,3,'ORD-2026-04-08-0599','ACCEPTED',136504.00,'2026-04-08 09:45:49','2026-04-08 09:48:10',NULL,3,1,NULL),(15,16,'ORD-2026-04-08-6C49','ACCEPTED',39500.00,'2026-04-08 09:50:08','2026-04-08 09:50:42',NULL,16,1,NULL),(16,16,'ORD-2026-04-08-E5A8','ACCEPTED',39500.00,'2026-04-08 09:51:19','2026-04-08 09:51:38',NULL,16,2,NULL),(17,3,'ORD-2026-04-08-8FC1','ACCEPTED',125000.00,'2026-04-08 10:04:11','2026-04-08 10:04:35',NULL,3,2,NULL),(18,18,'ORD-2026-04-08-FDB2','PENDING',750000.00,'2026-04-08 10:43:46',NULL,NULL,18,NULL,NULL),(19,18,'ORD-2026-04-08-3DC9','PENDING',750000.00,'2026-04-08 10:43:48',NULL,NULL,18,NULL,NULL),(20,18,'ORD-2026-04-08-8F35','PENDING',750000.00,'2026-04-08 10:43:49',NULL,NULL,18,NULL,NULL),(21,18,'ORD-2026-04-08-360E','PENDING',750000.00,'2026-04-08 10:43:51',NULL,NULL,18,NULL,NULL),(22,18,'ORD-2026-04-08-1FE7','PENDING',750000.00,'2026-04-08 10:43:52',NULL,NULL,18,NULL,NULL),(23,18,'ORD-2026-04-08-1026','PENDING',750000.00,'2026-04-08 10:43:54',NULL,NULL,18,NULL,NULL),(24,18,'ORD-2026-04-08-D0DE','PENDING',750000.00,'2026-04-08 10:43:55',NULL,NULL,18,NULL,NULL),(25,18,'ORD-2026-04-08-743E','PENDING',750000.00,'2026-04-08 10:43:55',NULL,NULL,18,NULL,NULL),(26,18,'ORD-2026-04-08-C426','PENDING',750000.00,'2026-04-08 10:43:56',NULL,NULL,18,NULL,NULL),(27,18,'ORD-2026-04-08-DFDB','PENDING',750000.00,'2026-04-08 10:43:57',NULL,NULL,18,NULL,NULL),(28,18,'ORD-2026-04-08-CBA3','PENDING',750000.00,'2026-04-08 10:43:58',NULL,NULL,18,NULL,NULL),(29,18,'ORD-2026-04-08-3FA0','PENDING',750000.00,'2026-04-08 10:44:00',NULL,NULL,18,NULL,NULL),(30,18,'ORD-2026-04-08-3C9A','PENDING',750000.00,'2026-04-08 10:44:00',NULL,NULL,18,NULL,NULL),(31,18,'ORD-2026-04-08-7350','PENDING',750000.00,'2026-04-08 10:44:00',NULL,NULL,18,NULL,NULL),(32,18,'ORD-2026-04-08-5667','PENDING',750000.00,'2026-04-08 10:44:01',NULL,NULL,18,NULL,NULL),(33,18,'ORD-2026-04-08-6085','PENDING',750000.00,'2026-04-08 10:44:01',NULL,NULL,18,NULL,NULL),(34,18,'ORD-2026-04-08-998F','PENDING',750000.00,'2026-04-08 10:44:02',NULL,NULL,18,NULL,NULL),(35,18,'ORD-2026-04-08-6D79','PENDING',750000.00,'2026-04-08 10:44:05',NULL,NULL,18,NULL,NULL),(36,18,'ORD-2026-04-08-5CB9','PENDING',750000.00,'2026-04-08 10:44:05',NULL,NULL,18,NULL,NULL),(37,18,'ORD-2026-04-08-AC3E','PENDING',750000.00,'2026-04-08 10:44:05',NULL,NULL,18,NULL,NULL),(38,18,'ORD-2026-04-08-CFB0','PENDING',750000.00,'2026-04-08 10:44:06',NULL,NULL,18,NULL,NULL),(39,18,'ORD-2026-04-08-3A9A','PENDING',750000.00,'2026-04-08 10:44:14',NULL,NULL,18,NULL,NULL),(40,18,'ORD-2026-04-08-1AC2','PENDING',750000.00,'2026-04-08 10:44:15',NULL,NULL,18,NULL,NULL),(41,18,'ORD-2026-04-08-2FCB','PENDING',750000.00,'2026-04-08 10:44:15',NULL,NULL,18,NULL,NULL),(42,18,'ORD-2026-04-08-7C19','PENDING',750000.00,'2026-04-08 10:44:18',NULL,NULL,18,NULL,NULL),(43,18,'ORD-2026-04-08-BFE3','PENDING',750000.00,'2026-04-08 10:44:18',NULL,NULL,18,NULL,NULL),(44,18,'ORD-2026-04-08-8FBA','PENDING',750000.00,'2026-04-08 10:44:18',NULL,NULL,18,NULL,NULL),(45,18,'ORD-2026-04-08-5200','PENDING',750000.00,'2026-04-08 10:44:19',NULL,NULL,18,NULL,NULL),(46,18,'ORD-2026-04-08-62BB','PENDING',750000.00,'2026-04-08 10:44:19',NULL,NULL,18,NULL,NULL),(47,18,'ORD-2026-04-08-C2F1','PENDING',750000.00,'2026-04-08 10:44:19',NULL,NULL,18,NULL,NULL),(48,18,'ORD-2026-04-08-C236','PENDING',750000.00,'2026-04-08 10:44:20',NULL,NULL,18,NULL,NULL),(49,18,'ORD-2026-04-08-ED3E','ACCEPTED',750000.00,'2026-04-08 10:44:20','2026-04-08 10:48:38',NULL,18,1,NULL),(50,18,'ORD-2026-04-08-4D43','PENDING',35000.00,'2026-04-08 10:44:43',NULL,NULL,18,NULL,NULL),(51,18,'ORD-2026-04-08-470E','PENDING',35000.00,'2026-04-08 10:44:44',NULL,NULL,18,NULL,NULL),(52,18,'ORD-2026-04-08-500D','PENDING',35000.00,'2026-04-08 10:44:46',NULL,NULL,18,NULL,NULL),(53,16,'ORD-2026-04-08-FD68','PENDING',44500.00,'2026-04-08 10:45:24',NULL,NULL,16,NULL,NULL),(54,20,'ORD-2026-04-08-339C','PENDING',80000.00,'2026-04-08 10:45:27',NULL,NULL,20,NULL,NULL),(55,20,'ORD-2026-04-08-DCF7','PENDING',80000.00,'2026-04-08 10:45:32',NULL,NULL,20,NULL,NULL),(56,19,'ORD-2026-04-08-E189','PENDING',20000.00,'2026-04-08 10:45:40',NULL,NULL,19,NULL,NULL),(57,19,'ORD-2026-04-08-47EA','PENDING',20000.00,'2026-04-08 10:45:41',NULL,NULL,19,NULL,NULL),(58,19,'ORD-2026-04-08-EE47','PENDING',20000.00,'2026-04-08 10:45:42',NULL,NULL,19,NULL,NULL),(59,18,'ORD-2026-04-08-6DC1','PENDING',636000.00,'2026-04-08 10:45:43',NULL,NULL,18,NULL,NULL),(60,18,'ORD-2026-04-08-07C2','PENDING',636000.00,'2026-04-08 10:45:44',NULL,NULL,18,NULL,NULL),(61,19,'ORD-2026-04-08-AC09','PENDING',75000.00,'2026-04-08 10:47:08',NULL,NULL,19,NULL,NULL),(62,20,'ORD-2026-04-08-F91E','PENDING',12500.00,'2026-04-08 10:48:21',NULL,NULL,20,NULL,NULL),(63,18,'ORD-2026-04-08-0824','PENDING',440000.00,'2026-04-08 10:48:24',NULL,NULL,18,NULL,NULL),(64,18,'ORD-2026-04-08-607F','PENDING',440000.00,'2026-04-08 10:48:27',NULL,NULL,18,NULL,NULL),(65,18,'ORD-2026-04-08-18D0','PENDING',440000.00,'2026-04-08 10:48:27',NULL,NULL,18,NULL,NULL),(66,18,'ORD-2026-04-08-CA0E','PENDING',440000.00,'2026-04-08 10:48:27',NULL,NULL,18,NULL,NULL),(67,18,'ORD-2026-04-08-C26B','PENDING',440000.00,'2026-04-08 10:48:28',NULL,NULL,18,NULL,NULL),(68,18,'ORD-2026-04-08-C3DB','PENDING',440000.00,'2026-04-08 10:48:28',NULL,NULL,18,NULL,NULL),(69,18,'ORD-2026-04-08-A324','PENDING',440000.00,'2026-04-08 10:48:28',NULL,NULL,18,NULL,NULL),(70,18,'ORD-2026-04-08-67CE','PENDING',440000.00,'2026-04-08 10:48:28',NULL,NULL,18,NULL,NULL),(71,18,'ORD-2026-04-08-EA25','PENDING',440000.00,'2026-04-08 10:48:29',NULL,NULL,18,NULL,NULL),(72,18,'ORD-2026-04-08-CF79','PENDING',440000.00,'2026-04-08 10:48:29',NULL,NULL,18,NULL,NULL),(73,18,'ORD-2026-04-08-486F','PENDING',440000.00,'2026-04-08 10:48:29',NULL,NULL,18,NULL,NULL),(74,18,'ORD-2026-04-08-C609','PENDING',440000.00,'2026-04-08 10:48:30',NULL,NULL,18,NULL,NULL),(75,18,'ORD-2026-04-08-B096','PENDING',467000.00,'2026-04-08 10:49:03',NULL,NULL,18,NULL,NULL),(76,18,'ORD-2026-04-08-0D4A','PENDING',467000.00,'2026-04-08 10:49:09',NULL,NULL,18,NULL,NULL),(77,18,'ORD-2026-04-08-2026','PENDING',467000.00,'2026-04-08 10:49:09',NULL,NULL,18,NULL,NULL),(78,18,'ORD-2026-04-08-D239','PENDING',467000.00,'2026-04-08 10:49:10',NULL,NULL,18,NULL,NULL),(79,18,'ORD-2026-04-08-2D57','PENDING',467000.00,'2026-04-08 10:49:10',NULL,NULL,18,NULL,NULL),(80,18,'ORD-2026-04-08-E010','PENDING',467000.00,'2026-04-08 10:49:11',NULL,NULL,18,NULL,NULL),(81,21,'ORD-2026-04-08-ED38','ACCEPTED',183000.00,'2026-04-08 10:49:42','2026-04-08 10:50:29',NULL,21,1,NULL),(82,18,'ORD-2026-04-08-C95E','PENDING',35000.00,'2026-04-08 10:50:01',NULL,NULL,18,NULL,NULL),(83,19,'ORD-2026-04-08-B256','PENDING',103000.00,'2026-04-08 10:50:17',NULL,NULL,19,NULL,NULL),(84,19,'ORD-2026-04-08-1B2A','PENDING',103000.00,'2026-04-08 10:50:20',NULL,NULL,19,NULL,NULL),(85,19,'ORD-2026-04-08-E3E9','PENDING',103000.00,'2026-04-08 10:50:20',NULL,NULL,19,NULL,NULL),(86,19,'ORD-2026-04-08-2542','PENDING',103000.00,'2026-04-08 10:50:22',NULL,NULL,19,NULL,NULL),(87,19,'ORD-2026-04-08-353B','PENDING',103000.00,'2026-04-08 10:50:24',NULL,NULL,19,NULL,NULL),(88,19,'ORD-2026-04-08-FC2E','PENDING',103000.00,'2026-04-08 10:50:24',NULL,NULL,19,NULL,NULL),(89,23,'ORD-2026-04-08-B882','PENDING',41699.00,'2026-04-08 10:50:51',NULL,NULL,23,NULL,NULL),(90,5,'ORD-2026-04-08-A048','PENDING',22000.00,'2026-04-08 10:52:19',NULL,NULL,5,NULL,NULL),(91,19,'ORD-2026-04-08-852D','PENDING',140000.00,'2026-04-08 11:44:36',NULL,NULL,19,NULL,NULL),(92,19,'ORD-2026-04-08-C450','PENDING',140000.00,'2026-04-08 11:44:39',NULL,NULL,19,NULL,NULL),(93,20,'ORD-2026-04-08-D322','PENDING',47500.00,'2026-04-08 11:44:58',NULL,NULL,20,NULL,NULL),(94,19,'ORD-2026-04-08-CB2F','PENDING',75000.00,'2026-04-08 11:46:18',NULL,NULL,19,NULL,NULL),(95,19,'ORD-2026-04-08-D241','PENDING',75000.00,'2026-04-08 11:46:25',NULL,NULL,19,NULL,NULL),(96,19,'ORD-2026-04-08-2BD1','PENDING',75000.00,'2026-04-08 11:46:43',NULL,NULL,19,NULL,NULL),(97,19,'ORD-2026-04-08-C102','PENDING',83000.00,'2026-04-08 11:47:13',NULL,NULL,19,NULL,NULL),(98,19,'ORD-2026-04-08-238F','PENDING',83000.00,'2026-04-08 11:47:15',NULL,NULL,19,NULL,NULL),(99,19,'ORD-2026-04-08-EC7A','PENDING',83000.00,'2026-04-08 11:47:16',NULL,NULL,19,NULL,NULL),(100,19,'ORD-2026-04-08-5606','PENDING',83000.00,'2026-04-08 11:47:16',NULL,NULL,19,NULL,NULL),(101,5,'ORD-2026-04-08-BDEA','PENDING',24101.00,'2026-04-08 11:48:22',NULL,NULL,5,NULL,NULL),(102,24,'ORD-2026-04-08-0E0D','PENDING',18800.00,'2026-04-08 21:05:44',NULL,NULL,24,NULL,NULL),(103,5,'ORD-2026-04-09-D509','PENDING',32500.00,'2026-04-09 11:12:39',NULL,NULL,5,NULL,NULL),(104,5,'ORD-2026-04-09-96C4','ACCEPTED',31000.00,'2026-04-09 11:16:01','2026-04-09 11:16:56',NULL,5,1,NULL);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
                              `id_order_item` int NOT NULL AUTO_INCREMENT,
                              `id_order` int NOT NULL,
                              `item_type` enum('BURGER','PRODUCT','ADDITION') DEFAULT NULL,
                              `id_burger` int DEFAULT NULL,
                              `id_product` int DEFAULT NULL,
                              `item_name` varchar(150) NOT NULL,
                              `quantity` int NOT NULL,
                              `unit_price` decimal(38,2) DEFAULT NULL,
                              `subtotal` decimal(38,2) DEFAULT NULL,
                              PRIMARY KEY (`id_order_item`),
                              KEY `FK91bchbncxidkjypdysx5pvwyb` (`id_order`),
                              CONSTRAINT `FK91bchbncxidkjypdysx5pvwyb` FOREIGN KEY (`id_order`) REFERENCES `order` (`id_order`)
) ENGINE=InnoDB AUTO_INCREMENT=348 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (10,1,'BURGER',1,NULL,'Clásica Tetris',1,19999.00,19999.00),(11,1,'PRODUCT',NULL,36,'Bebida Energética',1,7000.00,7000.00),(12,1,'PRODUCT',NULL,34,'Agua con Gas',1,3500.00,3500.00),(22,2,'BURGER',1,NULL,'Clásica Tetris',1,20000.00,20000.00),(23,2,'PRODUCT',NULL,34,'Agua con Gas',1,3500.00,3500.00),(24,2,'PRODUCT',NULL,37,'Granizado de Fresa',1,4500.00,4500.00),(44,4,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(45,6,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(47,5,'BURGER',12,NULL,'WCG',1,43299.00,43299.00),(48,3,'BURGER',2,NULL,'Hawaiana Burger',1,22000.00,22000.00),(49,3,'PRODUCT',NULL,36,'Bebida Energética',1,7000.00,7000.00),(50,3,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(51,3,'ADDITION',NULL,7,'Papas a la francesa',1,5000.00,5000.00),(52,3,'ADDITION',NULL,9,'Nuggets de Pollo',1,5000.00,5000.00),(53,7,'BURGER',13,NULL,'????????????????????‍??‍??‍??‍♂️?????????????????',1,43200.00,43200.00),(57,8,'BURGER',14,NULL,'Yisus burger',1,39300.00,39300.00),(60,9,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(61,9,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(64,10,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(65,10,'PRODUCT',NULL,27,'Coca-Cola 400ml',1,4500.00,4500.00),(78,11,'BURGER',8,NULL,'Pollo Burger',1,29000.00,29000.00),(79,11,'PRODUCT',NULL,27,'Coca-Cola 400ml',1,4500.00,4500.00),(80,11,'PRODUCT',NULL,38,'Granizado de Maracuyá',1,4500.00,4500.00),(81,11,'ADDITION',NULL,7,'Papas a la francesa',1,5000.00,5000.00),(94,12,'BURGER',15,NULL,'My Special Burger',1,38000.00,38000.00),(95,12,'ADDITION',NULL,9,'Nuggets de Pollo',2,5000.00,10000.00),(96,12,'PRODUCT',NULL,35,'Limonada de Coco',1,6500.00,6500.00),(97,12,'PRODUCT',NULL,29,'Pepsi 400ml',1,4000.00,4000.00),(108,13,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(109,13,'PRODUCT',NULL,36,'Bebida Energética',1,7000.00,7000.00),(110,13,'PRODUCT',NULL,37,'Granizado de Fresa',1,4500.00,4500.00),(111,13,'PRODUCT',NULL,33,'Agua Mineral',1,3000.00,3000.00),(112,13,'ADDITION',NULL,7,'Papas a la francesa',1,5000.00,5000.00),(119,14,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(120,14,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(121,14,'PRODUCT',NULL,36,'Bebida Energética',1,7000.00,7000.00),(122,14,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',1,4500.00,4500.00),(123,14,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(124,14,'BURGER',16,NULL,'Rayo McQueen Burger',2,23002.00,46004.00),(127,15,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(128,15,'PRODUCT',NULL,27,'Coca-Cola 400ml',1,4500.00,4500.00),(131,16,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(132,16,'PRODUCT',NULL,27,'Coca-Cola 400ml',1,4500.00,4500.00),(137,17,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(138,17,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(139,17,'BURGER',5,NULL,'?? Mexicana Picante',1,25000.00,25000.00),(140,17,'BURGER',6,NULL,'?? Americana',1,25000.00,25000.00),(141,18,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(142,18,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(143,19,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(144,19,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(145,20,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(146,20,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(147,21,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(148,21,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(149,22,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(150,22,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(151,23,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(152,23,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(153,24,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(154,24,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(155,25,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(156,25,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(157,26,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(158,26,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(159,27,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(160,27,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(161,28,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(162,28,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(163,29,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(164,29,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(165,30,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(166,30,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(167,31,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(168,31,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(169,32,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(170,32,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(171,33,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(172,33,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(173,34,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(174,34,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(175,35,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(176,35,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(177,36,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(178,36,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(179,37,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(180,37,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(181,38,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(182,38,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(183,39,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(184,39,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(185,40,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(186,40,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(187,41,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(188,41,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(189,42,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(190,42,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(191,43,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(192,43,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(193,44,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(194,44,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(195,45,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(196,45,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(197,46,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(198,46,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(199,47,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(200,47,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(201,48,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(202,48,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(205,50,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(206,51,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(207,52,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(208,53,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(209,53,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',1,4500.00,4500.00),(210,53,'ADDITION',NULL,7,'Papas a la francesa',1,5000.00,5000.00),(211,54,'BURGER',10,NULL,'Monster',2,40000.00,80000.00),(212,55,'BURGER',10,NULL,'Monster',2,40000.00,80000.00),(213,56,'BURGER',1,NULL,'Clásica Tetris',1,20000.00,20000.00),(214,57,'BURGER',1,NULL,'Clásica Tetris',1,20000.00,20000.00),(215,58,'BURGER',1,NULL,'Clásica Tetris',1,20000.00,20000.00),(216,59,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(217,59,'BURGER',3,NULL,'Crispy Chicken',1,22000.00,22000.00),(218,59,'BURGER',4,NULL,'Mega Doble',1,29000.00,29000.00),(219,59,'BURGER',2,NULL,'Hawaiana Burger',25,22000.00,550000.00),(220,60,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(221,60,'BURGER',3,NULL,'Crispy Chicken',1,22000.00,22000.00),(222,60,'BURGER',4,NULL,'Mega Doble',1,29000.00,29000.00),(223,60,'BURGER',2,NULL,'Hawaiana Burger',25,22000.00,550000.00),(224,61,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(225,61,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(226,62,'BURGER',17,NULL,'Burger super Che ',1,12500.00,12500.00),(227,63,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(228,64,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(229,65,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(230,66,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(231,67,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(232,68,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(233,69,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(234,70,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(235,71,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(236,72,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(237,73,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(238,74,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(239,49,'BURGER',9,NULL,'La Reina',10,35000.00,350000.00),(240,49,'BURGER',10,NULL,'Monster',10,40000.00,400000.00),(241,75,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(242,75,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(243,75,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(244,75,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(245,75,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(246,75,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(247,76,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(248,76,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(249,76,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(250,76,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(251,76,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(252,76,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(253,77,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(254,77,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(255,77,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(256,77,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(257,77,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(258,77,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(259,78,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(260,78,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(261,78,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(262,78,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(263,78,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(264,78,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(265,79,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(266,79,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(267,79,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(268,79,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(269,79,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(270,79,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(271,80,'BURGER',10,NULL,'Monster',11,40000.00,440000.00),(272,80,'PRODUCT',NULL,28,'Coca-Cola Zero 400ml',4,4500.00,18000.00),(273,80,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00),(274,80,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(275,80,'ADDITION',NULL,3,'Cebolla Caramelizada',1,2000.00,2000.00),(276,80,'ADDITION',NULL,4,'Jalapeños',1,1500.00,1500.00),(284,82,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(285,83,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(286,83,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(287,83,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(288,84,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(289,84,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(290,84,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(291,85,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(292,85,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(293,85,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(294,86,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(295,86,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(296,86,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(297,87,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(298,87,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(299,87,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(300,88,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(301,88,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(302,88,'BURGER',19,NULL,'La verger ',1,28000.00,28000.00),(303,81,'BURGER',10,NULL,'Monster',1,40000.00,40000.00),(304,81,'BURGER',1,NULL,'Clásica Tetris',1,20000.00,20000.00),(305,81,'BURGER',2,NULL,'Hawaiana Burger',1,22000.00,22000.00),(306,81,'BURGER',3,NULL,'Crispy Chicken',1,22000.00,22000.00),(307,81,'BURGER',4,NULL,'Mega Doble',1,29000.00,29000.00),(308,81,'BURGER',5,NULL,'?? Mexicana Picante',1,25000.00,25000.00),(309,81,'BURGER',6,NULL,'?? Americana',1,25000.00,25000.00),(310,89,'BURGER',18,NULL,'The Yahu special',1,41699.00,41699.00),(311,90,'BURGER',2,NULL,'Hawaiana Burger',1,22000.00,22000.00),(312,91,'BURGER',10,NULL,'Monster',2,40000.00,80000.00),(313,91,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(314,91,'BURGER',5,NULL,'?? Mexicana Picante',1,25000.00,25000.00),(315,92,'BURGER',10,NULL,'Monster',2,40000.00,80000.00),(316,92,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(317,92,'BURGER',5,NULL,'?? Mexicana Picante',1,25000.00,25000.00),(318,93,'BURGER',17,NULL,'Burger super Che ',1,12500.00,12500.00),(319,93,'BURGER',9,NULL,'La Reina',1,35000.00,35000.00),(320,94,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(321,95,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(322,96,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(323,97,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(324,97,'PRODUCT',NULL,29,'Pepsi 400ml',1,4000.00,4000.00),(325,97,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(326,98,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(327,98,'PRODUCT',NULL,29,'Pepsi 400ml',1,4000.00,4000.00),(328,98,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(329,99,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(330,99,'PRODUCT',NULL,29,'Pepsi 400ml',1,4000.00,4000.00),(331,99,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(332,100,'BURGER',5,NULL,'?? Mexicana Picante',3,25000.00,75000.00),(333,100,'PRODUCT',NULL,29,'Pepsi 400ml',1,4000.00,4000.00),(334,100,'PRODUCT',NULL,39,'Granizado de Limón Azul',1,4000.00,4000.00),(335,101,'BURGER',22,NULL,'la super',1,24101.00,24101.00),(336,102,'BURGER',23,NULL,'My burger',1,18800.00,18800.00),(337,103,'BURGER',2,NULL,'Hawaiana Burger',1,22000.00,22000.00),(338,103,'PRODUCT',NULL,34,'Agua con Gas',1,3500.00,3500.00),(339,103,'PRODUCT',NULL,36,'Bebida Energética',1,7000.00,7000.00),(344,104,'BURGER',2,NULL,'Hawaiana Burger',1,22000.00,22000.00),(345,104,'PRODUCT',NULL,34,'Agua con Gas',1,3500.00,3500.00),(346,104,'ADDITION',NULL,1,'Tocineta Crocante',1,3000.00,3000.00),(347,104,'ADDITION',NULL,2,'Huevo Frito',1,2500.00,2500.00);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
                           `id_payment` int NOT NULL AUTO_INCREMENT,
                           `id_order` int NOT NULL,
                           `id_user` int NOT NULL,
                           `payment_method` enum('CASH','CARD','TRANSFER') NOT NULL,
                           `amount` decimal(38,2) DEFAULT NULL,
                           `paid_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id_payment`),
                           UNIQUE KEY `id_order` (`id_order`),
                           CONSTRAINT `FK67xhvo5ix5a8386okpravdsoe` FOREIGN KEY (`id_order`) REFERENCES `order` (`id_order`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,1,'CASH',30499.00,'2026-04-06 02:10:52'),(2,2,1,'CASH',28000.00,'2026-04-06 17:50:33'),(3,3,2,'TRANSFER',43000.00,'2026-04-07 06:23:08'),(4,5,2,'TRANSFER',43299.00,'2026-04-07 06:37:09'),(5,4,2,'TRANSFER',40000.00,'2026-04-07 06:38:49'),(6,6,2,'TRANSFER',40000.00,'2026-04-07 06:39:09'),(7,8,6,'TRANSFER',39300.00,'2026-04-07 07:16:43'),(8,9,6,'TRANSFER',75000.00,'2026-04-07 17:15:13'),(9,10,6,'CASH',39500.00,'2026-04-07 17:41:55'),(10,11,1,'TRANSFER',43000.00,'2026-04-07 20:27:32'),(11,12,1,'TRANSFER',58500.00,'2026-04-07 21:45:20'),(12,13,3,'TRANSFER',59500.00,'2026-04-07 22:00:24'),(13,14,1,'TRANSFER',136504.00,'2026-04-08 09:48:02'),(14,15,1,'TRANSFER',39500.00,'2026-04-08 09:50:31'),(15,16,2,'CASH',39500.00,'2026-04-08 09:51:30'),(16,17,2,'CASH',125000.00,'2026-04-08 10:04:28'),(17,49,1,'CARD',750000.00,'2026-04-08 10:48:21'),(18,81,1,'CASH',183000.00,'2026-04-08 10:50:17'),(19,7,1,'CASH',43200.00,'2026-04-08 11:49:30'),(20,104,1,'CASH',31000.00,'2026-04-09 11:16:48');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pqrs`
--

DROP TABLE IF EXISTS `pqrs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pqrs` (
                        `id_pqrs` int NOT NULL AUTO_INCREMENT,
                        `id_user` int NOT NULL,
                        `type` varchar(255) NOT NULL,
                        `status` varchar(255) NOT NULL,
                        `subject` varchar(255) NOT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `response` varchar(255) DEFAULT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL,
                        `deleted_at` datetime DEFAULT NULL,
                        `created_by` int DEFAULT NULL,
                        `updated_by` int DEFAULT NULL,
                        `deleted_by` int DEFAULT NULL,
                        `assigned_to` int DEFAULT NULL,
                        `priority` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id_pqrs`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pqrs`
--

LOCK TABLES `pqrs` WRITE;
/*!40000 ALTER TABLE `pqrs` DISABLE KEYS */;
INSERT INTO `pqrs` VALUES (1,5,'CONGRATULATIONS','ANSWERED','Felicidades','Les quedó muy bonita la aplicación ??','Muchas Gracias ','2026-04-06 20:47:26','2026-04-06 20:48:00',NULL,5,1,NULL,1,'HIGH'),(2,13,'REPORT','ANSWERED','Condon en la burguer','La mayonesa estaba como rara','Lamentamos mucho este suceso, pero plata dada plata perdida ','2026-04-07 07:22:31','2026-04-07 07:23:50',NULL,13,1,NULL,1,'LOW'),(3,5,'CLAIM','ANSWERED','Demora','El producto se demora mucho en llegar ','que pena tu pedido ya esta en camino discúlpanos muchas novedades en el trafico  ','2026-04-07 20:31:24','2026-04-07 20:32:25',NULL,5,1,NULL,1,'HIGH'),(4,3,'SUGGESTION','ANSWERED','Demora en el pedido','Se demoraron un poco en traer mi pedido, pero de resto todo muy excelente, espero puedan mejorar el tiempo de espera.','Estamos trabajado muy fuerte para mejorar tu experiencia con nosotros mil disculpas v','2026-04-07 21:53:02','2026-04-07 21:54:31','2026-04-07 21:55:07',3,1,3,1,'MEDIUM'),(5,20,'COMPLAINT','ANSWERED','Nada funciona','Iba a comprar una hamburguesa y no me deja pagar…','ESTAMOS TRABAJANSO PARA SOLUCIONAR','2026-04-08 10:52:52','2026-04-08 11:50:47',NULL,20,1,NULL,1,'CRITICAL'),(6,18,'COMPLAINT','RECEIVED','No me deja pagar ','Hola podrían arreglar para poder hacer el pago, gracias ',NULL,'2026-04-08 10:53:34',NULL,NULL,18,NULL,NULL,NULL,'MEDIUM');
/*!40000 ALTER TABLE `pqrs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
                           `id_product` int NOT NULL AUTO_INCREMENT,
                           `name` varchar(100) NOT NULL,
                           `description` varchar(255) DEFAULT NULL,
                           `price` decimal(10,2) NOT NULL,
                           `is_available` tinyint(1) NOT NULL DEFAULT '1',
                           `is_burger_ingredient` tinyint(1) NOT NULL DEFAULT '0',
                           `product_type` enum('BEVERAGE','INGREDIENT','SIDE') NOT NULL,
                           `image_url` varchar(500) DEFAULT NULL,
                           `image_key` varchar(255) DEFAULT NULL,
                           `id_product_category` int NOT NULL,
                           `id_supplier` int NOT NULL,
                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` datetime DEFAULT NULL,
                           `deleted_at` datetime DEFAULT NULL,
                           `created_by` int DEFAULT NULL,
                           `updated_by` int DEFAULT NULL,
                           `deleted_by` int DEFAULT NULL,
                           `availability` bit(1) NOT NULL,
                           `quantity` int NOT NULL,
                           `supplier_id` int DEFAULT NULL,
                           PRIMARY KEY (`id_product`),
                           UNIQUE KEY `uc_product_name` (`name`),
                           KEY `idx_product_type` (`product_type`),
                           KEY `FKaym5xd86w6d803rgywi633we9` (`id_product_category`),
                           KEY `FK7vecnfptx4ologqg55y3v7mbm` (`id_supplier`),
                           CONSTRAINT `FK7vecnfptx4ologqg55y3v7mbm` FOREIGN KEY (`id_supplier`) REFERENCES `supplier` (`id_supplier`),
                           CONSTRAINT `FKaym5xd86w6d803rgywi633we9` FOREIGN KEY (`id_product_category`) REFERENCES `product_category` (`id_product_category`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Pan brioche','Pan suave y ligeramente dulce, ideal para hamburguesas gourmet',2500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png','products/1775457526144-90cdcaf0-gemini_generated_image_ned4ianed4ianed4.png',2,2,'2026-04-06 01:13:55','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',50,NULL),(2,'Pan Tradicional','Pan clásico de hamburguesa con textura esponjosa',2500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png','products/1775457112230-0be17051-gemini_generated_image_idv81pidv81pidv8.png',2,2,'2026-04-06 01:14:32','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',95,NULL),(3,'Lechuga','Hojas frescas y crujientes',800.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png','products/1775457656831-6718c103-gemini_generated_image_8q4qoe8q4qoe8q4q.png',5,3,'2026-04-06 01:14:58','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',58,NULL),(4,'Pan Integral','Pan elaborado con harina integral, opción más saludable',2500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457460356-89da4987-gemini_generated_image_twcf9twcf9twcf9t.png','products/1775457460356-89da4987-gemini_generated_image_twcf9twcf9twcf9t.png',2,2,'2026-04-06 01:15:18','2026-04-06 01:37:41',NULL,1,1,NULL,_binary '',100,NULL),(5,'Tomate','Rodajas de tomate fresco',899.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png','products/1775457772589-c0a7e148-gemini_generated_image_xj40ofxj40ofxj40.png',5,3,'2026-04-06 01:15:39','2026-04-08 10:50:29',NULL,3,1,NULL,_binary '',94,NULL),(6,'Cebolla Blanca','Rodajas de cebolla suave',699.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png','products/1775457892936-c1dd70da-gemini_generated_image_ds3rzgds3rzgds3r.png',5,3,'2026-04-06 01:16:17','2026-04-07 21:46:12',NULL,3,1,NULL,_binary '',98,NULL),(7,'Carne de Res 180g','Medallón grande de carne de res para hamburguesas más contundentes',7000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png','products/1775458068701-0fd5b5e3-gemini_generated_image_397tt1397tt1397t.png',3,1,'2026-04-06 01:16:47','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',53,NULL),(8,'Cebolla Morada','Cebolla con sabor más intenso y color llamativo',800.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png','products/1775458162101-4d9c47a6-gemini_generated_image_yl565yl565yl565y.png',5,3,'2026-04-06 01:16:51','2026-04-07 21:46:12',NULL,3,1,NULL,_binary '',95,NULL),(9,'Pechuga de Pollo 180g','Filete de pollo a la plancha bajo en grasa',6000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458278520-e122299f-gemini_generated_image_3d2jys3d2jys3d2j.png','products/1775458278520-e122299f-gemini_generated_image_3d2jys3d2jys3d2j.png',3,1,'2026-04-06 01:17:37','2026-04-07 21:46:12',NULL,1,1,NULL,_binary '',95,NULL),(10,'Pepinillos','Rodajas agridulces que aportan acidez',1200.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png','products/1775458339450-07551784-gemini_generated_image_8qtpb58qtpb58qtp.png',5,3,'2026-04-06 01:17:38','2026-04-08 10:50:29',NULL,3,1,NULL,_binary '',81,NULL),(11,'Espinaca','Hojas verdes ricas en nutrientes',900.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png','products/1775458507693-92369d7f-gemini_generated_image_5afq025afq025afq.png',5,3,'2026-04-06 01:18:29','2026-04-07 21:46:12',NULL,3,1,NULL,_binary '',98,NULL),(12,'Pollo Crispy','Pechuga de pollo empanizada y crujiente',6000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png','products/1775458607254-4129b08b-gemini_generated_image_87tq8o87tq8o87tq.png',3,1,'2026-04-06 01:18:51','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',64,NULL),(13,'Carne de Cerdo 180g','Medallón jugoso de carne de cerdo',6000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png','products/1775458756881-9a81a366-gemini_generated_image_w3lr44w3lr44w3lr.png',3,1,'2026-04-06 01:20:40','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',59,NULL),(14,'Salsa BBQ','Salsa dulce con toque ahumado',800.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png','products/1775459130669-3fd8ef7c-gemini_generated_image_usga47usga47usga.png',6,4,'2026-04-06 01:20:44','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',75,NULL),(15,'Queso Cheddar','Queso derretido de sabor fuerte y textura cremosa',2000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png','products/1775459677529-b5501beb-gemini_generated_image_idecm6idecm6idec.png',4,1,'2026-04-06 01:21:36','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',76,NULL),(16,'Mayonesa','Salsa cremosa clásica',500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png','products/1775459263655-0b445830-gemini_generated_image_y62ozhy62ozhy62o.png',6,4,'2026-04-06 01:21:38','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',92,NULL),(17,'Queso Americano','Queso suave y cremoso, clásico en hamburguesas',2000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png','products/1775459874971-5fa99306-gemini_generated_image_enl165enl165enl1.png',4,1,'2026-04-06 01:21:59','2026-04-07 20:27:43',NULL,1,1,NULL,_binary '',98,NULL),(18,'Queso Mozzarella','Queso elástico de sabor suave',2000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459933936-8f991b57-gemini_generated_image_insfkdinsfkdinsf.png','products/1775459933936-8f991b57-gemini_generated_image_insfkdinsfkdinsf.png',4,1,'2026-04-06 01:22:27','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',80,NULL),(19,'Mostaza','Salsa con sabor ácido y ligero',500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459381749-25d6fb80-gemini_generated_image_s5nuyds5nuyds5nu.png','products/1775459381749-25d6fb80-gemini_generated_image_s5nuyds5nuyds5nu.png',6,4,'2026-04-06 01:22:56','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',97,NULL),(20,'Ketchup','Salsa de tomate dulce',500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png','products/1775459396774-0291975a-gemini_generated_image_f8u4l8f8u4l8f8u4.png',6,4,'2026-04-06 01:23:44','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',75,NULL),(21,'Salsa de Ajo','Salsa cremosa con sabor a ajo',800.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460053426-00e97e00-gemini_generated_image_u1xuhu1xuhu1xuhu.png','products/1775460053426-00e97e00-gemini_generated_image_u1xuhu1xuhu1xuhu.png',6,4,'2026-04-06 01:24:26','2026-04-06 02:20:54',NULL,3,1,NULL,_binary '',100,NULL),(22,'Salsa de Queso','Salsa caliente de queso cheddar',2000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460100032-57694391-gemini_generated_image_lew13slew13slew1.png','products/1775460100032-57694391-gemini_generated_image_lew13slew13slew1.png',6,4,'2026-04-06 01:25:15','2026-04-06 02:21:40',NULL,3,1,NULL,_binary '',100,NULL),(23,'Salsa Picante','Salsa con nivel de picante medio-alto',900.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460149525-c93c25aa-gemini_generated_image_apgik1apgik1apgi.png','products/1775460149525-c93c25aa-gemini_generated_image_apgik1apgik1apgi.png',6,4,'2026-04-06 01:26:14','2026-04-08 10:50:29',NULL,3,1,NULL,_binary '',93,NULL),(24,'Cebolla Caramelizada','Cebolla cocinada lentamente con sabor dulce',1501.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png','products/1775460301365-487f3547-gemini_generated_image_xa47i5xa47i5xa47.png',7,7,'2026-04-06 01:29:40','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',59,NULL),(25,'Tocineta Crocante','Tiras de tocineta frita y crujiente',3000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png','products/1775460552294-e26ccd96-gemini_generated_image_x7r1ijx7r1ijx7r1.png',3,1,'2026-04-06 01:30:41','2026-04-08 10:50:29',NULL,3,1,NULL,_binary '',59,NULL),(26,'Salsa Maíz Dulce','Dulce Maiz',1500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775460624208-1d4aa786-gemini_generated_image_y1p3j7y1p3j7y1p3.png','products/1775460624208-1d4aa786-gemini_generated_image_y1p3j7y1p3j7y1p3.png',6,4,'2026-04-06 01:33:51','2026-04-06 02:30:25',NULL,3,1,NULL,_binary '',100,NULL),(27,'Coca-Cola 400ml','Gaseosa clásica sabor cola bien fría',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458471796-0d218544-7702535011089.jpg.webp','products/1775458471796-0d218544-7702535011089.jpg.webp',1,6,'2026-04-06 01:35:20','2026-04-08 11:53:53',NULL,3,3,NULL,_binary '\0',96,NULL),(28,'Coca-Cola Zero 400ml','Gaseosa sin azúcar con el mismo sabor',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458402753-1a3cbc12-gaseosa-coca-cola-zero-botella-x-400-mililitros.jpg','products/1775458402753-1a3cbc12-gaseosa-coca-cola-zero-botella-x-400-mililitros.jpg',1,6,'2026-04-06 01:35:51','2026-04-08 09:48:10',NULL,3,1,NULL,_binary '',99,NULL),(29,'Pepsi 400ml','Bebida gaseosa sabor cola',4000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458374509-4b3d9cca-gaseosa-pepsi-personal-400mlgaseosa-pepsi-personal-400mlpepsilicores-medellin-4039797.webp','products/1775458374509-4b3d9cca-gaseosa-pepsi-personal-400mlgaseosa-pepsi-personal-400mlpepsilicores-medellin-4039797.webp',1,5,'2026-04-06 01:37:06','2026-04-07 21:46:13',NULL,3,1,NULL,_binary '',99,NULL),(30,'Colombiana 400ml','Gaseosa dulce tradicional colombiana',4000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458346881-fdf0b06f-gaseosa-colombiana-personal-400mlgaseosa-colombiana-personal-400mlcolombianalicores-medellin-2691196.webp','products/1775458346881-fdf0b06f-gaseosa-colombiana-personal-400mlgaseosa-colombiana-personal-400mlcolombianalicores-medellin-2691196.webp',1,5,'2026-04-06 01:37:36','2026-04-06 01:52:27',NULL,3,3,NULL,_binary '',100,NULL),(31,'Manzana Postobón 400ml','Gaseosa sabor manzana refrescante',4000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458022808-7ca7660e-images.jpg','products/1775458022808-7ca7660e-images.jpg',1,5,'2026-04-06 01:38:11','2026-04-06 22:02:08',NULL,3,1,NULL,_binary '',100,NULL),(32,'Sprite 400ml','Gaseosa sabor limón refrescante',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775457972150-5c797ce2-gaseosa-sprite-x-400-ml-nr-160297-1.webp','products/1775457972150-5c797ce2-gaseosa-sprite-x-400-ml-nr-160297-1.webp',1,6,'2026-04-06 01:39:32','2026-04-06 01:46:16',NULL,3,3,NULL,_binary '',100,NULL),(33,'Agua Mineral','Agua sin gas para acompañar la comida',3000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458162817-ecb71c0c-unnamed.jpg','products/1775458162817-ecb71c0c-unnamed.jpg',1,6,'2026-04-06 01:40:28','2026-04-07 22:01:24',NULL,3,3,NULL,_binary '',100,NULL),(34,'Agua con Gas','Agua carbonatada refrescante',3500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458194534-a1a8529c-83601997-agua-cristal-con-gas.webp','products/1775458194534-a1a8529c-83601997-agua-cristal-con-gas.webp',1,5,'2026-04-06 01:41:41','2026-04-09 11:16:56',NULL,3,1,NULL,_binary '',97,NULL),(35,'Limonada de Coco','Limonada cremosa con coco',6500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458241098-6c4544af-limonada-coco--e1698292744657.webp','products/1775458241098-6c4544af-limonada-coco--e1698292744657.webp',1,7,'2026-04-06 01:42:23','2026-04-07 21:46:13',NULL,3,1,NULL,_binary '',99,NULL),(36,'Bebida Energética','Bebida energizante para mayor energía',7000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775458305369-e0de6e70-amper-energy-x473ml-blue.jpg','products/1775458305369-e0de6e70-amper-energy-x473ml-blue.jpg',1,7,'2026-04-06 01:42:55','2026-04-08 09:48:10',NULL,3,1,NULL,_binary '',97,NULL),(37,'Granizado de Fresa','Bebida fría tipo hielo triturado sabor fresa',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775461164203-f400b666-freee.jpg','products/1775461164203-f400b666-freee.jpg',1,7,'2026-04-06 02:29:04','2026-04-07 22:01:24',NULL,3,3,NULL,_binary '',99,NULL),(38,'Granizado de Maracuyá','Refrescante granizado de maracuyá',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775461296905-fabd9a3c-maracu.jpg','products/1775461296905-fabd9a3c-maracu.jpg',1,7,'2026-04-06 02:29:42','2026-04-07 20:27:43',NULL,3,1,NULL,_binary '',99,NULL),(39,'Granizado de Limón Azul','Granizado cítrico y refrescante',4000.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775461386885-433583ff-limon.jpg','products/1775461386885-433583ff-limon.jpg',1,7,'2026-04-06 02:30:29','2026-04-08 09:48:10',NULL,3,1,NULL,_binary '',98,NULL),(40,'Granizado de Mango','Granizado dulce con sabor a mango',4500.00,1,0,'BEVERAGE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775461209013-578c75c7-mango.jpg','products/1775461209013-578c75c7-mango.jpg',1,7,'2026-04-06 02:31:13','2026-04-06 02:40:09',NULL,3,1,NULL,_binary '',100,NULL),(41,'Piña a la Parrilla','Rodaja de piña dulce caramelizada',1500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775521866584-4935869c-gemini_generated_image_yzpfc6yzpfc6yzpf.png','products/1775521866584-4935869c-gemini_generated_image_yzpfc6yzpfc6yzpf.png',7,3,'2026-04-06 19:31:07','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',97,NULL),(42,'Papas a la francesa','papa crujientes',5000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png','products/1775522284437-0d19ad33-gemini_generated_image_odixnkodixnkodix.png',7,3,'2026-04-06 19:38:04','2026-04-07 07:16:49',NULL,1,6,NULL,_binary '',99,NULL),(43,'Jamón','Rebanada adicional de jamón',4000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png','products/1775522343633-db3ad324-gemini_generated_image_46pj8g46pj8g46pj.png',7,1,'2026-04-06 19:39:04','2026-04-09 11:16:56',NULL,1,1,NULL,_binary '',78,NULL),(44,'Nuggets de Pollo','Nuggets de Pollo',5000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522418534-91dc363f-gemini_generated_image_7rilgs7rilgs7ril.png','products/1775522418534-91dc363f-gemini_generated_image_7rilgs7rilgs7ril.png',7,7,'2026-04-06 19:40:19','2026-04-07 20:27:43',NULL,1,1,NULL,_binary '',99,NULL),(45,'Huevo Frito','huevo frito',1000.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775522751213-f199c78f-huevovv.png','products/1775522751213-f199c78f-huevovv.png',7,1,'2026-04-06 19:45:51','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',65,NULL),(46,'Jalapeños','Jalapeños',1500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png','products/1775523141100-ff3305cd-gemini_generated_image_tnaqe6tnaqe6tnaq.png',5,3,'2026-04-06 19:52:21','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',96,NULL),(47,'Aguacate','Rodajas de aguacate fresco',1500.00,1,1,'INGREDIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png','products/1775523183580-320e6073-gemini_generated_image_a3rxuda3rxuda3rx.png',5,3,'2026-04-06 19:53:04','2026-04-08 10:50:29',NULL,1,1,NULL,_binary '',90,NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
                                    `id_product_category` int NOT NULL AUTO_INCREMENT,
                                    `description` varchar(255) DEFAULT NULL,
                                    `available` bit(1) NOT NULL,
                                    `product_category_name` varchar(255) NOT NULL,
                                    `created_by` int NOT NULL,
                                    `deleted_at` datetime(6) DEFAULT NULL,
                                    `deleted_by` int DEFAULT NULL,
                                    `updated_at` datetime(6) DEFAULT NULL,
                                    `updated_by` int DEFAULT NULL,
                                    `created_at` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id_product_category`),
                                    UNIQUE KEY `uc_product_category_name` (`product_category_name`),
                                    KEY `idx_product_category_name` (`product_category_name`),
                                    KEY `idx_product_category_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1,'Refrescos, jugos, malteadas y bebidas artesanales para acompañar tu pedido.',_binary '','Bebidas',1,NULL,NULL,NULL,NULL,'2026-04-06 00:56:21'),(2,'Tipos de pan usados como base de la hamburguesa.',_binary '','Panes',1,NULL,NULL,NULL,NULL,'2026-04-06 00:58:32'),(3,'Proteínas principales de la hamburguesa.',_binary '','Carnes',1,NULL,NULL,NULL,NULL,'2026-04-06 00:58:53'),(4,'Tipos de queso que se agregan a la hamburguesa.',_binary '','Quesos',1,NULL,NULL,NULL,NULL,'2026-04-06 00:59:17'),(5,'Ingredientes frescos que complementan la hamburguesa.',_binary '','Vegetales y verduras',1,NULL,NULL,NULL,NULL,'2026-04-06 00:59:47'),(6,'Salsas que aportan sabor a la hamburguesa.',_binary '','Salsas',1,NULL,NULL,NULL,NULL,'2026-04-06 01:00:18'),(7,'Ingredientes adicionales que aportan sabor y proteína.',_binary '','Extras',1,NULL,NULL,NULL,NULL,'2026-04-06 01:01:29');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
                            `id_supplier` int NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) DEFAULT NULL,
                            `email` varchar(255) DEFAULT NULL,
                            `phone` varchar(255) DEFAULT NULL,
                            `address` varchar(255) DEFAULT NULL,
                            `created_at` datetime(6) DEFAULT NULL,
                            `updated_at` datetime DEFAULT NULL,
                            `deleted_at` datetime DEFAULT NULL,
                            `created_by` int DEFAULT NULL,
                            `deleted_by` int DEFAULT NULL,
                            `updated_by` int DEFAULT NULL,
                            PRIMARY KEY (`id_supplier`),
                            UNIQUE KEY `email` (`email`),
                            KEY `idx_supplier_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,'Carnes Premium Antioquia','ventas@carnespremium.com','3004567890','Calle 45 #23-18, Medellín','2026-04-06 00:41:06.542247',NULL,NULL,1,NULL,NULL),(2,'Panadería El Trigal','pedidos@eltrigal.com','3017894561','pedidos@eltrigal.com','2026-04-06 00:41:50.653363','2026-04-06 00:46:38',NULL,1,NULL,1),(3,'Verduras y Vegetales Antioquia','ventas@verdurasantioquia.com','3204561237','Plaza Minorista, Local 120, Medellín','2026-04-06 00:45:03.132558',NULL,NULL,1,NULL,NULL),(4,'Salsas  La Especial','info@laespecial.com','3109876543','Carrera 70 #44-15, Medellín','2026-04-06 00:46:12.726577',NULL,NULL,1,NULL,NULL),(5,'Postobón S.A.','servicioalcliente@postobon.com','018000515151','Calle 1 Sur #43A-83, Medellín, Colombia','2026-04-06 00:48:55.805726','2026-04-06 00:49:25',NULL,1,NULL,1),(6,'Coca-Cola FEMSA Colombia','contacto@coca-colafemsa.com','018000512121','Autopista Sur #64B-45, Bogotá, Colombia','2026-04-06 00:50:00.325716',NULL,NULL,1,NULL,NULL),(7,'Otros','otros@gmail.com','3216891122','CARRERA 58C # 45-22 PISO 2','2026-04-06 00:51:49.758377',NULL,NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id_user` int NOT NULL AUTO_INCREMENT,
                        `name` varchar(50) NOT NULL,
                        `email` varchar(150) NOT NULL,
                        `password` varchar(255) NOT NULL COMMENT 'Hash BCrypt',
                        `phone` varchar(20) DEFAULT NULL,
                        `role` enum('ADMIN','CLIENT','EMPLOYEE') NOT NULL,
                        `user_image` varchar(255) DEFAULT NULL,
                        `image_key` varchar(255) DEFAULT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL,
                        `deleted_at` datetime DEFAULT NULL,
                        `created_by` int DEFAULT NULL,
                        `updated_by` int DEFAULT NULL,
                        `deleted_by` int DEFAULT NULL,
                        `user_image_key` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id_user`),
                        UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'tetrisburger','tetrisburger8@gmail.com','$2a$10$/Ann/dc3TwNrcxUh8L1bBOgcvn9QG9bhL1Y8HMgdB4VU50mPROsU.','3243223551','ADMIN','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775453314790-3c49e1ed-logo.png',NULL,'2026-04-06 00:27:20','2026-04-08 11:20:50',NULL,NULL,1,NULL,'users/1775453314790-3c49e1ed-logo.png'),(2,'Sergio Martinez','jeffreymg123@gmail.com','$2a$10$jvIbEROFzuSrVxiewr638.jmnCL1U63D/LtOU6tznV49BbfR6Imc2','3216893635','EMPLOYEE','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775453713235-0a016bee-air-jordan.jpg',NULL,'2026-04-06 00:30:59','2026-04-07 21:57:09',NULL,NULL,1,NULL,'users/1775453713235-0a016bee-air-jordan.jpg'),(3,'Andres Loaiza Loaiza','loaiza.devsoft@gmail.com','$2a$10$hT151ceXrXWEWOToeHFPCugrRTmbR7U2wsg8apdsOtgbrxLO4sWKS','+57 3103842455','ADMIN','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775617508665-d3ec0efa-1000079401.jpg',NULL,'2026-04-06 00:33:24','2026-04-08 10:51:53',NULL,NULL,1,NULL,'users/1775617508665-d3ec0efa-1000079401.jpg'),(4,'Sergio Martinez','chechomm03@gmail.com','$2a$10$35qqEOrwE3Xwscpd4FeM9e5F/dJlJ4zW6zJiV1IgJRx31IvlxS/J.','3216893662','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775662183885-95f3be72-yamalmmm.jpg',NULL,'2026-04-06 02:08:31','2026-04-08 10:29:45',NULL,NULL,1,NULL,'users/1775662183885-95f3be72-yamalmmm.jpg'),(5,'Sergio Martinez','003sergiomartinez@gmail.com','$2a$10$lDBQrN4loU94BEYLzNKY6OqWhIHYP8zmK2jG7sPbPEYtkYfrvv90W','3216893662','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775659328621-5911699c-messi.webp',NULL,'2026-04-06 02:32:29','2026-04-08 09:42:09',NULL,NULL,1,NULL,'users/1775659328621-5911699c-messi.webp'),(6,'felipe Adarve','adarvefelipe58@gmail.com','$2a$10$/l29.MrFVaBuRPC0O7bjCe0UqXDcJSxODPsMDl9qK30HkmfkJLWw.','','ADMIN',NULL,NULL,'2026-04-06 19:54:47','2026-04-06 21:23:56',NULL,NULL,1,NULL,NULL),(7,'Carlos Andrés Monsalve cárdenas','zandres.96@gmail.com','$2a$10$aHH3tM4xv4lu3MmDSUFe8eRu4rngNaeAwh.j16fRLzTwLl8ld56AO',NULL,'CLIENT',NULL,NULL,'2026-04-06 21:12:02',NULL,NULL,NULL,NULL,NULL,NULL),(8,'Admin','admintetrisburger@gmail.com','$2a$10$/HzXK2ijg7qDvmJbhVTvKO5oWtf1ak6Q4oWBhOUn5I9cQQDd7P3Lu','','ADMIN',NULL,NULL,'2026-04-06 21:32:00',NULL,NULL,1,NULL,NULL,NULL),(9,'Empleado','employeetetrisburger@gmail.com','$2a$10$Iu.mV1w5MLwF4d7trLwuPuCGO64zX7m2cYomOiJIHvts7LSbE6.wy','','EMPLOYEE',NULL,NULL,'2026-04-06 21:33:37',NULL,NULL,1,NULL,NULL,NULL),(10,'Usuario01','usuariotetrisburger@gmail.com','$2a$10$OUA0JYxKqvmkmkf3t8QnoOh1h8hjz7s8X5lwG51PsjeDUrKNyRhh.','','CLIENT',NULL,NULL,'2026-04-06 21:34:35',NULL,NULL,1,NULL,NULL,NULL),(11,'Wilson Castro Gil','c.wilcas@gmail.com','$2a$10$5I00dwHwKm.o3a8u1FFvkev7xpEMT3HHlT8KZPHR1JjXisTLz7TIW','3127762265','CLIENT',NULL,NULL,'2026-04-07 06:29:01','2026-04-07 06:36:07',NULL,NULL,11,NULL,NULL),(12,'mateo gutierrez','mateogutierrez7112@gmail.com','$2a$10$agq/MUdxOSOBfZ2yp8.DZuWFa8PVlXQmcBh3Bd9f7fzMhtpDMpGR.','3144299397','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775561637238-3b58caa5-2edd00de629cc91234b3fbb07aaea5a0.jpg',NULL,'2026-04-07 06:29:30','2026-04-07 06:33:57',NULL,NULL,12,NULL,'users/1775561637238-3b58caa5-2edd00de629cc91234b3fbb07aaea5a0.jpg'),(13,'Fernando Villarreal','fernando4974@gmail.com','$2a$10$GKqkBNCp1EmJUoCh/2lrPOViup7Vcw9BPEqSMSoM9KQZ6Lz2XYuyO','3012172903','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775563928622-dc1ad5fc-17755638995995094209230639485014.jpg',NULL,'2026-04-07 07:07:53','2026-04-07 07:12:09',NULL,NULL,13,NULL,'users/1775563928622-dc1ad5fc-17755638995995094209230639485014.jpg'),(14,'cristian','cristiancuentaff11@gmail.com','$2a$10$WkCerlRsECi0T7J36xPTwOqPjVJqR8XzNk3Z7zFEzz8Af8LzWiScu','','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775578422975-de69be3e-1000108888.jpg',NULL,'2026-04-07 11:11:31','2026-04-07 11:13:43',NULL,NULL,14,NULL,'users/1775578422975-de69be3e-1000108888.jpg'),(15,'Felipe Zanabria','zanabriafelipe58@gmail.com','$2a$10$03LKdjsa3la8CRxpII0/ouHU.er.UoDRAXgacWjxzVsuuyIclkTXi','+57301153663','CLIENT',NULL,NULL,'2026-04-07 17:09:47','2026-04-07 17:11:46',NULL,NULL,15,NULL,NULL),(16,'jose carvajal','josecarva524@gmail.com','$2a$10$nsdw54dKKMpHHumP9e8FJ.koGqmxrtyYKflolSCpk8SMdBnDEOybS','3007385464','CLIENT',NULL,NULL,'2026-04-07 17:38:37','2026-04-08 11:17:45',NULL,NULL,16,NULL,NULL),(17,'Jaimito Cojoncio','jaimitocojoncio@gmail.com','$2a$10$zZCjdWNt2QDSh2ULpjOWe.dudH9de4K3q2hwqOulCdkSqg5axNPSi','+57 3103842455','CLIENT',NULL,NULL,'2026-04-07 21:39:23','2026-04-07 21:43:04',NULL,NULL,17,NULL,NULL),(18,'Sebastian Pulgarin','sebas465yt@gmail.com','$2a$10$vaBmlGSWovD/UtoF1K4N5uefe0kUURXoHAy7Y4co0MmLjCNhmC.EK','3243496511','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775663287280-26abe182-img_0628.jpeg',NULL,'2026-04-08 10:43:04','2026-04-08 10:48:08',NULL,NULL,18,NULL,'users/1775663287280-26abe182-img_0628.jpeg'),(19,'Elver','everymetal98@gmail.com','$2a$10$ALwjCDy8EdmOFypehTiW.egsZh2LdtKvqjOiCC3mCcmW4APUOeohi','3163797480','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775666748230-53b4a50c-image.jpg',NULL,'2026-04-08 10:44:27','2026-04-08 11:45:49',NULL,NULL,19,NULL,'users/1775666748230-53b4a50c-image.jpg'),(20,'Miguel Zapata','miguelzj0909@gmail.com','$2a$10$4QQFXIOWyjhlyG2O8YgJbuppqzKlFHRGIPBjMXElb052Itv1gpBHW','312 6458807','CLIENT',NULL,NULL,'2026-04-08 10:44:46','2026-04-08 10:45:24',NULL,NULL,20,NULL,NULL),(21,'Andrés Loaiza Loaiza ','andresll1205@gmail.com','$2a$10$LeU.9nuJOooBIFAIGAkcxeQG8avzAEdymDbaIDSPi7x2opYjND2Ka','+57 3103842455','CLIENT',NULL,NULL,'2026-04-08 10:45:23','2026-04-08 10:49:38',NULL,NULL,21,NULL,NULL),(22,'PLAY WITH TOILBACK','pipeelpapi2170@gmail.com','$2a$10$EtjZSKaOi2IqE2sapzBZU.nWt9kv7bb8uDHckUrTC0GegRxvpNZN2','3236858755','CLIENT',NULL,NULL,'2026-04-08 10:45:33','2026-04-08 10:51:38',NULL,NULL,22,NULL,NULL),(23,'Yesid Usuga','yesidusugal@gmail.com','$2a$10$juumbveZk.gLbS8ZztXdCeBLOp0N.VsXbna.fra0OQHVyQWwQDHL2','3107360013','CLIENT','https://tetrisburger-image.s3.us-east-2.amazonaws.com/users/1775664169375-ef9cd96b-1000079460.jpg',NULL,'2026-04-08 10:46:27','2026-04-08 11:02:50',NULL,NULL,3,NULL,'users/1775664169375-ef9cd96b-1000079460.jpg'),(24,'María ','marianita@gmail.com','$2a$10$cbb.sVsj6d79e.eKzpKwT.BYrwlSJ4eyo742EfTStICPxwWEW3MEe','333333334111','CLIENT',NULL,NULL,'2026-04-08 21:02:47','2026-04-08 21:05:21',NULL,NULL,24,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-09 11:56:57
