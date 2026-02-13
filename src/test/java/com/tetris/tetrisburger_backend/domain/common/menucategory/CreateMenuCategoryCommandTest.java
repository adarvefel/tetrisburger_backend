package com.tetris.tetrisburger_backend.domain.common.menucategory;

import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateMenuCategoryCommandTest {

    @Test
    void testCreateCommandWithValidData(){
        CreateMenuCategoryCommand command = new CreateMenuCategoryCommand(
                "Hamburguesas",
                "Deliciosas hamburguesas"
        );

        assertNotNull(command);
        assertEquals("Hamburguesas", command.menuCategoryName());
        assertEquals("Deliciosas hamburguesas", command.description());
    }

    @Test
    void testCreateCommandWithValidNameAndNullDescription(){
        CreateMenuCategoryCommand command = new CreateMenuCategoryCommand("CUCA", null);

        assertNotNull(command);
        assertEquals("CUCA", command.menuCategoryName());
        assertNull(command.description());
    }

    // ========== PRUEBAS DE VALIDACIÓN ==========

    @Test
    void testCreateCommandWithNullName(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateMenuCategoryCommand(null, "Descripción")
        );

        assertEquals("El nombre de la categoria es obligatorio", exception.getMessage());
    }

    @Test
    void testCreateCommandWithEmptyName(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateMenuCategoryCommand("", "Descripción")
        );

        assertEquals("El nombre de la categoria es obligatorio", exception.getMessage());
    }

    @Test
    void testCreateCommandWithBlankName(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateMenuCategoryCommand("   ", "Descripción")
        );

        assertEquals("El nombre de la categoria es obligatorio", exception.getMessage());
    }

    @Test
    void testCommandEquality(){
        CreateMenuCategoryCommand command1 = new CreateMenuCategoryCommand("Hamburguesas", "Deliciosas");
        CreateMenuCategoryCommand command2 = new CreateMenuCategoryCommand("Hamburguesas", "Deliciosas");

        assertEquals(command1, command2);
    }
}
