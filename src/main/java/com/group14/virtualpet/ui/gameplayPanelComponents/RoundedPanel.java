package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * Custom JPanel with rounded corners and optional gradient.
 */
public class RoundedPanel extends JPanel {
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