package com.worldcrush.game.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.worldcrush.game.R;
import com.worldcrush.game.adapter.GameRecordAdapter;
import com.worldcrush.game.database.AppDatabase;
import com.worldcrush.game.model.GameRecord;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private TextView tvTotalGames, tvHighScore, tvAvgScore, tvTotalWords, tvLongestWord, tvTotalTime;
    private RecyclerView rvGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvAvgScore = findViewById(R.id.tvAvgScore);
        tvTotalWords = findViewById(R.id.tvTotalWords);
        tvLongestWord = findViewById(R.id.tvLongestWord);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        rvGames = findViewById(R.id.rvGames);

        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            List<GameRecord> games = AppDatabase.getInstance(this).gameDao().getAllGames();

            int total = games.size();
            int highScore = 0;
            int totalScore = 0;
            int totalWords = 0;
            long totalDuration = 0;
            String longest = "-";

            for (GameRecord g : games) {
                if (g.score > highScore) highScore = g.score;
                totalScore += g.score;
                totalWords += g.wordCount;
                totalDuration += g.duration;
                if (g.longestWord != null && g.longestWord.length() > longest.length()) {
                    longest = g.longestWord;
                }
            }

            int avg = total > 0 ? totalScore / total : 0;
            long totalMin = totalDuration / 60;
            long totalHour = totalMin / 60;
            long remainMin = totalMin % 60;
            String timeStr = totalHour + " saat " + remainMin + " dakika";

            int finalHighScore = highScore;
            int finalAvg = avg;
            int finalTotalWords = totalWords;
            String finalLongest = longest;

            runOnUiThread(() -> {
                tvTotalGames.setText("Toplam Oyun: " + total);
                tvHighScore.setText("En Yüksek Puan: " + finalHighScore);
                tvAvgScore.setText("Ortalama Puan: " + finalAvg);
                tvTotalWords.setText("Toplam Kelime: " + finalTotalWords);
                tvLongestWord.setText("En Uzun Kelime: " + finalLongest);
                tvTotalTime.setText("Toplam Süre: " + timeStr);

                rvGames.setLayoutManager(new LinearLayoutManager(this));
                rvGames.setAdapter(new GameRecordAdapter(games));
            });
        }).start();
    }
}