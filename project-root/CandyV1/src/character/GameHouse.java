package character;

import java.util.Iterator;
import java.util.List;

import common.Coordinate;
import common.FedConfig;
import mapmanager.RTIAttribute;
import mapmanager.RTIObject;

public class GameHouse extends BaseGameCharacter {
    private int transferRate   = 0;
    private int reloadInterval = 0;

    public GameHouse() {
        this.rtiObject = new RTIObject("ObjectRoot.House");
        this.rtiObject.addNewAttribute(new RTIAttribute("candyAmount"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrX"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrY"));
        this.rtiObject.addNewAttribute(new RTIAttribute("id"));
        this.rtiObject.addNewAttribute(new RTIAttribute("transferRate"));
        this.rtiObject.addNewAttribute(new RTIAttribute("reloadInterval"));
        this.rtiObject.addNewAttribute(new RTIAttribute("visibilityRange"));

        this.setCandyAmount(FedConfig.getHouseMaxCandyAmount());
        this.setReloadInterval(FedConfig.getHouseReloadInterval());
        this.setTransferRate(FedConfig.getHouseTransferRate());
        this.setVisibilityRange(FedConfig.getHouseVisibilityRange());
    }

    public GameHouse(GameHouse cloneObject) {
        this.setCandyAmount(cloneObject.candyAmount);
        this.setReloadInterval(cloneObject.reloadInterval);
        this.setTransferRate(cloneObject.transferRate);
        this.setVisibilityRange(cloneObject.visibilityRange);
        this.setRtiObject(new RTIObject(cloneObject.rtiObject));
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
        // update previous values
        List<RTIAttribute> attributeList = this.rtiObject.getAttributeList();
        for (Iterator<RTIAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();

            if (rtiAttribute.getAttributeName().toLowerCase().equals("candyAmount".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.getCandyAmount());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrX".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.coordinate.getY());

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrY".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.coordinate.getX());

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

    public static GameHouse GenerateGameHouse(GameHouse cloneObject) {
        GameHouse instance = new GameHouse(cloneObject);
        instance.setId(FedConfig.UTIL_RND.nextInt(FedConfig.getHouseIdRange()));
        instance.setCoordinate(Coordinate.GenerateRandomCoordinate(FedConfig.getMapDimension().getWidth(), FedConfig.getMapDimension().getHeight()));
        return instance;
    }

    @Override
    public String toString() {
        return "GameHouse [transferRate=" + transferRate + ", reloadInterval=" + reloadInterval + ", coordinate=" + coordinate + ", candyAmount="
                + candyAmount + ", id=" + id + ", visibilityRange=" + visibilityRange + ", rtiObject=" + this.getRtiObject() + "]";
    }

}
