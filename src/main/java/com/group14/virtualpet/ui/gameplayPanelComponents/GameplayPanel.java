/**
 * Gameplay panel showing the gameplay interface for interacting with the virtual pet.
 * Provides real-time updates to the pet's status and allows the user to make gameplay decisions.
 * 
 * @author Group 14
 * @version 1.0
 */

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

/**
 * GameplayPanel is responsible for managing and updating the game loop,
 * handling user commands, updating pet status, and controlling sprite animations.
 * It also grants random items, processes key bindings, and handles game state changes.
 */
public class GameplayPanel extends JPanel implements ActionListener {

    // Constants for game loop timing and item grant interval.
    private static final int GAME_TICK_MS = 1000; // 1 second per tick
    private static final int ITEM_GRANT_INTERVAL_TICKS = 15; // Approximately 15 seconds per item grant
    private static final double LOW_STAT_THRESHOLD = 0.25; // Threshold for low stat warning

    // Modern color scheme constants.
    public static final Color PRIMARY_COLOR = new Color(13, 110, 253);   // Bootstrap primary blue
    public static final Color SUCCESS_COLOR = new Color(25, 135, 84);    // Bootstrap success green
    public static final Color DANGER_COLOR = new Color(220, 53, 69);     // Bootstrap danger red
    public static final Color LIGHT_COLOR = new Color(248, 249, 250);    // Bootstrap light
    public static final Color DARK_COLOR = new Color(33, 37, 41);        // Bootstrap dark

    // Predefined pool of available items (both food and gifts).
    private static final List<Item> AVAILABLE_ITEMS = List.of(
            new FoodItem("Basic Kibble", 20),
            new FoodItem("Tasty Treat", 40),
            new FoodItem("Luxury Meal", 75),
            new GiftItem("Squeaky Toy", 15),
            new GiftItem("Cozy Blanket", 30),
            new GiftItem("Sparkling Jewel", 60)
    );
    
    // Random instance for random item granting and movement mode toggling.
    private static final Random random = new Random();

    // Keyboard control constants.
    private static final char KEY_FEED = 'F';
    private static final char KEY_PLAY = 'P';
    private static final char KEY_SLEEP = 'S';
    private static final char KEY_GIFT = 'G';
    private static final char KEY_EXERCISE = 'E';

    // Game state fields.
    private Pet currentPet;
    private Inventory playerInventory;
    private GameState currentGameState;
    
    // Timers for the main game loop and sprite animation.
    private final Timer gameTimer;
    private final Timer spriteTimer;
    
    // Used for alternating sprite images rapidly if needed.
    private boolean spriteFlipFlop = false;
    // Last displayed pet state, used to detect changes.
    private PetState lastDisplayedState = null;
    
    // Callback to return to main menu.
    private Runnable returnToMainMenuCallback;

    // Panel components for displaying pet info, sprite, inventory, and commands.
    private PetInfoPanel petInfoPanel;
    private PetSpritePanel petSpritePanel;
    private InventoryPanel inventoryPanel;
    private CommandPanel commandPanel;

    // Fields for handling movement animation.
    // movementMode flag: if true, the movement sprite is shown.
    private boolean movementMode = false;
    // Counter to determine when to trigger the movement mode.
    private int movementTimerCounter = 0;
    
    // Game score and item grant counter.
    private int score;
    private int ticksSinceLastItemGrant = 0;

    /**
     * Constructor: Initializes the panel layout, key bindings, and game components.
     */
    public GameplayPanel() {
        // Set overall layout and border.
        setLayout(new BorderLayout(20, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setOpaque(true);
        setBackground(new Color(240, 242, 245));

        // Setup key bindings for game commands.
        setupKeyBindings();

        // Create and add the pet information panel (displayed on the right side).
        petInfoPanel = new PetInfoPanel();
        add(petInfoPanel, BorderLayout.EAST);

        // Create the center panel to hold the pet sprite and inventory.
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        centerPanel.setOpaque(false);

        // Create the pet sprite panel and the inventory panel.
        petSpritePanel = new PetSpritePanel();
        inventoryPanel = new InventoryPanel();

        // Add sprite and inventory panels to the center panel.
        centerPanel.add(petSpritePanel, BorderLayout.CENTER);
        centerPanel.add(inventoryPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Create and add the command panel (displayed at the bottom).
        commandPanel = new CommandPanel(this);
        add(commandPanel, BorderLayout.SOUTH);

        // Initialize the game loop timer (ticks every second).
        gameTimer = new Timer(GAME_TICK_MS, this);
        gameTimer.setInitialDelay(0);

        // Initialize the sprite timer (ticks every 500ms).
        spriteTimer = new Timer(500, this);
        spriteTimer.setInitialDelay(500);
    }

    /**
     * Sets the callback to return to the main menu.
     * 
     * @param callback The Runnable to execute when returning to the main menu.
     */
    public void setReturnToMainMenuCallback(Runnable callback) {
        System.out.println("Setting return to main menu callback: " + (callback != null ? "valid callback" : "null callback"));
        this.returnToMainMenuCallback = callback;
    }

    /**
     * Configures key bindings for gameplay commands.
     */
    private void setupKeyBindings() {
        // Enable the panel to receive key events.
        setFocusable(true);
        
        // Create input and action maps.
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        // Bind keys for Feed, Play, Sleep, Gift, and Exercise commands.
        addKeyBinding(inputMap, actionMap, KEY_FEED, "feed", e -> handleFeedCommand());
        addKeyBinding(inputMap, actionMap, KEY_PLAY, "play", e -> handlePlayCommand());
        addKeyBinding(inputMap, actionMap, KEY_SLEEP, "sleep", e -> handleGoToBedCommand());
        addKeyBinding(inputMap, actionMap, KEY_GIFT, "gift", e -> handleGiveGiftCommand());
        addKeyBinding(inputMap, actionMap, KEY_EXERCISE, "exercise", e -> handleExerciseCommand());
    }

    /**
     * Helper method to add key bindings for both uppercase and lowercase keys.
     */
    private void addKeyBinding(InputMap inputMap, ActionMap actionMap, char key, String actionKey, ActionListener action) {
        // Create key strokes for uppercase and lowercase.
        KeyStroke upperStroke = KeyStroke.getKeyStroke(Character.toUpperCase(key));
        KeyStroke lowerStroke = KeyStroke.getKeyStroke(Character.toLowerCase(key));
        
        inputMap.put(upperStroke, actionKey);
        inputMap.put(lowerStroke, actionKey);
        
        // Map the action using an AbstractAction.
        actionMap.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
                    // Play sound effect for key binding commands.
                    AudioManager.getInstance().playSoundEffect("mainButtonSound.mp3");
                    action.actionPerformed(e);
                }
            }
        });
    }

    /**
     * Starts a new game session with the specified pet.
     * 
     * @param pet The Pet object representing the pet to start the game with.
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
        this.repaint(); // Force repaint after starting the game.
    }

    /**
     * Loads a saved game state.
     * 
     * @param state The GameState object to load.
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
        this.repaint(); // Force repaint after loading.
    }

    /**
     * Stops the game loop and sprite timer, and resets the current game state.
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
     * Pauses the game timers temporarily.
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
        
        // Update UI to indicate paused state.
        this.repaint();
        System.out.println("Game paused while showing dialog");
    }
    
    /**
     * Resumes the game by restarting timers and updating the UI.
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
            updatePetStatusDisplay();
            this.repaint();
        } else {
            System.err.println("Cannot resume game: Pet or game state is missing.");
        }
    }

    /**
     * Main actionPerformed method handling game ticks, sprite animation, and command actions.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // ---------------------------
        // Game Loop Tick
        // ---------------------------
        if (source == gameTimer) {
            if (currentPet != null && currentGameState != null) {
                // Update playtime and process one game tick for the pet.
                currentGameState.addPlaytimeMillis(GAME_TICK_MS);
                currentPet.liveOneTick();
                updatePetStatusDisplay();

                // Check parental time limits if enabled.
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

                // Handle pet death scenario.
                if (currentPet.getCurrentState() == PetState.DEAD) {
                    handlePetDeath();
                } else {
                    // Grant a random item after a certain number of ticks.
                    ticksSinceLastItemGrant++;
                    if (ticksSinceLastItemGrant >= ITEM_GRANT_INTERVAL_TICKS) {
                        grantRandomItem();
                        ticksSinceLastItemGrant = 0;
                    }
                }
            }
        }
        // ---------------------------
        // Sprite Animation Tick
        // ---------------------------
        else if (source == spriteTimer) {
            if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
                // Alternate the sprite flip-flop flag.
                spriteFlipFlop = !spriteFlipFlop;
                // Increase the counter to determine when to trigger movement mode.
                movementTimerCounter++;
                // After 20 ticks (~10 seconds at 500ms per tick), show the movement sprite.
                if (movementTimerCounter >= 20) {
                    movementMode = true;
                    updateSpriteImage();
                    // Use a one-shot timer to revert back after 300ms.
                    Timer revertTimer = new Timer(300, new ActionListener() {
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
        // ---------------------------
        // Command Button Actions
        // ---------------------------
        else if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
            // Play sound effect on button click.
            AudioManager.getInstance().playSoundEffect("mainButtonSound.mp3");
            // Check which command button was pressed.
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
            repaint();
        }
        // Allow main menu button to work even if pet is dead.
        else if (source == commandPanel.getMainMenuButton()) {
            handleMainMenuCommand();
        }
    }

    /**
     * Updates all pet status displays, including pet info, commands, inventory, and sprite.
     */
    private void updatePetStatusDisplay() {
        if (currentPet == null) return;
    
        // Update pet info panel (name, score, state, etc.)
        petInfoPanel.updatePetInfo(currentPet, score);
        // Update command button availability based on pet state.
        commandPanel.updateCommandAvailability(currentPet);
        // Update inventory display.
        inventoryPanel.updateInventoryDisplay(playerInventory);
    
        // Emergency food logic: If the pet is hungry and there's no food, grant an emergency ration.
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
    
        // If the pet state has changed, update the sprite.
        PetState currentState = currentPet.getCurrentState();
        if (currentState != lastDisplayedState) {
            lastDisplayedState = currentState;
            spriteFlipFlop = false;
            updateSpriteImage();
        }
    }
    
    /**
     * Handles the pet death scenario by stopping the game loop and showing a Game Over dialog.
     */
    private void handlePetDeath() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(this,
                currentPet.getName() + " has passed away. Game Over.",
                "Game Over",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Handles the Feed command.
     * Prompts the user to choose a food item and feeds the pet if the item is available.
     */
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

    /**
     * Handles the Go to Bed command.
     */
    void handleGoToBedCommand() {
        currentPet.goToBed();
    }

    /**
     * Handles the Give Gift command.
     * Prompts the user to choose a gift item and gives it to the pet if available.
     */
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

    /**
     * Handles the Vet command.
     * If the pet can go to the vet, it does so; otherwise, it informs the user.
     */
    void handleVetCommand() {
        if (!currentPet.takeToVet()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't need the vet right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(-5);
        }
    }

    /**
     * Handles the Play command.
     */
    void handlePlayCommand() {
        if (!currentPet.play()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't want to play right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(5);
        }
    }

    /**
     * Handles the Exercise command.
     */
    void handleExerciseCommand() {
        currentPet.exercise();
        increaseScore(3);
    }

    /**
     * Handles the Save Game command.
     * Attempts to save the current game state.
     */
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
     * Saves the current game state to a file.
     * 
     * @return true if the game was saved successfully, false otherwise.
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
        
        // Sanitize filename by replacing unwanted characters.
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
     * Handles the "Return to Main Menu" command.
     * Pauses the game, optionally saves the game state, and then returns to the main menu.
     */
    void handleMainMenuCommand() {
        // Pause the game while confirming navigation.
        pauseGame();
        
        // Confirm if the user wants to return to the main menu.
        int confirmResult = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to return to the main menu?",
            "Return to Main Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmResult == JOptionPane.YES_OPTION) {
            // Optionally prompt to save before returning.
            if (currentPet != null && playerInventory != null) {
                int saveResult = JOptionPane.showConfirmDialog(
                    this,
                    "Would you like to save your game before returning to the main menu?",
                    "Save Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (saveResult == JOptionPane.YES_OPTION) {
                    boolean saveSuccess = saveGame();
                    if (saveSuccess) {
                        JOptionPane.showMessageDialog(this, "Game saved successfully!", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            
            // Stop the game and run the main menu callback.
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
            // If the user cancels, resume the game.
            System.out.println("User decided to continue the game. Resuming...");
            JOptionPane.showMessageDialog(
                this,
                "Game continuing...",
                "Returning to Game",
                JOptionPane.INFORMATION_MESSAGE
            );
            resumeGame();
        }
    }

    /**
     * Grants a random item to the player inventory.
     */
    private void grantRandomItem() {
        if (playerInventory != null && !AVAILABLE_ITEMS.isEmpty()) {
            Item grantedItem;
            if (score % 2 == 0) {  // Prioritize food every second item.
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

    /**
     * Increases the player's score by the given amount and updates the pet status display.
     * 
     * @param amount The amount to increase the score.
     */
    private void increaseScore(int amount) {
        this.score += amount;
        updatePetStatusDisplay();
    }

    /**
     * Updates the pet sprite image by delegating to the PetSpritePanel.
     */
    private void updateSpriteImage() {
        if (currentPet == null) return;
        petSpritePanel.updateSprite(currentPet.getPetType(), currentPet.getCurrentState(), spriteFlipFlop, movementMode);
    }
    
    /**
     * Overrides paintComponent to draw a solid background color before the rest of the painting.
     */
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Draw a solid background color.
        g.setColor(new Color(240, 242, 245));
        g.fillRect(0, 0, getWidth(), getHeight());
        // Call superclass paintComponent.
        super.paintComponent(g);
    }

    /**
     * Returns the current pet.
     * 
     * @return The current Pet object.
     */
    Pet getCurrentPet() {
        return currentPet;
    }
}
