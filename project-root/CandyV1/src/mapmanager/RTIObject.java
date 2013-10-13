package mapmanager;

import java.util.ArrayList;
import java.util.Iterator;

public class RTIObject {

    private int                     classHandle  = 0;
    private int                     objectHandle = 0;
    private String                  objectName   = "null";
    private ArrayList<RTIAttribute> attributeList = new ArrayList<>();

    public RTIObject(String name) {        
        this.objectName = name;
    }

    public RTIObject(RTIObject cloneObject) {        
        this.setClassHandle(cloneObject.classHandle);
        this.setObjectHandle(cloneObject.objectHandle);
        this.setObjectName(cloneObject.objectName);

        for (Iterator iterator = cloneObject.attributeList.iterator(); iterator.hasNext();) {
            RTIAttribute attribute = (RTIAttribute) iterator.next();
            this.addNewAttribute(new RTIAttribute(attribute));
        }

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

    public int getObjectHandle() {
        return objectHandle;
    }

    public void setObjectHandle(int objectHandle) {
        this.objectHandle = objectHandle;
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
        return "RTIObject [classHandle=" + classHandle + ", objectHandle=" + objectHandle + ", objectName=" + objectName + ", attributeList="
                + attributeList + "]";
    }

}
