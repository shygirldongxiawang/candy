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

    private final static int PLAYER = 0;
    private final static int GOD    = 1;
    private int              mode;
    private int              speed;

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
        this.setVisibilityRange((int) FedConfig.getPlayerVisibilityRange());
        this.setId(ID++);
        this.setCoordinate(CCoordinate.GenerateRandomCoordinate(FedConfig.getMapDimension().getWidth(), FedConfig.getMapDimension().getHeight()));
        this.mode = GamePlayer.PLAYER;
        this.speed = FedConfig.getPlayerSpeed();
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

    public boolean isVisible(CCoordinate externalItem) {
        double visibleX = this.getCoordinate().getX();
        double visibleY = this.getCoordinate().getY();

        double diffX = Math.abs(externalItem.getX() - visibleX);
        double diffY = Math.abs(externalItem.getY() - visibleY);

        double power2 = 2;
        double hypo = Math.sqrt(Math.pow(diffX, power2) + Math.pow(diffY, power2));

        if (hypo > this.visibilityRange) {
            return false;
        }

        return true;
    }

    public void modeSwitch() {
        if (this.mode == GamePlayer.PLAYER) {
            this.setVisibilityRange((int) FedConfig.getGodVisibilityRange());
            this.setSpeed(FedConfig.getGodSpeed());
            this.mode = GamePlayer.GOD;
        } else if (this.mode == GamePlayer.GOD) {
            this.setVisibilityRange((int) FedConfig.getPlayerVisibilityRange());
            this.setSpeed(FedConfig.getPlayerSpeed());
            this.mode = GamePlayer.PLAYER;
        }
    }

    public void move(int direction) {
        switch (direction) {
        case CDirection.UP:
            this.coordinate.setY(this.coordinate.getY() - this.speed);
            break;
        case CDirection.DOWN:
            this.coordinate.setY(this.coordinate.getY() + this.speed);
            break;
        case CDirection.LEFT:
            this.coordinate.setX(this.coordinate.getX() - this.speed);
            break;
        case CDirection.RIGHT:
            this.coordinate.setX(this.coordinate.getX() + this.speed);
            break;
        default:
            break;
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
