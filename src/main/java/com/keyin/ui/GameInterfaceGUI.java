package com.keyin.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

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
    
    public GameInterfaceGUI(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        createWelcomePanel();
        createHeroCreationPanel();
        createMainGamePanel();
        
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
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter your hero's name: "));
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameField);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JButton createButton = new JButton("Create Hero");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    currentHero = heroService.createHero(name);
                    JOptionPane.showMessageDialog(this, "Hero " + name + " created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "mainGame");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating hero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(createButton, BorderLayout.SOUTH);
        
        mainPanel.add(panel, "heroCreation");
    }

    private void createMainGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton viewLocationsButton = new JButton("View Areas");
        viewLocationsButton.addActionListener(e -> viewLocations());
        JButton viewPlushiesButton = new JButton("View Plushies");
        viewPlushiesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Plushies feature coming soon!"));
        JButton viewHeroStatusButton = new JButton("View Hero Status");
        viewHeroStatusButton.addActionListener(e -> viewHeroStatus());
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(viewLocationsButton);
        buttonPanel.add(viewPlushiesButton);
        buttonPanel.add(viewHeroStatusButton);
        buttonPanel.add(quitButton);
        panel.add(buttonPanel, BorderLayout.WEST);
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        mainPanel.add(panel, "mainGame");
    }

    private void viewLocations() {
        try {
            List<LocationDTO> locations = locationService.getAllLocations();
            StringBuilder sb = new StringBuilder();
            if (locations.isEmpty()) {
                sb.append("No locations found!");
            } else {
                sb.append("Available Locations:\n");
                for (LocationDTO location : locations) {
                    sb.append(location.getName()).append(":\n");
                    sb.append("  ").append(location.getDescription()).append("\n\n");
                }
            }
            outputArea.setText(sb.toString());
        } catch (Exception e) {
            outputArea.setText("Error fetching locations: " + e.getMessage());
        }
    }

    private void viewHeroStatus() {
        try {
            if (currentHero == null) {
                outputArea.setText("Error: Hero information not available");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Hero Status:\n");
            sb.append("------------\n");
            sb.append("Name: ").append(currentHero.getName()).append("\n");
            sb.append("Created: ").append(currentHero.getCreatedAt()).append("\n");
            outputArea.setText(sb.toString());
        } catch (Exception e) {
            outputArea.setText("Error fetching hero status: " + e.getMessage());
        }
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
