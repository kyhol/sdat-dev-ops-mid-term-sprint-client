package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Simplified Ancient Temple Mini-Game (Location ID 4) - Kyle TEST
 */
public class AncientTempleMiniGame extends AbstractMiniGame {
    private boolean gameCompleted = false;
    private JLabel statusLabel;

    public AncientTempleMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Add a simple status label
        statusLabel = new JLabel("Press any key to solve the temple puzzle", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(Color.BLACK);
        statusLabel.setOpaque(true);

        // Add the label to the game panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        gamePanel.add(topPanel, BorderLayout.NORTH);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        if (!dialogBox.isTyping() && !gameCompleted) {
            gameCompleted = true;
            statusLabel.setText("Puzzle solved successfully!");
            dialogBox.showText("You've deciphered the ancient temple's secret code! The temple guardian plushie appears before you.", () -> {
                completeGame();
            });
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return
                "      _______      \n" +
                        "     |       |     \n" +
                        "     | TEMPLE|     \n" +
                        "     |_______|     \n" +
                        "      |     |      \n" +
                        "    __|     |__    \n" +
                        "   |           |   \n" +
                        "   |           |   \n" +
                        "   |___________|   \n" +
                        "   |           |   \n" +
                        "   |     |     |   \n" +
                        "   |_____|_____|   \n" +
                        "         |         \n" +
                        "       __|__       \n";
    }

    @Override
    protected String getIntroText() {
        return "You've entered the Ancient Temple...\n" +
                "The walls are covered with mysterious symbols that pulse with otherworldly light.\n" +
                "A voice echoes in your mind: 'To prove your worth, solve the temple's puzzle.'\n" +
                "Press any key to solve the puzzle...";
    }
}