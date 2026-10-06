package com.worldcrush.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("WordCrush", MODE_PRIVATE);

        tvUsername = findViewById(R.id.tvUsername);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnScores = findViewById(R.id.btnScores);
        Button btnMarket = findViewById(R.id.btnMarket);

        tvUsername.setText("👤 " + prefs.getString("username", "Oyuncu"));

        tvUsername.setOnClickListener(v -> showChangeNameDialog());

        btnNewGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.worldcrush.game.activity.GridSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnScores.setOnClickListener(v -> {
            startActivity(new Intent(this, com.worldcrush.game.activity.ScoreActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnMarket.setOnClickListener(v -> {
            startActivity(new Intent(this, com.worldcrush.game.activity.MarketActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void showChangeNameDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(prefs.getString("username", ""));

        new AlertDialog.Builder(this)
                .setTitle("Kullanıcı adını değiştir")
                .setView(input)
                .setPositiveButton("Kaydet", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        prefs.edit().putString("username", newName).apply();
                        tvUsername.setText("👤 " + newName);
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }
}