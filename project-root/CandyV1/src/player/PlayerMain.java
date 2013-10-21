package player;

import java.util.Iterator;

import hla.rti.RTIexception;
import main.Main;
import rti.RTIFed;
import rti.RTIFedAmbHelper;
import rti.RTIFedProperties;
import rti.RTIFedPropertiesBase;
import rti.RTIObject;
import character.GameAvatar;
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

        this.federate.advanceTime(1.0);

        // main execution loop
        while (Main.FEDERATE_IS_RUNNING) {

            // // player - house candy interaction
            // for (Iterator iterator =
            // rtiFedAmbHelper.getHouseList().values().iterator();
            // iterator.hasNext();) {
            // GameHouse house = (GameHouse) iterator.next();
            // if (house.isVisible(gamePlayer.getCoordinate(),
            // FedConfig.getHouseDimension(), FedConfig.getPlayerDimension())) {
            // gamePlayer.getCandy(FedConfig.getHouseTransferRate());
            // }
            // }
            //
            // boolean sharedCandy = false;
            // // player - player candy interaction
            // for (Iterator iterator =
            // rtiFedAmbHelper.getAvatarList().values().iterator();
            // iterator.hasNext();) {
            // GameAvatar player = (GameAvatar) iterator.next();
            // if (gamePlayer.isVisible(player.getCoordinate(),
            // FedConfig.getPlayerDimension(), FedConfig.getPlayerDimension()))
            // {
            // int average = Math.abs((gamePlayer.getCandyAmount() +
            // player.getCandyAmount()) / 2);
            // gamePlayer.setCandyAmount(average);
            // sharedCandy = true;
            // break;
            // }
            // }
            //
            // if (sharedCandy == false) {
            // // player - avatar candy interaction
            // for (Iterator iterator =
            // rtiFedAmbHelper.getPlayerList().values().iterator();
            // iterator.hasNext();) {
            // GamePlayer avatar = (GamePlayer) iterator.next();
            // if (gamePlayer.isVisible(avatar.getCoordinate(),
            // FedConfig.getPlayerDimension(), FedConfig.getAvatarDimension()))
            // {
            // int average = Math.abs((gamePlayer.getCandyAmount() +
            // avatar.getCandyAmount()) / 2);
            // gamePlayer.setCandyAmount(average);
            // break;
            // }
            // }
            // }

            for (Iterator iterator = rtiFedAmbHelper.getGamePlayerUpdateInteractions().iterator(); iterator.hasNext();) {
                GamePlayerInteraction interaction = (GamePlayerInteraction) iterator.next();
                if (interaction.getObjectHandle() == this.gamePlayer.getRtiObject().getObjectHandle())
                    this.gamePlayer.setCandyAmount(interaction.getCandyAmount());
            }
            rtiFedAmbHelper.clearPrevInteraction();

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

        // publish & subscribe to avatars
        GameAvatar avatar = new GameAvatar();
        this.federate.subscribe(avatar.getRtiObject());

        // publish &subscribe players
        GamePlayer player = new GamePlayer();
        this.federate.publish(player.getRtiObject());
        this.federate.subscribe(player.getRtiObject());

        GamePlayerInteraction interaction = new GamePlayerInteraction();
        this.federate.subscribeInteraction(interaction.getRtiObject());

    }

    private void register() throws RTIexception {
        this.federate.loadRTIObject(this.gamePlayer.getRtiObject());
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
