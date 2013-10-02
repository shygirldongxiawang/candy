package common;

public class CharacterProperties {

    protected Coordinate corr;
    protected int        id;
    protected String     name;
    protected int        totalCandyAmount;
    protected int        visibilityRadius;

    public CharacterProperties() {
        this.initialize(0, "unnamed", 0, 0, new Coordinate());
    }

    public CharacterProperties(int id, String name, int candyValue, int visibilityRange, Coordinate corr) {
        this.initialize(id, name, candyValue, visibilityRange, corr);
    }

    public Coordinate getCorr() {
        return corr;
    }

    public void setCorr(Coordinate corr) {
        this.corr = corr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCandyAmount() {
        return totalCandyAmount;
    }

    public void setTotalCandyAmount(int totalCandyAmount) {
        this.totalCandyAmount = totalCandyAmount;
    }

    public int getVisibilityRadius() {
        return visibilityRadius;
    }

    public void setVisibilityRadius(int visibilityRadius) {
        this.visibilityRadius = visibilityRadius;
    }

    private void initialize(int id, String name, int candyValue, int visibilityRange, Coordinate corr) {
        this.id = id;
        this.name = name;
        this.totalCandyAmount = candyValue;
        this.visibilityRadius = visibilityRange;
        this.corr = corr;
    }

    @Override
    public String toString() {
        return "CharacterProperties [corr=" + corr + ", id=" + id + ", name=" + name + ", totalCandyAmount=" + totalCandyAmount + ", visibilityRadius=" + visibilityRadius + "]";
    }

}
