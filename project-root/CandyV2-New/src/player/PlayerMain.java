package player;

import hla.rti.RTIexception;

import javax.swing.JFrame;

import main.Main;
import rti.RTIFed;
import rti.RTIFedAmbHelper;
import rti.RTIFedProperties;
import rti.RTIFedPropertiesBase;
import character.GameHouse;
import character.GamePlayer;
import common.FedConfig;

public class PlayerMain {

    public static final String LOG_TAG         = "MainPlayer:: ";

    private RTIFedAmbHelper    rtiFedAmbHelper = null;
    private GamePlayer         gamePlayer      = null;
    private RTIFedProperties   properties      = null;
    private RTIFed             federate        = null;

    public PlayerMain(String[] args, RTIFedPropertiesBase federationProperties, String federateName) {
        this.properties = new RTIFedProperties(federationProperties, federateName);
        this.rtiFedAmbHelper = new RTIFedAmbHelper();
        this.gamePlayer = new GamePlayer();
        this.federate = new RTIFed();
        
    }

    public void start() throws RTIexception, InterruptedException {
        FedConfig.logDebug(LOG_TAG + "PlayerInit", "player is started");

        // initialize necessary objects
        this.initialize();

        // publish and subscribe to objects
        this.publishSubscribe();
        
        // register our objects
        this.register();
        
        // main execution loop
        while (Main.FEDERATE_IS_RUNNING) {            
            this.gamePlayer.updateRTIAttributes();
            this.federate.updateAttributeValues(this.gamePlayer.getRtiObject());
            this.federate.advanceTime(1.0);
        }

        // destroy federate
        this.federate.destroy(this.properties);
        FedConfig.logDebug(LOG_TAG + "PlayerInit", "player is finished");
    }

    private void initialize() throws RTIexception {
        gamePlayer.loadDefaultVariables();
        gamePlayer.updateRTIAttributes();
        
        federate.initialize(properties, rtiFedAmbHelper);
        rtiFedAmbHelper.setFederate(federate);
    }

    private void publishSubscribe() throws RTIexception {
        // publish & subscribe to houses
        GameHouse house = new GameHouse();
        this.federate.subscribe(house.getRtiObject());
        this.federate.publish(house.getRtiObject());

        // publish &subscribe players
        this.federate.subscribe(this.gamePlayer.getRtiObject());
        this.federate.publish(this.gamePlayer.getRtiObject());
    }
    
    private void register() throws RTIexception {
        federate.registerObject(this.gamePlayer.getRtiObject());
    }

    public RTIFedAmbHelper getRtiFedAmbHelper() {
        return rtiFedAmbHelper;
    }

    public void setRtiFedAmbHelper(RTIFedAmbHelper rtiFedAmbHelper) {
        this.rtiFedAmbHelper = rtiFedAmbHelper;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}
