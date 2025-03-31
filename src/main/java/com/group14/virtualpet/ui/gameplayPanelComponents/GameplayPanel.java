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

    // Component references
    private PetInfoPanel petInfoPanel;
    private PetSpritePanel petSpritePanel;
    private InventoryPanel inventoryPanel;
    private CommandPanel commandPanel;
    
    private int score;
    private int ticksSinceLastItemGrant = 0;

    public GameplayPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
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
        else if (source == spriteTimer) {
            if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
                spriteFlipFlop = !spriteFlipFlop;
                updateSpriteImage();
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
            }
            updatePetStatusDisplay();
            this.repaint(); // Ensure the panel repaints after UI updates
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
            String filename = currentPet.getName();
            if (filename == null || filename.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot save game: Pet name is invalid.", "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            filename = filename.trim().replaceAll("[^a-zA-Z0-9.-]", "_");
            GameState stateToSave = (currentGameState != null)
                    ? currentGameState
                    : new GameState(currentPet, playerInventory, score);
            boolean success = SaveLoadUtil.saveGame(stateToSave, filename);
            if (success) {
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Game Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save game.", "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.err.println("Cannot save game: No current pet or inventory.");
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
        petSpritePanel.updateSprite(currentPet.getPetType(), currentPet.getCurrentState(), spriteFlipFlop);
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