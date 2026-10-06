package com.worldcrush.game.util;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class WordDictionary {
    private static WordDictionary instance;
    private Set<String> words = new HashSet<>();

    private WordDictionary(Context context) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            context.getAssets().open("kelimeler.txt"), "UTF-8"
                    )
            );
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.length() >= 3) {
                    words.add(word.toUpperCase(new Locale("tr", "TR")));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WordDictionary getInstance(Context context) {
        if (instance == null) {
            instance = new WordDictionary(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isValidWord(String word) {
        if (word == null || word.length() < 3) return false;
        return words.contains(word.toUpperCase(new Locale("tr", "TR")));
    }

    public int getWordCount() {
        return words.size();
    }
}