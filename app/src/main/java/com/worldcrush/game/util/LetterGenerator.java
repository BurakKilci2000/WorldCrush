package com.worldcrush.game.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LetterGenerator {


    private static final char[] LETTERS = {

            'A', 'E', 'İ', 'I', 'O', 'U', 'Ö', 'Ü',
            'L', 'R', 'N', 'K', 'M', 'T', 'S', 'Y', 'D', 'B',
            'C', 'Ç', 'Ş', 'Z', 'H', 'G', 'P',
            'F', 'V', 'Ğ', 'J'
    };

    private static final int[] WEIGHTS = {

            13, 10, 8, 4, 4, 3, 1, 2,
            7, 7, 6, 5, 4, 4, 4, 3, 2, 1,
            1, 1, 1, 1, 1, 1, 2,
            1, 1, 1, 1
    };

    private static final Map<Character, Integer> POINTS = new HashMap<>();
    static {
        POINTS.put('A', 1); POINTS.put('B', 3); POINTS.put('C', 4); POINTS.put('Ç', 4);
        POINTS.put('D', 3); POINTS.put('E', 1); POINTS.put('F', 7); POINTS.put('G', 5);
        POINTS.put('Ğ', 8); POINTS.put('H', 5); POINTS.put('I', 2); POINTS.put('İ', 1);
        POINTS.put('J', 10); POINTS.put('K', 1); POINTS.put('L', 1); POINTS.put('M', 2);
        POINTS.put('N', 1); POINTS.put('O', 2); POINTS.put('Ö', 7); POINTS.put('P', 5);
        POINTS.put('R', 1); POINTS.put('S', 2); POINTS.put('Ş', 4); POINTS.put('T', 1);
        POINTS.put('U', 2); POINTS.put('Ü', 3); POINTS.put('V', 7); POINTS.put('Y', 3);
        POINTS.put('Z', 4);
    }

    private static final Random random = new Random();
    private static int totalWeight = 0;

    static {
        for (int w : WEIGHTS) totalWeight += w;
    }

    public static char randomLetter() {
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            cumulative += WEIGHTS[i];
            if (roll < cumulative) return LETTERS[i];
        }
        return 'A';
    }

    public static int getPoint(char letter) {
        Integer p = POINTS.get(Character.toUpperCase(letter));
        return p != null ? p : 0;
    }


    public static int calculateWordScore(String word) {
        int total = 0;
        for (char c : word.toCharArray()) {
            total += getPoint(c);
        }
        return total;
    }
    public static boolean isVowel(char c) {
        char uc = Character.toUpperCase(c);
        return uc == 'A' || uc == 'E' || uc == 'I' || uc == 'İ' ||
                uc == 'O' || uc == 'U' || uc == 'Ö' || uc == 'Ü';
    }
}