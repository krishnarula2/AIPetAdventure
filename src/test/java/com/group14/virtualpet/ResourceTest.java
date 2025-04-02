/**
 * Unit tests for the resources in the virtual pet game.
 * Validates game data and resource management.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test that required resources are available.
 * 
 * This test only verifies that the pet sprite images exist in their respective folders.
 *  
 */
class ResourceTest {

    @Test
    void testPetSpritesExist() {
        // Check that pet sprite images exist in each pet's folder.
        File friendlyRobotImage = new File("src/main/resources/images/pets/friendly_robot/friendly_robot.png");
        File balancedRobotImage = new File("src/main/resources/images/pets/balanced_robot/balanced_robot.png");
        File challengingRobotImage = new File("src/main/resources/images/pets/challenging_robot/challenging_robot.png");
        
        // Assert that each file exists.
        assertTrue(friendlyRobotImage.exists(), "friendly_robot image does not exist");
        assertTrue(balancedRobotImage.exists(), "balanced_robot image does not exist");
        assertTrue(challengingRobotImage.exists(), "challenging_robot image does not exist");
    }
}
