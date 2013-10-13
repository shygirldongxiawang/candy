package mapmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import hla.rti.ResignAction;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;

import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import common.FedConfig;

public class RTIFed {
    private RTIambassador rtiamb;
    private RTIFedAmb     fedamb;

    public void initialize(RTIFedProperties properties, RTIFedAmbHelper helper) throws RTIexception {
        rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        try {
            File fom = new File(properties.getRtiBaseProperties().getFomFilePath());
            rtiamb.createFederationExecution(properties.getRtiBaseProperties().getFederationName(), fom.toURI().toURL());
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception processing fom: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        fedamb = new RTIFedAmb(helper);
        rtiamb.joinFederationExecution(properties.getFederateName(), properties.getRtiBaseProperties().getFederationName(), fedamb);
        log("Joined Federation as " + properties.getFederateName());

        rtiamb.registerFederationSynchronizationPoint(properties.getRtiBaseProperties().getFedSyncPoint(), null);
        while (fedamb.isAnnounced == false) {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(properties.getRtiBaseProperties().getFedSyncPoint());
        log("Achieved sync point: " + properties.getRtiBaseProperties().getFedSyncPoint() + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            rtiamb.tick();
        }

        enableTimePolicy();
        log("Time Policy Enabled");
    }

    public void publish(RTIObject rtiObject) throws RTIexception {

        // get class handle
        int classHandle = rtiamb.getObjectClassHandle(rtiObject.getObjectName());
        rtiObject.setClassHandle(classHandle);

        // get attribute handle
        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        for (Iterator<RTIAttribute> iterator = rtiObject.getAttributeList().iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();
            int attributeHandle = rtiamb.getAttributeHandle(rtiAttribute.getAttributeName(), classHandle);
            rtiAttribute.setAttributeHandle(attributeHandle);
            attributes.add(attributeHandle);
        }
        rtiamb.publishObjectClass(classHandle, attributes);
        FedConfig.logDebug("RTIFederate:: " + "Publish:: ", rtiObject.toString());
    }

    public void subscribe(RTIObject rtiObject) throws RTIexception {

        // get class handle
        int classHandle = rtiamb.getObjectClassHandle(rtiObject.getObjectName());
        rtiObject.setClassHandle(classHandle);

        // get attributes handle
        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        for (Iterator<RTIAttribute> iterator = rtiObject.getAttributeList().iterator(); iterator.hasNext();) {
            RTIAttribute rtiAttribute = (RTIAttribute) iterator.next();
            int attributeHandle = rtiamb.getAttributeHandle(rtiAttribute.getAttributeName(), classHandle);
            rtiAttribute.setAttributeHandle(attributeHandle);
            attributes.add(attributeHandle);
        }
        rtiamb.subscribeObjectClassAttributes(classHandle, attributes);
        FedConfig.logDebug("RTIFederate:: " + "Subscribe:: ", rtiObject.toString());
    }

    public void registerObject(RTIObject rtiObject) throws RTIexception {

        int objectHandle = rtiamb.registerObjectInstance(rtiObject.getClassHandle());
        rtiObject.setObjectHandle(objectHandle);
        FedConfig.logDebug("RTIFederate:: " + "Register:: ", rtiObject.toString());
    }

    public void updateAttributeValues(ArrayList<RTIObject> objects) throws RTIexception {

        for (Iterator<RTIObject> iterator = objects.iterator(); iterator.hasNext();) {
            RTIObject rtiObject = (RTIObject) iterator.next();
            SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();

            for (Iterator<RTIAttribute> iterator2 = rtiObject.getAttributeList().iterator(); iterator2.hasNext();) {
                RTIAttribute rtiAttribute = (RTIAttribute) iterator2.next();
                attributes.add(rtiAttribute.getAttributeHandle(), EncodingHelpers.encodeString("" + rtiAttribute.getAttributeValue()));
            }

            rtiamb.updateAttributeValues(rtiObject.getObjectHandle(), attributes, generateTag());
            LogicalTime time = convertTime(fedamb.federateTime + fedamb.federateLookahead);
            rtiamb.updateAttributeValues(rtiObject.getObjectHandle(), attributes, generateTag(), time);

        }
    }

    public void advanceTime(double timestep) throws RTIexception {
        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(newTime);

        while (fedamb.isAdvancing) {
            rtiamb.tick();
        }
    }

    public void deleteObject(RTIObject rtiObject) throws RTIexception {
        rtiamb.deleteObjectInstance(rtiObject.getObjectHandle(), generateTag());
        FedConfig.logDebug("RTIFederate:: " + "Delete:: ", rtiObject.toString());
        rtiObject.setObjectHandle(0);
    }

    public void destroy(RTIFedProperties properties) throws RTIexception {
        rtiamb.resignFederationExecution(ResignAction.NO_ACTION);
        log("Resigned from Federation");
        try {
            rtiamb.destroyFederationExecution(properties.getRtiBaseProperties().getFederationName());
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");

        }
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * ===================================================================
     * Federation construction phase's callbacks
     * ===================================================================
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    private void enableTimePolicy() throws RTIexception {
        LogicalTime currentTime = convertTime(fedamb.federateTime);
        LogicalTimeInterval lookahead = convertInterval(fedamb.federateLookahead);

        this.rtiamb.enableTimeRegulation(currentTime, lookahead);

        while (fedamb.isRegulating == false) {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while (fedamb.isConstrained == false) {
            rtiamb.tick();
        }
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

    private LogicalTime convertTime(double time) {
        return new DoubleTime(time);
    }

    private LogicalTimeInterval convertInterval(double time) {
        return new DoubleTimeInterval(time);
    }

    private double getLbts() {
        return fedamb.federateTime + fedamb.federateLookahead;
    }

    private byte[] generateTag() {
        return ("" + System.currentTimeMillis()).getBytes();
    }

    /*
     * registerObject(objectCreationList);
     * 
     * for (int i = 0; i < 2000; i++) { updateAttributeValues(objectUpdateList);
     * 
     * advanceTime(1.0); log("Time Advanced to " + fedamb.federateTime); }
     * 
     * deleteObject(objectCreationList);
     * 
     * rtiamb.resignFederationExecution(ResignAction.NO_ACTION);
     * log("Resigned from Federation");
     * 
     * try {
     * rtiamb.destroyFederationExecution(properties.getRtiBaseProperties().
     * getFederationName()); log("Destroyed Federation"); } catch
     * (FederationExecutionDoesNotExist dne) {
     * log("No need to destroy federation, it doesn't exist"); } catch
     * (FederatesCurrentlyJoined fcj) {
     * log("Didn't destroy federation, federates still joined"); } }
     */
}
