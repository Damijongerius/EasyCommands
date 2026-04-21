package com.dami.easyCommands.Util;

import java.util.Collection;

public class StringUtil {

    /**
     * Calculates the Levenshtein distance between two strings.
     */
    public static int getLevenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1]
                                    + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1),
                            dp[i][j - 1] + 1);
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    public static String findClosestMatch(String input, Collection<String> options) {
        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (String option : options) {
            int distance = getLevenshteinDistance(input.toLowerCase(), option.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closest = option;
            }
        }

        // Only return if it's reasonably close (e.g., distance < 3 or less than half the length)
        if (closest != null && (minDistance < 3 || minDistance < closest.length() / 2)) {
            return closest;
        }
        return null;
    }
}
