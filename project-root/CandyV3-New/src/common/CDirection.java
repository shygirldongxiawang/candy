package common;

public class CDirection {
    private int             up    = 0;
    private int             down  = 0;
    private int             left  = 0;
    private int             right = 0;

    public static final int UP    = 0;
    public static final int DOWN  = 1;
    public static final int LEFT  = 2;
    public static final int RIGHT = 3;

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void shrink(int value) {
        this.up -= value;
        this.down -= value;
        this.left -= value;
        this.right -= value;
    }

    public boolean isValid() {
        if (this.up > 0 || this.down > 0 || this.left > 0 || this.right > 0)
            return true;
        return false;
    }

    public void reinit() {
        up = 0;
        down = 0;
        left = 0;
        right = 0;
    }
}
