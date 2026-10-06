package com.worldcrush.game.model;

public class Cell {
    public int row;
    public int col;
    public char letter;
    public boolean selected;
    public SpecialType special;

    public Cell(int row, int col, char letter) {
        this.row = row;
        this.col = col;
        this.letter = letter;
        this.selected = false;
        this.special = null;
    }

    public enum SpecialType {
        ROW_CLEAR,
        AREA_BLAST,
        COLUMN_CLEAR,
        MEGA_BLAST
    }
}