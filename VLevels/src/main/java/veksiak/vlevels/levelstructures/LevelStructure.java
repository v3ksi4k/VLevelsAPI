package veksiak.vlevels.levelstructures;

/**
 * Interface used to create LevelStructures.
 */
public interface LevelStructure {
    /**
     * @return The level that a player would have with specified amount of total XP.
     */
    Integer getLevelNumber(int xp);

    /**
     * @return The minimal amount of total XP required for a specified level.
     */
    Integer getTotalXPForLevel(int level);
}
