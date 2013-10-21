package character;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;

import rti.RTIAttribute;
import rti.RTIObject;

import common.CCoordinate;
import common.FedConfig;

public class GameHouse extends BaseGameCharacter {
    private int transferRate   = 0;
    private int reloadInterval = 0;
    private int transferIndex  = 0;

    public GameHouse() {
        this.rtiObject = new RTIObject(FedConfig.getHouseFedTag());
        this.rtiObject.addNewAttribute(new RTIAttribute("candyAmount"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrX"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrY"));
        this.rtiObject.addNewAttribute(new RTIAttribute("id"));
        this.rtiObject.addNewAttribute(new RTIAttribute("transferRate"));
        this.rtiObject.addNewAttribute(new RTIAttribute("reloadInterval"));
        this.rtiObject.addNewAttribute(new RTIAttribute("visibilityRange"));
    }

    public GameHouse(RTIObject rtiObject) {
        this.setRtiObject(new RTIObject(rtiObject));
    }

    public void loadDefaultVariables() {
        this.setCandyAmount(FedConfig.getHouseMaxCandyAmount());
        this.setReloadInterval(FedConfig.getHouseReloadInterval());
        this.setTransferRate(FedConfig.getHouseTransferRate());
        this.setVisibilityRange(FedConfig.getHouseVisibilityRange());
        this.setId(ID++);
        this.setCoordinate(CCoordinate.GenerateRandomCoordinate(FedConfig.getMapDimension().getWidth(), FedConfig.getMapDimension().getHeight()));
    }

    public void reloadCandies() {
        this.setCandyAmount(FedConfig.getHouseMaxCandyAmount());
    }

    public void loseCandy(int candyAmount) {
        super.loseCandy(candyAmount);
        this.transferIndex += 1;

        if (transferIndex % reloadInterval == 0) {
            transferIndex = 0;
            reloadCandies();
        }
    }

    public int getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(int transferRate) {
        this.transferRate = transferRate;
    }

    public int getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    @Override
    public RTIObject getRtiObject() {
        return this.rtiObject;
    }

    public void updateObjectAttributes() {
        List<RTIAttribute> attributeList = this.rtiObject.getAttributeList();
        for (Iterator<RTIAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();

            if (rtiAttribute.getAttributeName().toLowerCase().equals("candyAmount".toLowerCase()) == true)
                this.setCandyAmount(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrX".toLowerCase()) == true)
                this.getCoordinate().setX(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrY".toLowerCase()) == true)
                this.getCoordinate().setY(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("id".toLowerCase()) == true)
                this.setId(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("transferRate".toLowerCase()) == true)
                this.setTransferRate(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("reloadInterval".toLowerCase()) == true)
                this.setReloadInterval(rtiAttribute.getAttributeValue());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("visibilityRange".toLowerCase()) == true)
                this.setVisibilityRange(rtiAttribute.getAttributeValue());

            else
                System.err.println(rtiAttribute.getAttributeName());
        }

    }

    public RTIObject updateRTIAttributes() {
        // update previous values
        List<RTIAttribute> attributeList = this.rtiObject.getAttributeList();
        for (Iterator<RTIAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();

            if (rtiAttribute.getAttributeName().toLowerCase().equals("candyAmount".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.getCandyAmount());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrX".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.coordinate.getX());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrY".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.coordinate.getY());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("id".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.id);

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("transferRate".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.getTransferRate());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("reloadInterval".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.reloadInterval);

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("visibilityRange".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.visibilityRange);

            else
                System.err.println(rtiAttribute.getAttributeName());
        }

        return this.rtiObject;
    }

    @Override
    public void drawItself(Graphics g) {

        drawVisibilityRange(g, FedConfig.getHouseDimension());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(FedConfig.getHouseColor());
        g2d.fillRect(this.coordinate.getX(), this.coordinate.getY(), FedConfig.getHouseDimension().getWidth(), FedConfig.getHouseDimension()
                                                                                                                        .getHeight());
        drawCandyAmount(g, FedConfig.getHouseDimension());

    }

    @Override
    public String toString() {
        return "GameHouse [transferRate=" + transferRate + ", reloadInterval=" + reloadInterval + ", coordinate=" + coordinate + ", candyAmount="
                + candyAmount + ", id=" + id + ", visibilityRange=" + visibilityRange + ", rtiObject=" + this.getRtiObject() + "]";
    }

}
