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

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.NullFederateAmbassador;

import org.portico.impl.hla13.types.DoubleTime;

import common.FederationProperties;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link Example13Federate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback
 * information.
 */
public class PlayerFederateAmbassador extends NullFederateAmbassador {

    public double  federateTime          = 0.0;
    public double  federateLookahead     = 1.0;

    public boolean isRegulating          = false;
    public boolean isConstrained         = false;
    public boolean isAdvancing           = false;

    public boolean isAnnounced           = false;
    public boolean isReadyToRun          = false;

    public boolean isInteractionReceived = false;
    private String    interaction           = "no-interaction-yet";

    public PlayerFederateAmbassador() {
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime,
            EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        builder.append(" handle=" + interactionClass);
        builder.append(", tag=" + EncodingHelpers.decodeString(tag));
        this.interaction += "" + interactionClass + ":";

        builder.append(", parameterCount=" + theInteraction.size());
        builder.append("\n");
        for (int i = 0; i < theInteraction.size(); i++) {
            try {
                builder.append("\tparamHandle=");
                builder.append(theInteraction.getParameterHandle(i));
                builder.append(", paramValue=");
                builder.append(EncodingHelpers.decodeString(theInteraction.getValue(i)));
                builder.append("\n");

                this.interaction += theInteraction.getParameterHandle(i) + "=" + theInteraction.getValue(i) + ",";
            } catch (ArrayIndexOutOfBounds aioob) {
            }
        }
        this.isInteractionReceived = true;

    }

    public String getInteraction() {
        this.isInteractionReceived = false;
        return this.interaction;
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(FederationProperties.READY_TO_RUN))
            this.isAnnounced = true;
    }

    public void federationSynchronized(String label) {
        log("Federation Synchronized: " + label);
        if (label.equals(FederationProperties.READY_TO_RUN))
            this.isReadyToRun = true;
    }

}
