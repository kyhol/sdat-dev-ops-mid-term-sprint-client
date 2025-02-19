package com.keyin.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.FlowLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.Box;

import com.keyin.hero.HeroDTO;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationDTO;
import com.keyin.location.LocationService;

public class GameInterfaceGUI extends JFrame {
    private final HeroService heroService;
    private final LocationService locationService;
    private HeroDTO currentHero;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea outputArea;
    private JPanel locationButtonPanel;

    private int plushiesCollected = 0;
    private List<Long> completedLocations = new ArrayList<>();
    private List<LocationDTO> allLocations = new ArrayList<>();

    public GameInterfaceGUI(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createWelcomePanel();
        createHeroCreationPanel();
        createStartPanel();
        createLocationSelectionPanel();
        createMiniGamePanel();
        createFinalBossPanel();
        createFinalCongratulationsPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("RPG Adventure", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "heroCreation"));
        panel.add(startButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "welcome");
    }
    private void createHeroCreationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel nameLabel = new JLabel("Enter your hero's name: ");
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Hero");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // Update instead of create
                    currentHero = heroService.updateHero(name);
                    JOptionPane.showMessageDialog(this, "Hero name set to " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Go directly to start panel
                    cardLayout.show(mainPanel, "startPanel");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating hero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(createButton);

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(inputPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());

        panel.add(formPanel);
        mainPanel.add(panel, "heroCreation");
    }

    private void createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Press any key to start your adventure!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        JButton pressKeyButton = new JButton("Press any key");
        pressKeyButton.addActionListener(e -> cardLayout.show(mainPanel, "locationSelection"));
        panel.add(pressKeyButton, BorderLayout.SOUTH);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                cardLayout.show(mainPanel, "locationSelection");
            }
        });
        panel.setFocusable(true);

        mainPanel.add(panel, "startPanel");
    }
    private void createLocationSelectionPanel() {
        JPanel locationSelectionPanel = new JPanel(new BorderLayout());
        JLabel prompt = new JLabel("Choose a location to explore:", SwingConstants.CENTER);
        prompt.setFont(new Font("Arial", Font.BOLD, 18));
        locationSelectionPanel.add(prompt, BorderLayout.NORTH);

        locationButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(locationButtonPanel);
        locationSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Locations");
        refreshButton.addActionListener(e -> {
            try {
                allLocations = locationService.getAllLocations();
                populateLocationButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching locations: " + ex.getMessage());
            }
        });
        locationSelectionPanel.add(refreshButton, BorderLayout.SOUTH);

        locationSelectionPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                try {
                    allLocations = locationService.getAllLocations();
                    populateLocationButtons();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GameInterfaceGUI.this, "Error fetching locations: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(locationSelectionPanel, "locationSelection");
    }

    private void populateLocationButtons() {
        locationButtonPanel.removeAll();
        for (LocationDTO location : allLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.addActionListener((ActionEvent e) -> {
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this, "This location has already been completed!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        locationService.moveToNextLocation(currentHero.getId());
                    } catch (Exception ex) {
                        System.err.println("Error moving to location: " + ex.getMessage());
                    }
                    completedLocations.add(location.getId());
                    plushiesCollected++;

                    outputArea.setText("You have entered " + location.getName() + ".\nPlaying mini-game...");
                    cardLayout.show(mainPanel, "miniGame");
                }
            });
            if (completedLocations.contains(location.getId())) {
                locButton.setEnabled(false);
            }
            locationButtonPanel.add(locButton);
        }
        locationButtonPanel.revalidate();
        locationButtonPanel.repaint();
    }
    private void createMiniGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Mini-Game");
        winButton.addActionListener(e -> {
            if (plushiesCollected < 5) {
                cardLayout.show(mainPanel, "startPanel");
            } else {
                cardLayout.show(mainPanel, "finalBoss");
            }
        });
        JButton loseButton = new JButton("Lose Mini-Game");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost the mini-game. Game Over!");
            restartGame();
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "miniGame");
    }

    private void createFinalBossPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("FINAL BOSS!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Final Boss");
        winButton.addActionListener(e -> {
            outputArea.setText("Fighting final boss...\nVictory!");
            cardLayout.show(mainPanel, "finalCongratulations");
        });
        JButton loseButton = new JButton("Lose Final Boss");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost to the final boss. Game Over!");
            restartGame();
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalBoss");
    }
    private void createFinalCongratulationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Congratulations! You've completed the adventure!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        panel.add(quitButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalCongratulations");
    }

    private void restartGame() {
        plushiesCollected = 0;
        completedLocations.clear();
        try {
            allLocations = locationService.getAllLocations();
            populateLocationButtons();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error resetting game: " + ex.getMessage());
        }
        cardLayout.show(mainPanel, "welcome");
    }

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";
        HeroService heroService = new HeroService(baseUrl);
        LocationService locationService = new LocationService(baseUrl);
        SwingUtilities.invokeLater(() -> {
            GameInterfaceGUI gui = new GameInterfaceGUI(heroService, locationService);
            gui.setVisible(true);
        });
    }
}