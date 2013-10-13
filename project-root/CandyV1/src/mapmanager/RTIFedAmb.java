package mapmanager;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.NullFederateAmbassador;

import org.portico.impl.hla13.types.DoubleTime;

import common.FedConfig;

public class RTIFedAmb extends NullFederateAmbassador {
    protected double              federateTime      = 0.0;
    protected double              federateLookahead = 1.0;
    protected boolean             isRegulating      = false;
    protected boolean             isConstrained     = false;
    protected boolean             isAdvancing       = false;
    protected boolean             isAnnounced       = false;
    protected boolean             isReadyToRun      = false;
    protected RTIFedAmbHelper helper;

    public RTIFedAmb(RTIFedAmbHelper helper) {
        this.helper = helper;

    }

    // object created
    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) {
        log("Discovered Object: handle=" + theObject + ", classHandle=" + theObjectClass + ", name=" + objectName);
        helper.objectCreated(theObject, theObjectClass, objectName);
    }

    // object updated
    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag) {
        reflectAttributeValues(theObject, theAttributes, tag, null, null);
    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime,
            EventRetractionHandle retractionHandle) {
        helper.objectUpdated(theObject, theAttributes, tag, theTime, retractionHandle);
    }


    /**
     * ===================================================================
     * Federation construction phase's callbacks
     * ===================================================================
     */
    public void synchronizationPointRegistrationFailed(String label) {
        log("Failed to register sync point: " + label);
    }

    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(FedConfig.getFedSync()))
            this.isAnnounced = true;
    }

    public void federationSynchronized(String label) {
        log("Federation Synchronized: " + label);
        if (label.equals(FedConfig.getFedSync()))
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

    private double convertTime(LogicalTime logicalTime) {
        // PORTICO SPECIFIC!!
        return ((DoubleTime) logicalTime).getTime();
    }

    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

}
