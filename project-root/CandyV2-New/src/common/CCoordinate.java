package common;

// coordinate
public class CCoordinate {

    private int x; // width
    private int y; // height

    public CCoordinate() {
        this.x = 0;
        this.y = 0;
    }

    public CCoordinate(CCoordinate cloneObject) {
        this.x = cloneObject.x;
        this.y = cloneObject.y;
    }

    public CCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        CCoordinate rvalue = (CCoordinate) obj;

        if (rvalue.getX() == this.getX() && rvalue.getY() == this.getY())
            return true;

        return false;
    }

    @Override
    public String toString() {
        return "CCoordinate [x=" + x + ", y=" + y + "]";
    }

    public static CCoordinate GenerateRandomCoordinate(int XUpperBound, int YUpperBound) {
        int randomCoorX = FedConfig.UTIL_RND.nextInt(XUpperBound);
        randomCoorX = randomCoorX < 0 ? randomCoorX * -1 : randomCoorX;

        int randomCoorY = FedConfig.UTIL_RND.nextInt(YUpperBound);
        randomCoorY = randomCoorY < 0 ? randomCoorY * -1 : randomCoorY;

        return new CCoordinate(randomCoorX, randomCoorY);
    }

}
