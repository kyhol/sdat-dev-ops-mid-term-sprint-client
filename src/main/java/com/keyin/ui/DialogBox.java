package com.keyin.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialogBox extends JPanel {
    private final JTextArea textArea;
    private final JLabel continueIndicator;
    private final Timer typingTimer;
    private final Timer indicatorTimer;
    private String fullText = "";
    private int currentIndex = 0;
    private Runnable onCompleteCallback;
    private boolean isTyping = false;

    public DialogBox() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        setPreferredSize(new Dimension(600, 100));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        // Create the continue indicator
        continueIndicator = new JLabel("* press any key to continue");
        continueIndicator.setForeground(Color.WHITE);
        continueIndicator.setFont(new Font("Monospaced", Font.PLAIN, 12));
        continueIndicator.setHorizontalAlignment(SwingConstants.RIGHT);
        continueIndicator.setVisible(false);

        // Add components to panel
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.add(textArea, BorderLayout.CENTER);
        textPanel.add(continueIndicator, BorderLayout.SOUTH);
        add(textPanel, BorderLayout.CENTER);

        // Create typing timer
        typingTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex < fullText.length()) {
                    textArea.setText(fullText.substring(0, currentIndex + 1));
                    currentIndex++;
                } else {
                    completeTyping();
                }
            }
        });

        // Create timer for showing the continue indicator
        indicatorTimer = new Timer(2000, e -> {
            continueIndicator.setVisible(true);
            ((Timer)e.getSource()).stop();
        });

        // Add click listener to show all text immediately
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isTyping) {
                    completeTyping();
                }
            }
        });
    }

    private void completeTyping() {
        typingTimer.stop();
        textArea.setText(fullText);
        isTyping = false;
        // Start the timer to show the continue indicator
        indicatorTimer.restart();
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
    }

    public void showText(String text) {
        showText(text, null);
    }

    public void showText(String text, Runnable onComplete) {
        this.fullText = text;
        this.currentIndex = 0;
        this.onCompleteCallback = onComplete;
        this.isTyping = true;
        continueIndicator.setVisible(false);
        indicatorTimer.stop();
        typingTimer.start();
    }

    public void clear() {
        typingTimer.stop();
        indicatorTimer.stop();
        textArea.setText("");
        continueIndicator.setVisible(false);
        fullText = "";
        currentIndex = 0;
        isTyping = false;
    }

    public boolean isTyping() {
        return isTyping;
    }
}