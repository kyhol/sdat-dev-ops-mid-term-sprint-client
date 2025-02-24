package com.keyin.minigame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DragonsPeakMiniGame extends AbstractMiniGame {

    private String wordToGuess;
    private Set<Character> guessedLetters;
    private int wrongGuesses = 0;
    private final int maxWrongGuesses = 6;
    private boolean wonGame = false;
    private int consecutiveWrongGuesses = 0;
    private JLabel wordProgressLabel;
    private JLabel demonTauntLabel;
    private JTextArea asciiArtArea;
    private JTextField guessField;
    private JLabel storyLabel;
    private Timer animationTimer;
    private JButton guessButton;

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

    private final String[] rageTaunts = {
        "Bael: You dare continue this insolence?!",
        "Bael: My fury will consume you!",
        "Bael: RAAAAAAHHHH!!!",
        "Bael: I'll scorch the very ground you stand on!"
    };

    public DragonsPeakMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        wordToGuess = "UNDERWORLD";
        guessedLetters = new HashSet<>();

        GradientPanel gamePanel = new GradientPanel(
                new Color(25, 0, 0),
                new Color(70, 10, 10)
        );
        gamePanel.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("DRAGON'S PEAK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Algerian", Font.BOLD, 38));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 50, 50), 3),
                new EmptyBorder(10, 10, 10, 10)
        ));
        gamePanel.add(titleLabel, BorderLayout.NORTH);

        asciiArtArea = new JTextArea(getGameSpecificAsciiArt());
        asciiArtArea.setEditable(false);
        asciiArtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        asciiArtArea.setBackground(new Color(10, 10, 10));
        asciiArtArea.setForeground(new Color(220, 220, 220));
        asciiArtArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane asciiScroll = new JScrollPane(asciiArtArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        asciiScroll.setPreferredSize(new Dimension(320, 300));
        gamePanel.add(asciiScroll, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setOpaque(false);

        wordProgressLabel = new JLabel(getWordProgress(), SwingConstants.CENTER);
        wordProgressLabel.setFont(new Font("Serif", Font.BOLD, 32));
        wordProgressLabel.setForeground(new Color(230, 230, 230));
        centerPanel.add(wordProgressLabel);

        demonTauntLabel = new JLabel("Bael: Prepare to be crushed!", SwingConstants.CENTER);
        demonTauntLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        demonTauntLabel.setForeground(new Color(255, 80, 80));
        centerPanel.add(demonTauntLabel);

        storyLabel = new JLabel("You feel the searing heat of Bael's breath...", SwingConstants.CENTER);
        storyLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        storyLabel.setForeground(new Color(250, 200, 100));
        centerPanel.add(storyLabel);

        gamePanel.add(centerPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);

        JLabel promptLabel = new JLabel("Enter a letter: ");
        promptLabel.setFont(new Font("Serif", Font.BOLD, 18));
        promptLabel.setForeground(Color.WHITE);

        guessField = new JTextField(5);
        guessField.setFont(new Font("Serif", Font.PLAIN, 18));
        guessField.setForeground(new Color(20, 20, 20));

        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Serif", Font.BOLD, 18));
        guessButton.setBackground(new Color(120, 30, 30));
        guessButton.setForeground(new Color(245, 245, 245));

        guessButton.addActionListener(e -> processGuess());
        guessField.addActionListener(e -> processGuess());

        inputPanel.add(promptLabel);
        inputPanel.add(guessField);
        inputPanel.add(guessButton);

        gamePanel.add(inputPanel, BorderLayout.SOUTH);

        this.gamePanel.remove(artPanel);
        this.gamePanel.add(gamePanel, BorderLayout.CENTER);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // No extra key behavior
    }

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
            consecutiveWrongGuesses = 0;
            updateWordProgress();
            demonTauntLabel.setText(getRandomTaunt(correctTaunts));
            storyLabel.setText("Bael snarls, but you feel a surge of confidence...");
            showRoarAnimation();
        } else {
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

    private void updateWordProgress() {
        wordProgressLabel.setText(getWordProgress());
    }

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

    @Override
    protected void completeGame() {
        if (wonGame) {
            // Additional final logic if needed
        }
        super.completeGame();
    }

    @Override
    public void startGame() {
        wrongGuesses = 0;
        consecutiveWrongGuesses = 0;
        wonGame = false;
        if (guessedLetters != null) {
            guessedLetters.clear();
        }
        wordToGuess = "UNDERWORLD";

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
        super.startGame();
    }

    // Simple gradient panel to enhance the background
    private static class GradientPanel extends JPanel {
        private final Color color1;
        private final Color color2;

        public GradientPanel(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
    }
}
