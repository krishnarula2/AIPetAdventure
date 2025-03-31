package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.group14.virtualpet.model.FoodItem;
import com.group14.virtualpet.model.GiftItem;
import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Item;
import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.model.PetState;
import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.util.AudioManager;
import com.group14.virtualpet.util.SaveLoadUtil;

public class GameplayPanel extends JPanel implements ActionListener {

    private static final int GAME_TICK_MS = 1000; // 1 second per tick
    private static final int ITEM_GRANT_INTERVAL_TICKS = 15; //  (~15 seconds)
    private static final double LOW_STAT_THRESHOLD = 0.25;
    
    // Modern color scheme
    public static final Color PRIMARY_COLOR = new Color(13, 110, 253);      // Bootstrap primary blue
    public static final Color SUCCESS_COLOR = new Color(25, 135, 84);       // Bootstrap success green
    public static final Color DANGER_COLOR = new Color(220, 53, 69);        // Bootstrap danger red
    public static final Color LIGHT_COLOR = new Color(248, 249, 250);       // Bootstrap light
    public static final Color DARK_COLOR = new Color(33, 37, 41);           // Bootstrap dark

    // Predefined pool of available items (both food and gifts)
    private static final List<Item> AVAILABLE_ITEMS = List.of(
            new FoodItem("Basic Kibble", 20),
            new FoodItem("Tasty Treat", 40),
            new FoodItem("Luxury Meal", 75),
            new GiftItem("Squeaky Toy", 15),
            new GiftItem("Cozy Blanket", 30),
            new GiftItem("Sparkling Jewel", 60)
    );
    private static final Random random = new Random();

    // Add keyboard control constants
    private static final char KEY_FEED = 'F';
    private static final char KEY_PLAY = 'P';
    private static final char KEY_SLEEP = 'S';
    private static final char KEY_GIFT = 'G';
    private static final char KEY_EXERCISE = 'E';

    private Pet currentPet;
    private Inventory playerInventory;
    private GameState currentGameState;
    private final Timer gameTimer;
    private final Timer spriteTimer;
    private boolean spriteFlipFlop = false;
    private PetState lastDisplayedState = null;
    
    // Callback for returning to main menu
    private Runnable returnToMainMenuCallback;

    // Component references
    private PetInfoPanel petInfoPanel;
    private PetSpritePanel petSpritePanel;
    private InventoryPanel inventoryPanel;
    private CommandPanel commandPanel;

    // New fields for movement animation
    private boolean movementMode = false;
    private int movementTimerCounter = 0;
    
    private int score;
    private int ticksSinceLastItemGrant = 0;

    public GameplayPanel() {
        setLayout(new BorderLayout(20, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setOpaque(true);
        setBackground(new Color(240, 242, 245));

        // Add key bindings
        setupKeyBindings();

        // Create the info panel (right side)
        petInfoPanel = new PetInfoPanel();
        add(petInfoPanel, BorderLayout.EAST);

        // Create the center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        centerPanel.setOpaque(false);

        // Create sprite panel and inventory panel
        petSpritePanel = new PetSpritePanel();
        inventoryPanel = new InventoryPanel();

        centerPanel.add(petSpritePanel, BorderLayout.CENTER);
        centerPanel.add(inventoryPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Create the command panel (bottom)
        commandPanel = new CommandPanel(this);
        add(commandPanel, BorderLayout.SOUTH);

        // Initialize game loop and sprite timers
        gameTimer = new Timer(GAME_TICK_MS, this);
        gameTimer.setInitialDelay(0);
        spriteTimer = new Timer(500, this);
        spriteTimer.setInitialDelay(500);
    }

    /**
     * Sets the callback to return to the main menu.
     * This must be called by the parent component to enable the "Return to Main Menu" functionality.
     * 
     * @param callback The callback to execute when returning to main menu
     */
    public void setReturnToMainMenuCallback(Runnable callback) {
        System.out.println("Setting return to main menu callback: " + (callback != null ? "valid callback" : "null callback"));
        this.returnToMainMenuCallback = callback;
    }

    private void setupKeyBindings() {
        // Make the panel focusable to receive key events
        setFocusable(true);
        
        // Create input and action maps
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        // Add key bindings for each control
        addKeyBinding(inputMap, actionMap, KEY_FEED, "feed", e -> handleFeedCommand());
        addKeyBinding(inputMap, actionMap, KEY_PLAY, "play", e -> handlePlayCommand());
        addKeyBinding(inputMap, actionMap, KEY_SLEEP, "sleep", e -> handleGoToBedCommand());
        addKeyBinding(inputMap, actionMap, KEY_GIFT, "gift", e -> handleGiveGiftCommand());
        addKeyBinding(inputMap, actionMap, KEY_EXERCISE, "exercise", e -> handleExerciseCommand());
    }

    private void addKeyBinding(InputMap inputMap, ActionMap actionMap, char key, String actionKey, ActionListener action) {
        // Add binding for both uppercase and lowercase
        KeyStroke upperStroke = KeyStroke.getKeyStroke(Character.toUpperCase(key));
        KeyStroke lowerStroke = KeyStroke.getKeyStroke(Character.toLowerCase(key));
        
        inputMap.put(upperStroke, actionKey);
        inputMap.put(lowerStroke, actionKey);
        
        actionMap.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
                    // Play button click sound effect for keyboard shortcuts
                    AudioManager.getInstance().playSoundEffect("mainButtonSound.mp3");
                    action.actionPerformed(e);
                }
            }
        });
    }

    /**
     * Starts a new game session using the given pet.
     */
    public void startGame(Pet pet) {
        this.currentPet = pet;
        this.playerInventory = new Inventory();
        this.score = 0;
        this.currentGameState = new GameState(pet, playerInventory, score);
        this.currentGameState.startSessionTimer();
        this.lastDisplayedState = null;
        this.spriteFlipFlop = false;
        updatePetStatusDisplay();
        gameTimer.start();
        spriteTimer.start();
        this.repaint(); // Force repaint after starting game
    }

    /**
     * Loads a saved game state.
     */
    public void loadGameData(GameState state) {
        if (state == null) {
            System.err.println("GameplayPanel: Cannot load null game state.");
            return;
        }
        this.currentGameState = state;
        this.currentPet = state.getPet();
        this.playerInventory = state.getInventory();
        this.score = state.getScore();
        if (this.currentPet == null || this.playerInventory == null) {
            System.err.println("GameplayPanel: Invalid data in loaded game state.");
            this.currentPet = null;
            this.currentGameState = null;
            return;
        }
        this.currentGameState.startSessionTimer();
        this.ticksSinceLastItemGrant = 0;
        this.lastDisplayedState = null;
        this.spriteFlipFlop = false;
        updatePetStatusDisplay();
        gameTimer.start();
        spriteTimer.start();
        this.repaint(); // Force repaint after loading
    }

    /**
     * Stops the game loop and sprite timer.
     */
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (spriteTimer != null) {
            spriteTimer.stop();
        }
        if (currentGameState != null) {
            currentGameState.stopSessionTimer();
        }
        this.currentPet = null;
        this.currentGameState = null;
    }
    
    /**
     * Pauses the game timers temporarily without stopping the game completely.
     */
    private void pauseGame() {
        if (gameTimer != null && gameTimer.isRunning()) {
            System.out.println("Pausing game timer...");
            gameTimer.stop();
        }
        
        if (spriteTimer != null && spriteTimer.isRunning()) {
            System.out.println("Pausing sprite animation timer...");
            spriteTimer.stop();
        }
        
        // Update UI to show paused state if needed
        this.repaint();
        
        System.out.println("Game paused while showing dialog");
    }
    
    /**
     * Resumes the game after it was paused.
     */
    private void resumeGame() {
        if (currentPet != null && currentGameState != null) {
            if (gameTimer != null && !gameTimer.isRunning()) {
                System.out.println("Resuming game timer...");
                gameTimer.start();
            }
            
            if (spriteTimer != null && !spriteTimer.isRunning()) {
                System.out.println("Resuming sprite animation timer...");
                spriteTimer.start();
            }
            
            // Force a UI update to ensure everything is current
            updatePetStatusDisplay();
            this.repaint();
        } else {
            System.err.println("Cannot resume game: Pet or game state is missing.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Game loop tick
        if (source == gameTimer) {
            if (currentPet != null && currentGameState != null) {
                currentGameState.addPlaytimeMillis(GAME_TICK_MS);
                currentPet.liveOneTick();
                updatePetStatusDisplay();

                // Check parental time limit if enabled
                if (currentGameState.isTimeLimitEnabled()) {
                    long elapsedMinutes = currentGameState.getElapsedSessionTimeMinutes();
                    int limitMinutes = currentGameState.getMaxPlaytimeMinutes();
                    if (elapsedMinutes >= limitMinutes) {
                        stopGame();
                        JOptionPane.showMessageDialog(this,
                            "Playtime limit reached! Your session has ended.",
                            "Time Limit Reached",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                // If pet is dead, handle game over
                if (currentPet.getCurrentState() == PetState.DEAD) {
                    handlePetDeath();
                } else {
                    ticksSinceLastItemGrant++;
                    if (ticksSinceLastItemGrant >= ITEM_GRANT_INTERVAL_TICKS) {
                        grantRandomItem();
                        ticksSinceLastItemGrant = 0;
                    }
                }
            }
        }
        // Sprite animation tick
        // Sprite animation tick
else if (source == spriteTimer) {
    if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
        spriteFlipFlop = !spriteFlipFlop;
        movementTimerCounter++;
        // When 20 ticks (approximately 10 seconds at 500ms per tick) have passed:
        if (movementTimerCounter >= 20) {
            // Temporarily show the movement sprite
            movementMode = true;
            updateSpriteImage();
            // Set a one-shot timer to revert back after 100ms
            Timer revertTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movementMode = false;
                    updateSpriteImage();
                    ((Timer) e.getSource()).stop();
                }
            });
            revertTimer.setRepeats(false);
            revertTimer.start();
            movementTimerCounter = 0;
        } else {
            updateSpriteImage();
        }
    }
}

        
        // Command button actions
        else if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
            // Play button click sound effect
            AudioManager.getInstance().playSoundEffect("mainButtonSound.mp3");
            
            if (source == commandPanel.getFeedButton()) {
                handleFeedCommand();
            } else if (source == commandPanel.getGoToBedButton()) {
                handleGoToBedCommand();
            } else if (source == commandPanel.getGiveGiftButton()) {
                handleGiveGiftCommand();
            } else if (source == commandPanel.getVetButton()) {
                handleVetCommand();
            } else if (source == commandPanel.getPlayButton()) {
                handlePlayCommand();
            } else if (source == commandPanel.getExerciseButton()) {
                handleExerciseCommand();
            } else if (source == commandPanel.getSaveButton()) {
                handleSaveGameCommand();
            } else if (source == commandPanel.getMainMenuButton()) {
                handleMainMenuCommand();
            }
            updatePetStatusDisplay();
            this.repaint(); // Ensure the panel repaints after UI updates
        } else if (source == commandPanel.getMainMenuButton()) {
            // Allow main menu button to work even if pet is dead
            handleMainMenuCommand();
        }
    }

    private void updatePetStatusDisplay() {
        if (currentPet == null) return;
    
        petInfoPanel.updatePetInfo(currentPet, score);
        commandPanel.updateCommandAvailability(currentPet);
        inventoryPanel.updateInventoryDisplay(playerInventory);
    
        // Emergency food logic
        if (currentPet.getCurrentState() == PetState.HUNGRY) {
            boolean hasFood = playerInventory.getAllItems().keySet().stream()
                .anyMatch(item -> item instanceof FoodItem);
            if (!hasFood) {
                FoodItem emergencyFood = new FoodItem("Emergency Ration", 30);
                playerInventory.addItem(emergencyFood, 1);
                JOptionPane.showMessageDialog(this,
                    "Your pet is starving! You've received an Emergency Ration.",
                    "Emergency Food",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    
        // Update sprite if pet state has changed
        PetState currentState = currentPet.getCurrentState();
        if (currentState != lastDisplayedState) {
            lastDisplayedState = currentState;
            spriteFlipFlop = false;
            updateSpriteImage();
        }
    }
    
    private void handlePetDeath() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(this,
                currentPet.getName() + " has passed away. Game Over.",
                "Game Over",
                JOptionPane.WARNING_MESSAGE);
    }

    void handleFeedCommand() {
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<FoodItem, Integer> foodItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof FoodItem)
                .collect(Collectors.toMap(entry -> (FoodItem) entry.getKey(), Map.Entry::getValue));

        if (foodItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any food items!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FoodItem[] choices = foodItems.keySet().toArray(FoodItem[]::new);
        String[] choiceStrings = foodItems.entrySet().stream()
                .map(entry -> entry.getKey().toString() + " (x" + entry.getValue() + ")")
                .toArray(String[]::new);

        int choiceIndex = JOptionPane.showOptionDialog(this,
                "Which food would you like to feed " + currentPet.getName() + "?",
                "Feed Pet",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                choiceStrings,
                choiceStrings[0]);

        if (choiceIndex >= 0) {
            FoodItem selectedFood = choices[choiceIndex];
            if (playerInventory.removeItem(selectedFood)) {
                currentPet.feed(selectedFood);
                increaseScore(10);
            }
        }
    }

    void handleGoToBedCommand() {
        currentPet.goToBed();
    }

    void handleGiveGiftCommand() {
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<GiftItem, Integer> giftItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof GiftItem)
                .collect(Collectors.toMap(entry -> (GiftItem) entry.getKey(), Map.Entry::getValue));

        if (giftItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any gifts!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GiftItem[] choices = giftItems.keySet().toArray(GiftItem[]::new);
        String[] choiceStrings = giftItems.entrySet().stream()
                .map(entry -> entry.getKey().toString() + " (x" + entry.getValue() + ")")
                .toArray(String[]::new);

        int choiceIndex = JOptionPane.showOptionDialog(this,
                "Which gift would you like to give " + currentPet.getName() + "?",
                "Give Gift",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                choiceStrings,
                choiceStrings[0]);

        if (choiceIndex >= 0) {
            GiftItem selectedGift = choices[choiceIndex];
            if (playerInventory.removeItem(selectedGift)) {
                currentPet.receiveGift(selectedGift);
                increaseScore(15);
            }
        }
    }

    void handleVetCommand() {
        if (!currentPet.takeToVet()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't need the vet right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(-5);
        }
    }

    void handlePlayCommand() {
        if (!currentPet.play()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't want to play right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(5);
        }
    }

    void handleExerciseCommand() {
        currentPet.exercise();
        increaseScore(3);
    }

    void handleSaveGameCommand() {
        if (currentPet != null && playerInventory != null) {
            boolean success = saveGame();
            if (success) {
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            System.err.println("Cannot save game: No current pet or inventory.");
            JOptionPane.showMessageDialog(this, "No active game to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the current game state.
     * 
     * @return true if the save was successful, false otherwise
     */
    private boolean saveGame() {
        if (currentPet == null || playerInventory == null) {
            return false;
        }
        
        String filename = currentPet.getName();
        if (filename == null || filename.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot save game: Pet name is invalid.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        filename = filename.trim().replaceAll("[^a-zA-Z0-9.-]", "_");
        GameState stateToSave = (currentGameState != null)
                ? currentGameState
                : new GameState(currentPet, playerInventory, score);
        
        boolean success = SaveLoadUtil.saveGame(stateToSave, filename);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to save game.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return success;
    }

    /**
     * Handles the "Return to Main Menu" button click.
     * Pauses the game, shows confirmation dialogs, and potentially returns to main menu.
     */
    void handleMainMenuCommand() {
        // First, pause the game while showing dialogs
        pauseGame();
        
        // Ask for confirmation to return to main menu
        int confirmResult = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to return to the main menu?",
            "Return to Main Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmResult == JOptionPane.YES_OPTION) {
            // Ask if they want to save before returning
            if (currentPet != null && playerInventory != null) {
                int saveResult = JOptionPane.showConfirmDialog(
                    this,
                    "Would you like to save your game before returning to the main menu?",
                    "Save Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (saveResult == JOptionPane.YES_OPTION) {
                    // Save the game - when saving from here, we still return to menu
                    boolean saveSuccess = saveGame();
                    if (saveSuccess) {
                        JOptionPane.showMessageDialog(this, "Game saved successfully!", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            
            // Stop the game and return to main menu
            stopGame();
            if (returnToMainMenuCallback != null) {
                System.out.println("Executing return to main menu callback from main menu button...");
                returnToMainMenuCallback.run();
            } else {
                System.err.println("ERROR: Return to main menu callback not set! Cannot navigate back to main menu.");
                JOptionPane.showMessageDialog(this, 
                    "Could not return to main menu automatically. Please restart the application.",
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Resume the game if the user decides not to return to main menu
            System.out.println("User decided to continue the game. Resuming...");
            
            // Show a brief notification that the game is continuing
            JOptionPane.showMessageDialog(
                this,
                "Game continuing...",
                "Returning to Game",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Resume the game
            resumeGame();
        }
    }

    private void grantRandomItem() {
        if (playerInventory != null && !AVAILABLE_ITEMS.isEmpty()) {
            Item grantedItem;
            if (score % 2 == 0) {  // Prioritize food every second item
                List<FoodItem> foodItems = AVAILABLE_ITEMS.stream()
                        .filter(item -> item instanceof FoodItem)
                        .map(item -> (FoodItem) item)
                        .collect(Collectors.toList());
                grantedItem = foodItems.get(random.nextInt(foodItems.size()));
            } else {
                grantedItem = AVAILABLE_ITEMS.get(random.nextInt(AVAILABLE_ITEMS.size()));
            }
    
            playerInventory.addItem(grantedItem, 1);
            JOptionPane.showMessageDialog(this,
                    "You found an item: " + grantedItem.getName() + "!",
                    "Item Found!",
                    JOptionPane.INFORMATION_MESSAGE);
            inventoryPanel.updateInventoryDisplay(playerInventory);
        }
    }

    private void increaseScore(int amount) {
        this.score += amount;
        updatePetStatusDisplay();
    }

    private void updateSpriteImage() {
        if (currentPet == null) return;
        petSpritePanel.updateSprite(currentPet.getPetType(), currentPet.getCurrentState(), spriteFlipFlop, movementMode);
    }
    
    

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Draw a solid background color
        g.setColor(new Color(240, 242, 245));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Call superclass method
        super.paintComponent(g);
    }

    Pet getCurrentPet() {
        return currentPet;
    }
} 