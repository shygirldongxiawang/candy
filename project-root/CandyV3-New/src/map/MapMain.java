package map;

import hla.rti.RTIexception;
import main.Main;
import rti.RTIFed;
import rti.RTIFedAmbHelper;
import rti.RTIFedProperties;
import rti.RTIFedPropertiesBase;
import character.GameAvatar;
import character.GameHouse;

import common.FedConfig;

public class MapMain {

    public static final String LOG_TAG = "MainMapManager:: ";

    public static void Main(String[] args, RTIFedPropertiesBase federationProperties) throws RTIexception, InterruptedException {
        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is started");

        /**
         * 
         * INITIALIZATION
         * 
         */
        // federate properties
        RTIFedProperties properties = new RTIFedProperties(federationProperties, "MapMain");

        // federate ambassador helper initialization
        RTIFedAmbHelper mapmanager = new RTIFedAmbHelper();

        // federate initialization
        RTIFed federate = new RTIFed();
        federate.initialize(properties, mapmanager);
        mapmanager.setFederate(federate);

        /**
         * 
         * PRE-PROCESSING
         * 
         */
        /** HOUSE */
        GameHouse houseArr[] = new GameHouse[0];

        // initialize objects
        houseArr = new GameHouse[FedConfig.getMapHouseNum()];
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

        /** AVATAR */
        GameAvatar avatarArr[] = new GameAvatar[0];

        // initialize objects
        avatarArr = new GameAvatar[FedConfig.getMapAvatarNum()];
        for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
            avatarArr[i] = new GameAvatar();
            avatarArr[i].loadDefaultVariables();
            avatarArr[i].updateRTIAttributes();
            federate.loadRTIObject(avatarArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Creation:: Avatar:: Coordinate:: ", avatarArr[i].getCoordinate().toString());
        }

        // publish avatar object
        // load all class and attribute handles to base avatar instance
        federate.publish(avatarArr[0].getRtiObject());

        // register avatar objects
        for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
            federate.registerObject(avatarArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Registration:: Avatar:: Id:: ", "" + avatarArr[i].getRtiObject().getObjectHandle());
        }

        // update avatar variables
        for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
            federate.updateAttributeValues(avatarArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Update:: Avatar:: Id:: ", "" + avatarArr[i].getRtiObject().getObjectHandle());
            federate.advanceTime(1.0);
        }

        /**
         * 
         * EXECUTION
         * 
         */
        // main execution loop
        while (Main.FEDERATE_IS_RUNNING) {
            federate.advanceTime(1.0);

            // update avatar variables
            for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
                avatarArr[i].move();
                avatarArr[i].updateRTIAttributes();
                federate.updateAttributeValues(avatarArr[i].getRtiObject());
                FedConfig.logDebug(LOG_TAG + "Update:: Avatar:: Id:: ", "" + avatarArr[i].getRtiObject().getObjectHandle());
                federate.advanceTime(1.0);
            }
        }

        /**
         * 
         * POST-PROCESSING
         * 
         */
        // delete house objects
        for (int i = 0; i < FedConfig.getMapHouseNum(); i++) {
            federate.deleteObject(houseArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Deletion:: ", houseArr[i].getRtiObject().toString());
        }

        // delete avatar objects
        for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
            federate.deleteObject(avatarArr[i].getRtiObject());
            FedConfig.logDebug(LOG_TAG + "Deletion:: ", avatarArr[i].getRtiObject().toString());
        }

        // destroy federate
        federate.destroy(properties);
        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is finished");
    }

}
