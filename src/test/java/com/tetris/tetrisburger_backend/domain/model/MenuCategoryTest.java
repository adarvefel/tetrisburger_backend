package com.tetris.tetrisburger_backend.domain.model;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MenuCategoryTest {
    private MenuCategory menuCategory;

    @BeforeEach
    void setUp(){
        menuCategory = new MenuCategory();
    }

    @Test
    void testConstructorWithParams(){
        MenuCategory category = new MenuCategory(1,"Hamburguesas","Deliciosas hamburguesas");
        assertNotNull(category);
        assertEquals(1,category.getIdMenuCategory());
        assertEquals("Hamburguesas",category.getMenuCategoryName());
        assertEquals("Deliciosas hamburguesas",category.getDescription());

    }

    @Test
    void testConstructorEmpty(){
        assertNotNull(menuCategory); // El objeto existe
        assertNull(menuCategory.getIdMenuCategory()); // Los atributos son null
        assertNull(menuCategory.getMenuCategoryName());
        assertNull(menuCategory.getDescription());
    }
    @Test
    void testSetAndGetIdMenuCategory(){
        menuCategory.setIdMenuCategory(3);
        assertEquals(3,menuCategory.getIdMenuCategory());
    }



    @Test
    void testSetAndGetMenuCategoryName(){
        menuCategory.setMenuCategoryName("Bebidas");
        assertEquals("Bebidas",menuCategory.getMenuCategoryName());

    }

    @Test
    void testSetAndGetMenuCategoryDescription(){
        menuCategory.setDescription("Deliciosas bebidas");
        assertEquals("Deliciosas bebidas",menuCategory.getDescription());
    }

    @Test
    void testSetValorsNull() {
        // Primero establecer valores
        menuCategory.setIdMenuCategory(10);
        menuCategory.setMenuCategoryName("Categoría");
        menuCategory.setDescription("Descripción");

        // Luego establecer null
        menuCategory.setIdMenuCategory(null);
        menuCategory.setMenuCategoryName(null);
        menuCategory.setDescription(null);

        // Verificar que sean null
        assertNull(menuCategory.getIdMenuCategory());
        assertNull(menuCategory.getMenuCategoryName());
        assertNull(menuCategory.getDescription());
    }


    @Test
    void voidCreateWithValidData(){
        MenuCategory category = MenuCategory.create("Bebidas","Bebidas frias y calientes");
        assertNotNull(category);
        assertNull(category.getIdMenuCategory());
        assertEquals("Bebidas",category.getMenuCategoryName());
        assertEquals("Bebidas frias y calientes",category.getDescription());
    }

    @Test
    void testCreateWithNullName(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> MenuCategory.create(null,"Descripcion")
        );
        assertEquals("El nombre de la categoría es obligatorio",
                exception.getMessage());
    }

    void testCreateWithEmptyName(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> MenuCategory.create("","Description")
        );

        assertEquals("El nombre de la categoría es obligatorio",exception.getMessage());
    }

    @Test
    void testOfMethodWidthAllParams(){
        MenuCategory category = MenuCategory.of(5,"Postres","Dulces postres");
        assertNotNull(category);
        assertEquals(5,category.getIdMenuCategory());
        assertEquals("Postres",category.getMenuCategoryName());
        assertEquals("Dulces postres",  category.getDescription());

    }


    @Test
    void testOfMethodWithNullId(){
        MenuCategory category  = MenuCategory.of(null,"Entradas","Aperitivos");
        assertNotNull(category);
        assertNull(category.getIdMenuCategory());
        assertEquals("Entradas",category.getMenuCategoryName());
        assertEquals("Aperitivos",category.getDescription());

    }










}
