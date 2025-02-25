package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Simplified Crystal Cave Mini-Game
 */
public class CrystalCaveMiniGame extends AbstractMiniGame {
    private JTextField inputField;
    private JButton submitButton;

    public CrystalCaveMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Create input field and button
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.BLACK);

        JLabel promptLabel = new JLabel("Enter frequency:");
        promptLabel.setForeground(Color.WHITE);

        inputField = new JTextField(5);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> checkAnswer());

        inputPanel.add(promptLabel);
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        // Add to dialog box
        dialogBox.add(inputPanel, BorderLayout.NORTH);

        // Set focus to inputField
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkAnswer();
        }
    }

    private void checkAnswer() {
        try {
            int answer = Integer.parseInt(inputField.getText().trim());
            if (answer == 4) {
                dialogBox.showText("The crystal glows! You found the frequency!",
                        () -> completeGame());
            } else {
                dialogBox.showText("Wrong frequency. Try again.", null);
                inputField.setText("");
                inputField.requestFocusInWindow();
            }
        } catch (NumberFormatException ex) {
            dialogBox.showText("Please enter a valid number.", null);
            inputField.setText("");
            inputField.requestFocusInWindow();
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return """
            /\\      /\\      /\\    
           /  \\    /  \\    /  \\   
          /    \\  /    \\  /    \\  
         /      \\/      \\/      \\ 
        /        \\      /        \\
        \\        /      \\        /
         \\      /        \\      / 
          \\    /          \\    /  
           \\  /            \\  /   
            \\/______________\\/    
            """;
    }

    @Override
    protected String getIntroText() {
        return "Find the crystal's resonant frequency.";
    }
}