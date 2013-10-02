package common;

public class ItemProperties {

	private Coordinate corr;
	private int initialCandyAmount;
	private int replenishTimeInterval;
	private int candyTransferRate;
	private int visibilityRadius;

	public ItemProperties() {
		this.initialize(0, 0, 0, 0, new Coordinate());
	}

	public ItemProperties(int initialCandyAmount, int replenishTimeInterval, int candyTransferRate, int visibilityRadius, Coordinate corr) {
		this.initialize(initialCandyAmount, replenishTimeInterval, candyTransferRate, visibilityRadius, corr);
	}

	public Coordinate getCorr() {
		return corr;
	}

	public void setCorr(Coordinate corr) {
		this.corr = corr;
	}

	public int getInitialCandyAmount() {
		return initialCandyAmount;
	}

	public void setInitialCandyAmount(int initialCandyAmount) {
		this.initialCandyAmount = initialCandyAmount;
	}

	public int getReplenishTimeInterval() {
		return replenishTimeInterval;
	}

	public void setReplenishTimeInterval(int replenishTimeInterval) {
		this.replenishTimeInterval = replenishTimeInterval;
	}

	public int getCandyTransferRate() {
		return candyTransferRate;
	}

	public void setCandyTransferRate(int candyTransferRate) {
		this.candyTransferRate = candyTransferRate;
	}

	public int getVisibilityRadius() {
		return visibilityRadius;
	}

	public void setVisibilityRadius(int visibilityRadius) {
		this.visibilityRadius = visibilityRadius;
	}

	private void initialize(int initialCandyAmount, int replenishTimeInterval, int candyTransferRate, int visibilityRadius, Coordinate corr) {
		this.corr = corr;
		this.initialCandyAmount = initialCandyAmount;
		this.replenishTimeInterval = replenishTimeInterval;
		this.candyTransferRate = candyTransferRate;
		this.visibilityRadius = visibilityRadius;
	}

}
