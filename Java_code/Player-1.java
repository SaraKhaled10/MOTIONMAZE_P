//package com.motionmaze.game;

/**
 * Player in the game.
 */
public class Player {
    private int x;
    private int y;
    private int health;
    private int score;
    private boolean hasKey;
    private boolean trapped;
    private boolean shielded;
    private boolean slowed;
    private boolean speedBoost;
    private long statusEffectEndTime;

    /**
     * Constructor for creating a new player.
     *
     * @param x X position
     * @param y Y position
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.score = 0;
        this.hasKey = false;
        this.trapped = false;
        this.shielded = false;
        this.slowed = false;
        this.speedBoost = false;
        this.statusEffectEndTime = 0;
    }

    /**
     * Move the player.
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public void move(int dx, int dy) {
        // If trapped, cannot move
        if (trapped) {
            return;
        }

        // If slowed, only move at half speed (50% chance to not move)
        if (slowed && Math.random() < 0.5) {
            return;
        }

        // If speed boost, move twice as fast
        if (speedBoost) {
            this.x += dx * 2;
            this.y += dy * 2;
        } else {
            this.x += dx;
            this.y += dy;
        }
    }

    /**
     * Move the player to a specific position.
     *
     * @param x X position
     * @param y Y position
     */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Take damage.
     *
     * @param damage Damage to take
     */
    public void takeDamage(int damage) {
        // If shielded, take no damage
        if (shielded) {
            removeShield();
            return;
        }

        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Heal the player.
     *
     * @param heal Amount to heal
     */
    public void heal(int heal) {
        this.health += heal;
        if (this.health > 100) {
            this.health = 100;
        }
    }

    /**
     * Reset the player state.
     */
    public void reset() {
        this.health = 100;
        this.hasKey = false;
        this.trapped = false;
        this.shielded = false;
        this.slowed = false;
        this.speedBoost = false;
        this.statusEffectEndTime = 0;
    }

    /**
     * Trap the player for a period of time.
     *
     * @param durationMs Duration in milliseconds
     */
    public void trap(long durationMs) {
        // If shielded, cannot be trapped
        if (shielded) {
            removeShield();
            return;
        }

        this.trapped = true;
        this.statusEffectEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * Release the player from a trap.
     */
    public void release() {
        this.trapped = false;
    }

    /**
     * Add a shield to the player.
     *
     * @param durationMs Duration in milliseconds
     */
    public void addShield(long durationMs) {
        this.shielded = true;
        this.statusEffectEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * Remove the shield from the player.
     */
    public void removeShield() {
        this.shielded = false;
    }

    /**
     * Slow the player for a period of time.
     *
     * @param durationMs Duration in milliseconds
     */
    public void slow(long durationMs) {
        // If shielded, cannot be slowed
        if (shielded) {
            removeShield();
            return;
        }

        this.slowed = true;
        this.statusEffectEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * Remove the slowness effect from the player.
     */
    public void removeSlowness() {
        this.slowed = false;
    }

    /**
     * Add a speed boost to the player.
     *
     * @param durationMs Duration in milliseconds
     */
    public void addSpeedBoost(long durationMs) {
        this.speedBoost = true;
        this.statusEffectEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * Remove the speed boost from the player.
     */
    public void removeSpeedBoost() {
        this.speedBoost = false;
    }

    /**
     * Get the player's X position.
     *
     * @return X position
     */
    public int getX() {
        return x;
    }

    /**
     * Get the player's Y position.
     *
     * @return Y position
     */
    public int getY() {
        return y;
    }

    /**
     * Get the player's health.
     *
     * @return Health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the player's health.
     *
     * @param health New health
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Get the player's score.
     *
     * @return Score
     */
    public int getScore() {
        return score;
    }

    /**
     * Increase the player's score.
     *
     * @param amount Amount to increase by
     */
    public void increaseScore(int amount) {
        this.score += amount;
    }

    /**
     * Check if the player has a key.
     *
     * @return true if the player has a key, false otherwise
     */
    public boolean hasKey() {
        return hasKey;
    }

    /**
     * Set whether the player has a key.
     *
     * @param hasKey Whether the player has a key
     */
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    /**
     * Check if the player is trapped.
     *
     * @return true if the player is trapped, false otherwise
     */
    public boolean isTrapped() {
        return trapped;
    }

    /**
     * Check if the player is shielded.
     *
     * @return true if the player is shielded, false otherwise
     */
    public boolean isShielded() {
        return shielded;
    }

    /**
     * Check if the player is slowed.
     *
     * @return true if the player is slowed, false otherwise
     */
    public boolean isSlowed() {
        return slowed;
    }

    /**
     * Check if the player has a speed boost.
     *
     * @return true if the player has a speed boost, false otherwise
     */
    public boolean hasSpeedBoost() {
        return speedBoost;
    }

    /**
     * Get the time when the current status effect ends.
     *
     * @return Status effect end time in milliseconds
     */
    public long getStatusEffectEndTime() {
        return statusEffectEndTime;
    }
}
