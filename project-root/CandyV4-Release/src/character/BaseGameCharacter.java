package character;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import rti.RTIObject;
import common.CCoordinate;
import common.CDimension;
import common.FedConfig;

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

    public void getCandy(int candyAmount) {
        this.candyAmount = this.candyAmount + candyAmount;
    }

    public void loseCandy(int candyAmount) {
        this.candyAmount = this.candyAmount - candyAmount;

        if (this.candyAmount < 0)
            this.candyAmount = 0;
    }

    public void drawItself(Graphics g) {
    }

    public boolean isVisible(CCoordinate externalItem, CDimension callerDimension, CDimension calleeDimension) {

        int obj1CentreX = this.getCoordinate().getX() + callerDimension.getWidth() / 2;
        int obj1CentreY = this.getCoordinate().getY() + callerDimension.getHeight() / 2;

        int obj2CentreX = externalItem.getX() + calleeDimension.getWidth() / 2;
        int obj2CentreY = externalItem.getY() + calleeDimension.getHeight() / 2;

        double diffX = Math.abs(obj1CentreX - obj2CentreX) - (callerDimension.getWidth() / 2 + calleeDimension.getWidth() / 2);
        double diffY = Math.abs(obj1CentreY - obj2CentreY) - (callerDimension.getHeight() / 2 + calleeDimension.getHeight() / 2);

        if (diffX <= this.visibilityRange && diffY <= this.visibilityRange) {
            return true;
        }

        return false;
    }

    public void drawVisibilityRange(Graphics g, CDimension callerDimension) {
        Graphics2D g2d = (Graphics2D) g;

        int visibleX = this.getCoordinate().getX() - this.visibilityRange;
        int visibleY = this.getCoordinate().getY() - this.visibilityRange;

        int visibleWidth = this.visibilityRange * 2 + callerDimension.getWidth();
        int visibleHeight = this.visibilityRange * 2 + callerDimension.getHeight();

        g2d.setColor(FedConfig.getVisibilityRangeColor());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,2 * 0.1f));        
        g2d.fillRect(visibleX, visibleY, visibleWidth, visibleHeight);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,10 * 0.1f));

    }

    public void drawCandyAmount(Graphics g, CDimension callerDimension) {
        Graphics2D g2d = (Graphics2D) g;
        int textPaddingX = 5;
        int textPaddingY = 20;

        g2d.setColor(FedConfig.getTextColor());
        // int middleCoorX = this.coordinate.getX() + callerDimension.getWidth()
        // / 2;
        // int middleCoorY = this.coordinate.getY() +
        // callerDimension.getHeight() / 2;
        // g2d.drawString("" + this.candyAmount, middleCoorX, middleCoorY);
        g2d.drawString("" + this.candyAmount, this.coordinate.getX() + textPaddingX, this.coordinate.getY() + textPaddingY);
    }

    @Override
    public String toString() {
        return "BaseGameCharacter [coordinate=" + coordinate + ", candyAmount=" + candyAmount + ", id=" + id + ", rtiObject=" + rtiObject
                + ", visibilityRange=" + visibilityRange + "]";
    }

}
