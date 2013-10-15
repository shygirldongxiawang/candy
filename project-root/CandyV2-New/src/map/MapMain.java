package map;

import main.Main;
import rti.RTIFed;
import rti.RTIFedAmbHelper;
import rti.RTIFedProperties;
import rti.RTIFedPropertiesBase;
import hla.rti.RTIexception;
import character.GameHouse;
import common.FedConfig;

public class MapMain {

    public static final String LOG_TAG = "MainMapManager:: ";

    public static void Main(String[] args, RTIFedPropertiesBase federationProperties) throws RTIexception, InterruptedException {
        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is started");

        // federate properties
        RTIFedProperties properties = new RTIFedProperties(federationProperties, "MapMain");

        // federate ambassador helper initialization
        RTIFedAmbHelper mapmanager = new RTIFedAmbHelper();

        // federate initialization
        RTIFed federate = new RTIFed();
        federate.initialize(properties, mapmanager);
        mapmanager.setFederate(federate);

        // initialize objects
        GameHouse houseArr[] = new GameHouse[FedConfig.getMapHouseNum()];
        for (int i = 0; i < FedConfig.getMapHouseNum(); i++) {
            houseArr[i] = new GameHouse();
            houseArr[i].loadDefaultVariables();
            houseArr[i].updateRTIAttributes();
            federate.loadRTIObject(houseArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Creation:: House:: Coordinate:: ", houseArr[i].getCoordinate().toString());
        }

        // publish house object
        // load all class and attribute handles to base house instance
        federate.publish(houseArr[0].getRtiObject());

        // register house objects
        for (int i = 0; i < FedConfig.getMapHouseNum(); i++) {
            federate.registerObject(houseArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Registration:: House:: Id:: ", "" + houseArr[i].getRtiObject().getObjectHandle());
        }
        
        // update house variables
        for (int i = 0; i < FedConfig.getMapHouseNum(); i++) {            
            federate.updateAttributeValues(houseArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Update:: House:: Id:: ", "" + houseArr[i].getRtiObject().getObjectHandle());
            federate.advanceTime(1.0);
        }        

        // main execution loop
        while (Main.FEDERATE_IS_RUNNING) {
            federate.advanceTime(1.0);            
        }

        // delete house objects
        for (int i = 0; i < FedConfig.getMapHouseNum(); i++) {
            federate.deleteObject(houseArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Deletion:: ", houseArr[i].getRtiObject().toString());            
        }

        // destroy federate
        federate.destroy(properties);
        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is finished");
    }

}
