//package com.motionmaze.game;

/**
 * Cell in the maze.
 */
public class Cell {
    private int x;
    private int y;
    private boolean visited;
    private boolean topWall;
    private boolean rightWall;
    private boolean bottomWall;
    private boolean leftWall;

    /**
     * Constructor for creating a new cell.
     *
     * @param x X position
     * @param y Y position
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.topWall = true;
        this.rightWall = true;
        this.bottomWall = true;
        this.leftWall = true;
    }

    /**
     * Get the X position.
     *
     * @return X position
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y position.
     *
     * @return Y position
     */
    public int getY() {
        return y;
    }

    /**
     * Check if the cell has been visited.
     *
     * @return true if the cell has been visited, false otherwise
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Set whether the cell has been visited.
     *
     * @param visited Whether the cell has been visited
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Check if the top wall is present.
     *
     * @return true if the top wall is present, false otherwise
     */
    public boolean hasTopWall() {
        return topWall;
    }

    /**
     * Set whether the top wall is present.
     *
     * @param topWall Whether the top wall is present
     */
    public void setTopWall(boolean topWall) {
        this.topWall = topWall;
    }

    /**
     * Check if the right wall is present.
     *
     * @return true if the right wall is present, false otherwise
     */
    public boolean hasRightWall() {
        return rightWall;
    }

    /**
     * Set whether the right wall is present.
     *
     * @param rightWall Whether the right wall is present
     */
    public void setRightWall(boolean rightWall) {
        this.rightWall = rightWall;
    }

    /**
     * Check if the bottom wall is present.
     *
     * @return true if the bottom wall is present, false otherwise
     */
    public boolean hasBottomWall() {
        return bottomWall;
    }

    /**
     * Set whether the bottom wall is present.
     *
     * @param bottomWall Whether the bottom wall is present
     */
    public void setBottomWall(boolean bottomWall) {
        this.bottomWall = bottomWall;
    }

    /**
     * Check if the left wall is present.
     *
     * @return true if the left wall is present, false otherwise
     */
    public boolean hasLeftWall() {
        return leftWall;
    }

    /**
     * Set whether the left wall is present.
     *
     * @param leftWall Whether the left wall is present
     */
    public void setLeftWall(boolean leftWall) {
        this.leftWall = leftWall;
    }
}
