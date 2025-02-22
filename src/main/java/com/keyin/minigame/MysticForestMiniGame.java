package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Tutorial version of Mystic Forest Mini-Game with labeled elements
 * Each element has a detailed explanation of how it works
 */
public class MysticForestMiniGame extends AbstractMiniGame {
    private JButton winButton;

    public MysticForestMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // Create main panel with grid layout (3x3 grid)
        // This lets us easily place elements in top-left, top-center, etc.
        JPanel customPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        customPanel.setBackground(new Color(0, 30, 0)); // Dark green background

        // -----------------------------------------------
        // TOP ROW ELEMENTS (Left, Center, Right)
        // -----------------------------------------------

        // TOP LEFT: Simple colored panel with label
        // -----------------------------------------------
        JPanel topLeftPanel = createExamplePanel(
                "TOP LEFT: Simple Colored Panel",
                new Color(34, 139, 34), // Forest green
                "This is a basic colored JPanel.\n" +
                        "1. Create with: new JPanel()\n" +
                        "2. Set background: panel.setBackground(Color)\n" +
                        "3. Add a label: panel.add(new JLabel(...))"
        );
        customPanel.add(topLeftPanel);

        // TOP CENTER: Panel with image icon
        // -----------------------------------------------
        JPanel topCenterPanel = createExamplePanel(
                "TOP CENTER: Image Panel",
                new Color(50, 120, 50), // Different green shade
                "This demonstrates using an ImageIcon.\n" +
                        "1. Create with: new ImageIcon(\"path/to/image.png\")\n" +
                        "2. Add to label: new JLabel(imageIcon)\n" +
                        "3. For this example, we're using a simple tree shape"
        );

        // Add a tree icon to this panel (draw a simple tree since we can't load files)
        JPanel treeIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw a simple tree
                g.setColor(new Color(139, 69, 19)); // Brown trunk
                g.fillRect(getWidth()/2-10, getHeight()/2+20, 20, 40);
                g.setColor(new Color(0, 100, 0)); // Green foliage
                g.fillOval(getWidth()/2-30, getHeight()/2-30, 60, 60);
            }
        };
        treeIconPanel.setPreferredSize(new Dimension(80, 80));
        treeIconPanel.setBackground(new Color(50, 120, 50));

        // Add the tree drawing to the center panel
        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setBackground(new Color(50, 120, 50));
        centerContent.add(treeIconPanel, BorderLayout.CENTER);
        topCenterPanel.add(centerContent, BorderLayout.CENTER);

        customPanel.add(topCenterPanel);

        // TOP RIGHT: Custom drawing panel
        // -----------------------------------------------
        JPanel topRightPanel = createExamplePanel(
                "TOP RIGHT: Custom Drawing",
                new Color(85, 107, 47), // Olive green
                "This shows custom drawing with paintComponent().\n" +
                        "1. Extend JPanel and override paintComponent\n" +
                        "2. Use Graphics g to draw shapes\n" +
                        "3. g.fillOval() for circles, g.fillRect() for rectangles"
        );

        // Add a custom drawing panel
        JPanel customDrawing = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw a sun
                g.setColor(Color.YELLOW);
                g.fillOval(getWidth()/2-20, 10, 40, 40);

                // Draw sun rays
                g.drawLine(getWidth()/2, 5, getWidth()/2, 0);
                g.drawLine(getWidth()/2+30, 15, getWidth()/2+40, 5);
                g.drawLine(getWidth()/2+30, 30, getWidth()/2+50, 30);
                g.drawLine(getWidth()/2-30, 15, getWidth()/2-40, 5);
                g.drawLine(getWidth()/2-30, 30, getWidth()/2-50, 30);
            }
        };
        customDrawing.setPreferredSize(new Dimension(80, 80));
        customDrawing.setBackground(new Color(85, 107, 47));

        JPanel rightContent = new JPanel(new BorderLayout());
        rightContent.setBackground(new Color(85, 107, 47));
        rightContent.add(customDrawing, BorderLayout.CENTER);
        topRightPanel.add(rightContent, BorderLayout.CENTER);

        customPanel.add(topRightPanel);

        // -----------------------------------------------
        // MIDDLE ROW: Title + Main Content
        // -----------------------------------------------

        // Create a title panel spanning all three middle cells
        JLabel titleLabel = new JLabel("MYSTIC FOREST", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(144, 238, 144)); // Light green

        customPanel.add(titleLabel);

        // Add a simple explanation in the middle cell
        JTextArea explanationText = new JTextArea(
                "This is the main title area.\n" +
                        "For your game, you might put the main challenge here.\n" +
                        "The GridLayout makes it easy to organize elements."
        );
        explanationText.setLineWrap(true);
        explanationText.setWrapStyleWord(true);
        explanationText.setBackground(new Color(0, 30, 0));
        explanationText.setForeground(Color.WHITE);
        explanationText.setEditable(false);
        explanationText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        customPanel.add(explanationText);

        // Add an animated element in the middle-right
        JPanel animatedPanel = new JPanel() {
            private int step = 0;
            {
                // Create a timer for animation
                Timer timer = new Timer(500, e -> {
                    step = (step + 1) % 4;
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                String text = "Animation Step: " + step;
                g.drawString(text, 10, 20);

                // Draw a simple animated sprite
                g.setColor(Color.CYAN);
                switch(step) {
                    case 0: g.fillOval(20, 30, 30, 30); break;
                    case 1: g.fillRect(20, 30, 30, 30); break;
                    case 2: g.fillOval(50, 30, 30, 30); break;
                    case 3: g.fillRect(50, 30, 30, 30); break;
                }
            }
        };
        animatedPanel.setBackground(new Color(0, 30, 0));

        JPanel animatedContainer = new JPanel(new BorderLayout());
        animatedContainer.setBackground(new Color(0, 30, 0));
        animatedContainer.add(new JLabel("Animation Example:", SwingConstants.CENTER), BorderLayout.NORTH);
        animatedContainer.add(animatedPanel, BorderLayout.CENTER);

        customPanel.add(animatedContainer);

        // -----------------------------------------------
        // BOTTOM ROW ELEMENTS (Left, Center, Right)
        // -----------------------------------------------

        // BOTTOM LEFT: Interactive element (button)
        // -----------------------------------------------
        JPanel bottomLeftPanel = createExamplePanel(
                "BOTTOM LEFT: Interactive Button",
                new Color(46, 139, 87), // Sea green
                "This shows how to create interactive elements.\n" +
                        "1. Create button: new JButton(\"Text\")\n" +
                        "2. Add listener: button.addActionListener(e -> { ... })\n" +
                        "3. Style with background, font, etc."
        );

        JButton demoButton = new JButton("Demo Button");
        demoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(parentFrame,
                    "You clicked the demo button!\nIn your game, this could trigger game logic.",
                    "Button Clicked",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        demoButton.setBackground(new Color(144, 238, 144));
        demoButton.setForeground(new Color(0, 100, 0));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.setBackground(new Color(46, 139, 87));
        buttonContainer.add(demoButton);
        bottomLeftPanel.add(buttonContainer, BorderLayout.CENTER);

        customPanel.add(bottomLeftPanel);

        // BOTTOM CENTER: Text field input
        // -----------------------------------------------
        JPanel bottomCenterPanel = createExamplePanel(
                "BOTTOM CENTER: User Input",
                new Color(60, 179, 113), // Medium sea green
                "This demonstrates getting user input.\n" +
                        "1. Create text field: new JTextField(size)\n" +
                        "2. Get input: textField.getText()\n" +
                        "3. Listen for Enter: textField.addActionListener()"
        );

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(new Color(60, 179, 113));

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            String input = inputField.getText();
            if (!input.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame,
                        "You entered: " + input + "\nIn your game, this could be a password or answer.",
                        "Input Received",
                        JOptionPane.INFORMATION_MESSAGE);
                inputField.setText("");
            }
        });

        inputPanel.add(new JLabel("Enter text:"));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        bottomCenterPanel.add(inputPanel, BorderLayout.CENTER);
        customPanel.add(bottomCenterPanel);

        // BOTTOM RIGHT: Win button for the mini-game
        // -----------------------------------------------
        JPanel bottomRightPanel = createExamplePanel(
                "BOTTOM RIGHT: Win Game Button",
                new Color(0, 128, 0), // Green
                "This is the actual win button for your mini-game.\n" +
                        "1. When clicked, it shows congratulation text\n" +
                        "2. Then calls completeGame() to finish the game\n" +
                        "3. This is how players will win your mini-game"
        );

        // Create win button panel
        JPanel winButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        winButtonPanel.setBackground(new Color(0, 128, 0));

        // Create the actual win button
        winButton = new JButton("Win Mini Game");
        winButton.setFont(new Font("Arial", Font.BOLD, 16));
        winButton.setBackground(new Color(144, 238, 144));
        winButton.setForeground(new Color(0, 100, 0));

        winButton.addActionListener(e -> {
            // Show completion text and then call completeGame
            dialogBox.showText("Congratulations! You've completed the Mystic Forest challenge!\n" +
                    "You earned the Forest Spirit plushie!", () -> {
                completeGame();
            });
        });

        winButtonPanel.add(winButton);
        bottomRightPanel.add(winButtonPanel, BorderLayout.CENTER);

        customPanel.add(bottomRightPanel);

        // -----------------------------------------------
        // FINAL SETUP: Add everything to the game panel
        // -----------------------------------------------

        // Remove the original art panel and add our custom panel
        gamePanel.remove(artPanel);
        gamePanel.add(customPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create consistent example panels
     */
    private JPanel createExamplePanel(String title, Color backgroundColor, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Title at top
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Description at bottom
        JTextArea descLabel = new JTextArea(description);
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setBackground(backgroundColor);
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        descLabel.setEditable(false);
        panel.add(descLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // Space key triggers the win button
        if (!dialogBox.isTyping() && e.getKeyChar() == ' ') {
            winButton.doClick();
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return "MYSTIC FOREST TUTORIAL";
    }

    @Override
    protected String getIntroText() {
        return "Welcome to the Mystic Forest Tutorial!\n" +
                "This mini-game demonstrates different UI elements you can use.\n" +
                "Each section explains how to create and use a specific component.\n" +
                "Click the 'Win Mini Game' button in the bottom right to complete it.";
    }
}