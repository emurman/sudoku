import java.util.Random;


public class Sudoku {
    private static final int DEFAULT_SIZE = 9;
    private static final int DEFAULT_LOCAL_GRID_SIZE = 3;

    private static final char VERTICAL_DELIM_REPR = '|';
    private static final char HORIZONTAL_DELIM_REPR = 8212;  // long dash

    private final int n;
    private final int localGridSize;
    private final int[][] grid;
    private final boolean[][] knownCells;
    private final Random rng;

    private int currentCost;

    public Sudoku() {
        this.n = DEFAULT_SIZE;
        this.localGridSize = DEFAULT_LOCAL_GRID_SIZE;
        this.grid = new int[this.n][this.n];
        this.knownCells = new boolean[this.n][this.n];
        this.rng = new Random();
    }

    public Sudoku(String s) {
        this.rng = new Random();

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

    public void setSeed(long seed) {
        this.rng.setSeed(seed);
    }

    private void initCost() {
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

    public void randomAssignment() {
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[i].length; j++) {
                if (!this.knownCells[i][j]) {
                    this.grid[i][j] = this.rng.nextInt(this.n - 1) + 1;
                }
            }
        }

        this.initCost();
    }

    public int probeMove(Move m) {
        boolean[] violations = new boolean[3];
        boolean[] alleviations = new boolean[3];

        int localGridX = m.x - (m.x % this.localGridSize);
        int localGridY = m.y - (m.y % this.localGridSize);
        
        for (int i = 0; i < this.grid.length; i++) {
            int cell = i == m.x ? 0 : this.grid[m.y][i];
            if (cell == m.new_) violations[0]   = true;
            if (cell == m.old)  alleviations[0] = true;

            cell = i == m.y ? 0 : this.grid[i][m.x];
            if (cell == m.new_) violations[1]   = true;   
            if (cell == m.old)  alleviations[1] = true;

            int x = localGridX + i / localGridSize;
            int y = localGridY + i % localGridSize;
            cell = x == m.x && y == m.y ? 0: this.grid[y][x];
            if (cell == m.new_) violations[2]   = true;
            if (cell == m.old)  alleviations[2] = true;
        }

        int cost = 0;
        
        for (boolean constraintViolation : violations) {
            if (constraintViolation) cost++;
            System.out.println(constraintViolation);
        }
        
        for (boolean constraintAlleviation : alleviations) {
            if (constraintAlleviation) cost--;
            System.out.println(constraintAlleviation);
        }

        return cost;
    }

    public int cmp(int c, int new_, int old, int cost) {
        
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

