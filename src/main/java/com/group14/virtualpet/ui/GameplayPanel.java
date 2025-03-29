package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color; // Import Color for warnings
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL; // For loading resources
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon; // For images
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar; // Import JProgressBar
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.group14.virtualpet.model.FoodItem;
import com.group14.virtualpet.model.GiftItem;
import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Item;
import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.model.PetState;
import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.util.SaveLoadUtil;

/**
 * Represents the main gameplay screen.
 * Displays the pet, vital statistics, and interaction buttons.
 * Manages the game loop timer.
 * Requirements: 3.1.1, 3.1.6, 3.1.7, 3.1.8, 3.1.9, 3.1.10
 */
public class GameplayPanel extends JPanel implements ActionListener {

    private static final int GAME_TICK_MS = 1000; // How often the game updates (milliseconds)
    private static final int ITEM_GRANT_INTERVAL_TICKS = 60; // Grant item every 60 ticks (seconds)
    private static final Color LOW_STAT_WARNING_COLOR = Color.RED;
    private static final double LOW_STAT_THRESHOLD = 0.25; // 25%

    // Pool of items that can be randomly granted (Req 3.1.8)
    private static final List<Item> AVAILABLE_ITEMS = List.of(
            new FoodItem("Basic Kibble", 20),
            new FoodItem("Tasty Treat", 40),
            new FoodItem("Luxury Meal", 75),
            new GiftItem("Squeaky Toy", 15),
            new GiftItem("Cozy Blanket", 30),
            new GiftItem("Sparkling Jewel", 60)
    );
    private static final Random random = new Random();

    private Pet currentPet;
    private Inventory playerInventory;
    private GameState currentGameState; // Add field to hold GameState
    private Timer gameTimer;
    private Timer spriteTimer; // Timer for sprite animation (Req 3.1.10)
    private boolean spriteFlipFlop = false; // Flag for alternating sprite (Req 3.1.10)
    private PetState lastDisplayedState = null; // Track last state for sprite updates

    // UI Components (Placeholders)
    private JLabel petNameLabel;
    private JLabel petSpriteLabel; // To display the pet image
    private JProgressBar healthBar;
    private JProgressBar sleepBar;
    private JProgressBar fullnessBar;
    private JProgressBar happinessBar;
    private JLabel stateLabel;
    // Command Buttons (Req 3.1.7)
    private JButton feedButton;
    private JButton goToBedButton;
    private JButton giveGiftButton;
    private JButton vetButton;
    private JButton playButton;
    private JButton exerciseButton;
    // --- Save/Load --- (Req 3.1.5)
    private JButton saveButton;
    // private JButton tempAddItemButton; // No longer needed
    private JTextArea inventoryDisplay; // Simple text area for inventory
    // --- Score --- (Req 3.1.9)
    private JLabel scoreLabel;
    private int score;
    // --- Item Grant Timer --- (Req 3.1.8)
    private int ticksSinceLastItemGrant = 0;

    // TODO: Add button for Back/Menu?

    // TODO: Add progress bars or other visual indicators for stats (Req 3.1.6)
    // TODO: Add score display (Req 3.1.9)

    public GameplayPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel (Pet Info) ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Add gaps
        petNameLabel = new JLabel("Pet Name: ?");
        scoreLabel = new JLabel("Score: 0");

        // Initialize Progress Bars (Req 3.1.6)
        healthBar = createStatProgressBar("Health");
        sleepBar = createStatProgressBar("Sleep");
        fullnessBar = createStatProgressBar("Fullness");
        happinessBar = createStatProgressBar("Happiness");

        stateLabel = new JLabel("State: ?");

        infoPanel.add(petNameLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(healthBar);
        infoPanel.add(sleepBar);
        infoPanel.add(fullnessBar);
        infoPanel.add(happinessBar);
        infoPanel.add(stateLabel);
        add(infoPanel, BorderLayout.EAST);

        // --- Center Panel (Pet Sprite & Inventory) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        petSpriteLabel = new JLabel("[Pet Sprite Placeholder]", SwingConstants.CENTER);
        petSpriteLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        petSpriteLabel.setPreferredSize(new Dimension(300, 300));
        centerPanel.add(petSpriteLabel, BorderLayout.CENTER);

        // Inventory Display (Simple)
        inventoryDisplay = new JTextArea(5, 20);
        inventoryDisplay.setEditable(false);
        inventoryDisplay.setBorder(BorderFactory.createTitledBorder("Inventory"));
        JScrollPane scrollPane = new JScrollPane(inventoryDisplay);
        centerPanel.add(scrollPane, BorderLayout.SOUTH); // Inventory below sprite
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel (Commands) ---
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Tighter layout
        feedButton = createCommandButton("Feed");
        goToBedButton = createCommandButton("Go to Bed");
        giveGiftButton = createCommandButton("Give Gift");
        vetButton = createCommandButton("Take to Vet");
        playButton = createCommandButton("Play");
        exerciseButton = createCommandButton("Exercise");
        saveButton = createCommandButton("Save Game"); // Create save button
        // tempAddItemButton = createCommandButton("Get Items (TEMP)"); // Remove temp button

        commandPanel.add(feedButton);
        commandPanel.add(goToBedButton);
        commandPanel.add(giveGiftButton);
        commandPanel.add(vetButton);
        commandPanel.add(playButton);
        commandPanel.add(exerciseButton);
        commandPanel.add(saveButton); // Add save button to panel
        // commandPanel.add(tempAddItemButton); // Remove temp button from panel

        add(commandPanel, BorderLayout.SOUTH);

        // --- Game Timer Setup ---
        gameTimer = new Timer(GAME_TICK_MS, this);
        gameTimer.setInitialDelay(0); // Start ticking immediately once started

        // Sprite Animation Timer Setup (Req 3.1.10)
        spriteTimer = new Timer(500, this); // Change sprite every 500ms
        spriteTimer.setInitialDelay(500);
    }

    /**
     * Sets the current pet for this gameplay session and starts the game loop.
     * @param pet The pet to manage.
     */
    public void startGame(Pet pet) {
        this.currentPet = pet;
        this.playerInventory = new Inventory();
        this.score = 0; // Initialize score
        // Create the initial game state for this session
        this.currentGameState = new GameState(pet, playerInventory, score);
        // Start the session timer within the game state
        this.currentGameState.startSessionTimer();

        System.out.println("Starting game with pet: " + pet.getName());
        this.lastDisplayedState = null; // Reset last state
        this.spriteFlipFlop = false; // Reset flip flop
        updatePetStatusDisplay(); // Initial UI update
        gameTimer.start();
        spriteTimer.start(); // Start sprite animation timer
    }

    /**
     * Loads game data from a GameState object and starts the game.
     * @param state The loaded game state.
     */
    public void loadGameData(GameState state) {
        if (state == null) {
            System.err.println("GameplayPanel: Cannot load null game state.");
            // Optionally navigate back to main menu or show error
            return;
        }
        this.currentGameState = state; // Store the loaded GameState
        this.currentPet = state.getPet();
        this.playerInventory = state.getInventory();
        this.score = state.getScore();

        if (this.currentPet == null || this.playerInventory == null) {
            System.err.println("GameplayPanel: Invalid data in loaded game state.");
            // Handle corrupted save data - maybe navigate back to main menu
            this.currentPet = null; // Ensure inconsistent state isn't used
            this.currentGameState = null;
            return;
        }

        // Start the session timer for the loaded game
        this.currentGameState.startSessionTimer();

        System.out.println("Loading game with pet: " + currentPet.getName() + ", Score: " + score);
        this.ticksSinceLastItemGrant = 0; // Reset item grant timer
        this.lastDisplayedState = null; // Reset last state
        this.spriteFlipFlop = false; // Reset flip flop
        updatePetStatusDisplay(); // Update UI with loaded data
        gameTimer.start(); // Start the game timer
        spriteTimer.start(); // Start sprite animation timer
    }

    /**
     * Stops the game loop and sprite timers.
     */
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
            System.out.println("Game timer stopped.");
        }
        if (spriteTimer != null) {
             spriteTimer.stop(); // Stop sprite timer
             System.out.println("Sprite timer stopped.");
        }
        // Stop the session timer in GameState
        if (currentGameState != null) {
            currentGameState.stopSessionTimer();
        }
        this.currentPet = null; // Clear current pet
        this.currentGameState = null; // Clear current game state
    }

    /**
     * Handles the timer tick event.
     * @param e ActionEvent from the timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == gameTimer) {
            if (currentPet != null && currentGameState != null) { // Check gameState too
                // Add playtime to statistics (Req 3.1.11.2)
                currentGameState.addPlaytimeMillis(GAME_TICK_MS);

                // Pet lives one tick
                currentPet.liveOneTick();
                updatePetStatusDisplay();

                // --- Check Time Limit --- (Req 3.1.11.1)
                if (currentGameState != null && currentGameState.isTimeLimitEnabled()) {
                    long elapsedMinutes = currentGameState.getElapsedSessionTimeMinutes();
                    int limitMinutes = currentGameState.getMaxPlaytimeMinutes();
                    if (elapsedMinutes >= limitMinutes) {
                        System.out.println("Parental Time Limit Reached! (" + elapsedMinutes + "/" + limitMinutes + " min)");
                        stopGame(); // Stop timers and clear state
                        JOptionPane.showMessageDialog(this,
                            "Playtime limit reached! Your session has ended.",
                            "Time Limit Reached",
                            JOptionPane.INFORMATION_MESSAGE);
                        // TODO: Navigate back to main menu? Requires access to MainFrame/navigateCallback
                        return; // Stop further processing for this tick
                    }
                }

                if (currentPet.getCurrentState() == PetState.DEAD) {
                    handlePetDeath();
                } else {
                    // --- Periodic Item Grant --- (Req 3.1.8)
                    ticksSinceLastItemGrant++;
                    if (ticksSinceLastItemGrant >= ITEM_GRANT_INTERVAL_TICKS) {
                        grantRandomItem();
                        ticksSinceLastItemGrant = 0; // Reset counter
                    }
                }
            }
        } else if (source == spriteTimer) { // Handle sprite timer event (Req 3.1.10)
            if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
                spriteFlipFlop = !spriteFlipFlop; // Toggle flag
                updateSpriteImage(); // Update the displayed sprite
            }
        } else if (currentPet != null && currentPet.getCurrentState() != PetState.DEAD) {
            // Handle Command Button Clicks
            if (source == feedButton) {
                handleFeedCommand();
            } else if (source == goToBedButton) {
                handleGoToBedCommand();
            } else if (source == giveGiftButton) {
                handleGiveGiftCommand();
            } else if (source == vetButton) {
                handleVetCommand();
            } else if (source == playButton) {
                handlePlayCommand();
            } else if (source == exerciseButton) {
                handleExerciseCommand();
            } else if (source == saveButton) { // Handle save button click
                handleSaveGameCommand();
            }
            // Update UI immediately after a command action
            updatePetStatusDisplay();
        }
    }

    /**
     * Updates the UI elements to reflect the current state of the pet.
     */
    private void updatePetStatusDisplay() {
        if (currentPet == null) return;

        petNameLabel.setText("Pet Name: " + currentPet.getName());
        scoreLabel.setText("Score: " + score);
        stateLabel.setText("State: " + currentPet.getCurrentState());

        // Update Progress Bars and check for low stat warnings (Req 3.1.6)
        updateStatBar(healthBar, "Health", currentPet.getHealth(), currentPet.getMaxHealth());
        updateStatBar(sleepBar, "Sleep", currentPet.getSleep(), currentPet.getMaxSleep());
        updateStatBar(fullnessBar, "Fullness", currentPet.getFullness(), currentPet.getMaxFullness());
        updateStatBar(happinessBar, "Happiness", currentPet.getHappiness(), currentPet.getMaxHappiness());

        // Update pet sprite only if state has changed (Req 3.1.10)
        PetState currentState = currentPet.getCurrentState();
        if (currentState != lastDisplayedState) {
            System.out.println("Pet state changed to: " + currentState); // Debugging
            lastDisplayedState = currentState;
            spriteFlipFlop = false; // Reset flip-flop on state change
            updateSpriteImage(); // Load the new state's sprite
        }

        // Enable/disable command buttons based on state (Req 3.1.7)
        updateCommandAvailability();

        // Update inventory display
        updateInventoryDisplay();

        // Ensure the panel repaints to show changes
        // repaint(); // Often called automatically, but can be explicit if needed
    }

    /**
     * Handles the game over condition when the pet dies.
     */
    private void handlePetDeath() {
        gameTimer.stop();
        System.out.println("GAME OVER: " + currentPet.getName() + " has died.");
        JOptionPane.showMessageDialog(this,
                currentPet.getName() + " has passed away. Game Over.",
                "Game Over",
                JOptionPane.WARNING_MESSAGE);
        // TODO: Provide options like return to main menu or load game (Req 3.1.6a)
        // For now, just disable interaction or potentially switch back to main menu
        // Need access to the main frame/controller to switch panels
    }

    // --- Command Handling Methods ---

    private void handleFeedCommand() {
        System.out.println("Feed button clicked");
        // Filter inventory for food items
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<FoodItem, Integer> foodItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof FoodItem)
                .collect(Collectors.toMap(entry -> (FoodItem) entry.getKey(), Map.Entry::getValue));

        if (foodItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any food items!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create choice array for dialog
        FoodItem[] choices = foodItems.keySet().toArray(new FoodItem[0]);
        String[] choiceStrings = foodItems.entrySet().stream()
                                   .map(entry -> entry.getKey().toString() + " (x" + entry.getValue() + ")")
                                   .toArray(String[]::new);

        // Show selection dialog
        int choiceIndex = JOptionPane.showOptionDialog(this,
                "Which food would you like to feed " + currentPet.getName() + "?",
                "Feed Pet",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choiceStrings, choiceStrings[0]);

        if (choiceIndex >= 0) {
            FoodItem selectedFood = choices[choiceIndex];
            System.out.println("Attempting to feed: " + selectedFood.getName());
            if (playerInventory.removeItem(selectedFood)) { // Consume item
                currentPet.feed(selectedFood);
                System.out.println(currentPet.getName() + " ate " + selectedFood.getName() + ".");
                // TODO: Add score increase (Req 3.1.9)
                increaseScore(10); // Example score for feeding
            } else {
                System.err.println("Error consuming food item from inventory!"); // Should not happen if logic is correct
            }
        }
    }

    private void handleGoToBedCommand() {
        System.out.println("Go to Bed button clicked");
        currentPet.goToBed();
    }

    private void handleGiveGiftCommand() {
        System.out.println("Give Gift button clicked");
        // Filter inventory for gift items
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<GiftItem, Integer> giftItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof GiftItem)
                .collect(Collectors.toMap(entry -> (GiftItem) entry.getKey(), Map.Entry::getValue));

        if (giftItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any gifts!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create choice array for dialog
        GiftItem[] choices = giftItems.keySet().toArray(new GiftItem[0]);
         String[] choiceStrings = giftItems.entrySet().stream()
                                   .map(entry -> entry.getKey().toString() + " (x" + entry.getValue() + ")")
                                   .toArray(String[]::new);

        // Show selection dialog
         int choiceIndex = JOptionPane.showOptionDialog(this,
                "Which gift would you like to give " + currentPet.getName() + "?",
                "Give Gift",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choiceStrings, choiceStrings[0]);

        if (choiceIndex >= 0) {
            GiftItem selectedGift = choices[choiceIndex];
            System.out.println("Attempting to give gift: " + selectedGift.getName());
            if (playerInventory.removeItem(selectedGift)) { // Consume item
                currentPet.receiveGift(selectedGift);
                System.out.println(currentPet.getName() + " received " + selectedGift.getName() + "!");
                // TODO: Add score increase (Req 3.1.9)
                increaseScore(15); // Example score for gifting
            } else {
                System.err.println("Error consuming gift item from inventory!");
            }
        }
    }

    private void handleVetCommand() {
        System.out.println("Vet button clicked");
        if (!currentPet.takeToVet()) {
            // Action failed (likely cooldown)
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't need the vet right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
             increaseScore(-5); // Example score change for vet (can be 0 or positive too)
        }
        // TODO: Implement cooldown visual/disabling (Req 3.1.7d) -> Handled by updateCommandAvailability
    }

    private void handlePlayCommand() {
        System.out.println("Play button clicked");
        if (!currentPet.play()) {
            // Action failed (likely cooldown)
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't want to play right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(5); // Example score for playing
        }
        // TODO: Implement cooldown visual/disabling (Req 3.1.7e) -> Handled by updateCommandAvailability
    }

    private void handleExerciseCommand() {
        System.out.println("Exercise button clicked");
        currentPet.exercise();
        increaseScore(3); // Example score for exercising
    }

    /** Handles the Save Game button click. Req 3.1.5 */
    private void handleSaveGameCommand() {
        if (currentPet != null && playerInventory != null) {
            String filename = currentPet.getName(); // Use pet name as filename
            if (filename == null || filename.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot save game: Pet name is invalid.", "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Sanitize filename (simple example: replace spaces)
            filename = filename.trim().replaceAll("[^a-zA-Z0-9.-]", "_");

            System.out.println("Attempting to save game for " + currentPet.getName() + " to file: " + filename + ".sav");
            // Use the currentGameState object for saving if available
            GameState stateToSave = (currentGameState != null)
                                  ? currentGameState
                                  : new GameState(currentPet, playerInventory, score);
            // Ensure score is up-to-date in the state object before saving
            // Note: If score was mutable in GameState, we'd call stateToSave.setScore(this.score);
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

    /** Helper method to create and configure command buttons */
    private JButton createCommandButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        return button;
    }

    /** Enables/Disables command buttons based on current pet state (Req 3.1.7) */
    private void updateCommandAvailability() {
        if (currentPet == null) {
            setAllCommandsEnabled(false);
            return;
        }

        PetState state = currentPet.getCurrentState();
        boolean allowAll = false;
        boolean allowHappyActions = false;

        switch (state) {
            case NORMAL:
            case HUNGRY: // All commands allowed in Hungry state too
                allowAll = true;
                allowHappyActions = true;
                break;
            case ANGRY: // Only Give Gift and Play allowed
                allowHappyActions = true;
                break;
            case SLEEPING:
            case DEAD:
            default:
                // No commands allowed
                break;
        }

        // Enable/disable based on flags
        feedButton.setEnabled(allowAll);
        goToBedButton.setEnabled(allowAll);
        vetButton.setEnabled(allowAll && currentPet.isVetAvailable()); // Check cooldown
        exerciseButton.setEnabled(allowAll);

        giveGiftButton.setEnabled(allowHappyActions);
        playButton.setEnabled(allowHappyActions && currentPet.isPlayAvailable()); // Check cooldown

        saveButton.setEnabled(allowAll); // Enable/disable save button with others

        // TODO: Add logic for cooldowns - disable buttons temporarily after use -> Done via checks above
    }

    /** Convenience method to enable/disable all command buttons */
    private void setAllCommandsEnabled(boolean enabled) {
        feedButton.setEnabled(enabled);
        goToBedButton.setEnabled(enabled);
        giveGiftButton.setEnabled(enabled);
        vetButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        exerciseButton.setEnabled(enabled);
        saveButton.setEnabled(enabled); // Enable/disable save button with others
    }

    /** Updates the inventory text area */
    private void updateInventoryDisplay() {
        if (playerInventory == null) {
            inventoryDisplay.setText("Inventory: N/A");
            return;
        }
        Map<Item, Integer> items = playerInventory.getAllItems();
        if (items.isEmpty()) {
            inventoryDisplay.setText(" Inventory is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            items.forEach((item, count) -> {
                sb.append(" - ").append(item.getName()).append(": ").append(count).append("\n");
            });
            inventoryDisplay.setText(sb.toString());
        }
    }

    /** Grants a random item from the available pool to the player. Req 3.1.8 */
    private void grantRandomItem() {
        if (playerInventory != null && !AVAILABLE_ITEMS.isEmpty()) {
            Item grantedItem = AVAILABLE_ITEMS.get(random.nextInt(AVAILABLE_ITEMS.size()));
            playerInventory.addItem(grantedItem, 1);
            System.out.println("Granted item: " + grantedItem.getName());
            // Notify user
            JOptionPane.showMessageDialog(this,
                    "You found an item: " + grantedItem.getName() + "!",
                    "Item Found!",
                    JOptionPane.INFORMATION_MESSAGE);
            updateInventoryDisplay(); // Update UI
        }
    }

    /** Helper method to increase score and update display */
    private void increaseScore(int amount) {
        this.score += amount;
        updatePetStatusDisplay(); // Update display to show new score
        System.out.println("Score changed by " + amount + ". New score: " + this.score); // Logging
    }

    // --- Helper Methods ---

    /** Updates the pet's sprite label based on current state and flip-flop flag */
    private void updateSpriteImage() {
        if (currentPet == null) return;

        String petType = currentPet.getPetType();
        PetState currentState = currentPet.getCurrentState(); // Use lastDisplayedState? No, use current for immediate visual update
        ImageIcon petIcon = loadPetSprite(petType, currentState);

        if (petIcon != null) {
            petSpriteLabel.setIcon(petIcon);
            petSpriteLabel.setText(null); // Remove text if icon is loaded
        } else {
            petSpriteLabel.setIcon(null);
            // Provide more specific feedback if image load fails
            String stateName = (currentState != null) ? currentState.name().toLowerCase() : "unknown";
            petSpriteLabel.setText("[" + petType + "/" + stateName + " not found]");
            petSpriteLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Reset font for error text
        }
    }

     /** Loads an ImageIcon for the specified pet type and state, trying alternate if flipFlop is true */
    private ImageIcon loadPetSprite(String petType, PetState state) {
        if (petType == null || state == null) return null;

        String stateName = state.name().toLowerCase();
        String primaryImagePath = "/images/" + petType + "/" + stateName + ".png";
        String alternateImagePath = "/images/" + petType + "/" + stateName + "_alt.png"; // Convention for alternate

        String pathToAttempt = primaryImagePath; // path to attempt

        // If flipFlop is true, try the alternate path first (Req 3.1.10)
        if (spriteFlipFlop) {
            URL altURL = getClass().getResource(alternateImagePath);
            if (altURL != null) {
                pathToAttempt = alternateImagePath;
            } // else, stick with the primary path
        }

        URL imageURL = getClass().getResource(pathToAttempt);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            // If the attempted path failed, and it was the alternate, try the primary as a fallback
            if (spriteFlipFlop && pathToAttempt.equals(alternateImagePath)) {
                 URL primaryURL = getClass().getResource(primaryImagePath);
                 if (primaryURL != null) {
                     return new ImageIcon(primaryURL);
                 }
            }
            // If primary failed (or alt failed and primary fallback failed)
            System.err.println("Warning: Could not load sprite for " + petType + " state " + stateName + " (tried " + pathToAttempt + ")");
            return null; // Or return a default placeholder ImageIcon
        }
    }

    /** Helper to create and configure a stat progress bar */
    private JProgressBar createStatProgressBar(String label) {
        JProgressBar progressBar = new JProgressBar(0, 100); // Default max 100, updated later
        progressBar.setStringPainted(true);
        progressBar.setBorder(BorderFactory.createTitledBorder(label)); // Use border for label
        // progressBar.setString(label + ": ?/?"); // Initial string set in update method
        return progressBar;
    }

    /** Helper method to update a single stat progress bar and apply warning color */
    private void updateStatBar(JProgressBar bar, String label, int value, int max) {
        bar.setMaximum(max > 0 ? max : 100); // Prevent max value of 0
        bar.setValue(Math.max(0, value)); // Ensure value isn't negative
        bar.setString(String.format("%s: %d / %d", label, bar.getValue(), bar.getMaximum()));

        // Low stat warning (Req 3.1.6)
        if ((double)bar.getValue() / bar.getMaximum() < LOW_STAT_THRESHOLD) {
            bar.setForeground(LOW_STAT_WARNING_COLOR);
        } else {
            bar.setForeground(null); // Reset to default color
        }
    }
} 