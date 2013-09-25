package sample1;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

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

public class Example13Federate
{

    public static final int ITERATIONS = 20;
    public static final String READY_TO_RUN = "ReadyToRun";
    private RTIambassador rtiamb;
    private Example13FederateAmbassador fedamb;

    private void log(String message)
    {
        System.out.println("ExampleFederate   : " + message);
    }

    private void waitForUser()
    {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            reader.readLine();
        }
        catch (Exception e)
        {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private LogicalTime convertTime(double time)
    {
        return new DoubleTime(time);
    }

    private LogicalTimeInterval convertInterval(double time)
    {
        return new DoubleTimeInterval(time);
    }

    public void runFederate(String federateName, String federationName, String fomfilePath) throws RTIexception
    {
        rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        try
        {
            File fom = new File(fomfilePath);
            rtiamb.createFederationExecution(federationName,
                                             fom.toURI().toURL());
            log("Created Federation");
        }
        catch (FederationExecutionAlreadyExists exists)
        {
            log("Didn't create federation, it already existed");
        }
        catch (MalformedURLException urle)
        {
            log("Exception processing fom: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        fedamb = new Example13FederateAmbassador();
        rtiamb.joinFederationExecution(federateName, federationName, fedamb);
        log("Joined Federation as " + federateName);

        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        while (fedamb.isAnnounced == false)
        {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false)
        {
            rtiamb.tick();
        }

        enableTimePolicy();
        log("Time Policy Enabled");

        publishAndSubscribe();
        log("Published and Subscribed");

        int objectHandle = registerObject();
        log("Registered Object, handle=" + objectHandle);

        for (int i = 0; i < ITERATIONS; i++)
        {
            updateAttributeValues(objectHandle);

            sendInteraction();

            advanceTime(1.0);
            log("Time Advanced to " + fedamb.federateTime);
        }

        deleteObject(objectHandle);
        log("Deleted Object, handle=" + objectHandle);

        rtiamb.resignFederationExecution(ResignAction.NO_ACTION);
        log("Resigned from Federation");

        try
        {
            rtiamb.destroyFederationExecution(federationName);
            log("Destroyed Federation");
        }
        catch (FederationExecutionDoesNotExist dne)
        {
            log("No need to destroy federation, it doesn't exist");
        }
        catch (FederatesCurrentlyJoined fcj)
        {
            log("Didn't destroy federation, federates still joined");
        }
    }

    private void enableTimePolicy() throws RTIexception
    {
        LogicalTime currentTime = convertTime(fedamb.federateTime);
        LogicalTimeInterval lookahead = convertInterval(fedamb.federateLookahead);

        this.rtiamb.enableTimeRegulation(currentTime, lookahead);

        while (fedamb.isRegulating == false)
        {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while (fedamb.isConstrained == false)
        {
            rtiamb.tick();
        }
    }

    private void publishAndSubscribe() throws RTIexception
    {

        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.A");
        int aaHandle = rtiamb.getAttributeHandle("aa", classHandle);
        int abHandle = rtiamb.getAttributeHandle("ab", classHandle);
        int acHandle = rtiamb.getAttributeHandle("ac", classHandle);

        AttributeHandleSet attributes =
                           RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add(aaHandle);
        attributes.add(abHandle);
        attributes.add(acHandle);

        rtiamb.publishObjectClass(classHandle, attributes);


        rtiamb.subscribeObjectClassAttributes(classHandle, attributes);

        int interactionHandle = rtiamb.getInteractionClassHandle("InteractionRoot.X");

        rtiamb.publishInteractionClass(interactionHandle);

        rtiamb.subscribeInteractionClass(interactionHandle);
    }

    private int registerObject() throws RTIexception
    {
        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.A");
        return rtiamb.registerObjectInstance(classHandle);
    }

    private void updateAttributeValues(int objectHandle) throws RTIexception
    {
        SuppliedAttributes attributes =
                           RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();

        byte[] aaValue = EncodingHelpers.encodeString("aa:" + getLbts());
        byte[] abValue = EncodingHelpers.encodeString("ab:" + getLbts());
        byte[] acValue = EncodingHelpers.encodeString("ac:" + getLbts());

        int classHandle = rtiamb.getObjectClass(objectHandle);
        int aaHandle = rtiamb.getAttributeHandle("aa", classHandle);
        int abHandle = rtiamb.getAttributeHandle("ab", classHandle);
        int acHandle = rtiamb.getAttributeHandle("ac", classHandle);

        attributes.add(aaHandle, aaValue);
        attributes.add(abHandle, abValue);
        attributes.add(acHandle, acValue);

        rtiamb.updateAttributeValues(objectHandle, attributes, generateTag());

        LogicalTime time = convertTime(fedamb.federateTime + fedamb.federateLookahead);
        rtiamb.updateAttributeValues(objectHandle, attributes, generateTag(), time);
    }

    private void sendInteraction() throws RTIexception
    {
        SuppliedParameters parameters =
                           RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] xaValue = EncodingHelpers.encodeString("xa:" + getLbts());
        byte[] xbValue = EncodingHelpers.encodeString("xb:" + getLbts());

        int classHandle = rtiamb.getInteractionClassHandle("InteractionRoot.X");
        int xaHandle = rtiamb.getParameterHandle("xa", classHandle);
        int xbHandle = rtiamb.getParameterHandle("xb", classHandle);

        parameters.add(xaHandle, xaValue);
        parameters.add(xbHandle, xbValue);

        rtiamb.sendInteraction(classHandle, parameters, generateTag());

        LogicalTime time = convertTime(fedamb.federateTime
                                       + fedamb.federateLookahead);
        rtiamb.sendInteraction(classHandle, parameters, generateTag(), time);
    }

    private void advanceTime(double timestep) throws RTIexception
    {
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(newTime);

        while (fedamb.isAdvancing)
        {
            rtiamb.tick();
        }
    }

    private void deleteObject(int handle) throws RTIexception
    {
        rtiamb.deleteObjectInstance(handle, generateTag());
    }

    private double getLbts()
    {
        return fedamb.federateTime + fedamb.federateLookahead;
    }

    private byte[] generateTag()
    {
        return ("" + System.currentTimeMillis()).getBytes();
    }
}
