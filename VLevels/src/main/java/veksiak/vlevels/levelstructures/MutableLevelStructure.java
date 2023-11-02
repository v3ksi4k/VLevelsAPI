package veksiak.vlevels.levelstructures;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A level structure containing an unlimited or limited number of levels, in which each subsequent level requires an amount of XP changed by a certain amount from the amount of XP required for the previous level.
 */
public final class MutableLevelStructure implements LevelStructure {
    private final boolean infinite, operateOnTotalXP;
    private final int limit;
    private final int startingXPAmount;
    private final String pluginName;
    private final Map<Integer, Integer> definedLevels = new HashMap<>();
    private final Map<Integer, Integer> valueToAdd = new HashMap<>();
    private final IntMathOperation mathOperation;

    /**
     * Formula used to calculate XP for the next level.
     */
    public interface IntMathOperation {
        /**
         * @param xpAmount Amount of XP required to reach level n-1.
         * @return Amount of XP required to reach level n.
         */
        int math(int xpAmount);
    }

    /**
     * @param creatingPlugin   The plugin that creates and uses this structure.
     * @param startingXPAmount Amount of XP required to reach level 2 higher than 0.
     * @param mathOperation    Math operation that will be used to calculate XP required for next levels.
     * @param operateOnTotalXP If true, the calculations will be made on total XP. If false, the calculations will be made on total XP difference between level 2 and 3 [mathOperation(startingXPAmount)-startingXPAmount)].
     * @param preGenerate      Amount of levels to calculate required XP for. This improves the performance of the structure by not requiring it to make the calculations later. If infinite is set to false, this can't be less than 2.
     * @param infinite         Determines whether the structure should be infinite. If it is, its limit will be set to preGenerate. Using this option is unsafe. It is recommended to set the limit every time to prevent errors.
     */
    public MutableLevelStructure(Plugin creatingPlugin, int startingXPAmount, IntMathOperation mathOperation, boolean operateOnTotalXP, int preGenerate, boolean infinite) {
        if (startingXPAmount <= 0) {
            throw new IllegalArgumentException("startingXPAmount can't be equal or less than 0. Plugin: " + creatingPlugin.getName());
        }
        this.startingXPAmount = startingXPAmount;
        this.mathOperation = mathOperation;
        this.infinite = infinite;
        this.operateOnTotalXP = operateOnTotalXP;
        this.pluginName = creatingPlugin.getName();
        limit = preGenerate;
        int xp = startingXPAmount;
        definedLevels.put(2, xp);
        if (!operateOnTotalXP) {
            valueToAdd.put(2, mathOperation.math(startingXPAmount) - startingXPAmount);
        }
        if (preGenerate > 2) {
            for (int i = 3; i <= preGenerate; i++) {
                xp = calculateNext(xp, i);
                definedLevels.put(i, xp);
            }
        } else if (!infinite && !(preGenerate == 2)) {
            throw new IllegalArgumentException("preGenerate can't be less than 2 when infinite is set to false. Plugin: " + pluginName);
        }
    }

    /**
     * @param xp Amount of total XP higher or equal to 0.
     * @return The level that a player would have with specified amount of total XP, or 1 if the level is not found.
     */
    @Override
    public Integer getLevelNumber(int xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("XP can't be lower than 0. Plugin: " + pluginName);
        }
        int lastlevel = 2;
        for (int key = 2; key < definedLevels.keySet().size() + 2; key++) {
            if (definedLevels.get(key) > xp) {
                return key - 1;
            } else {
                lastlevel = key;
            }
        }
        int calculatedxp = definedLevels.get(lastlevel);
        int i;
        for (i = lastlevel; xp >= calculatedxp; i++) {
            if (i == limit) {
                return limit;
            }
            definedLevels.put(i, calculatedxp);
            calculatedxp = calculateNext(calculatedxp, i + 1);
        }
        return i - 1;
    }

    /**
     * @param level A level to calculate the xp for. If the structure isn't infinite, this mustn't be above the level limit.
     * @return A minimal amount of total XP required to reach this level.
     */
    @Nullable
    @Override
    public Integer getTotalXPForLevel(int level) {
        if (level > limit && !infinite) {
            throw new IllegalArgumentException("Level cannot be higher than the specified limit. Plugin: " + pluginName);
        }
        if (level == 1) return 0;
        else if (level == 2) return startingXPAmount;
        else {
            if (definedLevels.containsKey(level)) {
                return definedLevels.get(level);
            } else {
                for (int i = level - 1; i > 2; i--) {
                    if (definedLevels.containsKey(i)) {
                        int xp = definedLevels.get(i);
                        for (int j = 1; j <= level - i; j++) {
                            xp = calculateNext(xp, i + j);
                        }
                        return xp;
                    }
                }
            }
        }
        return null;
    }

    private Integer calculateNext(int xp, int level) {
        if (operateOnTotalXP) {
            int mathresult = mathOperation.math(xp);
            if (mathresult <= 0) {
                throw new NumberFormatException("Calculated XP cannot be equal or lower than 0. Plugin: " + pluginName);
            } else if (mathresult <= xp) {
                throw new NumberFormatException("Level n cannot require the same or lower amount of total XP than level n-1. This may happened because of reaching the limits of integer value. Plugin: " + pluginName);
            } else {
                return mathOperation.math(xp);
            }
        } else {
            if (valueToAdd.containsKey(level)) return xp + valueToAdd.get(level);
            for (int i = level - 1; i >= 2; i--) {
                if (valueToAdd.containsKey(i)) {
                    int newresult = valueToAdd.get(i);
                    int result;
                    for (int j = 1; j < level - i; j++) {
                        result = newresult;
                        newresult = mathOperation.math(newresult);
                        if (result >= newresult) {
                            throw new NumberFormatException("Level n cannot require the same or lower amount of total XP than level n-1. This may happened because of reaching the limits of integer value. Plugin: " + pluginName);
                        } else if (newresult <= 0) {
                            throw new NumberFormatException("Calculated XP cannot be equal or lower than 0. Plugin: " + pluginName);
                        }
                        valueToAdd.put(i + j, newresult);
                    }
                    return newresult + xp;
                }
            }
            throw new IllegalArgumentException("Failed to calculate XP for next level. Please verify the arguments passed into the constructor. Plugin: " + pluginName);
        }
    }
}

