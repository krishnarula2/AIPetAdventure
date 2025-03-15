package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

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
    private static final int ITEM_GRANT_INTERVAL_TICKS = 15; //  (~15 seconds)
    private static final double LOW_STAT_THRESHOLD = 0.25;
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);      // Bootstrap primary blue
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);       // Bootstrap success green
    private static final Color DANGER_COLOR = new Color(220, 53, 69);        // Bootstrap danger red
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);       // Bootstrap light
    private static final Color DARK_COLOR = new Color(33, 37, 41);           // Bootstrap dark

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
    private final Timer gameTimer;
    private final Timer spriteTimer;
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
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setOpaque(true);
        setBackground(new Color(240, 242, 245));

        // Create the info panel (right side)
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.EAST);

        // Create the center panel (pet sprite and inventory)
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Create the command panel (bottom)
        JPanel commandPanel = createCommandPanel();
        add(commandPanel, BorderLayout.SOUTH);

        // Initialize game loop and sprite timers
        gameTimer = new Timer(GAME_TICK_MS, this);
        gameTimer.setInitialDelay(0);
        spriteTimer = new Timer(500, this);
        spriteTimer.setInitialDelay(500);
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new RoundedPanel(15, LIGHT_COLOR, null);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.setPreferredSize(new Dimension(250, 0));
        
        // Pet name with stylish font
        petNameLabel = new JLabel("Pet Name: ?");
        petNameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        petNameLabel.setForeground(DARK_COLOR);
        petNameLabel.setAlignmentX(LEFT_ALIGNMENT);
        petNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Score with stylish font
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(PRIMARY_COLOR);
        scoreLabel.setAlignmentX(LEFT_ALIGNMENT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Create status panel with titled border
        JPanel statusPanel = new RoundedPanel(10, Color.WHITE, null);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Pet Status");
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        titledBorder.setTitleColor(DARK_COLOR);
        
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statusPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Create each stat bar with proper spacing
        healthBar = createStatProgressBar("Health");
        healthBar.setAlignmentX(LEFT_ALIGNMENT);
        
        sleepBar = createStatProgressBar("Sleep");
        sleepBar.setAlignmentX(LEFT_ALIGNMENT);
        
        fullnessBar = createStatProgressBar("Fullness");
        fullnessBar.setAlignmentX(LEFT_ALIGNMENT);
        
        happinessBar = createStatProgressBar("Happiness");
        happinessBar.setAlignmentX(LEFT_ALIGNMENT);
        
        stateLabel = new JLabel("State: ?");
        stateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        stateLabel.setForeground(DARK_COLOR);
        stateLabel.setAlignmentX(LEFT_ALIGNMENT);
        stateLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        // Add components to status panel
        statusPanel.add(healthBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(sleepBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(fullnessBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(happinessBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(stateLabel);
        
        // Add components to info panel
        infoPanel.add(petNameLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(statusPanel);
        
        return infoPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        centerPanel.setOpaque(false); // Keep center panel transparent

        // Create a colorful background panel for the pet sprite
        JPanel spriteBackgroundPanel = new ColorfulBackgroundPanel();
        spriteBackgroundPanel.setLayout(new BorderLayout());
        
        // Pet sprite display label (added to the background panel)
        petSpriteLabel = new JLabel();
        petSpriteLabel.setOpaque(false); // Make the label transparent
        petSpriteLabel.setHorizontalAlignment(JLabel.CENTER); // Center horizontally
        petSpriteLabel.setVerticalAlignment(JLabel.CENTER);   // Center vertically
        // Removed font/color settings here, as the icon will override text

        // Add the pet sprite label to the background panel
        spriteBackgroundPanel.add(petSpriteLabel, BorderLayout.CENTER);

        // Inventory panel
        JPanel inventoryPanel = new RoundedPanel(15, Color.WHITE, null);
        inventoryPanel.setLayout(new BorderLayout());
        
        TitledBorder inventoryBorder = BorderFactory.createTitledBorder("Inventory");
        inventoryBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        inventoryBorder.setTitleColor(DARK_COLOR);
        
        inventoryPanel.setBorder(BorderFactory.createCompoundBorder(
            inventoryBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        inventoryDisplay = new JTextArea("Inventory is empty.");
        inventoryDisplay.setEditable(false);
        inventoryDisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inventoryDisplay.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(inventoryDisplay);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 120));
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);

        // Add components directly to center panel
        centerPanel.add(spriteBackgroundPanel, BorderLayout.CENTER); // Add sprite background panel
        centerPanel.add(inventoryPanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createCommandPanel() {
        // Create a rounded panel with gradient background
        JPanel commandPanel = new RoundedPanel(15, LIGHT_COLOR, null);
        commandPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
        commandPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        feedButton = new JButton("Feed");
        feedButton.setBackground(new Color(40, 167, 69));
        feedButton.setForeground(isColorBright(new Color(40, 167, 69)) ? DARK_COLOR : Color.WHITE);
        feedButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        feedButton.setFocusPainted(false);
        feedButton.setBorderPainted(false);
        feedButton.setContentAreaFilled(false);
        feedButton.setOpaque(true);
        feedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        feedButton.addActionListener(this);
        
        goToBedButton = new JButton("Go to Bed");
        goToBedButton.setBackground(new Color(0, 123, 255));
        goToBedButton.setForeground(isColorBright(new Color(0, 123, 255)) ? DARK_COLOR : Color.WHITE);
        goToBedButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goToBedButton.setFocusPainted(false);
        goToBedButton.setBorderPainted(false);
        goToBedButton.setContentAreaFilled(false);
        goToBedButton.setOpaque(true);
        goToBedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToBedButton.addActionListener(this);
        
        giveGiftButton = new JButton("Give Gift");
        giveGiftButton.setBackground(new Color(255, 193, 7));
        giveGiftButton.setForeground(isColorBright(new Color(255, 193, 7)) ? DARK_COLOR : Color.WHITE);
        giveGiftButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        giveGiftButton.setFocusPainted(false);
        giveGiftButton.setBorderPainted(false);
        giveGiftButton.setContentAreaFilled(false);
        giveGiftButton.setOpaque(true);
        giveGiftButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        giveGiftButton.addActionListener(this);
        
        vetButton = new JButton("Take to Vet");
        vetButton.setBackground(new Color(108, 117, 125));
        vetButton.setForeground(isColorBright(new Color(108, 117, 125)) ? DARK_COLOR : Color.WHITE);
        vetButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        vetButton.setFocusPainted(false);
        vetButton.setBorderPainted(false);
        vetButton.setContentAreaFilled(false);
        vetButton.setOpaque(true);
        vetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        vetButton.addActionListener(this);
        
        playButton = new JButton("Play");
        playButton.setBackground(new Color(23, 162, 184));
        playButton.setForeground(isColorBright(new Color(23, 162, 184)) ? DARK_COLOR : Color.WHITE);
        playButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setOpaque(true);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.addActionListener(this);
        
        exerciseButton = new JButton("Exercise");
        exerciseButton.setBackground(new Color(111, 66, 193));
        exerciseButton.setForeground(isColorBright(new Color(111, 66, 193)) ? DARK_COLOR : Color.WHITE);
        exerciseButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exerciseButton.setFocusPainted(false);
        exerciseButton.setBorderPainted(false);
        exerciseButton.setContentAreaFilled(false);
        exerciseButton.setOpaque(true);
        exerciseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exerciseButton.addActionListener(this);
        
        saveButton = new JButton("Save Game");
        saveButton.setBackground(new Color(52, 58, 64));
        saveButton.setForeground(isColorBright(new Color(52, 58, 64)) ? DARK_COLOR : Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setOpaque(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(this);
        
        commandPanel.add(feedButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(goToBedButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(giveGiftButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(vetButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(playButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(exerciseButton);
        commandPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        commandPanel.add(saveButton);
        
        return commandPanel;
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
            this.repaint(); // Ensure the panel repaints after UI updates
        }
    }

    private void updatePetStatusDisplay() {
        if (currentPet == null) return;
    
        petNameLabel.setText("Pet Name: " + currentPet.getName());
        scoreLabel.setText("Score: " + score);
        stateLabel.setText("State: " + currentPet.getCurrentState());
    
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
    
        updateStatBar(healthBar, currentPet.getHealth(), currentPet.getMaxHealth());
        updateStatBar(sleepBar, currentPet.getSleep(), currentPet.getMaxSleep());
        updateStatBar(fullnessBar, currentPet.getFullness(), currentPet.getMaxFullness());
        updateStatBar(happinessBar, currentPet.getHappiness(), currentPet.getMaxHappiness());
    
        // Update sprite if pet state has changed
        PetState currentState = currentPet.getCurrentState();
        if (currentState != lastDisplayedState) {
            lastDisplayedState = currentState;
            spriteFlipFlop = false;
            updateSpriteImage();
        }
    
        updateCommandAvailability();
        updateInventoryDisplay();
    }
    
    private void handlePetDeath() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(this,
                currentPet.getName() + " has passed away. Game Over.",
                "Game Over",
                JOptionPane.WARNING_MESSAGE);
    }

    private void handleFeedCommand() {
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

    private void handleGoToBedCommand() {
        currentPet.goToBed();
    }

    private void handleGiveGiftCommand() {
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

    private void handleVetCommand() {
        if (!currentPet.takeToVet()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't need the vet right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(-5);
        }
    }

    private void handlePlayCommand() {
        if (!currentPet.play()) {
            JOptionPane.showMessageDialog(this, currentPet.getName() + " doesn't want to play right now (cooldown).", "Command Unavailable", JOptionPane.INFORMATION_MESSAGE);
        } else {
            increaseScore(5);
        }
    }

    private void handleExerciseCommand() {
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

    private JProgressBar createStatProgressBar(String label) {
        JProgressBar progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Draw the progress
                    int width = (int) (getWidth() * ((double) getValue() / getMaximum()));
                    
                    // Determine color based on value
                    if ((double) getValue() / getMaximum() < LOW_STAT_THRESHOLD) {
                        g2.setColor(DANGER_COLOR);
                    } else {
                        g2.setColor(SUCCESS_COLOR);
                    }
                    
                    g2.fillRoundRect(0, 0, width, getHeight(), 10, 10);
                    
                    // Draw the string
                    if (isStringPainted()) {
                        g2.setColor(Color.WHITE);
                        String text = getString();
                        int textWidth = g2.getFontMetrics().stringWidth(text);
                        int textHeight = g2.getFontMetrics().getHeight();
                        g2.drawString(text, (getWidth() - textWidth) / 2,
                            (getHeight() + textHeight) / 2 - 2);
                    }
                    
                    g2.dispose();
                }
            }
        };
        
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Create a TitledBorder instead of a regular Border
        TitledBorder titledBorder = BorderFactory.createTitledBorder(label);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        titledBorder.setTitleColor(DARK_COLOR);
        
        progressBar.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        progressBar.setPreferredSize(new Dimension(200, 50));
        
        return progressBar;
    }

    private void updateStatBar(JProgressBar bar, int value, int max) {
        bar.setMaximum(max > 0 ? max : 100);
        bar.setValue(Math.max(0, value));
        bar.setString(String.format("%d / %d", bar.getValue(), bar.getMaximum()));
        
        // The color is now handled in the custom paintComponent method
        bar.repaint();
    }

    private void updateCommandAvailability() {
        if (currentPet == null) {
            setAllCommandsEnabled(false);
            // Even if there's no pet, we still want to enable the save button
            saveButton.setEnabled(true);
            return;
        }
        
        PetState state = currentPet.getCurrentState();
        
        // Disable all buttons first
        setAllCommandsEnabled(false);
        
        // Save button is always enabled
        saveButton.setEnabled(true);
        
        // Enable buttons based on pet state using rule-based switch
        switch (state) {
            case NORMAL, HUNGRY -> {
                // All commands available
                feedButton.setEnabled(true);
                goToBedButton.setEnabled(true);
                giveGiftButton.setEnabled(true);
                vetButton.setEnabled(currentPet.isVetAvailable());
                playButton.setEnabled(currentPet.isPlayAvailable());
                exerciseButton.setEnabled(true);
            }
            case ANGRY -> {
                // Only Give Gift and Play available
                giveGiftButton.setEnabled(true);
                playButton.setEnabled(currentPet.isPlayAvailable());
            }
            case SLEEPING, DEAD -> {
                // No commands available
                // All buttons remain disabled
            }
        }
    }

    private void setAllCommandsEnabled(boolean enabled) {
        feedButton.setEnabled(enabled);
        goToBedButton.setEnabled(enabled);
        giveGiftButton.setEnabled(enabled);
        vetButton.setEnabled(enabled && currentPet != null && currentPet.isVetAvailable());
        playButton.setEnabled(enabled && currentPet != null && currentPet.isPlayAvailable());
        exerciseButton.setEnabled(enabled);
        // Save button is handled separately in updateCommandAvailability
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
            updateInventoryDisplay();
        }
    }
    

    private void increaseScore(int amount) {
        this.score += amount;
        updatePetStatusDisplay();
    }

    private void updateSpriteImage() {
        if (currentPet == null) return;
        String petType = currentPet.getPetType();
        PetState currentState = currentPet.getCurrentState();
        ImageIcon originalIcon = loadPetSprite(petType, currentState);

        if (originalIcon != null) {
            // --- Scaling Logic --- Start
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            int maxWidth = 300; // Max desired width
            int maxHeight = 300; // Max desired height
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            // Calculate scaling factor to fit within bounds while maintaining aspect ratio
            if (originalWidth > maxWidth) {
                double ratio = (double) maxWidth / originalWidth;
                newWidth = maxWidth;
                newHeight = (int) (originalHeight * ratio);
            }

            if (newHeight > maxHeight) {
                double ratio = (double) maxHeight / newHeight;
                newHeight = maxHeight;
                newWidth = (int) (newWidth * ratio); // Adjust width based on height scaling
            }

            // Scale the image
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            petSpriteLabel.setIcon(scaledIcon); // Set the scaled icon
             // --- Scaling Logic --- End
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER); // Center the icon
            petSpriteLabel.setVerticalAlignment(JLabel.CENTER);
        } else {
            petSpriteLabel.setIcon(null); // Clear icon if loading failed
            petSpriteLabel.setText("[Sprite Error]"); 
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    private ImageIcon loadPetSprite(String petType, PetState state) {
        if (petType == null || state == null) return null;
    
        // Convert PetState enum (e.g. ANGRY, DEAD, HUNGRY) to a capitalized string ("Angry", "Dead", "Hungry")
        // For NORMAL, we handle separately below.
        String capitalizedState = state.name().substring(0, 1) 
                                  + state.name().substring(1).toLowerCase();
    
        // If the pet is in NORMAL state, the file is "<petType>.png"
        // Otherwise, the file is "<petType>_<CapitalizedState>.png"
        String fileName;
        if (state == PetState.NORMAL) {
            fileName = petType + ".png"; 
        } else {
            fileName = petType + "_" + capitalizedState + ".png";
        }
    
        // Now build the full resource path. 
        String resourcePath = "/images/pets/" + petType + "/" + fileName;
    
        // Attempt to load the image
        URL imageURL = getClass().getResource(resourcePath);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            return null;
        }
    }
    
    private boolean isColorBright(Color color) {
        double brightness = (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255;
        return brightness > 0.5;
    }
    
    // Custom rounded panel class
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;
        private final Color gradientColor;
        
        public RoundedPanel(int radius, Color bgColor, Color gradColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.gradientColor = gradColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // First create the rounded rectangle shape
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // If we have a gradient color, create a gradient paint
            if (gradientColor != null) {
                // Create gradient paint if gradient color is provided
                GradientPaint gp = new GradientPaint(
                    0, 0, backgroundColor, 
                    0, getHeight(), gradientColor
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            }
            
            g2.dispose();
        }
    }
    
    // Inner class for creating a colorful background behind the pet sprite
    private class ColorfulBackgroundPanel extends JPanel {
        private final Color[] colors = {
            new Color(255, 192, 203), // Pink
            new Color(173, 216, 230), // Light Blue
            new Color(152, 251, 152), // Pale Green
            new Color(255, 255, 153)  // Light Yellow
        };
        
        private final float[] fractions = {0.0f, 0.33f, 0.66f, 1.0f};
        private final long startTime;
        private final Timer animationTimer;
        
        public ColorfulBackgroundPanel() {
            setOpaque(false);
            startTime = System.currentTimeMillis();
            
            // Create animation timer to repaint the panel periodically
            animationTimer = new Timer(50, e -> repaint());
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g.create();
            
            int width = getWidth();
            int height = getHeight();
            
            // Calculate time-based offset for animation
            long elapsed = System.currentTimeMillis() - startTime;
            float offset = (elapsed % 5000) / 5000.0f;
            
            // Create a circular radial gradient that shifts colors over time
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;
            float radius = Math.max(width, height) * 0.7f;
            
            // Shift the colors based on time
            Color[] shiftedColors = new Color[colors.length];
            for (int i = 0; i < colors.length; i++) {
                int index = (i + (int)(offset * colors.length)) % colors.length;
                shiftedColors[i] = colors[index];
            }
            
            // Create radial gradient paint
            RadialGradientPaint paint = new RadialGradientPaint(
                centerX, centerY, radius,
                fractions, shiftedColors,
                MultipleGradientPaint.CycleMethod.REFLECT
            );
            
            g2d.setPaint(paint);
            
            // Draw a rounded rectangle with the gradient
            int arcSize = 30;
            g2d.fillRoundRect(0, 0, width, height, arcSize, arcSize);
            
            g2d.dispose();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Draw a solid background color
        g.setColor(new Color(240, 242, 245));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Call superclass method
        super.paintComponent(g);
    }
}
