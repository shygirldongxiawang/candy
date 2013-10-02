package common;

import java.util.HashMap;

public class InteractionMap {

    private HashMap<String, Interaction> name2InteractionMap;
    private HashMap<Integer, String>     classHandle2NameMap;

    public InteractionMap() {
        this.name2InteractionMap = new HashMap<>();
        this.classHandle2NameMap = new HashMap<>();
    }

    public void newInteraction(String interactionName, int classHandle, int propertiesHandle) {
        this.name2InteractionMap.put(interactionName, new Interaction(classHandle, propertiesHandle));
        this.classHandle2NameMap.put(classHandle, interactionName);
    }

    public Interaction getInteraction(String interactionName) {
        return this.name2InteractionMap.get(interactionName);
    }

    public String getInteractionName(int classHandle) {
        return classHandle2NameMap.get(classHandle);
    }

    public HashMap<String, Interaction> getName2InteractionMap() {
        return name2InteractionMap;
    }

    public void setName2InteractionMap(HashMap<String, Interaction> name2InteractionMap) {
        this.name2InteractionMap = name2InteractionMap;
    }

    public HashMap<Integer, String> getClassHandle2NameMap() {
        return classHandle2NameMap;
    }

    public void setClassHandle2NameMap(HashMap<Integer, String> classHandle2NameMap) {
        this.classHandle2NameMap = classHandle2NameMap;
    }

    @Override
    public String toString() {
        return "InteractionMap [name2InteractionMap=" + name2InteractionMap + ", classHandle2NameMap=" + classHandle2NameMap + "]";
    }
    
    

}
