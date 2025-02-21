package com.keyin.minigame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AncientTempleMiniGame extends AbstractMiniGame {

    // Lazy initialization fields
    private String wordToGuess;
    private Set<Character> guessedLetters;

    // Maximum allowed wrong guesses
    private final int maxWrongGuesses = 6;
    private int wrongGuesses = 0;

    // GUI components
    private JLabel wordProgressLabel;
    private JLabel demonTauntLabel;
    private JTextArea asciiArtArea;
    private JTextField guessField;
    private JButton guessButton;

    // Taunt messages for wrong and correct guesses
    private final String[] wrongTaunts = {
        "Bael: Ha! You missed that, mortal!",
        "Bael: Is that the best you can do?",
        "Bael: Your puny letters are no match for me!",
        "Bael: Pathetic! Try harder!",
        "Bael: Your guesses are laughable!"
    };
    private final String[] correctTaunts = {
        "Bael: Hmph, you got one... for now.",
        "Bael: Not bad, but don't get cocky!",
        "Bael: A lucky guess, mortal.",
        "Bael: You may have some potential..."
    };

    // Completion callbacks to signal end of mini-game
    protected Runnable onCompleteCallback;
    protected Runnable onFailCallback;

    // Constructor
    public AncientTempleMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
        // Lazy getters will handle initialization of wordToGuess and guessedLetters
    }

    // Lazy getter for wordToGuess; defaults to "UNDERWORLD"
    private String getWordToGuess() {
        if (wordToGuess == null) {
            wordToGuess = "UNDERWORLD";
        }
        return wordToGuess;
    }

    // Lazy getter for guessedLetters; pre-populates with D, R, and O
    private Set<Character> getGuessedLetters() {
        if (guessedLetters == null) {
            guessedLetters = new HashSet<>();
            guessedLetters.add('D');
            guessedLetters.add('R');
            guessedLetters.add('O');
        }
        return guessedLetters;
    }

    // Callback setters
    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    public void setOnFailCallback(Runnable callback) {
        this.onFailCallback = callback;
    }

    @Override
    protected void customizeUI() {
        parentFrame.getContentPane().removeAll();

        JPanel gamePanel = new JPanel(new BorderLayout(10, 10));

        // ASCII Art Display
        asciiArtArea = new JTextArea(getGameSpecificAsciiArt());
        asciiArtArea.setEditable(false);
        asciiArtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        asciiArtArea.setRows(8);
        asciiArtArea.setBackground(parentFrame.getBackground());
        gamePanel.add(new JScrollPane(asciiArtArea), BorderLayout.NORTH);

        // Center panel for word progress and demon taunts
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        wordProgressLabel = new JLabel(getWordProgress(), SwingConstants.CENTER);
        wordProgressLabel.setFont(new Font("Serif", Font.BOLD, 28));
        centerPanel.add(wordProgressLabel);

        demonTauntLabel = new JLabel("Bael: Prepare to be crushed!", SwingConstants.CENTER);
        demonTauntLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        demonTauntLabel.setForeground(Color.RED);
        centerPanel.add(demonTauntLabel);
        gamePanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel: Letter input
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel promptLabel = new JLabel("Enter a letter: ");
        promptLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        guessField = new JTextField(5);
        guessField.setFont(new Font("Serif", Font.PLAIN, 18));
        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(promptLabel);
        inputPanel.add(guessField);
        inputPanel.add(guessButton);
        gamePanel.add(inputPanel, BorderLayout.SOUTH);

        // Attach listeners for guesses
        guessButton.addActionListener(e -> processGuess());
        guessField.addActionListener(e -> processGuess());

        parentFrame.getContentPane().add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        char guessedChar = Character.toUpperCase(e.getKeyChar());
        if (Character.isLetter(guessedChar)) {
            processGuess(guessedChar);
        }
    }

    private void processGuess() {
        String input = guessField.getText().trim().toUpperCase();
        guessField.setText("");
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            JOptionPane.showMessageDialog(parentFrame, "Please enter a single letter.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        processGuess(input.charAt(0));
    }

    private void processGuess(char guessedChar) {
        if (getGuessedLetters().contains(guessedChar)) {
            JOptionPane.showMessageDialog(parentFrame, "You've already guessed that letter!", "Duplicate Guess", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        getGuessedLetters().add(guessedChar);

        if (getWordToGuess().indexOf(guessedChar) >= 0) {
            updateWordProgress();
            updateDemonTaunt(getRandomTaunt(correctTaunts));
        } else {
            wrongGuesses++;
            updateAsciiArt();
            updateDemonTaunt(getRandomTaunt(wrongTaunts));
        }

        // Win condition: All letters guessed
        if (isWordGuessed()) {
            updateDemonTaunt("Bael: No! This cannot be... defeated by mere letters?!");
            // Instead of an immediate dialog, use the dialogBox (as in AncientTempleMiniGame)
            dialogBox.showText("Congratulations! You've defeated Bael by guessing: " + getWordToGuess() +
                    "\nYou earned the Dragon's Peak Plushie!", () -> {
                completeGame();
            });
        }
        // Lose condition: Exceeded wrong guesses
        else if (wrongGuesses >= maxWrongGuesses) {
            updateDemonTaunt("Bael: Hahaha! Your puny guesses are worthless!");
            dialogBox.showText("Game Over! Bael reigns! The word was: " + getWordToGuess() +
                    "\nRestarting mini-game...", () -> {
                if (onFailCallback != null) {
                    onFailCallback.run();
                }
            });
        }
    }

    private void updateWordProgress() {
        wordProgressLabel.setText(getWordProgress());
    }

    private void updateAsciiArt() {
        asciiArtArea.setText(getGameSpecificAsciiArt());
    }

    private void updateDemonTaunt(String message) {
        demonTauntLabel.setText(message);
    }

    private String getRandomTaunt(String[] taunts) {
        return taunts[(int) (Math.random() * taunts.length)];
    }

    private String getWordProgress() {
        String actualWord = getWordToGuess();
        StringBuilder progress = new StringBuilder();
        for (char c : actualWord.toCharArray()) {
            if (getGuessedLetters().contains(c)) {
                progress.append(c).append(" ");
            } else {
                progress.append("_ ");
            }
        }
        return progress.toString().trim();
    }

    private boolean isWordGuessed() {
        String actualWord = getWordToGuess();
        for (char c : actualWord.toCharArray()) {
            if (!getGuessedLetters().contains(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        switch (wrongGuesses) {
            case 0: return " +---+\n |   |\n     |\n     |\n     |\n     |\n=========";
            case 1: return " +---+\n |   |\n O   |\n     |\n     |\n     |\n=========";
            case 2: return " +---+\n |   |\n O   |\n |   |\n     |\n     |\n=========";
            case 3: return " +---+\n |   |\n O   |\n/|   |\n     |\n     |\n=========";
            case 4: return " +---+\n |   |\n O   |\n/|\\  |\n     |\n     |\n=========";
            case 5: return " +---+\n |   |\n O   |\n/|\\  |\n/    |\n     |\n=========";
            case 6: return " +---+\n |   |\n O   |\n/|\\  |\n/ \\  |\n     |\n=========";
            default: return "";
        }
    }

    @Override
    protected String getIntroText() {
        return "Welcome, mortal! You stand before Bael, the fearsome demon dragon of the underworld.\n" +
               "Defeat him in a game of Hangman to banish his evil from these lands!\n" +
               "Every wrong guess makes him laugh at you—so be careful!\n" +
               "Current word: " + getWordProgress();
    }

    @Override
    protected void completeGame() {
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
    }
}