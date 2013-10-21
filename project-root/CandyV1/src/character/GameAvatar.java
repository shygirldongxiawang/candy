package character;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import rti.RTIAttribute;
import rti.RTIObject;

import common.CCoordinate;
import common.CDirection;
import common.FedConfig;

public class GameAvatar extends BaseGameCharacter {

    private int           speed                = 0;
    protected CCoordinate MoveIncrement        = new CCoordinate(0, 0);
    protected int         RandomDirectionAngle = 0;
    protected double      MoveSpeed            = 0;
    protected int         RandomMoveTime       = -1;
    protected CCoordinate AverIncre            = new CCoordinate(0, 0);

    public GameAvatar() {
        this.rtiObject = new RTIObject(FedConfig.getAvatarFedTag());
        this.rtiObject.addNewAttribute(new RTIAttribute("candyAmount"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrX"));
        this.rtiObject.addNewAttribute(new RTIAttribute("corrY"));
        this.rtiObject.addNewAttribute(new RTIAttribute("id"));
        this.rtiObject.addNewAttribute(new RTIAttribute("visibilityRange"));

        MoveSpeed = 1.4;
        RandomMoveTime = 1; // default time
    }

    public GameAvatar(RTIObject rtiObject) {
        this.setRtiObject(new RTIObject(rtiObject));
    }

    public void loadDefaultVariables() {
        this.setCandyAmount(FedConfig.getAvatarInitialCandyAmount());
        this.setVisibilityRange((int) FedConfig.getAvatarVisibilityRange());
        this.setId(ID++);
        this.setCoordinate(CCoordinate.GenerateRandomCoordinate(FedConfig.getMapDimension().getWidth(), FedConfig.getMapDimension().getHeight()));
        this.speed = FedConfig.getAvatarSpeed();
    }

    public boolean iscontinue() {
        if (this.RandomMoveTime == 0)
            return false;
        else
            return true;
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

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrX".toLowerCase()) == true) {
                if (this.RandomMoveTime != -1) {
                    this.coordinate.setX(this.coordinate.getX() + this.AverIncre.getX());
                }
                rtiAttribute.setAttributeValue(this.coordinate.getX());
            }

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("corrY".toLowerCase()) == true) {
                if (this.RandomMoveTime != -1) {
                    this.coordinate.setY(this.coordinate.getY() + this.AverIncre.getY());
                }
                rtiAttribute.setAttributeValue(this.coordinate.getY());
            } else if (rtiAttribute.getAttributeName().toLowerCase().equals("id".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.id);

            else if (rtiAttribute.getAttributeName().toLowerCase().equals("visibilityRange".toLowerCase()) == true)
                rtiAttribute.setAttributeValue(this.visibilityRange);

            else
                System.err.println(rtiAttribute.getAttributeName());
        }

        this.RandomMoveTime--;

        return this.rtiObject;
    }

    @Override
    public void drawItself(Graphics g) {

        //drawVisibilityRange(g, FedConfig.getAvatarDimension());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(FedConfig.getAvatarColor());
        g2d.fillRect(this.coordinate.getX(), this.coordinate.getY(), FedConfig.getAvatarDimension().getWidth(), FedConfig.getAvatarDimension()
                                                                                                                         .getHeight());

        drawCandyAmount(g, FedConfig.getAvatarDimension());
    }

    @Override
    public RTIObject getRtiObject() {
        return this.rtiObject;
    }

    public void move() {

        double border_x = FedConfig.getMapDimension().getWidth(), border_y = FedConfig.getMapDimension().getHeight();
        double curr_x, curr_y;
        double mov_x, mov_y;
        int max_time;
        double mov_dis, max_dis;

        Boolean judge;
        int judgecount;

        Random rdTime = FedConfig.UTIL_RND;
        Random rdAngle = FedConfig.UTIL_RND;

        curr_x = (double) this.coordinate.getX();
        curr_y = (double) this.coordinate.getY(); // cordinate I think should be
                                                  // double type
        judge = false;
        judgecount = 0;

        while (!judge) {
            if (curr_x == 0 && curr_y == 0) {
                this.RandomDirectionAngle = rdAngle.nextInt(90);
                max_dis = 2 * this.visibilityRange;
            } else if ((max_dis = (Math.sqrt(Math.pow(border_x - curr_x, 2) + Math.pow(border_y - curr_y, 2)))) < this.visibilityRange) {
                this.RandomDirectionAngle = 90 + rdAngle.nextInt(270);
            } else if ((max_dis = Math.sqrt(Math.pow(curr_x, 2) + Math.pow(curr_y, 2))) < this.visibilityRange) {
                this.RandomDirectionAngle = rdAngle.nextInt(270) - 90;
                if (this.RandomDirectionAngle < 0) {
                    this.RandomDirectionAngle += 360;
                }
            } else {
                this.RandomDirectionAngle = rdAngle.nextInt(360);
                max_dis = Math.sqrt(Math.pow(curr_x, 2) + Math.pow(curr_y, 2));
            }

            max_time = (int) Math.floor(max_dis / this.MoveSpeed);
            if (max_time < 1)
                max_time = 10;
            this.RandomMoveTime = rdTime.nextInt(max_time + 1); // time blong to
                                                                // [0,5)

            mov_dis = (double) (this.MoveSpeed * this.RandomMoveTime);

            mov_x = Math.cos((double) RandomDirectionAngle) * mov_dis;
            mov_y = Math.sin((double) RandomDirectionAngle) * mov_dis;

            if (curr_x + mov_x < border_x && curr_y + mov_y < border_y) {
                this.MoveIncrement.setX((int) Math.ceil(mov_x)); // I think you
                                                                 // should make
                // coordinate x,y to be Double
                this.MoveIncrement.setY((int) Math.ceil(mov_y));
                judge = true;
            } else {
                judgecount++;
                if (judgecount > 10) // this threshold can be defined in file
                {
                    this.MoveIncrement.setX(0);
                    this.MoveIncrement.setY(0);
                    break;
                }
            }

        }

        if (this.RandomMoveTime != 0 && this.MoveIncrement.getX() / this.RandomMoveTime != 0) {
            this.AverIncre.setX((int) Math.ceil(this.MoveIncrement.getX() / this.RandomMoveTime));
        } else {
            this.AverIncre.setX(0);
        }
        if (this.RandomMoveTime != 0 && this.MoveIncrement.getY() / this.RandomMoveTime != 0) {
            this.AverIncre.setY((int) Math.ceil(this.MoveIncrement.getY() / this.RandomMoveTime));
        } else {
            this.AverIncre.setY(0);
        }
        // this.coordinate.setX(this.coordinate.getX() +
        // this.MoveIncrement.getX());
        // this.coordinate.setY(this.coordinate.getY() +
        // this.MoveIncrement.getY());

    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
