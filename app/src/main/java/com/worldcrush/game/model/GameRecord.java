package com.worldcrush.game.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_records")
public class GameRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public int score;
    public int wordCount;
    public String longestWord;
    public int gridSize;
    public int moveCount;
    public long duration;
    public long date;
}