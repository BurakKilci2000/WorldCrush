package com.worldcrush.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.worldcrush.game.R;

public class MoveSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_select);

        int gridSize = getIntent().getIntExtra("gridSize", 8);
        int moves = getIntent().getIntExtra("moves", 20);

        TextView tvInfo = findViewById(R.id.tvInfo);
        Button btnStart = findViewById(R.id.btnStart);

        tvInfo.setText("Grid: " + gridSize + "x" + gridSize + "\nHamle Sayısı: " + moves);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gridSize", gridSize);
            intent.putExtra("moves", moves);
            startActivity(intent);
            finish();
        });
    }
}