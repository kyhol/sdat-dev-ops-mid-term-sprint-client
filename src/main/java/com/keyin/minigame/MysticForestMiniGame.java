package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/** ADAMS REMOVE / CHANGE ALL THIS TO FIT YOUR GAME
 *
 *
 * Example implementation for the Mystic Forest mini-game (Location ID 2)
 */
public class MysticForestMiniGame extends AbstractMiniGame {
    private int currentQuestion = 0;
    private final String[] questions = {
            "The forest whispers a riddle: 'I'm light as a feather, but even the strongest person can't hold me for long.' What am I?",
            "A magical tree asks: 'What has roots nobody sees, is taller than trees, up, up it goes, and yet never grows?'",
            "A woodland sprite challenges you: 'Forward I'm heavy, backward I'm not. What am I?'"
    };
    private final String[] answers = {
            "breath",
            "mountain",
            "ton"
    };
    private String userInput = "";
    private JLabel inputLabel;

    public MysticForestMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Add a panel for user input at the bottom of the game panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.BLACK);

        JLabel promptLabel = new JLabel("Answer: ");
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        inputLabel = new JLabel("");
        inputLabel.setForeground(Color.GREEN);
        inputLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        inputPanel.add(promptLabel);
        inputPanel.add(inputLabel);

        // Add the input panel below the dialog box
        gamePanel.add(inputPanel, BorderLayout.NORTH);
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // If still in introduction mode, move to the first question
        if (!dialogBox.isTyping() && currentQuestion == 0 && userInput.isEmpty()) {
            askNextQuestion();
            return;
        }

        // Handle special keys
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkAnswer();
            return;
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !userInput.isEmpty()) {
            userInput = userInput.substring(0, userInput.length() - 1);
            inputLabel.setText(userInput);
            return;
        }

        // Add the typed character to the input if it's a letter or space
        char c = e.getKeyChar();
        if (Character.isLetterOrDigit(c) || c == ' ') {
            userInput += c;
            inputLabel.setText(userInput);
        }
    }

    private void askNextQuestion() {
        if (currentQuestion < questions.length) {
            dialogBox.showText(questions[currentQuestion]);
            userInput = "";
            inputLabel.setText("");
        } else {
            // All questions answered correctly
            dialogBox.showText("You have solved all the forest's riddles! The magic plushie reveals itself to you!", () -> {
                JOptionPane.showMessageDialog(parentFrame,
                        "You've collected the Forest Spirit plushie!",
                        "Plushie Collected",
                        JOptionPane.INFORMATION_MESSAGE);
                completeGame();
            });
        }
    }

    private void checkAnswer() {
        if (currentQuestion < answers.length) {
            String correctAnswer = answers[currentQuestion];
            if (userInput.toLowerCase().trim().equals(correctAnswer)) {
                // Correct answer
                currentQuestion++;
                dialogBox.showText("Correct! The forest hums with approval...", this::askNextQuestion);
            } else {
                // Wrong answer
                dialogBox.showText("That's not right. The forest gives you another chance...", () -> {
                    userInput = "";
                    inputLabel.setText("");
                });
            }
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return
                "              ,@@@@@@@,\n" +
                        "      ,,,.   ,@@@@@@/@@,  .oo8888o.\n" +
                        "   ,&%%&%&&%,@@@@@/@@@@@@,8888\\88/8o\n" +
                        "  ,%&\\%&&%&&%,@@@\\@@@/@@@88\\88888/88'\n" +
                        "  %&&%&%&/%&&%@@\\@@/ /@@@88888\\88888'\n" +
                        "  %&&%/ %&%%&&@@\\ V /@@' `88\\8 `/88'\n" +
                        "  `&%\\ ` /%&'    |.|        \\ '|8'\n" +
                        "      |o|        | |         | |\n" +
                        "      |.|        | |         | |\n" +
                        "   \\\\/ ._\\//_/__/  ,\\_//__\\\\/.  \\_//__/_";
    }

    @Override
    protected String getIntroText() {
        return "You've entered the Mystic Forest...\n" +
                "The trees seem to whisper ancient secrets.\n" +
                "To find the hidden plushie, you must solve the forest's riddles.\n" +
                "Press any key to begin your challenge...";
    }
}