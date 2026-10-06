package com.worldcrush.game.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.worldcrush.game.R;
import com.worldcrush.game.model.Cell;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.CellViewHolder> {

    private Cell[][] grid;
    private int size;
    private OnCellTouchListener listener;

    public interface OnCellTouchListener {
        void onCellTouchDown(Cell cell, View view);
        void onCellTouchMove(Cell cell, View view);
        void onTouchEnd();
    }

    public GridAdapter(Cell[][] grid, int size, OnCellTouchListener listener) {
        this.grid = grid;
        this.size = size;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cell, parent, false);
        return new CellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        int row = position / size;
        int col = position % size;
        Cell cell = grid[row][col];

        holder.tvLetter.setText(String.valueOf(cell.letter));
        holder.itemView.setTag(cell);

        if (cell.selected) {
            holder.tvLetter.setBackgroundResource(R.drawable.cell_selected);
            holder.tvLetter.setTextColor(0xFFFFFFFF);
            holder.tvLetter.animate()
                    .translationY(-6f)
                    .scaleX(1.08f).scaleY(1.08f)
                    .setDuration(100)
                    .start();
        } else {
            holder.tvLetter.setBackgroundResource(R.drawable.cell_normal);
            holder.tvLetter.setTextColor(0xFF1A1A2E);
            holder.tvLetter.animate()
                    .translationY(0f)
                    .scaleX(1.0f).scaleY(1.0f)
                    .setDuration(100)
                    .start();
        }

        if (cell.special != null) {
            String symbol = "";
            switch (cell.special) {
                case ROW_CLEAR:    symbol = " ⇆"; break;
                case AREA_BLAST:   symbol = " ✹"; break;
                case COLUMN_CLEAR: symbol = " ⇅"; break;
                case MEGA_BLAST:   symbol = " ✪"; break;
            }
            holder.tvLetter.setText(cell.letter + symbol);
        }
    }

    @Override
    public int getItemCount() {
        return size * size;
    }

    public void updateGrid(Cell[][] newGrid) {
        this.grid = newGrid;
        notifyDataSetChanged();
    }

    static class CellViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;
        TextView tvLetter;

        CellViewHolder(@NonNull View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            tvLetter = itemView.findViewById(R.id.tvLetter);
        }
    }
}