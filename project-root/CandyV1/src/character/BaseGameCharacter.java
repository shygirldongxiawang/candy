package character;

import java.util.ArrayList;

import mapmanager.RTIAttribute;
import mapmanager.RTIObject;
import common.Coordinate;

public class BaseGameCharacter {
    protected Coordinate coordinate      = new Coordinate(0, 0);
    protected int        candyAmount     = 0;
    protected int        id              = 0;
    protected RTIObject  rtiObject       = new RTIObject("null");
    protected int        visibilityRange = 0;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getCandyAmount() {
        return candyAmount;
    }

    public void setCandyAmount(int candyAmount) {
        this.candyAmount = candyAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RTIObject getRtiObject() {
        return rtiObject;
    }

    public void setRtiObject(RTIObject rtiObject) {
        this.rtiObject = rtiObject;
    }

    public int getVisibilityRange() {
        return visibilityRange;
    }

    public void setVisibilityRange(int visibilityRange) {
        this.visibilityRange = visibilityRange;
    }

    @Override
    public String toString() {
        return "BaseGameCharacter [coordinate=" + coordinate + ", candyAmount=" + candyAmount + ", id=" + id + ", rtiObject=" + rtiObject
                + ", visibilityRange=" + visibilityRange + "]";
    }

}
