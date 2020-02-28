import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LocalSearch {
    public static void main(String[] args) throws FileNotFoundException {
        String sudokuRepr = readFile("test.sdk");

        Sudoku s = new Sudoku(sudokuRepr);
        System.out.println(s);
        System.out.println(s.getCurrentCost() + "");
    }

    public static String readFile(String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        Scanner in = new Scanner(new FileReader(fileName));
        boolean first = true;
        int n = -1;

        while(in.hasNext()) {
            String line = in.next();

            if (first) {
                n = Integer.parseInt(line);
                sb.append(line + " ");
                first = false;
            } else {
                if (line.length() != n) throw new InputMismatchException(line);
                
                sb.append(line);
            }
        }

        in.close();
        return sb.toString();
    }
}