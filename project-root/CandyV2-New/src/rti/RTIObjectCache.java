package rti;

import java.util.HashMap;

public class RTIObjectCache {

    private static RTIObjectCache    instance = new RTIObjectCache();

    private HashMap<Integer, String> objectHandle2ClassName;
    private HashMap<Integer, String> classHandle2ClassName;
    private HashMap<String, Integer> className2ClassHandle;

    private RTIObjectCache() {
        this.classHandle2ClassName = new HashMap<>();
        this.className2ClassHandle = new HashMap<>();
        this.objectHandle2ClassName = new HashMap<>();
    }

    public static RTIObjectCache getInstance() {
        return instance;
    }

    public void addNewRTIClass(Integer classHandle, String className) {
        this.classHandle2ClassName.put(classHandle, className);
        this.className2ClassHandle.put(className, classHandle);
    }

    public void addNewRTIObject(Integer objectHandle, String className) {
        this.objectHandle2ClassName.put(objectHandle, className);

    }

    public String getRTIByCHandle(Integer classHandle) {
        return this.classHandle2ClassName.get(classHandle);
    }

    public Integer getRTIByCName(String className) {
        return this.className2ClassHandle.get(className);
    }

    public String getRTIByOHandle(Integer objectHandle) {
        return this.objectHandle2ClassName.get(objectHandle);
    }
}
