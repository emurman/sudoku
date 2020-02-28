

public class Move {
    final int x;
    final int y;
    final int old;
    final int new_;

    public Move(int x, int y, int old, int new_) {
        this.x = x;
        this.y = y;
        this.old = old;
        this.new_ = new_;
    }

    @Override
    public String toString() {
        return this.old + "@(" + this.x + ", " + this.y + ")" +" -> " + this.new_;
    }
}