package com.group14.virtualpet;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.util.SaveLoadUtil;

/**
 * Tests for SaveGame functionality
 */
public class SaveGameTest {
    private GameState testState;
    private Inventory testInventory;
    private Pet testPet;
    private final String TEST_SAVE_FILENAME = "test_save.dat";
    
    @BeforeEach
    void setUp() {
        testInventory = new Inventory();
        testPet = new Pet("TestPet", "friendly_robot");
        testState = new GameState(testPet, testInventory, 100);
    }
    
    @AfterEach
    void tearDown() {
        // Delete test save file if it exists
        File saveFile = new File(TEST_SAVE_FILENAME);
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
    
    @Test
    void testSaveAndLoadGame() {
        // Arrange
        
        // Act
        boolean saveResult = SaveLoadUtil.saveGame(testState, TEST_SAVE_FILENAME);
        GameState loadedState = SaveLoadUtil.loadGame(TEST_SAVE_FILENAME);
        
        // Assert
        assertTrue(saveResult, "Save operation should succeed");
        assertNotNull(loadedState, "Loaded state should not be null");
        assertEquals(testState.getScore(), loadedState.getScore(), "Score should be preserved");
        assertEquals(testPet.getName(), loadedState.getPet().getName(), "Pet name should be preserved");
        // Add more assertions for pet stats, inventory items, etc.
    }
} 