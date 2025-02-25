package com.keyin.minigame;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

/**
 * Simplified implementation for Shadow Valley Mini-Game (Location ID 6)
 */
public class ShadowValleyMiniGame extends AbstractMiniGame {
    private static final int BOARD_SIZE = 8;
    private static final int REQUIRED_VICTIMS = 3;  // Added constant for required victims
    private JButton[][] boardButtons;
    private boolean[][] hasShadowCat;
    private boolean[][] initialShadowCats;
    private boolean[][] conflictSquares;
    private JLabel statusLabel;
    private Color[] boardColors;
    private Color highlightColor;
    private Color victimColor;
    private Color shadowCatColor;
    private List<Point> allPieces;
    private List<Point> playerPieces;
    private Clip bowserClip;

    public ShadowValleyMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
        loadAudioFiles();
        playAudio(bowserClip);
    }

    @Override
    protected void customizeUI() {
        boardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        hasShadowCat = new boolean[BOARD_SIZE][BOARD_SIZE];
        initialShadowCats = new boolean[BOARD_SIZE][BOARD_SIZE];
        conflictSquares = new boolean[BOARD_SIZE][BOARD_SIZE];

        boardColors = new Color[]{new Color(120, 120, 120), new Color(161, 159, 159)};
        highlightColor = new Color(244, 29, 234, 255);
        victimColor = new Color(138, 206, 0);
        shadowCatColor = new Color(0, 0, 0);

        allPieces = new ArrayList<>();
        playerPieces = new ArrayList<>();

        statusLabel = new JLabel("I was going to name this place Death Valley but it was already taken.");

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout(10, 10));
        gamePanel.setPreferredSize(new Dimension(600, 800));
        gamePanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Shadow Valley", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(138, 206, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        URL imageUrl = getClass().getResource("/image/Shadow.jpg");
        JLabel imageLabel = new JLabel();
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            // Make the image stretch to fill the panel width
            imageLabel.setIcon(originalIcon);
            imageLabel.setPreferredSize(new Dimension(450, 300));
            // Use horizontal stretch by setting the horizontal alignment
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            // Enable image scaling
            imageLabel.setIcon(new ImageIcon(originalIcon.getImage().getScaledInstance(450, 300, Image.SCALE_FAST)));
        }

        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setBackground(new Color(30, 30, 30));
        rulesText.setForeground(new Color(200, 200, 200));
        rulesText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        rulesText.setPreferredSize(new Dimension(450, 200));
        rulesText.setMinimumSize(new Dimension(450, 200));
        // Increase text size
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String[] rulesLines = {
                "I am Maquito, Lord of the Shadows:",
                "If you seek a Shadow Cat Plushie,",
                "You have to show me you can take on my Shadow Cats.",
                "Place your victims on the board so that no Shadow Cat can attack them.",
                "The victims are also unable to cross each other's paths.",
                "Shadow Cats lie in wait but can move horizontally, vertically, and diagonally,",
                "As can the members of your team.",
                "Click on empty squares to place or remove your victims.",
                "Good Luck.",
                "I didn't really mean that.",
                "It's just a thing I heard people say."
        };

        Timer textTimer = new Timer(700, new ActionListener() {
            private int index = 0;
            private StringBuilder displayedText = new StringBuilder();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < rulesLines.length) {
                    displayedText.append(rulesLines[index]).append("\n");
                    rulesText.setText(displayedText.toString());
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });

        textTimer.start();

        JPanel maquitoPanel = new JPanel();
        maquitoPanel.setLayout(new BoxLayout(maquitoPanel, BoxLayout.Y_AXIS));
        maquitoPanel.setBackground(Color.BLACK);
        maquitoPanel.setPreferredSize(new Dimension(450, 600));

        // Wrap imageLabel in a panel to make it fill the width
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.BLACK);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        maquitoPanel.add(imagePanel);
        maquitoPanel.add(rulesText);

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 2, 2));
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setPreferredSize(new Dimension(600, 600));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int r = row;
                final int c = col;
                boardButtons[row][col] = new JButton();
                boardButtons[row][col].setPreferredSize(new Dimension(65, 65));
                boardButtons[row][col].setMargin(new Insets(0, 0, 0, 0));
                boardButtons[row][col].addActionListener(e -> handleSquareClick(r, c));
                boardPanel.add(boardButtons[row][col]);
            }
        }

        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(Color.BLACK);
        boardContainer.add(boardPanel);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(maquitoPanel);
        mainPanel.add(boardContainer);

        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(new Color(138, 206, 0));
        statusLabel.setBackground(new Color(30, 30, 30));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusLabel.setPreferredSize(new Dimension(600, 40));

        gamePanel.add(titleLabel, BorderLayout.NORTH);
        gamePanel.add(mainPanel, BorderLayout.CENTER);
        gamePanel.add(statusLabel, BorderLayout.SOUTH);

        generateNewGame();
        colorBoard();
    }

    private void loadAudioFiles() {
        try {
            // Try multiple approaches to find the resource
            URL bowserURL = getClass().getResource("/audio/ShadowValley.wav");

            if (bowserURL == null) {
                // Try without leading slash
                bowserURL = getClass().getResource("audio/ShadowValley.wav");
            }

            if (bowserURL == null) {
                // Try using ClassLoader directly
                bowserURL = getClass().getClassLoader().getResource("audio/ShadowValley.wav");
            }

            if (bowserURL != null) {
                System.out.println("Audio file found at: " + bowserURL);
                bowserClip = loadAudioClip(bowserURL);
                System.out.println("Audio clip loaded successfully: " + (bowserClip != null));
            } else {
                System.err.println("Could not find ShadowValley.wav - resource path may be incorrect");
                // Print class path for debugging
                System.err.println("Class path: " + getClass().getProtectionDomain().getCodeSource().getLocation());
            }
        } catch (Exception ex) {
            System.err.println("Error loading audio files: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void playAudio(Clip clip) {
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
                System.out.println("Audio playback started");
            } catch (Exception e) {
                System.err.println("Error playing audio: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot play audio: clip is null");
        }
    }

    private Clip loadAudioClip(URL audioURL) throws Exception {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }

    private void cleanupAudioResources() {
        if (bowserClip != null) {
            bowserClip.stop();
            bowserClip.close();
        }
    }

    private void colorBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = boardButtons[row][col];
                button.setBackground(boardColors[(row + col) % 2]);
                button.setOpaque(true);
                button.setBorderPainted(false);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        if (initialShadowCats[row][col]) {
            return;
        }

        Point clickedPoint = new Point(row, col);
        if (hasShadowCat[row][col]) {
            // Remove piece
            hasShadowCat[row][col] = false;
            playerPieces.remove(clickedPoint);
            allPieces.remove(clickedPoint);
        } else {
            // Add piece
            hasShadowCat[row][col] = true;
            playerPieces.add(clickedPoint);
            allPieces.add(clickedPoint);
        }

        checkConflicts();
        updateBoard();

        if (playerPieces.size() == REQUIRED_VICTIMS && !hasAnyConflicts()) {
            // Show victory animation for 3 seconds
            showVictoryAnimation();
        }
    }

    private void showVictoryAnimation() {
        // Create a JDialog to display the GIF in the center of the screen
        JDialog animationDialog = new JDialog();
        animationDialog.setUndecorated(true);
        animationDialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        URL gifUrl = getClass().getResource("/gif/ShadowVictory.gif");
        if (gifUrl != null) {
            ImageIcon gifIcon = new ImageIcon(gifUrl);
            JLabel gifLabel = new JLabel(gifIcon);

            // Create a panel with a semi-transparent background
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.add(gifLabel, BorderLayout.CENTER);

            animationDialog.add(panel);
            animationDialog.pack();

            // Center the dialog on the screen
            animationDialog.setLocationRelativeTo(gamePanel);

            // Show the dialog
            animationDialog.setVisible(true);

            // Schedule the dialog to close after 3 seconds
            Timer closeTimer = new Timer(5000, e -> {
                animationDialog.dispose();
                // Now show the victory message
                cleanupAudioResources();
                dialogBox.showText("You've conquered the shadows! The Shadow Cat plushie is yours!", this::completeGame);
            });
            closeTimer.setRepeats(false);
            closeTimer.start();
        } else {
            // If GIF can't be loaded, just show the victory message
            cleanupAudioResources();
            dialogBox.showText("You've conquered the shadows! The Shadow Cat plushie is yours!", this::completeGame);
        }
    }

    private boolean hasAnyConflicts() {
        // Check each piece against every other piece
        for (int i = 0; i < allPieces.size(); i++) {
            Point p1 = allPieces.get(i);
            for (int j = i + 1; j < allPieces.size(); j++) {
                Point p2 = allPieces.get(j);
                if (isConflict(p1, p2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isConflict(Point p1, Point p2) {
        return p1.x == p2.x || // Same row
                p1.y == p2.y || // Same column
                Math.abs(p1.x - p2.x) == Math.abs(p1.y - p2.y); // Diagonal
    }

    private void checkConflicts() {
        // Clear all conflicts
        for (boolean[] row : conflictSquares) {
            Arrays.fill(row, false);
        }

        // Check each piece against every other piece
        for (int i = 0; i < allPieces.size(); i++) {
            Point p1 = allPieces.get(i);
            for (int j = i + 1; j < allPieces.size(); j++) {
                Point p2 = allPieces.get(j);
                if (isConflict(p1, p2)) {
                    conflictSquares[p1.x][p1.y] = true;
                    conflictSquares[p2.x][p2.y] = true;
                }
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = boardButtons[row][col];
                Color baseColor = boardColors[(row + col) % 2];

                if (hasShadowCat[row][col]) {
                    button.setIcon(createShadowCatIcon(initialShadowCats[row][col] ? shadowCatColor : victimColor));
                } else {
                    button.setIcon(null);
                }

                button.setBackground(conflictSquares[row][col] ?
                        new Color(Math.min(255, baseColor.getRed() + highlightColor.getRed()),
                                Math.min(255, baseColor.getGreen() + highlightColor.getGreen()),
                                Math.min(255, baseColor.getBlue() + highlightColor.getBlue())) :
                        baseColor);
            }
        }
    }

    private ImageIcon createShadowCatIcon(Color color) {
        try {
            // Load the SVG file from resources
            InputStream inputStream = getClass().getResourceAsStream("/image/ShadowCat.svg");
            if (inputStream == null) {
                throw new IOException("Resource not found: /image/ShadowCat.svg");
            }

            // Create SVG document
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            SVGDocument document = factory.createSVGDocument(
                    null, // No URI needed when using InputStream
                    inputStream);

            // Apply color to the SVG elements if needed
            NodeList elements = document.getElementsByTagName("*");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                if (element.hasAttribute("fill") && !element.getAttribute("fill").equals("none")) {
                    element.setAttribute("fill", String.format("#%02x%02x%02x",
                            color.getRed(), color.getGreen(), color.getBlue()));
                }
            }

            // Set up the transcoder
            int iconSize = 50;
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) iconSize);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) iconSize);

            // Perform the transcoding
            TranscoderInput input = new TranscoderInput(document);
            transcoder.transcode(input, null);

            // Get the resulting image
            BufferedImage image = transcoder.getBufferedImage();

            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper class to get a BufferedImage from the transcoder
    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage image = null;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.image = img;
        }

        public BufferedImage getBufferedImage() {
            return image;
        }
    }

    private boolean isGameWon() {
        int placedVictims = 0;
        boolean hasConflicts = false;

        // Count placed victims and check for conflicts
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (hasShadowCat[i][j] && !initialShadowCats[i][j]) {
                    placedVictims++;
                    if (conflictSquares[i][j]) {
                        hasConflicts = true;
                    }
                }
            }
        }

        // Win condition: exactly 3 victims placed with no conflicts
        return placedVictims == REQUIRED_VICTIMS && !hasConflicts;
    }

    private void generateNewGame() {
        // Clear all arrays
        for (boolean[] row : hasShadowCat) {
            Arrays.fill(row, false);
        }
        for (boolean[] row : initialShadowCats) {
            Arrays.fill(row, false);
        }
        for (boolean[] row : conflictSquares) {
            Arrays.fill(row, false);
        }

        allPieces.clear();
        playerPieces.clear();

        boolean[][] solution = generateRandomSolution();
        ArrayList<Point> possiblePositions = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (solution[i][j]) {
                    possiblePositions.add(new Point(i, j));
                }
            }
        }

        Collections.shuffle(possiblePositions);
        for (int i = 0; i < 5; i++) {
            Point p = possiblePositions.get(i);
            initialShadowCats[p.x][p.y] = true;
            hasShadowCat[p.x][p.y] = true;
            allPieces.add(p);
        }

        updateBoard();
    }

    private boolean[][] generateRandomSolution() {
        boolean[][] solution = new boolean[BOARD_SIZE][BOARD_SIZE];
        int[] shadowCats = new int[BOARD_SIZE];
        Arrays.fill(shadowCats, -1);

        Random random = new Random();
        solveNShadowCats(shadowCats, 0, random);

        for (int row = 0; row < BOARD_SIZE; row++) {
            solution[row][shadowCats[row]] = true;
        }

        return solution;
    }

    private boolean solveNShadowCats(int[] shadowCats, int row, Random random) {
        if (row == BOARD_SIZE) {
            return true;
        }

        ArrayList<Integer> validPositions = new ArrayList<>();
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (isValid(shadowCats, row, col)) {
                validPositions.add(col);
            }
        }

        Collections.shuffle(validPositions, random);

        for (Integer col : validPositions) {
            shadowCats[row] = col;
            if (solveNShadowCats(shadowCats, row + 1, random)) {
                return true;
            }
        }

        shadowCats[row] = -1;
        return false;
    }

    private boolean isValid(int[] shadowCats, int row, int col) {
        for (int i = 0; i < row; i++) {
            if (shadowCats[i] == col ||
                    shadowCats[i] - i == col - row ||
                    shadowCats[i] + i == col + row) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return """
               /\\___/\\   Shadow Valley   /\\___/\\  \s
              (  o o  )  Maquito's Lair  (  o o  ) \s
              (  T_T  )     Beware...    (  T_T  ) \s
               \\~(*)~/   Dark Shadows...  \\~(*)~/   \
            """;
    }

    @Override
    protected String getIntroText() {
        return "Welcome to Shadow Valley, domain of Maquito, Lord of Shadows.\n" +
                "Place your pieces carefully, for the shadows are watching...";
    }
}