//package com.motionmaze.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Maze for the game.
 */
public class Maze {
    private int width;
    private int height;
    private int level;
    private Cell[][] cells;
    private int startX;
    private int startY;
    private int finishX;
    private int finishY;
    private List<PowerUp> powerUps;
    private List<Obstacle> obstacles;

    /**
     * Constructor for creating a new maze.
     *
     * @param width Width of the maze
     * @param height Height of the maze
     * @param level Level of the maze
     */
    public Maze(int width, int height, int level) {
        this.width = width;
        this.height = height;
        this.level = level;
        this.cells = new Cell[width][height];
        this.powerUps = new ArrayList<>();
        this.obstacles = new ArrayList<>();

        // Initialize cells
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        // Generate the maze
        generateMaze();

        // Set start and finish points
        this.startX = 0;
        this.startY = 0;
        this.finishX = width - 1;
        this.finishY = height - 1;

        // Add power-ups and obstacles
        addPowerUpsAndObstacles();
    }

    /**
     * Generate the maze using a depth-first search algorithm.
     */
    private void generateMaze() {
        Random random = new Random();
        Stack<Cell> stack = new Stack<>();
        Cell current = cells[0][0];
        current.setVisited(true);

        // Depth-first search
        do {
            List<Cell> unvisitedNeighbors = getUnvisitedNeighbors(current);

            if (!unvisitedNeighbors.isEmpty()) {
                // Choose a random unvisited neighbor
                Cell next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));

                // Remove the wall between the current cell and the chosen cell
                removeWalls(current, next);

                // Mark the chosen cell as visited and push it to the stack
                next.setVisited(true);
                stack.push(current);
                current = next;
            } else if (!stack.isEmpty()) {
                // Backtrack
                current = stack.pop();
            }
        } while (!stack.isEmpty());

        // Reset visited flags
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].setVisited(false);
            }
        }
    }

    /**
     * Get the unvisited neighbors of a cell.
     *
     * @param cell Cell to get the neighbors of
     * @return List of unvisited neighbors
     */
    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.getX();
        int y = cell.getY();

        // Check the four adjacent cells
        if (x > 0 && !cells[x - 1][y].isVisited()) {
            neighbors.add(cells[x - 1][y]);
        }

        if (x < width - 1 && !cells[x + 1][y].isVisited()) {
            neighbors.add(cells[x + 1][y]);
        }

        if (y > 0 && !cells[x][y - 1].isVisited()) {
            neighbors.add(cells[x][y - 1]);
        }

        if (y < height - 1 && !cells[x][y + 1].isVisited()) {
            neighbors.add(cells[x][y + 1]);
        }

        return neighbors;
    }

    /**
     * Remove the walls between two cells.
     *
     * @param current Current cell
     * @param next Next cell
     */
    private void removeWalls(Cell current, Cell next) {
        int dx = next.getX() - current.getX();
        int dy = next.getY() - current.getY();

        if (dx == 1) {
            current.setRightWall(false);
            next.setLeftWall(false);
        } else if (dx == -1) {
            current.setLeftWall(false);
            next.setRightWall(false);
        } else if (dy == 1) {
            current.setBottomWall(false);
            next.setTopWall(false);
        } else if (dy == -1) {
            current.setTopWall(false);
            next.setBottomWall(false);
        }
    }

    /**
     * Add power-ups and obstacles to the maze.
     */
    private void addPowerUpsAndObstacles() {
        Random random = new Random();

        // Add power-ups
        int numPowerUps = Math.min(5, level + 2);
        for (int i = 0; i < numPowerUps; i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while ((x == startX && y == startY) || (x == finishX && y == finishY));

            PowerUpType[] types = PowerUpType.values();
            PowerUpType type = types[random.nextInt(types.length)];
            PowerUp powerUp = new PowerUp(x, y, type);
            powerUps.add(powerUp);
        }

        // Add obstacles
        int numObstacles = Math.min(3, level);
        for (int i = 0; i < numObstacles; i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while ((x == startX && y == startY) || (x == finishX && y == finishY));

            ObstacleType[] types = ObstacleType.values();
            ObstacleType type = types[random.nextInt(types.length)];
            Obstacle obstacle = new Obstacle(x, y, type);
            obstacles.add(obstacle);
        }
    }

    /**
     * Check if a move is valid.
     *
     * @param x X position
     * @param y Y position
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int x, int y) {
        // Check if the position is within the maze bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        // Check if there is a wall blocking the move
        // This would be more complex in a real implementation
        return true;
    }

    /**
     * Check if a position is the exit of the maze.
     *
     * @param x X position
     * @param y Y position
     * @return true if the position is the exit, false otherwise
     */
    public boolean isExit(int x, int y) {
        return x == finishX && y == finishY;
    }

    /**
     * Get a power-up at a specific position.
     *
     * @param x X position
     * @param y Y position
     * @return Power-up at the position, or null if there is none
     */
    public PowerUp getPowerUpAt(int x, int y) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.getX() == x && powerUp.getY() == y) {
                return powerUp;
            }
        }

        return null;
    }

    /**
     * Get an obstacle at a specific position.
     *
     * @param x X position
     * @param y Y position
     * @return Obstacle at the position, or null if there is none
     */
    public Obstacle getObstacleAt(int x, int y) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getX() == x && obstacle.getY() == y) {
                return obstacle;
            }
        }

        return null;
    }

    /**
     * Remove a power-up from the maze.
     *
     * @param powerUp Power-up to remove
     */
    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    /**
     * Add a power-up to the maze.
     *
     * @param powerUp Power-up to add
     */
    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    /**
     * Remove an obstacle from the maze.
     *
     * @param obstacle Obstacle to remove
     */
    public void removeObstacle(Obstacle obstacle) {
        obstacles.remove(obstacle);
    }

    /**
     * Add an obstacle to the maze.
     *
     * @param obstacle Obstacle to add
     */
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    /**
     * Get the width of the maze.
     *
     * @return Width of the maze
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the maze.
     *
     * @return Height of the maze
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the level of the maze.
     *
     * @return Level of the maze
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the cells of the maze.
     *
     * @return Cells of the maze
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Get the starting X position.
     *
     * @return Starting X position
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Get the starting Y position.
     *
     * @return Starting Y position
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Get the finishing X position.
     *
     * @return Finishing X position
     */
    public int getFinishX() {
        return finishX;
    }

    /**
     * Get the finishing Y position.
     *
     * @return Finishing Y position
     */
    public int getFinishY() {
        return finishY;
    }

    /**
     * Get the power-ups in the maze.
     *
     * @return Power-ups in the maze
     */
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * Get the obstacles in the maze.
     *
     * @return Obstacles in the maze
     */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
