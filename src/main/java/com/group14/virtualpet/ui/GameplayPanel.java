package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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

public class GameplayPanel extends JPanel implements ActionListener {

    private static final int GAME_TICK_MS = 1000; // 1 second per tick
    private static final int ITEM_GRANT_INTERVAL_TICKS = 60; // Grant an item every 60 ticks (~60 seconds)
    private static final Color LOW_STAT_WARNING_COLOR = Color.RED;
    private static final double LOW_STAT_THRESHOLD = 0.25;

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

    private Pet currentPet;
    private Inventory playerInventory;
    private GameState currentGameState;
    private Timer gameTimer;
    private Timer spriteTimer;
    private boolean spriteFlipFlop = false;
    private PetState lastDisplayedState = null;

    private JLabel petNameLabel;
    private JLabel petSpriteLabel;
    private JProgressBar healthBar;
    private JProgressBar sleepBar;
    private JProgressBar fullnessBar;
    private JProgressBar happinessBar;
    private JLabel stateLabel;
    private JButton feedButton;
    private JButton goToBedButton;
    private JButton giveGiftButton;
    private JButton vetButton;
    private JButton playButton;
    private JButton exerciseButton;
    private JButton saveButton;
    private JTextArea inventoryDisplay;
    private JLabel scoreLabel;
    private int score;
    private int ticksSinceLastItemGrant = 0;

    public GameplayPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create the info panel (right side)
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        petNameLabel = new JLabel("Pet Name: ?");
        scoreLabel = new JLabel("Score: 0");
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

        // Create the center panel (pet sprite and inventory)
        JPanel centerPanel = new JPanel(new BorderLayout());
        petSpriteLabel = new JLabel("[Pet Sprite Placeholder]", SwingConstants.CENTER);
        petSpriteLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        petSpriteLabel.setPreferredSize(new Dimension(300, 300));
        centerPanel.add(petSpriteLabel, BorderLayout.CENTER);
        inventoryDisplay = new JTextArea(5, 20);
        inventoryDisplay.setEditable(false);
        inventoryDisplay.setBorder(BorderFactory.createTitledBorder("Inventory"));
        JScrollPane scrollPane = new JScrollPane(inventoryDisplay);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Create the command panel (bottom)
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        feedButton = createCommandButton("Feed");
        goToBedButton = createCommandButton("Go to Bed");
        giveGiftButton = createCommandButton("Give Gift");
        vetButton = createCommandButton("Take to Vet");
        playButton = createCommandButton("Play");
        exerciseButton = createCommandButton("Exercise");
        saveButton = createCommandButton("Save Game");
        commandPanel.add(feedButton);
        commandPanel.add(goToBedButton);
        commandPanel.add(giveGiftButton);
        commandPanel.add(vetButton);
        commandPanel.add(playButton);
        commandPanel.add(exerciseButton);
        commandPanel.add(saveButton);
        add(commandPanel, BorderLayout.SOUTH);

        // Initialize game loop and sprite timers
        gameTimer = new Timer(GAME_TICK_MS, this);
        gameTimer.setInitialDelay(0);
        spriteTimer = new Timer(500, this);
        spriteTimer.setInitialDelay(500);
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
        System.out.println("Starting game with pet: " + pet.getName());
        this.lastDisplayedState = null;
        this.spriteFlipFlop = false;
        updatePetStatusDisplay();
        gameTimer.start();
        spriteTimer.start();
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
        System.out.println("Loading game with pet: " + currentPet.getName() + ", Score: " + score);
        this.ticksSinceLastItemGrant = 0;
        this.lastDisplayedState = null;
        this.spriteFlipFlop = false;
        updatePetStatusDisplay();
        gameTimer.start();
        spriteTimer.start();
    }

    /**
     * Stops the game loop and sprite timer.
     */
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
            System.out.println("Game timer stopped.");
        }
        if (spriteTimer != null) {
            spriteTimer.stop();
            System.out.println("Sprite timer stopped.");
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
                        System.out.println("Parental Time Limit Reached! (" + elapsedMinutes + "/" + limitMinutes + " min)");
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
            } else if (source == saveButton) {
                handleSaveGameCommand();
            }
            updatePetStatusDisplay();
        }
    }

    private void updatePetStatusDisplay() {
        if (currentPet == null) return;
        petNameLabel.setText("Pet Name: " + currentPet.getName());
        scoreLabel.setText("Score: " + score);
        stateLabel.setText("State: " + currentPet.getCurrentState());
        updateStatBar(healthBar, "Health", currentPet.getHealth(), currentPet.getMaxHealth());
        updateStatBar(sleepBar, "Sleep", currentPet.getSleep(), currentPet.getMaxSleep());
        updateStatBar(fullnessBar, "Fullness", currentPet.getFullness(), currentPet.getMaxFullness());
        updateStatBar(happinessBar, "Happiness", currentPet.getHappiness(), currentPet.getMaxHappiness());

        // Update sprite if pet state has changed
        PetState currentState = currentPet.getCurrentState();
        if (currentState != lastDisplayedState) {
            System.out.println("Pet state changed to: " + currentState);
            lastDisplayedState = currentState;
            spriteFlipFlop = false;
            updateSpriteImage();
        }
        updateCommandAvailability();
        updateInventoryDisplay();
    }

    private void handlePetDeath() {
        gameTimer.stop();
        System.out.println("GAME OVER: " + currentPet.getName() + " has died.");
        JOptionPane.showMessageDialog(this,
                currentPet.getName() + " has passed away. Game Over.",
                "Game Over",
                JOptionPane.WARNING_MESSAGE);
    }

    private void handleFeedCommand() {
        System.out.println("Feed button clicked");
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<FoodItem, Integer> foodItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof FoodItem)
                .collect(Collectors.toMap(entry -> (FoodItem) entry.getKey(), Map.Entry::getValue));

        if (foodItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any food items!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FoodItem[] choices = foodItems.keySet().toArray(new FoodItem[0]);
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
            System.out.println("Attempting to feed: " + selectedFood.getName());
            if (playerInventory.removeItem(selectedFood)) {
                currentPet.feed(selectedFood);
                System.out.println(currentPet.getName() + " ate " + selectedFood.getName() + ".");
                increaseScore(10);
            } else {
                System.err.println("Error consuming food item from inventory!");
            }
        }
    }

    private void handleGoToBedCommand() {
        System.out.println("Go to Bed button clicked");
        currentPet.goToBed();
    }

    private void handleGiveGiftCommand() {
        System.out.println("Give Gift button clicked");
        Map<Item, Integer> allItems = playerInventory.getAllItems();
        Map<GiftItem, Integer> giftItems = allItems.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof GiftItem)
                .collect(Collectors.toMap(entry -> (GiftItem) entry.getKey(), Map.Entry::getValue));

        if (giftItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any gifts!", "Inventory Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GiftItem[] choices = giftItems.keySet().toArray(new GiftItem[0]);
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
            System.out.println("Attempting to give gift: " + selectedGift.getName());
            if (playerInventory.removeItem(selectedGift)) {
                currentPet.receiveGift(selectedGift);
                System.out.println(currentPet.getName() + " received " + selectedGift.getName() + "!");
                increaseScore(15);
            } else {
                System.err.println("Error consuming gift item from inventory!");
            }
        }
    }

    private void handleVetCommand() {
        System.out.println("Vet button clicked");
        if (!currentPet.takeToVet()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't need the vet right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(-5);
        }
    }

    private void handlePlayCommand() {
        System.out.println("Play button clicked");
        if (!currentPet.play()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't want to play right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(5);
        }
    }

    private void handleExerciseCommand() {
        System.out.println("Exercise button clicked");
        currentPet.exercise();
        increaseScore(3);
    }

    private void handleSaveGameCommand() {
        if (currentPet != null && playerInventory != null) {
            String filename = currentPet.getName();
            if (filename == null || filename.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot save game: Pet name is invalid.", "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            filename = filename.trim().replaceAll("[^a-zA-Z0-9.-]", "_");
            System.out.println("Attempting to save game for " + currentPet.getName() + " to file: " + filename + ".sav");
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

    private JButton createCommandButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        return button;
    }

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
            case HUNGRY:
                allowAll = true;
                allowHappyActions = true;
                break;
            case ANGRY:
                allowHappyActions = true;
                break;
            case SLEEPING:
            case DEAD:
            default:
                break;
        }
        feedButton.setEnabled(allowAll);
        goToBedButton.setEnabled(allowAll);
        vetButton.setEnabled(allowAll && currentPet.isVetAvailable());
        exerciseButton.setEnabled(allowAll);
        giveGiftButton.setEnabled(allowHappyActions);
        playButton.setEnabled(allowHappyActions && currentPet.isPlayAvailable());
        saveButton.setEnabled(allowAll);
    }

    private void setAllCommandsEnabled(boolean enabled) {
        feedButton.setEnabled(enabled);
        goToBedButton.setEnabled(enabled);
        giveGiftButton.setEnabled(enabled);
        vetButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        exerciseButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
    }

    private void updateInventoryDisplay() {
        if (playerInventory == null) {
            inventoryDisplay.setText("Inventory: N/A");
            return;
        }
        Map<Item, Integer> items = playerInventory.getAllItems();
        if (items.isEmpty()) {
            inventoryDisplay.setText("Inventory is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            items.forEach((item, count) -> sb.append(" - ").append(item.getName()).append(": ").append(count).append("\n"));
            inventoryDisplay.setText(sb.toString());
        }
    }

    private void grantRandomItem() {
        if (playerInventory != null && !AVAILABLE_ITEMS.isEmpty()) {
            Item grantedItem = AVAILABLE_ITEMS.get(random.nextInt(AVAILABLE_ITEMS.size()));
            playerInventory.addItem(grantedItem, 1);
            System.out.println("Granted item: " + grantedItem.getName());
            JOptionPane.showMessageDialog(this,
                    "You found an item: " + grantedItem.getName() + "!",
                    "Item Found!",
                    JOptionPane.INFORMATION_MESSAGE);
            updateInventoryDisplay();
        }
    }

    private void increaseScore(int amount) {
        this.score += amount;
        updatePetStatusDisplay();
        System.out.println("Score changed by " + amount + ". New score: " + this.score);
    }

    private void updateSpriteImage() {
        if (currentPet == null) return;
        String petType = currentPet.getPetType();
        PetState currentState = currentPet.getCurrentState();
        ImageIcon petIcon = loadPetSprite(petType, currentState);
        if (petIcon != null) {
            petSpriteLabel.setIcon(petIcon);
            petSpriteLabel.setText(null);
        } else {
            petSpriteLabel.setIcon(null);
            String stateName = (currentState != null) ? currentState.name().toLowerCase() : "unknown";
            petSpriteLabel.setText("[" + petType + "/" + stateName + " not found]");
            petSpriteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        }
    }

    private ImageIcon loadPetSprite(String petType, PetState state) {
        if (petType == null || state == null) return null;
        String stateName = state.name().toLowerCase();
        String primaryImagePath = "/images/" + petType + "/" + stateName + ".png";
        String alternateImagePath = "/images/" + petType + "/" + stateName + "_alt.png";
        String pathToAttempt = primaryImagePath;
        if (spriteFlipFlop) {
            URL altURL = getClass().getResource(alternateImagePath);
            if (altURL != null) {
                pathToAttempt = alternateImagePath;
            }
        }
        URL imageURL = getClass().getResource(pathToAttempt);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            if (spriteFlipFlop && pathToAttempt.equals(alternateImagePath)) {
                URL primaryURL = getClass().getResource(primaryImagePath);
                if (primaryURL != null) {
                    return new ImageIcon(primaryURL);
                }
            }
            System.err.println("Warning: Could not load sprite for " + petType + " state " + stateName + " (tried " + pathToAttempt + ")");
            return null;
        }
    }

    private JProgressBar createStatProgressBar(String label) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setBorder(BorderFactory.createTitledBorder(label));
        return progressBar;
    }

    private void updateStatBar(JProgressBar bar, String label, int value, int max) {
        bar.setMaximum(max > 0 ? max : 100);
        bar.setValue(Math.max(0, value));
        bar.setString(String.format("%s: %d / %d", label, bar.getValue(), bar.getMaximum()));
        if ((double) bar.getValue() / bar.getMaximum() < LOW_STAT_THRESHOLD) {
            bar.setForeground(LOW_STAT_WARNING_COLOR);
        } else {
            bar.setForeground(null);
        }
    }
}
