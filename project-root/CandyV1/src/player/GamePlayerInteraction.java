package player;

import java.util.Iterator;
import java.util.List;

import common.FedConfig;
import rti.RTIAttribute;
import rti.RTIInteraction;
import rti.RTIObject;

public class GamePlayerInteraction {

    private int            candyAmount  = 0;
    private int            objectHandle = 0;
    private RTIInteraction rtiObject;

    public GamePlayerInteraction() {
        this.rtiObject = new RTIInteraction(FedConfig.getPlayerInteractionTag());
        this.rtiObject.addNewAttribute(new RTIAttribute("candyAmount"));
        this.rtiObject.addNewAttribute(new RTIAttribute("objectHandle"));
    }

    public void updateObjectAttributes() {
        List<RTIAttribute> attributeList = this.rtiObject.getAttributeList();

        for (Iterator<RTIAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();
            if (rtiAttribute.getAttributeName().toLowerCase().equals("candyAmount".toLowerCase()) == true)
                this.setCandyAmount(rtiAttribute.getAttributeValue());
            
            else if (rtiAttribute.getAttributeName().toLowerCase().equals("objectHandle".toLowerCase()) == true)
                this.setObjectHandle(rtiAttribute.getAttributeValue());
        }
    }

    public RTIInteraction updateRTIAttributes() {
        List<RTIAttribute> attributeList = this.rtiObject.getAttributeList();

        for (Iterator<RTIAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();
            if (rtiAttribute.getAttributeName().toLowerCase().equals("candyAmount".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.getCandyAmount());
            
            if (rtiAttribute.getAttributeName().toLowerCase().equals("objectHandle".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.getObjectHandle());
        }

        return this.rtiObject;
    }

    public int getCandyAmount() {
        return candyAmount;
    }

    public void setCandyAmount(int candyAmount) {
        this.candyAmount = candyAmount;
    }

    public RTIInteraction getRtiObject() {
        return rtiObject;
    }

    public void setRtiObject(RTIInteraction rtiObject) {
        this.rtiObject = rtiObject;
    }

    public int getObjectHandle() {
        return objectHandle;
    }

    public void setObjectHandle(int objectHandle) {
        this.objectHandle = objectHandle;
    }

    @Override
    public String toString() {
        return "GamePlayerInteraction [candyAmount=" + candyAmount + ", rtiObject=" + rtiObject + "]";
    }

}
