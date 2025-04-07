
import javax.swing.*;
import java.awt.*;

/**
 * Game panel that renders the game.
 * Implements the Observer pattern.
 */
public class GamePanel extends JPanel implements GameObserver {
    private static final int CELL_SIZE = 40;
    private static final int WALL_THICKNESS = 3;

    private GameEngine gameEngine;

    /**
     * Constructor for creating a new game panel.
     *
     * @param gameEngine Game engine to observe
     */
    public GamePanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        gameEngine.addObserver(this);

        // Set up the panel
        setPreferredSize(new Dimension(CELL_SIZE * 15, CELL_SIZE * 15));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Cast to Graphics2D for better rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get game objects
        Maze maze = gameEngine.getMaze();
        Player player = gameEngine.getPlayer();

        // Render the maze
        if (maze != null) {
            renderMaze(g2d, maze);
        }

        // Render power-ups
        if (maze != null) {
            renderPowerUps(g2d, maze);
        }

        // Render obstacles
        if (maze != null) {
            renderObstacles(g2d, maze);
        }

        // Render the player
        if (player != null) {
            renderPlayer(g2d, player);
        }

        // Render game state information
        renderGameInfo(g2d);
    }

    /**
     * Render the maze.
     *
     * @param g2d Graphics2D context
     * @param maze Maze to render
     */
    private void renderMaze(Graphics2D g2d, Maze maze) {
        Cell[][] cells = maze.getCells();

        // Set color for walls
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(WALL_THICKNESS));

        // Draw cells
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                Cell cell = cells[x][y];
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                // Draw walls
                if (cell.hasTopWall()) {
                    g2d.drawLine(cellX, cellY, cellX + CELL_SIZE, cellY);
                }

                if (cell.hasRightWall()) {
                    g2d.drawLine(cellX + CELL_SIZE, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE);
                }

                if (cell.hasBottomWall()) {
                    g2d.drawLine(cellX, cellY + CELL_SIZE, cellX + CELL_SIZE, cellY + CELL_SIZE);
                }

                if (cell.hasLeftWall()) {
                    g2d.drawLine(cellX, cellY, cellX, cellY + CELL_SIZE);
                }
            }
        }

        // Draw start and finish
        g2d.setColor(Color.GREEN);
        g2d.fillRect(maze.getStartX() * CELL_SIZE + CELL_SIZE / 4, maze.getStartY() * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);

        g2d.setColor(Color.RED);
        g2d.fillRect(maze.getFinishX() * CELL_SIZE + CELL_SIZE / 4, maze.getFinishY() * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
    }

    /**
     * Render power-ups.
     *
     * @param g2d Graphics2D context
     * @param maze Maze containing power-ups
     */
    private void renderPowerUps(Graphics2D g2d, Maze maze) {
        for (PowerUp powerUp : maze.getPowerUps()) {
            int x = powerUp.getX() * CELL_SIZE + CELL_SIZE / 4;
            int y = powerUp.getY() * CELL_SIZE + CELL_SIZE / 4;

            // Choose color based on power-up type
            switch (powerUp.getType()) {
                case HEALTH:
                    g2d.setColor(Color.RED);
                    break;

                case SPEED:
                    g2d.setColor(Color.YELLOW);
                    break;

                case KEY:
                    g2d.setColor(Color.ORANGE);
                    break;

                case SCORE_BOOST:
                    g2d.setColor(Color.CYAN);
                    break;

                case INVINCIBILITY:
                    g2d.setColor(Color.MAGENTA);
                    break;
            }

            // Draw power-up as a star
            drawStar(g2d, x + CELL_SIZE / 4, y + CELL_SIZE / 4, CELL_SIZE / 4);
        }
    }

    /**
     * Render obstacles.
     *
     * @param g2d Graphics2D context
     * @param maze Maze containing obstacles
     */
    private void renderObstacles(Graphics2D g2d, Maze maze) {
        for (Obstacle obstacle : maze.getObstacles()) {
            int x = obstacle.getX() * CELL_SIZE + CELL_SIZE / 4;
            int y = obstacle.getY() * CELL_SIZE + CELL_SIZE / 4;

            // Choose color based on obstacle type
            switch (obstacle.getType()) {
                case TRAP:
                    g2d.setColor(Color.RED);
                    break;

                case ENEMY:
                    g2d.setColor(Color.DARK_GRAY);
                    break;

                case PIT:
                    g2d.setColor(Color.BLACK);
                    break;

                case FIRE:
                    g2d.setColor(Color.ORANGE);
                    break;

                case ICE:
                    g2d.setColor(Color.CYAN);
                    break;
            }

            // Draw obstacle as a triangle
            int[] xPoints = {x, x + CELL_SIZE / 2, x + CELL_SIZE / 2 * 2};
            int[] yPoints = {y + CELL_SIZE / 2, y, y + CELL_SIZE / 2};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Render the player.
     *
     * @param g2d Graphics2D context
     * @param player Player to render
     */
    private void renderPlayer(Graphics2D g2d, Player player) {
        int x = player.getX() * CELL_SIZE;
        int y = player.getY() * CELL_SIZE;

        // Draw player as a circle
        g2d.setColor(Color.BLUE);
        g2d.fillOval(x + CELL_SIZE / 4, y + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);

        // Draw player health bar
        g2d.setColor(Color.RED);
        g2d.fillRect(x + CELL_SIZE / 4, y - CELL_SIZE / 6, CELL_SIZE / 2, CELL_SIZE / 8);

        g2d.setColor(Color.GREEN);
        g2d.fillRect(x + CELL_SIZE / 4, y - CELL_SIZE / 6, player.getHealth() * CELL_SIZE / 200, CELL_SIZE / 8);
    }

    /**
     * Render game information.
     *
     * @param g2d Graphics2D context
     */
    private void renderGameInfo(Graphics2D g2d) {
        // Set font and color
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);

        // Draw game information
        g2d.drawString("Level: " + gameEngine.getLevel(), 10, getHeight() - 40);
        g2d.drawString("Score: " + gameEngine.getScore(), 10, getHeight() - 20);

        // Draw game state information
        if (gameEngine.getState() instanceof GameOverState) {
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.setColor(Color.RED);
            g2d.drawString("GAME OVER", getWidth() / 2 - 80, getHeight() / 2);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Press SPACE to try again", getWidth() / 2 - 80, getHeight() / 2 + 30);
        } else if (gameEngine.getState() instanceof CompletedState) {
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.setColor(Color.GREEN);
            g2d.drawString("LEVEL COMPLETE!", getWidth() / 2 - 100, getHeight() / 2);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Press SPACE to continue", getWidth() / 2 - 80, getHeight() / 2 + 30);
        }
    }

    /**
     * Draw a star shape.
     *
     * @param g2d Graphics2D context
     * @param x X position
     * @param y Y position
     * @param radius Radius of the star
     */
    private void drawStar(Graphics2D g2d, int x, int y, int radius) {
        int nPoints = 5;
        int[] xPoints = new int[nPoints * 2];
        int[] yPoints = new int[nPoints * 2];

        double angleStep = Math.PI / nPoints;

        for (int i = 0; i < nPoints * 2; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2;
            double angle = i * angleStep;
            xPoints[i] = (int) (x + r * Math.sin(angle));
            yPoints[i] = (int) (y - r * Math.cos(angle));
        }

        g2d.fillPolygon(xPoints, yPoints, nPoints * 2);
    }

    @Override
    public void onGameUpdate(GameEngine gameEngine) {
        // Repaint the panel when the game is updated
        repaint();
    }
}
