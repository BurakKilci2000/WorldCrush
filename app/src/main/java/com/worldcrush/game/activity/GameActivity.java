package com.worldcrush.game.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.worldcrush.game.R;
import com.worldcrush.game.adapter.GridAdapter;
import com.worldcrush.game.game.GridEngine;
import com.worldcrush.game.model.Cell;
import com.worldcrush.game.util.LetterGenerator;
import com.worldcrush.game.util.WordDictionary;
import java.util.ArrayList;
import java.util.List;
import com.worldcrush.game.util.ComboHelper;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.worldcrush.game.database.AppDatabase;
import com.worldcrush.game.model.GameRecord;
import com.worldcrush.game.database.GameDao;
import com.worldcrush.game.model.UserProfile;
import android.widget.Button;
public class GameActivity extends AppCompatActivity {

    private GridEngine engine;
    private GridAdapter adapter;
    private int gridSize;
    private int remainingMoves;
    private int currentScore = 0;
    private long gameStartTime;
    private int foundWordsCount = 0;
    private String longestWord = "";

    private GameDao dao;
    private Button btnFish, btnWheel, btnLollipop, btnSwap, btnShuffle, btnParty;
    private String activeJoker = null;
    private Cell swapFirstCell = null;

    private TextView tvScore, tvMoves, tvWordCount, tvCurrentWord;
    private RecyclerView rvGrid;

    private List<Cell> selectedCells = new ArrayList<>();
    private boolean isTouching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridSize = getIntent().getIntExtra("gridSize", 8);
        remainingMoves = getIntent().getIntExtra("moves", 20);

        tvScore = findViewById(R.id.tvScore);
        tvMoves = findViewById(R.id.tvMoves);
        tvWordCount = findViewById(R.id.tvWordCount);
        tvCurrentWord = findViewById(R.id.tvCurrentWord);
        rvGrid = findViewById(R.id.rvGrid);

        engine = new GridEngine(gridSize, this);
        adapter = new GridAdapter(engine.getGrid(), gridSize, null);

        rvGrid.setLayoutManager(new GridLayoutManager(this, gridSize));
        rvGrid.setAdapter(adapter);


        rvGrid.setOnTouchListener((v, event) -> handleTouch(event));

        gameStartTime = System.currentTimeMillis();
        dao = AppDatabase.getInstance(this).gameDao();

        btnFish = findViewById(R.id.btnJokerFish);
        btnWheel = findViewById(R.id.btnJokerWheel);
        btnLollipop = findViewById(R.id.btnJokerLollipop);
        btnSwap = findViewById(R.id.btnJokerSwap);
        btnShuffle = findViewById(R.id.btnJokerShuffle);
        btnParty = findViewById(R.id.btnJokerParty);

        btnFish.setOnClickListener(v -> useFish());
        btnWheel.setOnClickListener(v -> activeJoker = "wheel");
        btnLollipop.setOnClickListener(v -> activeJoker = "lollipop");
        btnSwap.setOnClickListener(v -> { activeJoker = "swap"; swapFirstCell = null; });
        btnShuffle.setOnClickListener(v -> useShuffle());
        btnParty.setOnClickListener(v -> useParty());

        refreshJokerCounts();
        updateUI();
    }

    private boolean handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        View childView = rvGrid.findChildViewUnder(x, y);
        if (childView == null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                finishWord();
            }
            return true;
        }

        Cell cell = (Cell) childView.getTag();
        if (cell == null) return true;


        if (activeJoker != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (handleJokerTap(cell)) return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                selectedCells.clear();
                resetSelections();
                addCell(cell);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isTouching) {
                    if (!cell.selected && !selectedCells.isEmpty()) {
                        Cell last = selectedCells.get(selectedCells.size() - 1);
                        if (engine.areNeighbors(last, cell)) {
                            addCell(cell);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                finishWord();
                break;
        }
        return true;
    }

    private void addCell(Cell cell) {
        cell.selected = true;
        selectedCells.add(cell);
        adapter.notifyDataSetChanged();
        tvCurrentWord.setText(engine.buildWord(selectedCells));
    }

    private void resetSelections() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                engine.getCell(r, c).selected = false;
            }
        }
    }

    private void finishWord() {
        isTouching = false;
        if (selectedCells.size() < 3) {
            resetSelections();
            selectedCells.clear();
            adapter.notifyDataSetChanged();
            tvCurrentWord.setText("");
            return;
        }

        String word = engine.buildWord(selectedCells);
        WordDictionary dict = WordDictionary.getInstance(this);

        remainingMoves--;

        if (dict.isValidWord(word)) {
            foundWordsCount++;
            if (word.length() > longestWord.length()) {
                longestWord = word;
            }


            ComboHelper.ComboResult combo = ComboHelper.findCombos(word, this);
            currentScore += combo.totalScore;

            String message;
            if (combo.comboCount > 1) {
                message = "✓ " + word + " (+" + combo.totalScore + ") 🔥 " +
                        combo.comboCount + "x COMBO";
            } else {
                message = "✓ " + word + " (+" + combo.totalScore + ")";
            }


            List<Cell> extraCells = engine.activateSpecials(new ArrayList<>(selectedCells));
            if (!extraCells.isEmpty()) {

                for (Cell ec : extraCells) {
                    currentScore += LetterGenerator.getPoint(ec.letter);
                }
                message += " ⚡ +" + extraCells.size() + " harf";
            }

            engine.applySpecialPower(new ArrayList<>(selectedCells));

            List<Cell> allCells = new ArrayList<>(selectedCells);
            allCells.addAll(extraCells);


            engine.explodeCellsWithSpecial(allCells);

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "✗ " + word + " geçerli değil", Toast.LENGTH_SHORT).show();
        }

        resetSelections();
        selectedCells.clear();
        adapter.notifyDataSetChanged();
        tvCurrentWord.setText("");

        new Thread(() -> {
            engine.shuffleIfNeeded();
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();

        updateUI();

        if (remainingMoves <= 0) {
            endGame();
        }
    }

    private void updateUI() {
        tvScore.setText("Puan: " + currentScore);
        tvScore.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150)
                .withEndAction(() -> tvScore.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start())
                .start();

        tvMoves.setText("Hamle: " + remainingMoves);
        tvWordCount.setText("Olası Kelime: ...");

        new Thread(() -> {
            int count = engine.countPossibleWords();
            runOnUiThread(() -> tvWordCount.setText("Olası Kelime: " + count));
        }).start();
    }
    private void refreshJokerCounts() {
        new Thread(() -> {
            UserProfile u = dao.getUser();
            if (u == null) return;
            runOnUiThread(() -> {
                btnFish.setText("🐟\n" + u.jokerFish);
                btnWheel.setText("🎯\n" + u.jokerWheel);
                btnLollipop.setText("🍭\n" + u.jokerLollipop);
                btnSwap.setText("✋\n" + u.jokerSwap);
                btnShuffle.setText("🎲\n" + u.jokerShuffle);
                btnParty.setText("🎉\n" + u.jokerParty);
            });
        }).start();
    }

    private void useFish() {
        new Thread(() -> {
            UserProfile u = dao.getUser();
            if (u == null || u.jokerFish <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Balık jokeri yok!", Toast.LENGTH_SHORT).show());
                return;
            }
            dao.useJokerFish();
            runOnUiThread(() -> {
                engine.jokerFish();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "🐟 Balık kullanıldı!", Toast.LENGTH_SHORT).show();
                refreshJokerCounts();
            });
        }).start();
    }

    private void useShuffle() {
        new Thread(() -> {
            UserProfile u = dao.getUser();
            if (u == null || u.jokerShuffle <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Karıştırma jokeri yok!", Toast.LENGTH_SHORT).show());
                return;
            }
            dao.useJokerShuffle();
            runOnUiThread(() -> {
                engine.jokerShuffle();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "🎲 Grid karıştırıldı!", Toast.LENGTH_SHORT).show();
                refreshJokerCounts();
            });
        }).start();
    }

    private void useParty() {
        new Thread(() -> {
            UserProfile u = dao.getUser();
            if (u == null || u.jokerParty <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Parti jokeri yok!", Toast.LENGTH_SHORT).show());
                return;
            }
            dao.useJokerParty();
            runOnUiThread(() -> {
                engine.jokerParty();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "🎉 Yeni harfler geldi!", Toast.LENGTH_SHORT).show();
                refreshJokerCounts();
            });
        }).start();
    }

    private boolean handleJokerTap(Cell cell) {
        if (activeJoker == null) return false;

        new Thread(() -> {
            UserProfile u = dao.getUser();
            if (u == null) return;

            String joker = activeJoker;
            boolean used = false;

            if (joker.equals("wheel") && u.jokerWheel > 0) {
                dao.useJokerWheel();
                runOnUiThread(() -> {
                    engine.jokerWheel(cell.row, cell.col);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "🎯 Satır+Sütun temizlendi!", Toast.LENGTH_SHORT).show();
                });
                activeJoker = null;
                used = true;
            } else if (joker.equals("lollipop") && u.jokerLollipop > 0) {
                dao.useJokerLollipop();
                runOnUiThread(() -> {
                    engine.jokerLollipop(cell.row, cell.col);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "🍭 Harf silindi!", Toast.LENGTH_SHORT).show();
                });
                activeJoker = null;
                used = true;
            } else if (joker.equals("swap") && u.jokerSwap > 0) {
                if (swapFirstCell == null) {
                    swapFirstCell = cell;
                    runOnUiThread(() -> Toast.makeText(this, "Şimdi komşu harfi seç", Toast.LENGTH_SHORT).show());
                } else {
                    final Cell first = swapFirstCell;
                    runOnUiThread(() -> {
                        if (engine.jokerSwap(first, cell)) {
                            dao.useJokerSwap();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "✋ Harfler değişti!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Komşu değil!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    activeJoker = null;
                    swapFirstCell = null;
                    used = true;
                }
            }

            if (used) {
                refreshJokerCounts();
            }
        }).start();
        return true;
    }

    private void endGame() {
        saveGameToDatabase();

        new AlertDialog.Builder(this)
                .setTitle("🏁 Oyun Bitti!")
                .setMessage("Puan: " + currentScore +
                        "\nBulunan Kelime: " + foundWordsCount +
                        "\nEn Uzun: " + (longestWord.isEmpty() ? "-" : longestWord))
                .setPositiveButton("Ana Ekrana Dön", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    private void saveGameToDatabase() {
        new Thread(() -> {
            GameRecord record = new GameRecord();
            record.username = getSharedPreferences("WordCrush", MODE_PRIVATE)
                    .getString("username", "Oyuncu");
            record.score = currentScore;
            record.wordCount = foundWordsCount;
            record.longestWord = longestWord.isEmpty() ? "-" : longestWord;
            record.gridSize = gridSize;
            record.moveCount = remainingMoves;
            record.duration = (System.currentTimeMillis() - gameStartTime) / 1000;
            record.date = System.currentTimeMillis();

            AppDatabase.getInstance(this).gameDao().insertGame(record);
        }).start();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Oyundan Çık")
                .setMessage("Çıkmak istediğinize emin misiniz? Skor kaydedilecek.")
                .setPositiveButton("Evet", (d, w) -> {
                    saveGameToDatabase();
                    finish();
                })
                .setNegativeButton("Hayır", null)
                .show();
    }
}