package mapmanager;

public class RTIFedPropertiesBase {
    private String fomFilePath;
    private String fedSyncPoint;
    private String federationName;

    public RTIFedPropertiesBase(String fomFilePath, String fedSyncPoint, String federationName) {
        super();
        this.fomFilePath = fomFilePath;
        this.fedSyncPoint = fedSyncPoint;
        this.federationName = federationName;
    }

    public String getFomFilePath() {
        return fomFilePath;
    }

    public void setFomFilePath(String fomFilePath) {
        this.fomFilePath = fomFilePath;
    }

    public String getFedSyncPoint() {
        return fedSyncPoint;
    }

    public void setFedSyncPoint(String fedSyncPoint) {
        this.fedSyncPoint = fedSyncPoint;
    }

    public String getFederationName() {
        return federationName;
    }

    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }

}
