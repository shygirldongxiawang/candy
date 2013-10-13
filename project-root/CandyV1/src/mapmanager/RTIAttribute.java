package mapmanager;

public class RTIAttribute {
    private int    attributeHandle = 0;
    private String attributeName   = "null";
    private int    attributeValue  = 0;

    public RTIAttribute(String name) {
        this.attributeName = name;
    }

    public RTIAttribute(RTIAttribute cloneObject) {
        this.setAttributeHandle(cloneObject.attributeHandle);
        this.setAttributeName(cloneObject.attributeName);
        this.setAttributeValue(cloneObject.attributeValue);
    }

    public int getAttributeHandle() {
        return attributeHandle;
    }

    public void setAttributeHandle(int attributeHandle) {
        this.attributeHandle = attributeHandle;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public int getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(int attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return "RTIAttribute [attributeHandle=" + attributeHandle + ", attributeName=" + attributeName + ", attributeValue=" + attributeValue + "]";
    }

}
