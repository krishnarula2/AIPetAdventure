/**
 * Panel displaying the virtual pet's inventory.
 * Allows the player to view and manage items in the pet's inventory.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Item;

/**
 * Panel that displays the player's inventory.
 */
public class InventoryPanel extends JPanel {
    
    private JTextArea inventoryDisplay;
    
    public InventoryPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create titled border
        TitledBorder inventoryBorder = BorderFactory.createTitledBorder("Inventory");
        inventoryBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        inventoryBorder.setTitleColor(GameplayPanel.DARK_COLOR);
        
        setBorder(BorderFactory.createCompoundBorder(
            inventoryBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create inventory text display
        inventoryDisplay = new JTextArea("Inventory is empty.");
        inventoryDisplay.setEditable(false);
        inventoryDisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inventoryDisplay.setBackground(Color.WHITE);
        
        // Add inventory display to a scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryDisplay);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 120));
        
        // Add scroll pane to panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Updates the inventory display based on the current inventory.
     * 
     * @param inventory The player's inventory
     */
    public void updateInventoryDisplay(Inventory inventory) {
        if (inventory == null) {
            inventoryDisplay.setText("Inventory: N/A");
            return;
        }
        
        Map<Item, Integer> items = inventory.getAllItems();
        if (items.isEmpty()) {
            inventoryDisplay.setText("Inventory is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            items.forEach((item, count) -> 
                sb.append(" - ").append(item.getName()).append(": ").append(count).append("\n")
            );
            inventoryDisplay.setText(sb.toString());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        g2.dispose();
        
        super.paintComponent(g);
    }
} 