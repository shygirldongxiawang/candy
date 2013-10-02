package common;

public class Interaction {
    private int classHandle;
    private int propertiesHandle;

    public Interaction() {
        init(0, 0);
    }

    public Interaction(int classHandle, int propertiesHandle) {
        init(classHandle, propertiesHandle);
    }

    private void init(int classHandle, int propertiesHandle) {
        this.classHandle = classHandle;
        this.propertiesHandle = propertiesHandle;
    }

    public int getClassHandle() {
        return classHandle;
    }

    public void setClassHandle(int classHandle) {
        this.classHandle = classHandle;
    }

    public int getPropertiesHandle() {
        return propertiesHandle;
    }

    public void setPropertiesHandle(int propertiesHandle) {
        this.propertiesHandle = propertiesHandle;
    }

    @Override
    public String toString() {
        return "Interaction [classHandle=" + classHandle + ", propertiesHandle=" + propertiesHandle + "]";
    }

}
