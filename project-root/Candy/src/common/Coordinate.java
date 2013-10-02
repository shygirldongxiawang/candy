package common;

public class Coordinate {

	private int x;
	private int y;

	public Coordinate() {
		this.x = 0;
		this.y = 0;
	}

	public Coordinate(int x, int y) {
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
		Coordinate rvalue = (Coordinate) obj;

		if (rvalue.getX() == this.getX() && rvalue.getY() == this.getY())
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + "]";
	}

	public static Coordinate GenerateRandomCoordinate(int XUpperBound, int YUpperBound) {
		int randomCoorX = FederationProperties.R.nextInt(XUpperBound);
		randomCoorX = randomCoorX < 0 ? randomCoorX * -1 : randomCoorX;

		int randomCoorY = FederationProperties.R.nextInt(YUpperBound);
		randomCoorY = randomCoorY < 0 ? randomCoorY * -1 : randomCoorY;

		return new Coordinate(randomCoorX, randomCoorY);
	}

}
