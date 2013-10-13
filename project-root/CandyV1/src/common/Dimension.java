package common;

public class Dimension {
	private int width;
	private int height;

	public Dimension() {
		init(0, 0);
	}

	public Dimension(int height, int width) {
		init(height, width);
	}

	private void init(int height, int width) {
		this.height = height;
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}
