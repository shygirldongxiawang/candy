package rti;

import java.util.ArrayList;

public class RTIInteraction {
    private int                     classHandle   = 0;
    private String                  objectName    = "null";
    private ArrayList<RTIAttribute> attributeList = new ArrayList<>();

    public RTIInteraction(String name) {
        this.objectName = name;
    }

    public void addNewAttribute(RTIAttribute rtiAttribute) {
        this.attributeList.add(rtiAttribute);
    }

    public int getClassHandle() {
        return classHandle;
    }

    public void setClassHandle(int classHandle) {
        this.classHandle = classHandle;
    }

    public ArrayList<RTIAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(ArrayList<RTIAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String string) {
        this.objectName = string;
    }

    @Override
    public String toString() {
        return "RTIInteraction [classHandle=" + classHandle + ", objectName=" + objectName + ", attributeList=" + attributeList + "]";
    }

}
