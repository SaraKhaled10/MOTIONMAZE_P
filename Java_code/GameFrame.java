//package com.motionmaze.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Game frame that displays the game.
 */
public class GameFrame extends JFrame {
    private GameEngine gameEngine;
    private GamePanel gamePanel;

    /**
     * Constructor for creating a new game frame.
     *
     * @param gameEngine Game engine
     */
    public GameFrame(GameEngine gameEngine) {
        this.gameEngine = gameEngine;

        // Set up the frame
        setTitle("Motion Maze");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create the game panel
        gamePanel = new GamePanel(gameEngine);

        // Add the game panel to a scroll pane to handle large mazes
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Add a status panel at the bottom
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add a help label
        JLabel helpLabel = new JLabel("Use Arrow Keys to move or simulate motion control with keyboard.");
        statusPanel.add(helpLabel, BorderLayout.WEST);

        // Add a ZigBee status label
        JLabel zigbeeLabel = new JLabel("ZigBee: " + (gameEngine.getZigBeeManager().isConnected() ? "Connected" : "Disconnected"));
        statusPanel.add(zigbeeLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // Add keyboard controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Ensure the frame can receive key events
        setFocusable(true);

        // Pack the frame and center it on the screen
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Handle a key press event.
     *
     * @param e Key event
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameEngine.processMovement(1); // Up
                break;

            case KeyEvent.VK_RIGHT:
                gameEngine.processMovement(2); // Right
                break;

            case KeyEvent.VK_DOWN:
                gameEngine.processMovement(3); // Down
                break;

            case KeyEvent.VK_LEFT:
                gameEngine.processMovement(4); // Left
                break;

            case KeyEvent.VK_SPACE:
                gameEngine.processButtonPress(); // Button press
                break;

            case KeyEvent.VK_SHIFT:
                gameEngine.processMotionDetection(); // Motion detection
                break;

            case KeyEvent.VK_R:
                gameEngine.resetGame(); // Reset game
                break;

            case KeyEvent.VK_Q:
                System.exit(0); // Quit game
                break;
        }
    }

    /**
     * Update the game frame.
     */
    public void update() {
        gamePanel.repaint();
    }
}
