package com.group14.virtualpet;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test that required resources are available
 */
class ResourceTest {

    @Test
    void testPetSpritesExist() {
        // Check that pet sprite images exist
        File friendlyRobotImage = new File("src/main/resources/images/pets/friendly_robot.png");
        File balancedRobotImage = new File("src/main/resources/images/pets/balanced_robot.png");
        File challengingRobotImage = new File("src/main/resources/images/pets/challenging_robot.png");
        
        assertTrue(friendlyRobotImage.exists(), "friendly_robot image does not exist");
        assertTrue(balancedRobotImage.exists(), "balanced_robot image does not exist");
        assertTrue(challengingRobotImage.exists(), "challenging_robot image does not exist");
    }
    
    @Test
    public void testFoodImageResourcesExist() {
        // Test that food image resources exist
        File basicFood = new File("src/main/resources/images/food/basic.png");
        File premiumFood = new File("src/main/resources/images/food/premium.png");
        File deluxeFood = new File("src/main/resources/images/food/deluxe.png");
        
        assertTrue(basicFood.exists(), "Basic food image does not exist");
        assertTrue(premiumFood.exists(), "Premium food image does not exist");
        assertTrue(deluxeFood.exists(), "Deluxe food image does not exist");
    }
    
    @Test
    public void testGiftImageResourcesExist() {
        // Test that gift image resources exist
        File toyGift = new File("src/main/resources/images/gifts/toy.png");
        File treatGift = new File("src/main/resources/images/gifts/treat.png");
        File premiumToyGift = new File("src/main/resources/images/gifts/premium_toy.png");
        
        assertTrue(toyGift.exists(), "Toy gift image does not exist");
        assertTrue(treatGift.exists(), "Treat gift image does not exist");
        assertTrue(premiumToyGift.exists(), "Premium toy gift image does not exist");
    }
} 