package com.worldcrush.game.game;

import android.content.Context;
import com.worldcrush.game.model.Cell;
import com.worldcrush.game.util.LetterGenerator;
import com.worldcrush.game.util.WordDictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class GridEngine {

    private int size;
    private Cell[][] grid;
    private WordDictionary dictionary;

    public GridEngine(int size, Context context) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.dictionary = WordDictionary.getInstance(context);
        generateGrid();
    }

    private void generateGrid() {
        int maxAttempts = 20;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    char letter;
                    int tries = 0;

                    while (true) {
                        letter = LetterGenerator.randomLetter();
                        tries++;
                        if (tries > 30) break;

                        int vowelsAround = countVowelsAround(r, c);
                        int consonantsAround = countConsonantsAround(r, c);
                        boolean isVowel = LetterGenerator.isVowel(letter);


                        if (!isVowel && consonantsAround >= 3) continue;

                        if (isVowel && vowelsAround >= 3) continue;

                        break;
                    }

                    grid[r][c] = new Cell(r, c, letter);
                }
            }


            if (hasAtLeastOneWord()) return;
        }

    }

    private int countVowelsAround(int r, int c) {
        return countAround(r, c, true);
    }


    private int countConsonantsAround(int r, int c) {
        return countAround(r, c, false);
    }

    private int countAround(int r, int c, boolean wantVowel) {
        int count = 0;
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < size && nc >= 0 && nc < size && grid[nr][nc] != null) {
                boolean v = LetterGenerator.isVowel(grid[nr][nc].letter);
                if (v == wantVowel) count++;
            }
        }
        return count;
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public int getSize() {
        return size;
    }

    public Cell[][] getGrid() {
        return grid;
    }


    public int getCellCount() {
        return size * size;
    }


    public boolean areNeighbors(Cell a, Cell b) {
        int dr = Math.abs(a.row - b.row);
        int dc = Math.abs(a.col - b.col);
        return (dr <= 1 && dc <= 1) && !(dr == 0 && dc == 0);
    }


    public String buildWord(List<Cell> selectedCells) {
        StringBuilder sb = new StringBuilder();
        for (Cell c : selectedCells) sb.append(c.letter);
        return sb.toString();
    }

    public int countPossibleWords() {
        List<String> found = new ArrayList<>();
        boolean[][] visited = new boolean[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                dfs(r, c, new StringBuilder(), visited, found);
            }
        }
        return found.size();
    }

    private void dfs(int r, int c, StringBuilder current, boolean[][] visited, List<String> found) {
        if (r < 0 || r >= size || c < 0 || c >= size || visited[r][c]) return;
        if (current.length() >= 6) return;

        visited[r][c] = true;
        current.append(grid[r][c].letter);

        if (current.length() >= 3) {
            String word = current.toString();
            if (dictionary.isValidWord(word) && !found.contains(word)) {
                found.add(word);
            }
        }

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            dfs(r + dr[i], c + dc[i], current, visited, found);
        }

        current.deleteCharAt(current.length() - 1);
        visited[r][c] = false;
    }

    public void explodeCells(List<Cell> cells) {
        for (Cell c : cells) {
            grid[c.row][c.col].letter = ' ';
            grid[c.row][c.col].special = null;
            grid[c.row][c.col].selected = false;
        }

        for (int col = 0; col < size; col++) {
            applyGravity(col);
        }
    }

    private void applyGravity(int col) {
        List<Character> letters = new ArrayList<>();
        List<Cell.SpecialType> specials = new ArrayList<>();

        for (int r = size - 1; r >= 0; r--) {
            if (grid[r][col].letter != ' ') {
                letters.add(grid[r][col].letter);
                specials.add(grid[r][col].special);
            }
        }

        int idx = 0;
        for (int r = size - 1; r >= 0; r--) {
            if (idx < letters.size()) {
                grid[r][col].letter = letters.get(idx);
                grid[r][col].special = specials.get(idx);
                idx++;
            } else {
                grid[r][col].letter = LetterGenerator.randomLetter();
                grid[r][col].special = null;
            }
            grid[r][col].selected = false;
        }
    }
    public void applySpecialPower(List<Cell> selectedCells) {
        if (selectedCells.size() < 4) return;

        Cell lastCell = selectedCells.get(selectedCells.size() - 1);

        int row = lastCell.row;
        int col = lastCell.col;

        Cell.SpecialType type;
        if (selectedCells.size() == 4) {
            type = Cell.SpecialType.ROW_CLEAR;
        } else if (selectedCells.size() == 5) {
            type = Cell.SpecialType.AREA_BLAST;
        } else if (selectedCells.size() == 6) {
            type = Cell.SpecialType.COLUMN_CLEAR;
        } else {
            type = Cell.SpecialType.MEGA_BLAST;
        }

        pendingSpecialRow = row;
        pendingSpecialCol = col;
        pendingSpecialType = type;
    }

    private int pendingSpecialRow = -1;
    private int pendingSpecialCol = -1;
    private Cell.SpecialType pendingSpecialType = null;

    public void explodeCellsWithSpecial(List<Cell> cells) {
        Cell preserved = null;
        if (pendingSpecialType != null && cells.size() >= 4) {
            preserved = cells.get(cells.size() - 1);
        }

        for (Cell c : cells) {
            if (c == preserved) continue;
            grid[c.row][c.col].letter = ' ';
            grid[c.row][c.col].special = null;
            grid[c.row][c.col].selected = false;
        }

        if (preserved != null) {
            grid[preserved.row][preserved.col].special = pendingSpecialType;
            grid[preserved.row][preserved.col].selected = false;
        }

        for (int col = 0; col < size; col++) {
            applyGravity(col);
        }

        pendingSpecialRow = -1;
        pendingSpecialCol = -1;
        pendingSpecialType = null;
    }

    public List<Cell> activateSpecials(List<Cell> selectedCells) {
        List<Cell> extraCells = new ArrayList<>();

        for (Cell c : selectedCells) {
            if (c.special == null) continue;

            switch (c.special) {
                case ROW_CLEAR:
                    for (int col = 0; col < size; col++) {
                        if (!extraCells.contains(grid[c.row][col]) &&
                                !selectedCells.contains(grid[c.row][col])) {
                            extraCells.add(grid[c.row][col]);
                        }
                    }
                    break;

                case COLUMN_CLEAR:
                    for (int row = 0; row < size; row++) {
                        if (!extraCells.contains(grid[row][c.col]) &&
                                !selectedCells.contains(grid[row][c.col])) {
                            extraCells.add(grid[row][c.col]);
                        }
                    }
                    break;

                case AREA_BLAST:
                    addRadius(c, 1, selectedCells, extraCells);
                    break;

                case MEGA_BLAST:
                    addRadius(c, 2, selectedCells, extraCells);
                    break;
            }
        }
        return extraCells;
    }

    private void addRadius(Cell center, int radius, List<Cell> selected, List<Cell> extra) {
        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                int nr = center.row + dr;
                int nc = center.col + dc;
                if (nr < 0 || nr >= size || nc < 0 || nc >= size) continue;
                Cell target = grid[nr][nc];
                if (!extra.contains(target) && !selected.contains(target)) {
                    extra.add(target);
                }
            }
        }
    }
    public boolean hasAtLeastOneWord() {
        Set<String> found = new HashSet<>();
        boolean[][] visited = new boolean[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (dfsFindAny(r, c, new StringBuilder(), visited, found)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsFindAny(int r, int c, StringBuilder current,
                               boolean[][] visited, Set<String> found) {
        if (r < 0 || r >= size || c < 0 || c >= size || visited[r][c]) return false;
        if (current.length() >= 5) return false;

        visited[r][c] = true;
        current.append(grid[r][c].letter);

        boolean result = false;
        if (current.length() >= 3) {
            String word = current.toString();
            if (dictionary.isValidWord(word)) {
                found.add(word);
                result = true;
            }
        }

        if (!result) {
            int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
            for (int i = 0; i < 8 && !result; i++) {
                if (dfsFindAny(r + dr[i], c + dc[i], current, visited, found)) {
                    result = true;
                }
            }
        }

        current.deleteCharAt(current.length() - 1);
        visited[r][c] = false;
        return result;
    }

    public void shuffleIfNeeded() {
        if (hasAtLeastOneWord()) return;

        for (int attempt = 0; attempt < 10; attempt++) {
            shuffleGrid();
            if (hasAtLeastOneWord()) return;
        }

        generateGrid();
    }

    private void shuffleGrid() {
        List<Character> letters = new ArrayList<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                letters.add(grid[r][c].letter);
            }
        }
        java.util.Collections.shuffle(letters);

        int idx = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c].letter = letters.get(idx++);
                grid[r][c].special = null;
                grid[r][c].selected = false;
            }
        }
    }
    public void jokerFish() {
        java.util.Random rand = new java.util.Random();
        List<Cell> toExplode = new ArrayList<>();
        List<Cell> all = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                all.add(grid[r][c]);

        java.util.Collections.shuffle(all, rand);
        for (int i = 0; i < Math.min(5, all.size()); i++) {
            toExplode.add(all.get(i));
        }
        explodeCells(toExplode);
    }

    public void jokerWheel(int row, int col) {
        List<Cell> toExplode = new ArrayList<>();
        for (int c = 0; c < size; c++) toExplode.add(grid[row][c]);
        for (int r = 0; r < size; r++) {
            if (r != row) toExplode.add(grid[r][col]);
        }
        explodeCells(toExplode);
    }

    public void jokerLollipop(int row, int col) {
        List<Cell> toExplode = new ArrayList<>();
        toExplode.add(grid[row][col]);
        explodeCells(toExplode);
    }

    public boolean jokerSwap(Cell a, Cell b) {
        if (!areNeighbors(a, b)) return false;
        char tmp = grid[a.row][a.col].letter;
        grid[a.row][a.col].letter = grid[b.row][b.col].letter;
        grid[b.row][b.col].letter = tmp;

        Cell.SpecialType tmpS = grid[a.row][a.col].special;
        grid[a.row][a.col].special = grid[b.row][b.col].special;
        grid[b.row][b.col].special = tmpS;
        return true;
    }

    public void jokerShuffle() {
        List<Character> letters = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                letters.add(grid[r][c].letter);
        java.util.Collections.shuffle(letters);

        int idx = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c].letter = letters.get(idx++);
                grid[r][c].special = null;
                grid[r][c].selected = false;
            }
        }
    }

    public void jokerParty() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c].letter = LetterGenerator.randomLetter();
                grid[r][c].special = null;
                grid[r][c].selected = false;
            }
        }
    }
}
