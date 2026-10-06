package com.worldcrush.game.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.worldcrush.game.R;
import com.worldcrush.game.database.AppDatabase;
import com.worldcrush.game.model.UserProfile;
import com.worldcrush.game.MainActivity;



public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("WordCrush", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        // Proje gereği: altın sınırsız (oyun deneyimi kesintisiz olmalı)
        new Thread(() -> {
            UserProfile u = AppDatabase.getInstance(this).gameDao().getUser();
            if (u != null && u.totalGold < 5000) {
                AppDatabase.getInstance(this).gameDao().updateGold(99999);
            }
        }).start();

        if (username != null) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_splash);

        EditText etUsername = findViewById(R.id.etUsername);
        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Lütfen kullanıcı adı girin!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveUser(name);
        });
    }

    private void saveUser(String name) {
        prefs.edit().putString("username", name).apply();

        new Thread(() -> {
            UserProfile user = new UserProfile();
            user.username = name;
            user.totalGold = 9999;
            AppDatabase.getInstance(this).gameDao().insertUser(user);
            runOnUiThread(this::goToMain);
        }).start();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}