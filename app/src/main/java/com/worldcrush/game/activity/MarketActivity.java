package com.worldcrush.game.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.worldcrush.game.R;
import com.worldcrush.game.database.AppDatabase;
import com.worldcrush.game.database.GameDao;
import com.worldcrush.game.model.Joker;
import com.worldcrush.game.model.UserProfile;

public class MarketActivity extends AppCompatActivity {

    private TextView tvGold;
    private GameDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        tvGold = findViewById(R.id.tvGold);
        dao = AppDatabase.getInstance(this).gameDao();

        setupBuyButton(R.id.btnFish, Joker.JokerType.FISH);
        setupBuyButton(R.id.btnWheel, Joker.JokerType.WHEEL);
        setupBuyButton(R.id.btnLollipop, Joker.JokerType.LOLLIPOP);
        setupBuyButton(R.id.btnSwap, Joker.JokerType.SWAP);
        setupBuyButton(R.id.btnShuffle, Joker.JokerType.SHUFFLE);
        setupBuyButton(R.id.btnParty, Joker.JokerType.PARTY);

        refreshGold();
    }

    private void setupBuyButton(int btnId, Joker.JokerType type) {
        Button btn = findViewById(btnId);
        btn.setText("SATIN AL\n" + type.cost + " 🪙");
        btn.setOnClickListener(v -> buy(type));
    }

    private void buy(Joker.JokerType type) {
        new Thread(() -> {
            int gold = dao.getGold();
            if (gold < type.cost) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Yeterli altın yok!", Toast.LENGTH_SHORT).show());
                return;
            }

            dao.decreaseGold(type.cost);
            switch (type) {
                case FISH: dao.addJokerFish(); break;
                case WHEEL: dao.addJokerWheel(); break;
                case LOLLIPOP: dao.addJokerLollipop(); break;
                case SWAP: dao.addJokerSwap(); break;
                case SHUFFLE: dao.addJokerShuffle(); break;
                case PARTY: dao.addJokerParty(); break;
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "✓ " + type.displayName + " satın alındı!",
                        Toast.LENGTH_SHORT).show();
                refreshGold();
            });
        }).start();
    }

    private void refreshGold() {
        new Thread(() -> {
            UserProfile user = dao.getUser();

            if (user != null && user.totalGold < 5000) {
                dao.updateGold(99999);
                user = dao.getUser();
            }

            int gold = user == null ? 0 : user.totalGold;

            int fish = user == null ? 0 : user.jokerFish;
            int wheel = user == null ? 0 : user.jokerWheel;
            int loll = user == null ? 0 : user.jokerLollipop;
            int swap = user == null ? 0 : user.jokerSwap;
            int shuf = user == null ? 0 : user.jokerShuffle;
            int part = user == null ? 0 : user.jokerParty;

            runOnUiThread(() -> {
                tvGold.setText("🪙 Altın: " + gold);
                ((TextView) findViewById(R.id.tvFishCount)).setText("Stok: " + fish);
                ((TextView) findViewById(R.id.tvWheelCount)).setText("Stok: " + wheel);
                ((TextView) findViewById(R.id.tvLollipopCount)).setText("Stok: " + loll);
                ((TextView) findViewById(R.id.tvSwapCount)).setText("Stok: " + swap);
                ((TextView) findViewById(R.id.tvShuffleCount)).setText("Stok: " + shuf);
                ((TextView) findViewById(R.id.tvPartyCount)).setText("Stok: " + part);
            });
        }).start();
    }
}