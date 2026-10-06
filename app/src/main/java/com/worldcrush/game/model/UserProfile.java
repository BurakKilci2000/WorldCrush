package com.worldcrush.game.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfile {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public int totalGold;
    public int totalGames;
    public int highScore;
    public int totalWords;

    public int jokerFish;
    public int jokerWheel;
    public int jokerLollipop;
    public int jokerSwap;
    public int jokerShuffle;
    public int jokerParty;
}