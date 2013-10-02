package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.FederatesCurrentlyJoined;
import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.FederationExecutionDoesNotExist;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.ReceivedInteraction;
import hla.rti.ResignAction;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactoryFactory;
import common.CharacterProperties;
import common.Coordinate;
import common.FederationProperties;
import common.GameLogger;
import common.InteractionMap;
import common.ThreadSafeProducerConsumerQueue;

public class Player extends NullFederateAmbassador {

    private CharacterProperties           characterProperties;
    private InteractionMap                rtiInteractionMap;
    private ConcurrentLinkedQueue<String> messageQueueFromPlayer;
    private ConcurrentLinkedQueue<String> messageQueueToPlayer;

    private double                        federateTime      = 0.0;
    private double                        federateLookahead = 1.0;
    private boolean                       isRegulating      = false;
    private boolean                       isConstrained     = false;
    private boolean                       isAdvancing       = false;
    private boolean                       isAnnounced       = false;
    private boolean                       isReadyToRun      = false;
    private boolean                       isRunning         = true;

    private RTIambassador                 rtiamb;
    private String                        READY_TO_RUN;
    private String                        federateName;
    private String                        federationName;
    private String                        fomFilePath;

    public Player(String federateName, String federationName, String fomfilePath, String readyToRun) {
        init(federateName, federationName, fomfilePath, readyToRun);
    }

    private void init(String federateName, String federationName, String fomfilePath, String readyToRun) {

        this.READY_TO_RUN = readyToRun;
        this.federateName = federateName;
        this.federationName = federationName;
        this.fomFilePath = fomfilePath;

        // random coordinate allocation
        Coordinate playerCoordinate = Coordinate.GenerateRandomCoordinate(FederationProperties.GMAP_DIMENSION.getHeight(),
                                                                          FederationProperties.GMAP_DIMENSION.getWidth());
        // collision is possible
        int playerId = FederationProperties.R.nextInt(FederationProperties.PLAYER_ID_UPPERBOUND);

        int initialCandyAmount = 0;

        String playerName = federateName;

        // actual player initialization
        this.characterProperties = new CharacterProperties(playerId, playerName, initialCandyAmount, FederationProperties.PLAYER_VISIBILITY_RANGE,
                                                           playerCoordinate);
        GameLogger.log(FederationProperties.PLAYER_FEDERATE_TAG, "> PlayerObjectInitialization> " + this.characterProperties.toString());

        // interaction map
        this.rtiInteractionMap = new InteractionMap();
        GameLogger.log(FederationProperties.PLAYER_FEDERATE_TAG, "> InteractionMapInitialization> " + this.rtiInteractionMap.toString());

        // message queue
        messageQueueFromPlayer = new ConcurrentLinkedQueue<>();
        messageQueueToPlayer = new ConcurrentLinkedQueue<>();
        GameLogger.log(FederationProperties.PLAYER_FEDERATE_TAG, "MessageQueues> " + this.messageQueueFromPlayer.toString() + " "
                + this.messageQueueToPlayer.toString());
    }

    public void constructFederate() throws RTIexception {
        rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        try {
            File fom = new File(this.fomFilePath);
            rtiamb.createFederationExecution(federationName, fom.toURI().toURL());
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception processing fom: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        rtiamb.joinFederationExecution(federateName, federationName, this);
        log("Joined Federation as " + federateName);

        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        while (this.isAnnounced == false) {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (this.isReadyToRun == false) {
            rtiamb.tick();
        }

        enableTimePolicy();
        log("Time Policy Enabled");

        int interactionHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Player.Init");
        System.out.println("InteractionRoot.Player.Init handle : " + interactionHandle);

        int propertiesHandle = rtiamb.getParameterHandle("query", interactionHandle);
        System.out.println("InteractionRoot.Player.Init.query handle : " + propertiesHandle);

        rtiamb.publishInteractionClass(interactionHandle);
        rtiamb.subscribeInteractionClass(interactionHandle);
        log("Published and Subscribed");
    }

    public void sendInteraction(String interactionName, String parameterName, String parameterValue) throws RTIexception {

        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        byte[] propertiesValue = EncodingHelpers.encodeString(parameterValue);

        int classHandle = rtiamb.getInteractionClassHandle(interactionName);
        int propertiesHandle = rtiamb.getParameterHandle(parameterName, classHandle);

        parameters.add(propertiesHandle, propertiesValue);

        rtiamb.sendInteraction(classHandle, parameters, generateTag());

        LogicalTime time = convertTime(this.federateTime + this.federateLookahead);
        rtiamb.sendInteraction(classHandle, parameters, generateTag(), time);

        advanceTime(1.0);
        log("Time Advanced to " + this.federateTime);
        log("Interaction is sent");
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime,
            EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        builder.append(interactionClass);
        builder.append(" ");

        for (int i = 0; i < theInteraction.size(); i++) {
            try {
                builder.append(theInteraction.getParameterHandle(i));
                builder.append(" ");

                builder.append(EncodingHelpers.decodeString(theInteraction.getValue(i)));
            } catch (ArrayIndexOutOfBounds aioob) {
            }
        }

        this.messageQueueToPlayer.add(builder.toString());
        log(builder.toString());
    }

    public void destroyFederate() throws RTIexception {
        rtiamb.resignFederationExecution(ResignAction.NO_ACTION);
        log("Resigned from Federation");

        try {
            rtiamb.destroyFederationExecution(federationName);
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    private void advanceTime(double timestep) throws RTIexception {
        this.isAdvancing = true;
        LogicalTime newTime = convertTime(this.federateTime + timestep);
        rtiamb.timeAdvanceRequest(newTime);

        while (this.isAdvancing) {
            rtiamb.tick();
        }
    }

    private LogicalTime convertTime(double time) {
        return new DoubleTime(time);
    }

    private LogicalTimeInterval convertInterval(double time) {
        return new DoubleTimeInterval(time);
    }

    private void enableTimePolicy() throws RTIexception {
        LogicalTime currentTime = convertTime(this.federateTime);
        LogicalTimeInterval lookahead = convertInterval(this.federateLookahead);

        this.rtiamb.enableTimeRegulation(currentTime, lookahead);

        while (this.isRegulating == false) {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while (this.isConstrained == false) {
            rtiamb.tick();
        }
    }

    private double getLbts() {
        return this.federateTime + this.federateLookahead;
    }

    private byte[] generateTag() {
        return ("" + System.currentTimeMillis()).getBytes();
    }

    public void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double convertTime(LogicalTime logicalTime) {
        return ((DoubleTime) logicalTime).getTime();
    }

    public void log(String message) {
        GameLogger.log("FederateAmbassador", message);
    }

    public void synchronizationPointRegistrationFailed(String label) {
        log("Failed to register sync point: " + label);
    }

    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(this.READY_TO_RUN))
            this.isAnnounced = true;
    }

    public void federationSynchronized(String label) {
        log("Federation Synchronized: " + label);
        if (label.equals(this.READY_TO_RUN))
            this.isReadyToRun = true;
    }

    public void timeRegulationEnabled(LogicalTime theFederateTime) {
        this.federateTime = convertTime(theFederateTime);
        this.isRegulating = true;
    }

    public void timeConstrainedEnabled(LogicalTime theFederateTime) {
        this.federateTime = convertTime(theFederateTime);
        this.isConstrained = true;
    }

    public void timeAdvanceGrant(LogicalTime theTime) {
        this.federateTime = convertTime(theTime);
        this.isAdvancing = false;
    }

    public CharacterProperties getCharacterProperties() {
        return characterProperties;
    }

    public void setCharacterProperties(CharacterProperties characterProperties) {
        this.characterProperties = characterProperties;
    }

    public InteractionMap getRtiInteractionMap() {
        return rtiInteractionMap;
    }

    public void setRtiInteractionMap(InteractionMap rtiInteractionMap) {
        this.rtiInteractionMap = rtiInteractionMap;
    }

    public ConcurrentLinkedQueue<String> getMessageQueueFromPlayer() {
        return messageQueueFromPlayer;
    }

    public void setMessageQueueFromPlayer(ConcurrentLinkedQueue<String> messageQueueFromPlayer) {
        this.messageQueueFromPlayer = messageQueueFromPlayer;
    }

    public ConcurrentLinkedQueue<String> getMessageQueueToPlayer() {
        return messageQueueToPlayer;
    }

    public void setMessageQueueToPlayer(ConcurrentLinkedQueue<String> messageQueueToPlayer) {
        this.messageQueueToPlayer = messageQueueToPlayer;
    }

    public double getFederateTime() {
        return federateTime;
    }

    public void setFederateTime(double federateTime) {
        this.federateTime = federateTime;
    }

    public double getFederateLookahead() {
        return federateLookahead;
    }

    public void setFederateLookahead(double federateLookahead) {
        this.federateLookahead = federateLookahead;
    }

    public boolean isRegulating() {
        return isRegulating;
    }

    public void setRegulating(boolean isRegulating) {
        this.isRegulating = isRegulating;
    }

    public boolean isConstrained() {
        return isConstrained;
    }

    public void setConstrained(boolean isConstrained) {
        this.isConstrained = isConstrained;
    }

    public boolean isAdvancing() {
        return isAdvancing;
    }

    public void setAdvancing(boolean isAdvancing) {
        this.isAdvancing = isAdvancing;
    }

    public boolean isAnnounced() {
        return isAnnounced;
    }

    public void setAnnounced(boolean isAnnounced) {
        this.isAnnounced = isAnnounced;
    }

    public boolean isReadyToRun() {
        return isReadyToRun;
    }

    public void setReadyToRun(boolean isReadyToRun) {
        this.isReadyToRun = isReadyToRun;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}

