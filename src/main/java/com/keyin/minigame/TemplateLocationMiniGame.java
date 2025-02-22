package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Template for a basic mini-game with extensive comments
 * Replace "TemplateLocation" with your location name (e.g., "MysticForest")
 */
public class TemplateLocationMiniGame extends AbstractMiniGame {
    // This button will complete the mini-game when clicked
    private JButton winButton;

    /**
     * Constructor - Called when the mini-game is created
     * The parameters are passed from MiniGameFactory
     */
    public TemplateLocationMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        // Always call the parent constructor first
        super(locationId, heroId, parentFrame);
    }

    /**
     * This method sets up the user interface for your mini-game
     * It's called automatically when the mini-game starts
     */
    @Override
    protected void customizeUI() {
        // Create a custom panel for your location's theme
        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(Color.BLACK); // Choose a background color that fits your theme

        // Create a title label - Replace with your location name
        JLabel locationLabel = new JLabel("YOUR LOCATION NAME", SwingConstants.CENTER);
        locationLabel.setFont(new Font("Arial", Font.BOLD, 32));
        locationLabel.setForeground(Color.WHITE); // Choose a text color that fits your theme
        // Add a decorative border if you want
        locationLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

        // Add the label to the top of your panel
        customPanel.add(locationLabel, BorderLayout.NORTH);

        // -------------------------------------------------------
        // THIS IS WHERE YOU ADD YOUR MINI-GAME'S VISUAL ELEMENTS
        // -------------------------------------------------------
        // You can create custom graphics, add interactive elements, etc.
        // For example, here's a simple colored panel:
        JPanel gameContentPanel = new JPanel();
        gameContentPanel.setBackground(Color.DARK_GRAY);
        gameContentPanel.setPreferredSize(new Dimension(400, 200));

        // Add your custom game content panel to the center
        customPanel.add(gameContentPanel, BorderLayout.CENTER);

        // Remove the default art panel and add your custom panel
        gamePanel.remove(artPanel);
        gamePanel.add(customPanel, BorderLayout.CENTER);

        // Create a panel for the win button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);

        // Create the win button - This is what players will click to complete the game
        winButton = new JButton("Win Mini Game");
        winButton.setFont(new Font("Arial", Font.BOLD, 16));
        winButton.setBackground(Color.GREEN);
        winButton.setForeground(Color.BLACK);

        // Add the action for when the button is clicked
        winButton.addActionListener(e -> {
            // This is where you put your congratulations text
            // The second parameter is a callback that runs when the player
            // presses a key to continue after reading the text
            dialogBox.showText("This is where you put your complete game congratulations text! " +
                    "Mention the specific plushie they earned.", () -> {
                // This marks the mini-game as complete and returns to the previous screen
                completeGame();
            });
        });

        // Add the button to the panel
        buttonPanel.add(winButton);

        // Add the button panel to the bottom of the screen
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * This method handles keyboard input during the mini-game
     * It's called automatically when a key is pressed
     */
    @Override
    protected void handleKeyPress(KeyEvent e) {
        // This allows the player to press SPACE to win the game
        // Only respond if the dialog box isn't currently showing text
        if (!dialogBox.isTyping() && e.getKeyChar() == ' ') {
            winButton.doClick(); // Simulate clicking the win button
        }

        // You can add additional key handling here for more complex games
    }

    /**
     * This method returns the ASCII art for your location
     * It's used by the base mini-game class, but we're replacing
     * the art panel with our own panel, so this isn't as important
     */
    @Override
    protected String getGameSpecificAsciiArt() {
        // Return a simple string - this won't be displayed with our custom UI
        return "YOUR LOCATION NAME";
    }

    /**
     * This method returns the introductory text for your mini-game
     * It's displayed in the dialog box when the mini-game starts
     */
    @Override
    protected String getIntroText() {
        // This is the first text players will see
        // Introduce your location and any instructions for your mini-game
        return "This is your intro text.\n" +
                "You can explain your location and give instructions here.\n" +
                "Click the 'Win Mini Game' button or press the SPACE key to complete it.";
    }
}