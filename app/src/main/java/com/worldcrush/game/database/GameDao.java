package com.worldcrush.game.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.worldcrush.game.model.GameRecord;
import com.worldcrush.game.model.UserProfile;
import java.util.List;

@Dao
public interface GameDao {

    @Insert
    void insertGame(GameRecord game);

    @Query("SELECT * FROM game_records ORDER BY date DESC")
    List<GameRecord> getAllGames();

    @Query("SELECT MAX(score) FROM game_records")
    int getHighScore();

    @Query("SELECT COUNT(*) FROM game_records")
    int getTotalGames();

    @Query("SELECT SUM(wordCount) FROM game_records")
    int getTotalWords();

    @Insert
    void insertUser(UserProfile user);

    @Query("SELECT * FROM user_profile LIMIT 1")
    UserProfile getUser();

    @Query("UPDATE user_profile SET username = :name WHERE id = 1")
    void updateUsername(String name);

    @Query("UPDATE user_profile SET totalGold = :gold WHERE id = 1")
    void updateGold(int gold);

    @Query("UPDATE user_profile SET totalGold = totalGold - :amount WHERE id = 1")
    void decreaseGold(int amount);

    @Query("SELECT totalGold FROM user_profile LIMIT 1")
    int getGold();

    @Query("UPDATE user_profile SET jokerFish = jokerFish + 1 WHERE id = 1")
    void addJokerFish();

    @Query("UPDATE user_profile SET jokerWheel = jokerWheel + 1 WHERE id = 1")
    void addJokerWheel();

    @Query("UPDATE user_profile SET jokerLollipop = jokerLollipop + 1 WHERE id = 1")
    void addJokerLollipop();

    @Query("UPDATE user_profile SET jokerSwap = jokerSwap + 1 WHERE id = 1")
    void addJokerSwap();

    @Query("UPDATE user_profile SET jokerShuffle = jokerShuffle + 1 WHERE id = 1")
    void addJokerShuffle();

    @Query("UPDATE user_profile SET jokerParty = jokerParty + 1 WHERE id = 1")
    void addJokerParty();

    @Query("UPDATE user_profile SET jokerFish = jokerFish - 1 WHERE id = 1 AND jokerFish > 0")
    void useJokerFish();

    @Query("UPDATE user_profile SET jokerWheel = jokerWheel - 1 WHERE id = 1 AND jokerWheel > 0")
    void useJokerWheel();

    @Query("UPDATE user_profile SET jokerLollipop = jokerLollipop - 1 WHERE id = 1 AND jokerLollipop > 0")
    void useJokerLollipop();

    @Query("UPDATE user_profile SET jokerSwap = jokerSwap - 1 WHERE id = 1 AND jokerSwap > 0")
    void useJokerSwap();

    @Query("UPDATE user_profile SET jokerShuffle = jokerShuffle - 1 WHERE id = 1 AND jokerShuffle > 0")
    void useJokerShuffle();

    @Query("UPDATE user_profile SET jokerParty = jokerParty - 1 WHERE id = 1 AND jokerParty > 0")
    void useJokerParty();
}
