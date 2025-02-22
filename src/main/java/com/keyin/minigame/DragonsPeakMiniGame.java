package com.keyin.minigame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * DragonsPeakMiniGame - Hangman-style mini-game for the word "UNDERWORLD".
 * 
 * Features:
 *  - Must guess the entire word (no "Win" button).
 *  - Straight game over if you run out of guesses -> triggers onFailCallback,
 *    which can call `restartGame()` in GameInterfaceGUI (or do whatever).
 *  - "Roar" animation on correct guesses.
 *  - "Rage" animation if you guess wrong twice in a row.
 *  - Plushie only awarded if you fully guess the word (onCompleteCallback).
 * 
 *  Now also overrides `startGame()` to reset everything each time
 *  it's called, guaranteeing a fresh puzzle on re-entry.
 */
public class DragonsPeakMiniGame extends AbstractMiniGame {

    private String wordToGuess;
    private Set<Character> guessedLetters;
    private int wrongGuesses = 0;
    private final int maxWrongGuesses = 6;

    // Track if the game was truly won
    private boolean wonGame = false;

    // This will let us do a "rage" animation if the player misses multiple times in a row
    private int consecutiveWrongGuesses = 0;

    private JLabel wordProgressLabel;
    private JLabel demonTauntLabel;
    private JTextArea asciiArtArea;
    private JTextField guessField;
    private JLabel storyLabel;

    private Timer animationTimer;
    private JButton guessButton;

    // Taunts for correct/wrong guesses
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

    // Extra "rage" taunts if the dragon gets furious
    private final String[] rageTaunts = {
        "Bael: You dare continue this insolence?!",
        "Bael: My fury will consume you!",
        "Bael: RAAAAAAHHHH!!!",
        "Bael: I'll scorch the very ground you stand on!"
    };

    /**
     * Constructor
     */
    public DragonsPeakMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Do initial setups
        wordToGuess = "UNDERWORLD";
        guessedLetters = new HashSet<>();

        // Main container
        JPanel gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(Color.BLACK);

        // Title
        JLabel titleLabel = new JLabel("DRAGON'S PEAK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        gamePanel.add(titleLabel, BorderLayout.NORTH);

        // ASCII text area on the left
        asciiArtArea = new JTextArea(getGameSpecificAsciiArt());
        asciiArtArea.setEditable(false);
        asciiArtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        asciiArtArea.setBackground(Color.BLACK);
        asciiArtArea.setForeground(Color.WHITE);
        gamePanel.add(new JScrollPane(asciiArtArea), BorderLayout.WEST);

        // Center panel with word progress, demon taunt, storyline
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setBackground(Color.BLACK);

        wordProgressLabel = new JLabel(getWordProgress(), SwingConstants.CENTER);
        wordProgressLabel.setFont(new Font("Serif", Font.BOLD, 28));
        wordProgressLabel.setForeground(Color.WHITE);
        centerPanel.add(wordProgressLabel);

        demonTauntLabel = new JLabel("Bael: Prepare to be crushed!", SwingConstants.CENTER);
        demonTauntLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        demonTauntLabel.setForeground(Color.RED);
        centerPanel.add(demonTauntLabel);

        storyLabel = new JLabel("You feel the searing heat of Bael's breath...", SwingConstants.CENTER);
        storyLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        storyLabel.setForeground(Color.ORANGE);
        centerPanel.add(storyLabel);

        gamePanel.add(centerPanel, BorderLayout.CENTER);

        // Input panel (guess field + button)
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(Color.BLACK);

        JLabel promptLabel = new JLabel("Enter a letter: ");
        promptLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        promptLabel.setForeground(Color.WHITE);

        guessField = new JTextField(5);
        guessField.setFont(new Font("Serif", Font.PLAIN, 18));

        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Serif", Font.PLAIN, 18));

        guessButton.addActionListener(e -> processGuess());
        guessField.addActionListener(e -> processGuess());

        inputPanel.add(promptLabel);
        inputPanel.add(guessField);
        inputPanel.add(guessButton);

        gamePanel.add(inputPanel, BorderLayout.SOUTH);

        // We remove the default art panel from AbstractMiniGame and add our custom layout
        this.gamePanel.remove(artPanel);
        this.gamePanel.add(gamePanel, BorderLayout.CENTER);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // If you'd like a key-based skip or other action, do it here.
    }

    /**
     * Called whenever the user clicks "Guess" or presses Enter in guessField.
     */
    private void processGuess() {
        String input = guessField.getText().trim().toUpperCase();
        guessField.setText("");

        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            JOptionPane.showMessageDialog(parentFrame,
                "Please enter a single letter.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        processGuess(input.charAt(0));
    }

    /**
     * Main logic for guess correctness, ASCII updates, game over checks, etc.
     */
    private void processGuess(char guessedChar) {
        if (guessedLetters.contains(guessedChar)) {
            JOptionPane.showMessageDialog(parentFrame,
                "You've already guessed that letter!",
                "Duplicate Guess",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        guessedLetters.add(guessedChar);

        if (wordToGuess.indexOf(guessedChar) >= 0) {
            // Correct guess
            consecutiveWrongGuesses = 0;
            updateWordProgress();
            demonTauntLabel.setText(getRandomTaunt(correctTaunts));
            storyLabel.setText("Bael snarls, but you feel a surge of confidence...");
            showRoarAnimation();

        } else {
            // Wrong guess
            wrongGuesses++;
            consecutiveWrongGuesses++;
            asciiArtArea.setText(getGameSpecificAsciiArt());

            if (consecutiveWrongGuesses >= 2) {
                demonTauntLabel.setText(getRandomTaunt(rageTaunts));
                showRageAnimation();
            } else {
                demonTauntLabel.setText(getRandomTaunt(wrongTaunts));
            }
            storyLabel.setText("A gust of scorching wind grazes you. Stay focused!");
        }

        if (isWordGuessed()) {
            wonGame = true;
            dialogBox.showText(
                "Congratulations! You've bested Bael!\nYou earned the Dragon's Peak Plushie!",
                () -> {
                    if (onCompleteCallback != null) {
                        onCompleteCallback.run();
                    }
                }
            );
        } else if (wrongGuesses >= maxWrongGuesses) {
            dialogBox.showText(
                "GAME OVER! Bael reigns victorious!\nThe word was: " + wordToGuess,
                () -> {
                    if (onFailCallback != null) {
                        onFailCallback.run();
                    }
                }
            );
        }
    }

    /**
     * Show a short "roaring" ASCII, then revert after 1 second.
     */
    private void showRoarAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        asciiArtArea.setText(getDragonAsciiRoar() + "\n\n" + getHangmanState());

        animationTimer = new Timer(1000, e -> {
            asciiArtArea.setText(getGameSpecificAsciiArt());
            animationTimer.stop();
        });
        animationTimer.start();
    }

    /**
     * Show a short "rage" ASCII (angrier than roar), then revert after 1 second.
     */
    private void showRageAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        asciiArtArea.setText(getDragonAsciiRage() + "\n\n" + getHangmanState());

        animationTimer = new Timer(1000, e -> {
            asciiArtArea.setText(getGameSpecificAsciiArt());
            animationTimer.stop();
        });
        animationTimer.start();
    }

    /**
     * Update the word progress label with underscores or letters.
     */
    private void updateWordProgress() {
        wordProgressLabel.setText(getWordProgress());
    }

    /**
     * Return a spaced-out version of the puzzle's progress.
     */
    private String getWordProgress() {
        StringBuilder progress = new StringBuilder();
        for (char c : wordToGuess.toCharArray()) {
            progress.append(guessedLetters.contains(c) ? (c + " ") : "_ ");
        }
        return progress.toString().trim();
    }

    private boolean isWordGuessed() {
        for (char c : wordToGuess.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private String getRandomTaunt(String[] taunts) {
        return taunts[(int) (Math.random() * taunts.length)];
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return getDragonAscii() + "\n\n" + getHangmanState();
    }

    /**
     * The standard "dragon" ASCII
     */
    private String getDragonAscii() {
        return """
               , ,, ,                              
           | || |    ,/  _____  \\.
           \\_||_/    ||_/     \\_||
             ||       \\_| . . |_/
             ||         |  L  |
            ,||         |`==='|
            |>|      ___`>  -<'___
            |>\\    /             \\
            \\>|  /  ,    .    .  |
             ||  \\  /| .  |  . |  |
             ||\\  ` / | ___|___ |  |
         (( || `--'  | _______ |  |     ))
        """;
    }

    /**
     * A roaring dragon for correct guesses
     */
    private String getDragonAsciiRoar() {
        return """
               , ,, ,                              
           | || |   (R O A R)_____  \\.
           \\_||_/    ||_/     \\_||   ~~~~~
             ||       \\_| @ @ |_/
             ||         |  L  |   "Fssss!"
            ,||         |`===='|
            |>|      ___`>  -<'___
            |>\\    /             \\
            \\>|  /  ,   .   .   . |
             ||  \\  /|  . | . |  |
             ||\\  ` / | ___|___ |  |
         (( || `--'  | _______ |  |     ))
        """;
    }

    /**
     * An even more furious dragon for consecutive wrong guesses (rage).
     */
    private String getDragonAsciiRage() {
        return """
                .-===-.
               (  RAGE  )
                `-===-'
                //   \\\\
               //  x  \\\\
      /\\  /\\  (   ____   )  /\\  /\\
     (  \\/  )  \\ (    ) /  (  \\/  )
      )    (    )      (    )    (
      |(  )|    \\  --  /    |(  )|
      ( || )      (  )      ( || )
      /||||\\      )  (      /||||\\
     /||||||\\    /    \\    /||||||\\
    Bael's eyes burn with unholy fury!!!
        """;
    }

    /**
     * Return the hangman ASCII for how many wrong guesses so far
     */
    private String getHangmanState() {
        return switch (wrongGuesses) {
            case 1 -> """
                 +---+
                 |   |
                 O   |
                     |
                     |
                     |
                =========
                """;
            case 2 -> """
                 +---+
                 |   |
                 O   |
                 |   |
                     |
                     |
                =========
                """;
            case 3 -> """
                 +---+
                 |   |
                 O   |
                /|   |
                     |
                     |
                =========
                """;
            case 4 -> """
                 +---+
                 |   |
                 O   |
                /|\\  |
                     |
                     |
                =========
                """;
            case 5 -> """
                 +---+
                 |   |
                 O   |
                /|\\  |
                /    |
                     |
                =========
                """;
            case 6 -> """
                 +---+
                 |   |
                 O   |
                /|\\  |
                / \\  |
                     |
                =========
                """;
            default -> """
                 +---+
                 |   |
                     |
                     |
                     |
                     |
                =========
                """;
        };
    }

    @Override
    protected String getIntroText() {
        return "Bael the Dragon awaits your challenge...\n" +
               "Guess the word, or suffer a scorching defeat!";
    }

    /**
     * If you want to do something special for finishing the mini-game, override here.
     * But we rely on the onCompleteCallback / onFailCallback logic for awarding plushies or resetting game.
     */
    @Override
    protected void completeGame() {
        if (wonGame) {
            // They truly guessed the word, so awarding plushie or final steps can happen.
        }
        super.completeGame();
    }

    /**
     * --------------------------------------------------------------
     *  OVERRIDE startGame() to reset everything each time it's called
     * --------------------------------------------------------------
     */
    @Override
    public void startGame() {
        // Clear all relevant fields
        wrongGuesses = 0;
        consecutiveWrongGuesses = 0;
        wonGame = false;
        if (guessedLetters != null) {
            guessedLetters.clear();
        }

        // Reset the puzzle (or pick a new word if you want random)
        wordToGuess = "UNDERWORLD";

        // Refresh the UI if already built
        if (asciiArtArea != null) {
            asciiArtArea.setText(getGameSpecificAsciiArt());
        }
        if (wordProgressLabel != null) {
            wordProgressLabel.setText(getWordProgress());
        }
        if (demonTauntLabel != null) {
            demonTauntLabel.setText("Bael: Prepare to be crushed!");
        }
        if (storyLabel != null) {
            storyLabel.setText("You feel the searing heat of Bael's breath...");
        }
        if (guessField != null) {
            guessField.setText("");
        }

        // Now call the parent's startGame() so any parent logic happens
        super.startGame();
    }
}
