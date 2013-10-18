package rti;

import interaction.GamePlayerInteraction;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.portico.lrc.management.Federate;

import common.FedConfig;
import character.GameAvatar;
import character.GameHouse;
import character.GamePlayer;
import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti13.java1.EncodingHelpers;
import hla.rti13.java1.FederateInternalError;

public class RTIFedAmbHelper implements IRTIFedAmbHelper {

    public final static String                           LOG_TAG                      = "MapMain:AmbassadorHelper:: ";
    private RTIFed                                       federate                     = null;
    private ConcurrentHashMap<Integer, GameHouse>        houseList                    = new ConcurrentHashMap<Integer, GameHouse>();
    private ConcurrentHashMap<Integer, GamePlayer>       playerList                   = new ConcurrentHashMap<Integer, GamePlayer>();
    private ConcurrentHashMap<Integer, GameAvatar>       avatarList                   = new ConcurrentHashMap<Integer, GameAvatar>();

    private ConcurrentLinkedQueue<GamePlayerInteraction> gamePlayerUpdateInteractions = new ConcurrentLinkedQueue<>();

    public RTIFedAmbHelper() {
    }

    public RTIFedAmbHelper(RTIFed rtifed) {
        this.federate = rtifed;
    }

    public synchronized void interactionReceived(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime,
            EventRetractionHandle eventRetractionHandle) throws RTIexception, NumberFormatException, FederateInternalError {

        GamePlayerInteraction playerInteraction = new GamePlayerInteraction();
        federate.loadRTIInteraction(playerInteraction.getRtiObject());

        for (int i = 0; i < theInteraction.size(); i++) {
            int attHandle = theInteraction.getParameterHandle(i);
            int attValue = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(i)));

            for (Iterator iterator = playerInteraction.getRtiObject().getAttributeList().iterator(); iterator.hasNext();) {
                RTIAttribute objectAttribute = (RTIAttribute) iterator.next();
                if (objectAttribute.getAttributeHandle() == attHandle) {
                    objectAttribute.setAttributeValue(attValue);
                    break;
                }
            }
        }

        playerInteraction.updateObjectAttributes();
        gamePlayerUpdateInteractions.add(playerInteraction);

        // System.err.println(interaction.toString());
    }

    @Override
    public synchronized void objectCreated(int theObject, int theObjectClass, String objectName) throws RTIexception {
        // convert class handle to class name
        String className = RTIObjectCache.getInstance().getRTIByCHandle(theObjectClass);

        // house object
        if (className.toLowerCase().equals(FedConfig.getHouseFedTag().toLowerCase()) == true) {
            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "House is created");

            // load class and attribute handles
            GameHouse house = new GameHouse();
            federate.loadRTIObject(house.getRtiObject());

            // set unique object handle
            house.getRtiObject().setObjectHandle(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "House is loaded:: " + house.toString());

            // put to our list
            houseList.put(theObject, house);

            // put to cache
            RTIObjectCache.getInstance().addNewRTIObject(theObject, className);

        }
        // avatar
        else if (className.toLowerCase().equals(FedConfig.getAvatarFedTag().toLowerCase()) == true) {

            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "Avatar is created");

            // load class and attribute handles
            GameAvatar avatar = new GameAvatar();
            federate.loadRTIObject(avatar.getRtiObject());

            // set unique object handle
            avatar.getRtiObject().setObjectHandle(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "Avatar is loaded:: " + avatar.toString());

            // put to our list
            avatarList.put(theObject, avatar);

            // put to cache
            RTIObjectCache.getInstance().addNewRTIObject(theObject, className);

        }
        // player
        else if (className.toLowerCase().equals(FedConfig.getPlayerFedTag().toLowerCase()) == true) {

            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "PlayerMain is created");
            // load class and attribute handles
            GamePlayer player = new GamePlayer();
            federate.loadRTIObject(player.getRtiObject());

            // set unique object handle
            player.getRtiObject().setObjectHandle(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectCreationCallback:: ", "Player is loaded:: " + player.toString());

            // put to our list
            playerList.put(theObject, player);

            // put to cache
            RTIObjectCache.getInstance().addNewRTIObject(theObject, className);
        }
        // else
        else {
            System.err.println("No found object tag");
        }

    }

    @Override
    public synchronized void objectUpdated(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime,
            EventRetractionHandle retractionHandle) throws RTIexception, NumberFormatException, FederateInternalError {

        // convert class handle to class name
        String className = RTIObjectCache.getInstance().getRTIByOHandle(theObject);

        // house object
        if (className.toLowerCase().equals(FedConfig.getHouseFedTag().toLowerCase()) == true) {

            // get house
            GameHouse house = houseList.get(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "House:: Old:: " + house.toString());

            updateRTIObject(theAttributes, house.getRtiObject());

            // update local variables with rti update
            house.updateObjectAttributes();

            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "House:: New:: " + house.toString());
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "House is updated");

        }

        // avatar
        else if (className.toLowerCase().equals(FedConfig.getAvatarFedTag().toLowerCase()) == true) {

            // get avatar
            GameAvatar avatar = avatarList.get(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "avatar:: Old:: " + avatar.toString());

            updateRTIObject(theAttributes, avatar.getRtiObject());

            // update local variables with rti update
            avatar.updateObjectAttributes();

            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "avatar:: New:: " + avatar.toString());
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "avatar is updated");

        }
        // player
        else if (className.toLowerCase().equals(FedConfig.getPlayerFedTag().toLowerCase()) == true) {
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "PlayerMain is updated");

            // get player
            GamePlayer player = playerList.get(theObject);
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "Player:: Old:: " + player.toString());

            updateRTIObject(theAttributes, player.getRtiObject());

            // update local variables with rti update
            player.updateObjectAttributes();

            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "Player:: New:: " + player.toString());
            FedConfig.logDebug(LOG_TAG + "ObjectUpdateCallback:: ", "Player is updated");

        } else {
            System.err.println("No found object tag");
        }

    }

    private void updateRTIObject(ReflectedAttributes theAttributes, RTIObject rtiObject) throws ArrayIndexOutOfBounds, NumberFormatException,
            FederateInternalError {
        // load updated attributes to rti object
        for (int i = 0; i < theAttributes.size(); i++) {
            int updateAttributeHandle = theAttributes.getAttributeHandle(i);
            int updateAttributeValue = Integer.parseInt(EncodingHelpers.decodeString(theAttributes.getValue(i)));
            // System.err.println(updateAttributeHandle + "::" +
            // updateAttributeValue);

            for (Iterator iterator = rtiObject.getAttributeList().iterator(); iterator.hasNext();) {
                RTIAttribute objectAttribute = (RTIAttribute) iterator.next();
                if (objectAttribute.getAttributeHandle() == updateAttributeHandle) {
                    objectAttribute.setAttributeValue(updateAttributeValue);
                    break;
                }
            }
        }
    }

    public RTIFed getFederate() {
        return federate;
    }

    public void setFederate(RTIFed federate) {
        this.federate = federate;
    }

    public ConcurrentHashMap<Integer, GameHouse> getHouseList() {
        return houseList;
    }

    public void setHouseList(ConcurrentHashMap<Integer, GameHouse> houseList) {
        this.houseList = houseList;
    }

    public ConcurrentHashMap<Integer, GamePlayer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ConcurrentHashMap<Integer, GamePlayer> playerList) {
        this.playerList = playerList;
    }

    public ConcurrentHashMap<Integer, GameAvatar> getAvatarList() {
        return avatarList;
    }

    public void setAvatarList(ConcurrentHashMap<Integer, GameAvatar> avatarList) {
        this.avatarList = avatarList;
    }

    public ConcurrentLinkedQueue<GamePlayerInteraction> getGamePlayerUpdateInteractions() {
        return gamePlayerUpdateInteractions;
    }

    public void setGamePlayerUpdateInteractions(ConcurrentLinkedQueue<GamePlayerInteraction> gamePlayerUpdateInteractions) {
        this.gamePlayerUpdateInteractions = gamePlayerUpdateInteractions;
    }

}
