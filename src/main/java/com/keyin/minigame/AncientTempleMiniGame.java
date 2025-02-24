package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class AncientTempleMiniGame extends AbstractMiniGame {
    private JButton winButton;
    private Timer dialogTimer;
    private GrowingImagePanel imagePanel;
    private boolean gifStarted = false;
    private JButton runButton;
    private JButton leftButton;
    private JButton rightButton;
    private int currentStep = 0;
    private final int[] correctSequence = {0, 1, 0, 0, 1}; // 0 = left, 1 = right
    private FloatControl gainControl;

    private Clip dungeonEntranceClip;
    private Clip bossEnterClip;

    public AncientTempleMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
        loadAudioFiles();
    }

    private void loadAudioFiles() {
        try {
            URL dungeonEntranceURL = getClass().getResource("/audio/AncientDungeonEntrance.wav");
            if (dungeonEntranceURL != null) {
                dungeonEntranceClip = loadAudioClip(dungeonEntranceURL);
            } else {
                System.err.println("Could not find AncientDungeonEntrance.wav");
            }

            URL bossEnterURL = getClass().getResource("/audio/BossEnter.wav");
            if (bossEnterURL != null) {
                bossEnterClip = loadAudioClip(bossEnterURL);
            } else {
                System.err.println("Could not find BossEnter.wav");
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

    @Override
    protected void customizeUI() {
        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("ANCIENT TEMPLE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 100, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        imagePanel = new GrowingImagePanel();
        imagePanel.setBackground(Color.BLACK);

        winButton = new JButton("Escape the Temple");
        winButton.setFont(new Font("Serif", Font.BOLD, 16));
        winButton.setBackground(new Color(100, 0, 0));
        winButton.setForeground(Color.WHITE);
        winButton.setEnabled(false);
        winButton.setVisible(false);
        winButton.addActionListener(e -> {
            dialogBox.showText("You've managed to escape the ancient evil...\nYou earned the Temple Guardian plushie!", () -> {
                cleanupAudioResources();
                completeGame();
            });
        });

        dialogBox.setBackground(new Color(20, 20, 20));
        dialogBox.setForeground(Color.WHITE);
        dialogBox.setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 2));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(winButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(dialogBox, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        gamePanel.add(titleLabel, BorderLayout.NORTH);
        gamePanel.add(imagePanel, BorderLayout.CENTER);
        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        gamePanel.revalidate();
        gamePanel.repaint();

        SwingUtilities.invokeLater(this::startScaryDialogSequence);
    }

    private void startScaryDialogSequence() {
        dialogBox.showText("Why is it so dark here?", () -> {
            playAudio(dungeonEntranceClip);
        });

        Timer messageSequence = new Timer(2500, e -> {
            dialogBox.showText("I have a horrible feeling...", null);

            Timer message2 = new Timer(2500, e2 -> {
                dialogBox.showText("...", null);

                Timer message3 = new Timer(2500, e3 -> {
                    dialogBox.showText("Something's coming", () -> {
                        Timer scaryGifTimer = new Timer(2500, e4 -> {
                            playScaryGif();
                            ((Timer)e4.getSource()).stop();
                        });
                        scaryGifTimer.setRepeats(false);
                        scaryGifTimer.start();
                    });
                    ((Timer)e3.getSource()).stop();
                });
                message3.setRepeats(false);
                message3.start();

                ((Timer)e2.getSource()).stop();
            });
            message2.setRepeats(false);
            message2.start();

            ((Timer)e.getSource()).stop();
        });
        messageSequence.setRepeats(false);
        messageSequence.start();
    }

    private void playScaryGif() {
        if (gifStarted) return;
        gifStarted = true;

        try {
            URL gifURL = getClass().getResource("/gif/DarknessDevilAttempt.gif");
            if (gifURL != null) {
                imagePanel.startGifAnimation(gifURL);

                Timer stopGifTimer = new Timer(2700, e -> {
                    imagePanel.stopGifAnimation();
                    ((Timer)e.getSource()).stop();
                });
                stopGifTimer.setRepeats(false);
                stopGifTimer.start();

                Timer imageGrowthTimer = new Timer(2500, e -> {
                    loadDevilImage();
                    playAudio(bossEnterClip);
                    ((Timer)e.getSource()).stop();
                });
                imageGrowthTimer.setRepeats(false);
                imageGrowthTimer.start();

                Timer runButtonTimer = new Timer(7500, e -> {
                    showRunButton();
                    ((Timer)e.getSource()).stop();
                });
                runButtonTimer.setRepeats(false);
                runButtonTimer.start();
            } else {
                showError("Could not find animation resource");
            }
        } catch (Exception ex) {
            showError("Error loading animation: " + ex.getMessage());
        }
    }

    private void showRunButton() {
        runButton = new JButton("RUN");
        runButton.setFont(new Font("Arial", Font.BOLD, 32));
        runButton.setBackground(new Color(150, 0, 0));
        runButton.setForeground(Color.WHITE);
        runButton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        runButton.setPreferredSize(new Dimension(200, 100));
        runButton.setFocusPainted(false);
        runButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        runButton.addActionListener(e -> {
            startEscapeSequence();
        });

        dialogBox.add(runButton);
        dialogBox.revalidate();
        dialogBox.repaint();

        SwingUtilities.invokeLater(() -> {
            runButton.setEnabled(true);
            runButton.requestFocusInWindow();
        });
    }

    private JButton createDirectionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(150, 75));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        button.setFocusPainted(false);

        button.addActionListener(e -> handleDirectionChoice(text.equals("LEFT") ? 0 : 1));

        return button;
    }

    private void reduceVolume() {
        if (bossEnterClip != null) {
            FloatControl gainControl = (FloatControl) bossEnterClip.getControl(FloatControl.Type.MASTER_GAIN);
            float currentVolume = gainControl.getValue();
            float newVolume = currentVolume - 6.0f; // Approximately 20% reduction in dB
            gainControl.setValue(Math.max(gainControl.getMinimum(), newVolume));
        }
    }

    private void handleDirectionChoice(int direction) {
        if (direction == correctSequence[currentStep]) {
            reduceVolume();
            currentStep++;

            if (currentStep >= correctSequence.length) {
                dialogBox.showText("You've managed to escape the ancient evil...\nYou earned the Temple Guardian plushie!", () -> {
                    cleanupAudioResources();
                    completeGame();
                });
            } else {
                dialogBox.showText("The sound fades... Keep going!", null);
            }
        }
    }

    private void startEscapeSequence() {
        gamePanel.removeAll();
        gamePanel.add(imagePanel, BorderLayout.CENTER);

        JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        directionPanel.setBackground(Color.BLACK);

        leftButton = createDirectionButton("LEFT");
        rightButton = createDirectionButton("RIGHT");

        directionPanel.add(leftButton);
        directionPanel.add(rightButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(dialogBox, BorderLayout.CENTER);
        bottomPanel.add(directionPanel, BorderLayout.SOUTH);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
        gamePanel.revalidate();
        gamePanel.repaint();

        if (bossEnterClip != null) {
            gainControl = (FloatControl) bossEnterClip.getControl(FloatControl.Type.MASTER_GAIN);
            if (!bossEnterClip.isRunning()) {
                bossEnterClip.setFramePosition(0);
                bossEnterClip.start();
            }
        }

        dialogBox.showText("The sound grows quieter when you choose correctly...\nFind your way out!", null);
    }

    private void showError(String message) {
        System.err.println(message);
        dialogBox.showText("ERROR: " + message, () -> {
            cleanupAudioResources();
            winButton.setEnabled(true);
            winButton.setVisible(true);
        });
    }

    private void loadDevilImage() {
        try {
            URL imageUrl = getClass().getResource("/image/Darkness_Devil.jpg");
            if (imageUrl != null) {
                BufferedImage img = ImageIO.read(imageUrl);
                if (img != null) {
                    imagePanel.startGrowingImageFromBufferedImage(img);
                    return;
                }
            }

            BufferedImage fallbackImage = new BufferedImage(200, 300, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = fallbackImage.createGraphics();
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, 200, 300);
            g2d.dispose();

            imagePanel.startGrowingImageFromBufferedImage(fallbackImage);
        } catch (Exception ex) {
            System.err.println("Error loading image: " + ex.getMessage());
        }
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        if (!dialogBox.isTyping()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (leftButton != null && leftButton.isVisible()) {
                        leftButton.doClick();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (rightButton != null && rightButton.isVisible()) {
                        rightButton.doClick();
                    }
                    break;
            }
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return "ANCIENT TEMPLE";
    }

    @Override
    protected String getIntroText() {
        return "";
    }

    private void cleanupAudioResources() {
        if (dungeonEntranceClip != null) {
            dungeonEntranceClip.stop();
            dungeonEntranceClip.close();
        }

        if (bossEnterClip != null) {
            bossEnterClip.stop();
            bossEnterClip.close();
        }
    }

    private class GrowingImagePanel extends JPanel {
        private ImageIcon gifIcon;
        private boolean showGif = false;
        private Image devilImage;
        private Timer growthTimer;
        private double currentScale = 0.0;
        private final double SCALE_INCREASE = 0.05;
        private final double MAX_SCALE = 1.0;


        private final double ORIGIN_X_PERCENT = 0.5;
        private final double ORIGIN_Y_PERCENT = 0.15;

        private int originX = 0;
        private int originY = 0;

        public GrowingImagePanel() {
            setPreferredSize(new Dimension(600, 400));
            setBackground(Color.BLACK);
        }

        public void startGifAnimation(URL gifUrl) {
            try {
                gifIcon = new ImageIcon(gifUrl);
                showGif = true;
                repaint();
            } catch (Exception ex) {
                System.err.println("Error loading GIF: " + ex.getMessage());
            }
        }

        public void stopGifAnimation() {
            showGif = false;
            repaint();
        }

        public void startGrowingImageFromBufferedImage(BufferedImage image) {
            try {
                devilImage = image;
                startImageGrowthAnimation();
            } catch (Exception ex) {
                System.err.println("Error setting up image animation: " + ex.getMessage());
            }
        }

        private void startImageGrowthAnimation() {
            currentScale = 0.0;

            if (growthTimer != null && growthTimer.isRunning()) {
                growthTimer.stop();
            }

            growthTimer = new Timer(50, e -> {
                currentScale += SCALE_INCREASE;
                if (currentScale >= MAX_SCALE) {
                    currentScale = MAX_SCALE;
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            growthTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (gifIcon != null && showGif) {
                int gifWidth = gifIcon.getIconWidth();
                int gifHeight = gifIcon.getIconHeight();
                int gifX = (panelWidth - gifWidth) / 2;
                int gifY = (panelHeight - gifHeight) / 2;

                gifIcon.paintIcon(this, g, gifX, gifY);

                originX = gifX + (int)(gifWidth * ORIGIN_X_PERCENT);
                originY = gifY + (int)(gifHeight * ORIGIN_Y_PERCENT);
            }

            if (devilImage != null && currentScale > 0.0) {
                int imgFullWidth = devilImage.getWidth(this);
                int imgFullHeight = devilImage.getHeight(this);

                if (imgFullWidth > 0 && imgFullHeight > 0) {
                    int currentWidth = (int)(imgFullWidth * currentScale);
                    int currentHeight = (int)(imgFullHeight * currentScale);

                    int imgX = originX - (currentWidth / 2);
                    int imgY = originY - (int)(currentHeight * ORIGIN_Y_PERCENT);

                    g2d.drawImage(
                            devilImage,
                            imgX, imgY,
                            imgX + currentWidth, imgY + currentHeight,
                            0, 0,
                            imgFullWidth, imgFullHeight,
                            this
                    );
                }
            }
        }
    }
}