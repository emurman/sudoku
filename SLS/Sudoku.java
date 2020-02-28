
public class Sudoku {
    private static final int DEFAULT_SIZE = 9;
    private static final int DEFAULT_LOCAL_GRID_SIZE = 3;

    private static final char VERTICAL_DELIM_REPR = '|';
    private static final char HORIZONTAL_DELIM_REPR = 8212;  // long dash

    private final int n;
    private final int localGridSize;
    private final int[][] grid;
    private final boolean[][] knownCells;

    private int currentCost;

    public Sudoku() {
        this.n = DEFAULT_SIZE;
        this.localGridSize = DEFAULT_LOCAL_GRID_SIZE;
        this.grid = new int[this.n][this.n];
        this.knownCells = new boolean[this.n][this.n];
    }

    public Sudoku(String s) {
        String size = s.split(" ")[0];
        String repr = s.split(" ")[1];

        this.n = Integer.parseInt(size);
        this.localGridSize = (int) Math.sqrt(this.n);
        this.grid = new int[this.n][this.n];
        this.knownCells = new boolean[this.n][this.n];

        int count = 0;
        for (int x = 0; x < this.grid.length; x++) {
            for (int y = 0; y < this.grid[x].length; y++) {
                char cell = repr.charAt(count);
                count++;
                if (cell != '.') {
                    this.grid[x][y] = Character.getNumericValue(cell);
                    this.knownCells[x][y] = true;
                }
            }
        }

        this.initCost();
        if (this.currentCost != 0) throw new InvalidSudokuException();
    }

    public void initCost() {
        boolean[] mask = new boolean[this.n + 1];
        int conflicts = 0;

        // Rows
        for (int[] row : this.grid) {
            for (int cell : row) {
                if (cell > 0) {
                    if (mask[cell]) {
                        conflicts++;
                    } else {
                        mask[cell] = true;
                    }   
                }
            }
            for (int i = 0; i < mask.length; i++) mask[i] = false;
        }

        // Columns
        for (int x = 0; x < this.grid.length; x++) {
            for (int y = 0; y < this.grid[x].length; y++) {
                int cell = this.grid[y][x];
                if (cell > 0) {
                    if (mask[cell]) {
                        conflicts++;
                    } else {
                        mask[cell] = true;
                    }   
                }
            }
            for (int i = 0; i < mask.length; i++) mask[i] = false;
        }

        // Local grids
        for (int x = 0; x < this.grid.length; x += this.localGridSize) {
            for (int y = 0; y < this.grid[x].length; y += this.localGridSize) {
                for (int i = 0; i < this.n; i++) {
                    int cell = grid[x + i / this.localGridSize][y + i % this.localGridSize];

                    if (cell > 0) {
                        if (mask[cell]) {
                            conflicts++;
                        } else {
                            mask[cell] = true;
                        }   
                    }
                }
                for (int i = 0; i < mask.length; i++) mask[i] = false;
            }
        }
        this.currentCost = conflicts;
    }

    public int probeMove(Move m) {
        int cost = 0;

        return cost;
    }

    public int getCurrentCost() {
        return this.currentCost;
    }
    
    public int graphicReprWidth() {
        return this.n * 2 + ((this.localGridSize - 1) * 2) - 1;
    }

    @Override
    public String toString() {
        String result = "";

        result += this.localGridSize + "x" + this.localGridSize + " sudoku, n = " + this.n + ".\n\n";

        for (int x = 0; x < this.grid.length; x++) {
            for (int y = 0; y < this.grid[x].length; y++) {
                result += this.grid[x][y] + " ";
                if (y % this.localGridSize == this.localGridSize - 1 && y < this.grid[x].length - 1) {
                    result += VERTICAL_DELIM_REPR + " ";
                }
            }
            result += '\n';
            
            if (x % this.localGridSize == this.localGridSize - 1 && x < this.grid.length - 1) {
                for (int i = 0; i < this.graphicReprWidth(); i++)
                    result += HORIZONTAL_DELIM_REPR;
                result += '\n';
            }
        }

        return result;
    }

    class InvalidSudokuException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    };
}

