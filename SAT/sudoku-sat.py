import math
import itertools
import argparse
import fileinput

class Sudoku:
    default_size = 9

    def __init__(self, n = default_size, load = None):
        super().__init__()
        self.known_values = set()

        if load:
            self.load_sudoku(load)
        else:
            self.n = n

        self.grid_size = int(math.sqrt(self.n))

        self.create_grid()

    def load_sudoku(self, f):
        i = 0

        for line_no, line in enumerate(fileinput.input(f)):
            if line_no == 0:
                if not line.split()[0].isdigit():
                    print(f"Error: Unexpected sudoku size '{int(line)}' in input file {f}. Expected an integer.")
                    raise ValueError
                else:
                    self.n = int(line)
                    continue
            for b in line:
                if b == '\n':
                    continue
                if not (b.isdigit() or b == '.'):
                    print(f"Error: Unexpected character '{b}' in input file {f}")
                    raise ValueError
                else:
                    if not b == '.':
                        self.known_values.add(int(b) + i * self.n)
                    i = i + 1
                    

    def print_sudoku(self, sudoku):
        if len(sudoku) != self.n ** 3:
            print(f"Error, tried to load sudoku of size {len(sudoku)} into sudoku of size {self.n ** 3}")
            return

        count = 0
        result = ""
        for i in range(self.n * 2 + (self.grid_size + 1) * 2 - 1):
            result = result + "-"
        result = result + "\n| "

        for lit in sudoku:
            lit = int(lit)

            if lit > 0:
                count = count + 1
                value = lit % self.n
                if value == 0:
                    value = n
                result = result + str(value) + " "

                if count % self.n == 0:
                    if count % (self.grid_size * self.n) == 0:
                        result = result + '|\n'
                        for i in range(self.n * 2 + (self.grid_size + 1) * 2 - 1):
                            result = result + "-"

                        result = result + "\n"
                    else:
                     result = result + "|\n"
                
                if count % self.grid_size == 0 and count < self.n ** 2:
                    result = result + "| "

        return result
        
    def create_grid(self):
        grid = []
        var_count = 1

        for i in range(self.n):
            grid.append([])
            for j in range(self.n):
                grid[i].append([])
                for k in range (self.n):
                    grid[i][j].append(str(var_count))
                    var_count = var_count + 1
        
        self.grid = grid        

    def cell_constr(self):
        for i in range(self.n):
            for j in range(self.n):
                print(Sudoku.exactly_one(self.grid[i][j]))

    def row_constr(self):
        for i in range(self.n):
            for j in range(self.n):
                row = []
                for k in range(self.n):
                    row.append(self.grid[i][k][j])
                print(Sudoku.exactly_one(row))

    def col_constr(self):
        for i in range(self.n):
            for j in range(self.n):
                col = []
                for k in range(self.n):
                    col.append(self.grid[k][i][j])
                print(Sudoku.exactly_one(col))

    def grid_constr(self):
        for l in range(self.n):
            for i in range(self.grid_size):
                i_ = i * self.grid_size

                for j in range(self.grid_size):
                    j_ = j * self.grid_size

                    grid = []
                    for k in range(self.n):                                    
                        grid.append(self.grid[i_ + math.floor(k / self.grid_size)][j_ + k % self.grid_size][l])
                    print(Sudoku.exactly_one(grid))
    
    def value_constr(self):
        for val in self.known_values:
            print(f'{val} 0')

    @staticmethod
    def exactly_one(vars):
        result = ""

        for l in vars:
            result = result + l + " "

        result = result + "0\n"

        pairs = list(map(list, itertools.combinations(vars, 2)))

        for i, pair in enumerate(pairs):
            result = result + '-' + pair[0] + " -" + pair[1] + " 0"
            if i != len(pairs) - 1:
                result = result + '\n'

        return result


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Encode sudoku in CNF form')
    parser.add_argument("-s", "--size", type=int,
                        help='size of the sudoku grid. default = 9')

    parser.add_argument("-l", "--load", type=str,
                    help='specify a sudoku file to load')    

    parser.add_argument("-p", "--print", action="store_true",
                    help='whether or not to print a sudoku given a CNF formula of assignments. Reads from stdin.')  

    args = parser.parse_args()

    if args.size:
        n = args.size
    else:
        n = Sudoku.default_size

    if args.print:
        solution = ""
        for i, line in enumerate(fileinput.input(files='-')):
            if i > 0:
                solution = line.split()
        
        del solution[-1]
        
        s = Sudoku(n)
        print(s.print_sudoku(solution))
    else:
        if args.load:
            s = Sudoku(load = args.load)
            s.value_constr()
        else:
            s = Sudoku(n)

        s.cell_constr()
        s.row_constr()
        s.col_constr()
        s.grid_constr()
