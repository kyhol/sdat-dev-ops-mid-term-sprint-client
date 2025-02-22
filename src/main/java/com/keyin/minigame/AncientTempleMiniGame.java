package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Simplified implementation for Ancient Temple Mini-Game (Location ID 4)
 */
public class AncientTempleMiniGame extends AbstractMiniGame {
    private JButton winButton;

    public AncientTempleMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Create a custom panel with temple theme
        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(new Color(210, 180, 140)); // Tan background for stone

        // Create a title for the temple
        JLabel templeLabel = new JLabel("ANCIENT TEMPLE", SwingConstants.CENTER);
        templeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        templeLabel.setForeground(new Color(139, 69, 19)); // Brown text
        templeLabel.setBorder(BorderFactory.createLineBorder(new Color(160, 82, 45), 3)); // Brown border
        customPanel.add(templeLabel, BorderLayout.NORTH);

        // Create a visual representation of temple pillars
        JPanel templePanel = new JPanel(new GridLayout(1, 5, 10, 10));
        templePanel.setBackground(new Color(210, 180, 140));

        for (int i = 0; i < 5; i++) {
            JPanel pillar = new JPanel(new BorderLayout());
            pillar.setBackground(new Color(210, 180, 140));

            // Create a pillar shape
            JPanel column = new JPanel();
            column.setBackground(new Color(188, 143, 143)); // Rosy brown for stone pillars
            column.setPreferredSize(new Dimension(40, 150));
            column.setBorder(BorderFactory.createBevelBorder(0));

            pillar.add(column, BorderLayout.CENTER);
            templePanel.add(pillar);
        }

        // Create a temple roof
        JPanel roofPanel = new JPanel();
        roofPanel.setBackground(new Color(139, 69, 19)); // Brown roof
        roofPanel.setPreferredSize(new Dimension(100, 50));

        JPanel structurePanel = new JPanel(new BorderLayout());
        structurePanel.setBackground(new Color(210, 180, 140));
        structurePanel.add(roofPanel, BorderLayout.NORTH);
        structurePanel.add(templePanel, BorderLayout.CENTER);

        customPanel.add(structurePanel, BorderLayout.CENTER);

        // Replace the existing art panel
        gamePanel.remove(artPanel);
        gamePanel.add(customPanel, BorderLayout.CENTER);

        // Create a panel for the win button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(210, 180, 140));

        // Create the win button
        winButton = new JButton("Win Mini Game");
        winButton.setFont(new Font("Arial", Font.BOLD, 16));
        winButton.setBackground(new Color(205, 133, 63));
        winButton.setForeground(Color.WHITE);
        winButton.addActionListener(e -> {
            dialogBox.showText("Congratulations! You've completed the Ancient Temple challenge!\nYou earned the Temple Guardian plushie!", () -> {
                completeGame();
            });
        });

        buttonPanel.add(winButton);
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // If space key is pressed and not currently typing, simulate a button press
        if (!dialogBox.isTyping() && e.getKeyChar() == ' ') {
            winButton.doClick();
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return "ANCIENT TEMPLE";
    }

    @Override
    protected String getIntroText() {
        return "Welcome to the Ancient Temple...\n" +
                "Stone pillars reach toward the sky, adorned with mysterious symbols.\n" +
                "Click the 'Win Mini Game' button or press the SPACE key to complete it.";
    }
}