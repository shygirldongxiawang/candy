package character;

import java.awt.Graphics;

import rti.RTIObject;

import common.CCoordinate;

public class BaseGameCharacter {
    protected static int  ID              = 1;
    protected CCoordinate coordinate      = new CCoordinate(0, 0);
    protected int         candyAmount     = 0;
    protected int         id              = 0;
    protected RTIObject   rtiObject       = new RTIObject("null");
    protected int         visibilityRange = 0;

    public CCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(CCoordinate coordinate) {
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

    public void drawItself(Graphics g) {
    }

    @Override
    public String toString() {
        return "BaseGameCharacter [coordinate=" + coordinate + ", candyAmount=" + candyAmount + ", id=" + id + ", rtiObject=" + rtiObject
                + ", visibilityRange=" + visibilityRange + "]";
    }

}
