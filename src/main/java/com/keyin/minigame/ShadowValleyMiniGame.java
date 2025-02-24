package com.keyin.minigame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;

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
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(138, 206, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        URL imageUrl = getClass().getResource("/image/Shadow.jpg");
        JLabel imageLabel = new JLabel();
        if (imageUrl != null) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            imageLabel.setIcon(imageIcon);
        }

        JTextArea rulesText = new JTextArea(
                "I am Maquito, Lord of the Shadows:\n" +
                        "If you seek a Shadow Cat Plushie,\n" +
                        "You have to show me you can take on my Shadow Cats.\n" +
                        "Place your victims on the board so that no Shadow Cat can attack them.\n" +
                        "The victims are also unable to cross each other's paths.\n" +
                        "Shadow Cats lie in wait but can move horizontally, vertically, and diagonally,\n" +
                        "As can the members of your team.\n" +
                        "Click on empty squares to place or remove your victims.\n" +
                        "Good Luck.\n" +
                        "I didn't really mean that.\n" +
                        "It's just a thing I heard people say.");
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setBackground(new Color(30, 30, 30));
        rulesText.setForeground(new Color(200, 200, 200));
        rulesText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        rulesText.setPreferredSize(new Dimension(400, 150));

        JPanel maquitoPanel = new JPanel(new BorderLayout(10, 0));
        maquitoPanel.setBackground(Color.BLACK);
        maquitoPanel.add(imageLabel, BorderLayout.WEST);
        maquitoPanel.add(rulesText, BorderLayout.CENTER);

        JPanel introPanel = new JPanel(new BorderLayout());
        introPanel.setBackground(Color.BLACK);
        introPanel.add(titleLabel, BorderLayout.NORTH);
        introPanel.add(maquitoPanel, BorderLayout.CENTER);

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 2, 2));
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setPreferredSize(new Dimension(560, 560));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int r = row;
                final int c = col;
                boardButtons[row][col] = new JButton();
                boardButtons[row][col].setPreferredSize(new Dimension(70, 70));
                boardButtons[row][col].setMargin(new Insets(0, 0, 0, 0));
                boardButtons[row][col].addActionListener(e -> handleSquareClick(r, c));
                boardPanel.add(boardButtons[row][col]);
            }
        }

        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(new Color(138, 206, 0));
        statusLabel.setBackground(new Color(30, 30, 30));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusLabel.setPreferredSize(new Dimension(600, 40));

        gamePanel.add(introPanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(statusLabel, BorderLayout.SOUTH);

        generateNewGame();
        colorBoard();
        playAudio(bowserClip);
    }

    private void loadAudioFiles() {
        try {
            URL bowserURL = getClass().getResource("/audio/ShadowValley.wav");
            if (bowserURL != null) {
                bowserClip = loadAudioClip(bowserURL);
            } else {
                System.err.println("Could not find ShadowValley.wav");
            }
        } catch (Exception ex) {
            System.err.println("Error loading audio files: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Clip loadAudioClip(URL audioURL) throws Exception {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }
    private void playAudio(Clip clip) {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
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
        int iconSize = 50;
        BufferedImage image = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(color);
        int[] xPoints = {iconSize/2, iconSize/5, iconSize/10, iconSize/5, iconSize*3/10,
                iconSize/2, iconSize*7/10, iconSize*4/5, iconSize*9/10, iconSize*4/5};
        int[] yPoints = {iconSize/8, iconSize/3, iconSize*2/3, iconSize*3/4, iconSize*5/6,
                iconSize*7/8, iconSize*5/6, iconSize*3/4, iconSize*2/3, iconSize/3};
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        g2d.setColor(color.brighter());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, xPoints.length);

        g2d.dispose();
        return new ImageIcon(image);
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