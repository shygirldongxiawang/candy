package main;

import java.util.ArrayList;
import java.util.List;

import character.GameHouse;
import hla.rti.RTIexception;
import mapmanager.MapManager;
import mapmanager.RTIAttribute;
import mapmanager.RTIFed;
import mapmanager.RTIFedProperties;
import mapmanager.RTIFedPropertiesBase;
import mapmanager.RTIObject;
import common.Coordinate;
import common.FedConfig;

public class Main {

    public static final String LOG_TAG             = "MainFunction:: ";
    public static boolean      FEDERATE_IS_RUNNING = true;

    public static void main(String[] args) throws RTIexception {

        // generic federation properties
        RTIFedPropertiesBase federationProperties = new RTIFedPropertiesBase(FedConfig.getFedFom(), FedConfig.getFedSync(), FedConfig.getFedName());

        // map manager runs in this node
        if (args[0].toLowerCase().equals(FedConfig.getMapManagerTag().toLowerCase()) == true)
            mainMapManager(args, federationProperties);

    }

    private static void mainMapManager(String[] args, RTIFedPropertiesBase federationProperties) throws RTIexception {
        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is started");

        // federate properties
        RTIFedProperties properties = new RTIFedProperties(federationProperties, "MapManager");

        // federate ambassador helper initialization
        MapManager mapmanager = new MapManager();

        // federate initialization
        RTIFed federate = new RTIFed();
        federate.initialize(properties, mapmanager);

        // base house for clone operations
        GameHouse baseHouse = new GameHouse();

        // publish objects
        // load all class and attribute handles to base house instance
        federate.publish(baseHouse.getRtiObject());

        // create objects
        GameHouse house1 = GameHouse.GenerateGameHouse(baseHouse);
        GameHouse house2 = GameHouse.GenerateGameHouse(baseHouse);
        FedConfig.logDebug(LOG_TAG + "Creation:: ",  house1.toString());

        // register objects
        federate.registerObject(house1.getRtiObject());
        FedConfig.logDebug(LOG_TAG + "Registration:: ", house1.toString());
        
        
        // main execution loop
        while (FEDERATE_IS_RUNNING) {
            System.out.println("running");
            federate.advanceTime(1.0);
            break;
        }
        
        // delete object
        federate.deleteObject(house1.getRtiObject());
        FedConfig.logDebug(LOG_TAG + "Deletion:: ", house1.toString());

        // destroy federate
        federate.destroy(properties);

        FedConfig.logDebug(LOG_TAG + "MapManagerInit", "map manager is finished");
    }
}
