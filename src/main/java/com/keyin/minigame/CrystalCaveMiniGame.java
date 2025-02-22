package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Implementation for Crystal Cave Mini-Game that tests the ASCII art functionality
 */
public class CrystalCaveMiniGame extends AbstractMiniGame {
    private JButton winButton;
    private boolean asciiArtMethodCalled = false;

    public CrystalCaveMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Add a special panel to indicate if getGameSpecificAsciiArt was called
        JPanel testPanel = new JPanel(new BorderLayout());
        testPanel.setBackground(Color.BLACK);

        // Check if artPanel exists and contains the expected ASCII art
        JLabel statusLabel = new JLabel("ASCII Art Method Status: NOT CALLED YET", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.RED);
        statusLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Update in 2 seconds to give time for ASCII art to be set
        Timer timer = new Timer(2000, e -> {
            if (asciiArtMethodCalled) {
                statusLabel.setText("ASCII Art Method Status: CALLED ✓");
                statusLabel.setForeground(Color.GREEN);
            }
        });
        timer.setRepeats(false);
        timer.start();

        testPanel.add(statusLabel, BorderLayout.NORTH);

        // Create the win button
        winButton = new JButton("Win Mini Game");
        winButton.setFont(new Font("Arial", Font.BOLD, 16));
        winButton.addActionListener(e -> {
            dialogBox.showText("Congratulations! You've completed the Crystal Cave challenge!\nYou earned the Crystal Dragon plushie!", () -> {
                completeGame();
            });
        });

        testPanel.add(winButton, BorderLayout.SOUTH);

        // Add a label to display the output of getGameSpecificAsciiArt
        JTextArea asciiOutput = new JTextArea("Waiting for ASCII art...");
        asciiOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        asciiOutput.setBackground(Color.BLACK);
        asciiOutput.setForeground(Color.WHITE);
        asciiOutput.setEditable(false);

        // Update the text area in 1 second
        Timer asciiTimer = new Timer(1000, e -> {
            // Get the actual output from getGameSpecificAsciiArt
            String asciiArt = getGameSpecificAsciiArt();
            asciiOutput.setText("Output from getGameSpecificAsciiArt():\n\n" + asciiArt);
        });
        asciiTimer.setRepeats(false);
        asciiTimer.start();

        testPanel.add(new JScrollPane(asciiOutput), BorderLayout.CENTER);

        // Add the test panel to the game panel
        gamePanel.add(testPanel, BorderLayout.CENTER);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // If any key is pressed and not currently typing, simulate a button press
        if (!dialogBox.isTyping() && e.getKeyChar() == ' ') {
            winButton.doClick();
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        // Set flag to indicate this method was called
        asciiArtMethodCalled = true;

        // Return a distinctive ASCII art to make it clear if it's being displayed
        return
                "╔═══════════════════════════════╗\n" +
                        "║  THIS IS A TEST ASCII ART     ║\n" +
                        "║  IF YOU CAN SEE THIS TEXT     ║\n" +
                        "║  THEN getGameSpecificAsciiArt ║\n" +
                        "║  IS WORKING CORRECTLY!        ║\n" +
                        "╚═══════════════════════════════╝";
    }

    @Override
    protected String getIntroText() {
        return "Welcome to the Crystal Cave...\n" +
                "This version is testing if getGameSpecificAsciiArt() is being called.\n" +
                "Check if the status indicator turns green.";
    }
}