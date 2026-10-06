package com.worldcrush.game.util;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComboHelper {

    public static class ComboResult {
        public List<String> foundWords = new ArrayList<>();
        public int totalScore = 0;
        public int comboCount = 0;
    }

    public static ComboResult findCombos(String mainWord, Context context) {
        ComboResult result = new ComboResult();
        WordDictionary dict = WordDictionary.getInstance(context);

        Set<String> uniqueWords = new HashSet<>();


        int n = mainWord.length();
        for (int i = 0; i < n; i++) {
            for (int j = i + 3; j <= n; j++) {
                String sub = mainWord.substring(i, j);
                if (dict.isValidWord(sub) && !uniqueWords.contains(sub)) {
                    uniqueWords.add(sub);
                    result.foundWords.add(sub);
                    result.totalScore += LetterGenerator.calculateWordScore(sub);
                }
            }
        }

        if (!uniqueWords.contains(mainWord) && dict.isValidWord(mainWord)) {
            uniqueWords.add(mainWord);
            result.foundWords.add(mainWord);
            result.totalScore += LetterGenerator.calculateWordScore(mainWord);
        }

        result.comboCount = result.foundWords.size();
        return result;
    }
}