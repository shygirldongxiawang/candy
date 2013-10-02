/*
 *   Copyright 2007 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package player;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hla.rti.AttributeHandleSet;
import hla.rti.FederatesCurrentlyJoined;
import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.FederationExecutionDoesNotExist;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.RTIinternalError;
import hla.rti.ResignAction;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;

import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;


public class PlayerFederate {
    /**
     * The number of times we will update our attributes and send an interaction
     */
    public static final int          ITERATIONS   = 20;

    /** The sync point all federates will sync up on before starting */
    public static String             READY_TO_RUN = "ReadyToRun";

    private RTIambassador            rtiamb;
    private PlayerFederateAmbassador fedamb;
    private String                   federateName;
    private String                   federationName;
    private String                   fomfilePath;
    private HashMap<Integer, String> interactionHandleMap;
    List<String>                     publishList;
    List<String>                     subscribeList;

    public PlayerFederate(String federateName, String federationName, String fomFilePath, String readyToRun, PlayerFederateAmbassador fedamb,
            List<String> publishList, List<String> subscribeList) throws RTIinternalError {
        this.fedamb = fedamb;
        this.rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
        this.federateName = federateName;
        this.federationName = federationName;
        this.fomfilePath = fomFilePath;
        this.interactionHandleMap = new HashMap<>();
        this.publishList = publishList;
        this.subscribeList = subscribeList;
        PlayerFederate.READY_TO_RUN = readyToRun;
    }

    /**
     * This is the main simulation loop. It can be thought of as the main method
     * of the federate. For a description of the basic flow of this federate,
     * see the class level comments
     */
    public void runFederate() throws RTIexception {
        try {
            File fom = new File(fomfilePath);
            rtiamb.createFederationExecution(federationName, fom.toURI().toURL());
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception processing fom: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        rtiamb.joinFederationExecution(federateName, federationName, fedamb);
        log("Joined Federation as " + federateName);

        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        while (fedamb.isAnnounced == false) {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            rtiamb.tick();
        }

        publishAndSubscribe(this.publishList, this.subscribeList);
        log("Published and Subscribed");
    }

    private void publishAndSubscribe(List<String> publishList, List<String> subscribeList) throws RTIexception {

        for (String interaction : publishList) {
            int interactionHandle = rtiamb.getInteractionClassHandle(interaction);
            rtiamb.publishInteractionClass(interactionHandle);
            this.interactionHandleMap.put(interactionHandle, interaction);
        }

        for (String interaction : subscribeList) {
            int interactionHandle = rtiamb.getInteractionClassHandle(interaction);
            rtiamb.subscribeInteractionClass(interactionHandle);
            this.interactionHandleMap.put(interactionHandle, interaction);
        }

    }

    public void sendInteraction(String interactionName, String value) throws RTIexception {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] xaValue = EncodingHelpers.encodeString(value);

        int classHandle = rtiamb.getInteractionClassHandle(interactionName);
        int xaHandle = rtiamb.getParameterHandle("query", classHandle);
        parameters.add(xaHandle, xaValue);

        rtiamb.sendInteraction(classHandle, parameters, ("" + System.currentTimeMillis()).getBytes());
        log("interaction is sent");
    }

    private void log(String message) {
        System.out.println("ExampleFederate   : " + message);
    }

    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
