package character;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;

import rti.RTIAttribute;
import rti.RTIObject;
import common.CCoordinate;
import common.CDirection;
import common.FedConfig;

public class GamePlayer extends BaseGameCharacter {

    public GamePlayer() {
        this.rtiObject = new RTIObject(FedConfig.getPlayerFedTag());
        this.rtiObject.addNewAttribute(new RTIAttribute("candyAmount"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrX"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrY"));
        this.rtiObject.addNewAttribute(new RTIAttribute("id"));
        this.rtiObject.addNewAttribute(new RTIAttribute("visibilityRange"));        
    }

    public GamePlayer(RTIObject rtiObject) {
        this.setRtiObject(new RTIObject(rtiObject));
    }

    public void loadDefaultVariables() {
        this.setCandyAmount(FedConfig.getPlayerInitialCandyAmount());
        this.setVisibilityRange(FedConfig.getPlayerVisibilityRange());
        this.setId(ID++);
        this.setCoordinate(CCoordinate.GenerateRandomCoordinate(FedConfig.getMapDimension().getWidth(), FedConfig.getMapDimension().getHeight()));
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

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("visibilityRange".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.visibilityRange);

            else
                System.err.println(rtiAttribute.getAttributeName());
        }

        return this.rtiObject;
    }

    @Override
    public void drawItself(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(FedConfig.getPlayerColor());
        g2d.fillRect(this.coordinate.getX(), this.coordinate.getY(), FedConfig.getPlayerDimension().getWidth(), FedConfig.getPlayerDimension()
                                                                                                                         .getHeight());
    }

    public void move(int direction) {
        switch (direction) {
        case CDirection.UP:
            this.coordinate.setY(this.coordinate.getY() - 1);
            break;
        case CDirection.DOWN:
            this.coordinate.setY(this.coordinate.getY() + 1);
            break;
        case CDirection.LEFT:
            this.coordinate.setX(this.coordinate.getX() - 1);
            break;
        case CDirection.RIGHT:
            this.coordinate.setX(this.coordinate.getX() + 1);
            break;
        default:
            break;
        }
    }

}
