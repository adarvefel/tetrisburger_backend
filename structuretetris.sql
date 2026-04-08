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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=425 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=521 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-07 23:20:53
