package com.worldcrush.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.worldcrush.game.R;

public class GridSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_select);

        Button btn6x6 = findViewById(R.id.btn6x6);
        Button btn8x8 = findViewById(R.id.btn8x8);
        Button btn10x10 = findViewById(R.id.btn10x10);

        btn6x6.setOnClickListener(v -> goToMoveSelect(6, 15));   // Zor
        btn8x8.setOnClickListener(v -> goToMoveSelect(8, 20));   // Orta
        btn10x10.setOnClickListener(v -> goToMoveSelect(10, 25)); // Kolay
    }

    private void goToMoveSelect(int gridSize, int moves) {
        Intent intent = new Intent(this, MoveSelectActivity.class);
        intent.putExtra("gridSize", gridSize);
        intent.putExtra("moves", moves);
        startActivity(intent);
    }
}