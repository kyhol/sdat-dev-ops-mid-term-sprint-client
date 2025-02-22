package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Simplified implementation for Shadow Valley Mini-Game (Location ID 6)
 */
public class ShadowValleyMiniGame extends AbstractMiniGame {
    private JButton winButton;

    public ShadowValleyMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Create a custom panel with shadow theme
        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(Color.BLACK);

        // Create a title for the valley
        JLabel valleyLabel = new JLabel("SHADOW VALLEY", SwingConstants.CENTER);
        valleyLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valleyLabel.setForeground(new Color(138, 43, 226)); // Purple text
        valleyLabel.setBorder(BorderFactory.createLineBorder(new Color(75, 0, 130), 3)); // Dark purple border
        customPanel.add(valleyLabel, BorderLayout.NORTH);

        // Create a visual representation of shadows
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Fill with dark background
                g2d.setColor(new Color(25, 25, 25)); // Very dark gray
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw some shadow silhouettes
                g2d.setColor(new Color(10, 10, 10)); // Nearly black

                // Draw hills
                g2d.fillOval(-100, getHeight()-50, 300, 100);
                g2d.fillOval(getWidth()-200, getHeight()-70, 300, 100);
                g2d.fillOval(getWidth()/2-100, getHeight()-60, 200, 100);

                // Draw eerie trees
                for (int i = 0; i < 5; i++) {
                    int x = i * getWidth()/5 + 20;
                    int y = getHeight() - 80;
                    int width = 10;
                    int height = 100;

                    // Tree trunk
                    g2d.setColor(new Color(25, 25, 25)); // Very dark gray
                    g2d.fillRect(x, y-height, width, height);

                    // Tree branches
                    g2d.drawLine(x, y-height+20, x-20, y-height-10);
                    g2d.drawLine(x+width, y-height+20, x+width+20, y-height-10);
                    g2d.drawLine(x, y-height+40, x-15, y-height+20);
                    g2d.drawLine(x+width, y-height+40, x+width+15, y-height+20);
                }

                // Draw glowing eyes
                g2d.setColor(new Color(138, 43, 226)); // Purple
                g2d.fillOval(100, 150, 15, 8);
                g2d.fillOval(130, 150, 15, 8);

                g2d.fillOval(getWidth()-150, 120, 15, 8);
                g2d.fillOval(getWidth()-120, 120, 15, 8);

                g2d.fillOval(getWidth()/2-50, 180, 15, 8);
                g2d.fillOval(getWidth()/2-20, 180, 15, 8);

                // Draw mysterious mist
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(new Color(128, 0, 128)); // Purple

                for (int i = 0; i < 5; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    int width = (int)(Math.random() * 100) + 50;
                    int height = (int)(Math.random() * 30) + 10;

                    g2d.fillOval(x, y, width, height);
                }
            }
        };

        customPanel.add(shadowPanel, BorderLayout.CENTER);

        // Replace the existing art panel
        gamePanel.remove(artPanel);
        gamePanel.add(customPanel, BorderLayout.CENTER);

        // Create a panel for the win button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);

        // Create the win button
        winButton = new JButton("Win Mini Game");
        winButton.setFont(new Font("Arial", Font.BOLD, 16));
        winButton.setBackground(new Color(138, 43, 226));
        winButton.setForeground(Color.WHITE);
        winButton.addActionListener(e -> {
            dialogBox.showText("Congratulations! You've completed the Shadow Valley challenge!\nYou earned the Shadow Cat plushie!", () -> {
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
        return "SHADOW VALLEY";
    }

    @Override
    protected String getIntroText() {
        return "Welcome to Shadow Valley...\n" +
                "Darkness surrounds you, and eerie shapes move at the edge of your vision.\n" +
                "Click the 'Win Mini Game' button or press the SPACE key to complete it.";
    }
}