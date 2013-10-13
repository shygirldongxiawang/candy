package mapmanager;

public class RTIFedProperties {
    private RTIFedPropertiesBase rtiBaseProperties;
    private String            federateName;

    public RTIFedProperties(RTIFedPropertiesBase rtiBaseProperties, String federateName) {
        super();
        this.rtiBaseProperties = rtiBaseProperties;
        this.federateName = federateName;
    }

    public RTIFedPropertiesBase getRtiBaseProperties() {
        return rtiBaseProperties;
    }

    public void setRtiBaseProperties(RTIFedPropertiesBase rtiBaseProperties) {
        this.rtiBaseProperties = rtiBaseProperties;
    }

    public String getFederateName() {
        return federateName;
    }

    public void setFederateName(String federateName) {
        this.federateName = federateName;
    }

}
