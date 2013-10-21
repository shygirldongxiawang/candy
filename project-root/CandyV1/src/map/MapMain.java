package map;

import hla.rti.RTIexception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import player.GamePlayerInteraction;
import player.PlayerMain;
import main.Main;
import rti.RTIFed;
import rti.RTIFedAmbHelper;
import rti.RTIFedProperties;
import rti.RTIFedPropertiesBase;
import character.GameAvatar;
import character.GameHouse;
import character.GamePlayer;
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
        }

        /** PLAYER */
        // publish and subscribe player object
        GamePlayer sample = new GamePlayer();
        federate.subscribe(sample.getRtiObject());
        System.err.println(sample.toString());

        GamePlayerInteraction interaction = new GamePlayerInteraction();
        federate.publishInteraction(interaction.getRtiObject());

        // send all updates together
        federate.advanceTime(1.0);

        /**
         * 
         * EXECUTION
         * 
         */
        // main execution loop
        while (Main.FEDERATE_IS_RUNNING) {

            // move avatar
            for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
                if (avatarArr[i].iscontinue() == false) {
                    avatarArr[i].move();
                }
            }

            // avatar - house candy interaction
            for (int i = 0; i < houseArr.length; i++) {
                for (int j = 0; j < avatarArr.length; j++) {
                    if (houseArr[i].isVisible(avatarArr[j].getCoordinate(), FedConfig.getHouseDimension(), FedConfig.getAvatarDimension())) {
                        houseArr[i].loseCandy(FedConfig.getHouseTransferRate());
                        avatarArr[j].getCandy(FedConfig.getHouseTransferRate());
                    }
                }
            }

            // avatar - avatar candy interaction
            for (int i = 0; i < FedConfig.getMapAvatarNum(); i++) {
                GameAvatar avatar1 = avatarArr[i];
                for (int j = 0; j < avatarArr.length; j++) {
                    GameAvatar avatar = avatarArr[j];
                    if (avatar.getRtiObject().getObjectHandle() != avatar1.getRtiObject().getObjectHandle()
                            && avatar1.isVisible(avatar.getCoordinate(), FedConfig.getAvatarDimension(), FedConfig.getAvatarDimension())) {
                        // System.err.println(avatar.toString());
                        // System.err.println(avatar1.toString());
                        // System.err.println("Shared");
                        int average = (avatar1.getCandyAmount() + avatar.getCandyAmount()) / 2;
                        avatar.setCandyAmount(average);
                        avatar1.setCandyAmount(average);
                        // System.err.println(avatar.toString());
                        // System.err.println(avatar1.toString());
                        // System.err.println("");
                        break;
                    }
                }
            }

            // player - house candy interaction
            for (Iterator iterator = mapmanager.getPlayerList().values().iterator(); iterator.hasNext();) {
                GamePlayer gamePlayer = (GamePlayer) iterator.next();
                for (int i = 0; i < houseArr.length; i++) {
                    GameHouse house = houseArr[i];
                    if (house.isVisible(gamePlayer.getCoordinate(), FedConfig.getHouseDimension(), FedConfig.getPlayerDimension())) {
                        house.loseCandy(FedConfig.getHouseTransferRate());

                        GamePlayerInteraction playerUpdateInteraction = new GamePlayerInteraction();
                        federate.loadRTIInteraction(playerUpdateInteraction.getRtiObject());

                        playerUpdateInteraction.setCandyAmount(gamePlayer.getCandyAmount() + FedConfig.getHouseTransferRate());
                        playerUpdateInteraction.setObjectHandle(gamePlayer.getRtiObject().getObjectHandle());
                        playerUpdateInteraction.updateRTIAttributes();

                        federate.sendInteraction(playerUpdateInteraction.getRtiObject());
                        // System.err.println("interaction sent");
                        break;
                    }
                }
            }

            // player - avatar candy interaction
            for (Iterator iterator = mapmanager.getPlayerList().values().iterator(); iterator.hasNext();) {
                GamePlayer gamePlayer = (GamePlayer) iterator.next();
                for (int i = 0; i < avatarArr.length; i++) {
                    GameAvatar avatar = avatarArr[i];
                    if (avatar.isVisible(gamePlayer.getCoordinate(), FedConfig.getAvatarDimension(), FedConfig.getPlayerDimension())) {

                        int average = (gamePlayer.getCandyAmount() + avatar.getCandyAmount()) / 2;
                        avatar.setCandyAmount(average);

                        GamePlayerInteraction playerUpdateInteraction = new GamePlayerInteraction();
                        federate.loadRTIInteraction(playerUpdateInteraction.getRtiObject());

                        playerUpdateInteraction.setCandyAmount(average);
                        playerUpdateInteraction.setObjectHandle(gamePlayer.getRtiObject().getObjectHandle());
                        playerUpdateInteraction.updateRTIAttributes();

                        federate.sendInteraction(playerUpdateInteraction.getRtiObject());
                        // System.err.println("interaction sent");
                        break;
                    }
                }
            }

            // player - player candy interaction
            for (Iterator iterator = mapmanager.getPlayerList().values().iterator(); iterator.hasNext();) {
                GamePlayer player1 = (GamePlayer) iterator.next();
                for (Iterator iterator2 = mapmanager.getPlayerList().values().iterator(); iterator.hasNext();) {
                    GamePlayer player2 = (GamePlayer) iterator.next();

                    if (player1.getRtiObject().getObjectHandle() != player2.getRtiObject().getObjectHandle()
                            && player1.isVisible(player2.getCoordinate(), FedConfig.getPlayerDimension(), FedConfig.getPlayerDimension())) {

                        int average = (player1.getCandyAmount() + player2.getCandyAmount()) / 2;

                        GamePlayerInteraction playerUpdateInteraction = new GamePlayerInteraction();
                        federate.loadRTIInteraction(playerUpdateInteraction.getRtiObject());
                        playerUpdateInteraction.setCandyAmount(average);
                        playerUpdateInteraction.setObjectHandle(player1.getRtiObject().getObjectHandle());
                        playerUpdateInteraction.updateRTIAttributes();
                        federate.sendInteraction(playerUpdateInteraction.getRtiObject());

                        GamePlayerInteraction playerUpdateInteraction2 = new GamePlayerInteraction();
                        federate.loadRTIInteraction(playerUpdateInteraction2.getRtiObject());
                        playerUpdateInteraction2.setCandyAmount(average);
                        playerUpdateInteraction2.setObjectHandle(player2.getRtiObject().getObjectHandle());
                        playerUpdateInteraction2.updateRTIAttributes();
                        federate.sendInteraction(playerUpdateInteraction2.getRtiObject());

                        // System.err.println("interaction sent");
                        break;
                    }
                }
            }

            // send avatar update
            for (int i = 0; i < avatarArr.length; i++) {
                avatarArr[i].updateRTIAttributes();
                federate.updateAttributeValues(avatarArr[i].getRtiObject());
            }
            // send house update
            for (int i = 0; i < houseArr.length; i++) {
                houseArr[i].updateRTIAttributes();
                federate.updateAttributeValues(houseArr[i].getRtiObject());
            }

            federate.advanceTime(1.0);
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
