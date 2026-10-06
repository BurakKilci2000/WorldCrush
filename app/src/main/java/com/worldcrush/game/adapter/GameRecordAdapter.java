package com.worldcrush.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.worldcrush.game.R;
import com.worldcrush.game.model.GameRecord;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameRecordAdapter extends RecyclerView.Adapter<GameRecordAdapter.GameViewHolder> {

    private List<GameRecord> games;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("tr"));

    public GameRecordAdapter(List<GameRecord> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_record, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameRecord game = games.get(position);
        int gameNumber = games.size() - position;

        holder.tvGameNumber.setText("Oyun #" + gameNumber);
        holder.tvDate.setText(dateFormat.format(new Date(game.date)));
        holder.tvGridSize.setText("Grid: " + game.gridSize + "x" + game.gridSize);
        holder.tvScore.setText("Puan: " + game.score);
        holder.tvWordCount.setText("Kelime: " + game.wordCount);
        holder.tvLongest.setText("En Uzun: " + (game.longestWord == null ? "-" : game.longestWord));
        holder.tvDuration.setText("Süre: " + (game.duration / 60) + " dk " + (game.duration % 60) + " sn");
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameNumber, tvDate, tvGridSize, tvScore, tvWordCount, tvLongest, tvDuration;

        GameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameNumber = itemView.findViewById(R.id.tvGameNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvGridSize = itemView.findViewById(R.id.tvGridSize);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvWordCount = itemView.findViewById(R.id.tvWordCount);
            tvLongest = itemView.findViewById(R.id.tvLongest);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}