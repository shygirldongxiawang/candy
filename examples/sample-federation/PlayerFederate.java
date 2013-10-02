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

/**
 * This is an example federate demonstrating how to properly use the HLA 1.3 Java
 * interface supplied with Portico.
 * 
 * As it is intended for example purposes, this is a rather simple federate. The
 * process is goes through is as follows:
 * 
 *  1. Create the RTIambassador
 *  2. Try to create the federation (nofail)
 *  3. Join the federation
 *  4. Announce a Synchronization Point (nofail)
 *  5. Wait for the federation to Synchronized on the point
 *  6. Enable Time Regulation and Constrained
 *  7. Publish and Subscribe
 *  8. Register an Object Instance
 *  9. Main Simulation Loop (executes 20 times)
 *       9.1 Update attributes of registered object
 *       9.2 Send an Interaction
 *       9.3 Advance time by 1.0
 * 10. Delete the Object Instance
 * 11. Resign from Federation
 * 12. Try to destroy the federation (nofail)
 * 
 * NOTE: Those items marked with (nofail) deal with situations where multiple
 *       federates may be working in the federation. In this sitaution, the
 *       federate will attempt to carry out the tasks defined, but it won't
 *       stop or exit if they fail. For example, if another federate has already
 *       created the federation, the call to create it again will result in an
 *       exception. The example federate expects this and will not fail.
 * NOTE: Between actions 4. and 5., the federate will pause until the uses presses
 *       the enter key. This will give other federates a chance to enter the
 *       federation and prevent other federates from racing ahead.
 * 
 * 
 * The main method to take notice of is {@link #runFederate(String)}. It controls the
 * main simulation loop and triggers most of the important behaviour. To make the code
 * simpler to read and navigate, many of the important HLA activities are broken down
 * into separate methods. For example, if you want to know how to send an interaction,
 * see the {@link #sendInteraction()} method.
 * 
 * With regard to the FederateAmbassador, it will log all incoming information. Thus,
 * if it receives any reflects or interactions etc... you will be notified of them.
 * 
 * Note that all of the methods throw an RTIexception. This class is the parent of all
 * HLA exceptions. The HLA Java interface is full of exceptions, with only a handful 
 * being actually useful. To make matters worse, they're all checked exceptions, so
 * unlike C++, we are forced to handle them by the compiler. This is unnecessary in
 * this small example, so we'll just throw all exceptions out to the main method and
 * handle them there, rather than handling each exception independently as they arise.
 */
public class Example13Federate
{
	/** The number of times we will update our attributes and send an interaction */
	public static final int ITERATIONS = 20;

	/** The sync point all federates will sync up on before starting */
	public static final String READY_TO_RUN = "ReadyToRun";

	private RTIambassador rtiamb;
	private Example13FederateAmbassador fedamb;


	/**
	 * This is just a helper method to make sure all logging it output in the same form
	 */
	private void log( String message )
	{
		System.out.println( "ExampleFederate   : " + message );
	}

	/**
	 * This method will block until the user presses enter
	 */
	private void waitForUser()
	{
		log( " >>>>>>>>>> Press Enter to Continue <<<<<<<<<<" );
		BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
		try
		{
			reader.readLine();
		}
		catch( Exception e )
		{
			log( "Error while waiting for user input: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * As all time-related code is Portico-specific, we have isolated it into a 
	 * single method. This way, if you need to move to a different RTI, you only need
	 * to change this code, rather than more code throughout the whole class.
	 */
	private LogicalTime convertTime( double time )
	{
		return new DoubleTime( time );
	}
	
	/**
	 * Same as for {@link #convertTime(double)}
	 */
	private LogicalTimeInterval convertInterval( double time )
	{
		return new DoubleTimeInterval( time );
	}


	/**
	 * This is the main simulation loop. It can be thought of as the main method of
	 * the federate. For a description of the basic flow of this federate, see the
	 * class level comments
	 */
    public void runFederate(String federateName, String federationName, String fomfilePath) throws RTIexception
	{
		rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

		try
		{
            File fom = new File(fomfilePath);
			rtiamb.createFederationExecution( federationName,
			                                  fom.toURI().toURL() );
			log( "Created Federation" );
		}
		catch( FederationExecutionAlreadyExists exists )
		{
			log( "Didn't create federation, it already existed" );
		}
		catch( MalformedURLException urle )
		{
			log( "Exception processing fom: " + urle.getMessage() );
			urle.printStackTrace();
			return;
		}
		
		fedamb = new Example13FederateAmbassador();
		rtiamb.joinFederationExecution( federateName, federationName, fedamb );
		log( "Joined Federation as " + federateName );

		rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );
		while( fedamb.isAnnounced == false )
		{
			rtiamb.tick();
		}

		waitForUser();

		rtiamb.synchronizationPointAchieved( READY_TO_RUN );
		log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
		while( fedamb.isReadyToRun == false )
		{
			rtiamb.tick();
		}

		enableTimePolicy();
		log( "Time Policy Enabled" );

		publishAndSubscribe();
		log( "Published and Subscribed" );

		int objectHandle = registerObject();
		log( "Registered Object, handle=" + objectHandle );
		
		for( int i = 0; i < ITERATIONS; i++ )
		{
			updateAttributeValues( objectHandle );
			
			sendInteraction();
			
			advanceTime( 1.0 );
			log( "Time Advanced to " + fedamb.federateTime );
		}

		deleteObject( objectHandle );
		log( "Deleted Object, handle=" + objectHandle );

		rtiamb.resignFederationExecution( ResignAction.NO_ACTION );
		log( "Resigned from Federation" );

		try
		{
			rtiamb.destroyFederationExecution( federationName );
			log( "Destroyed Federation" );
		}
		catch( FederationExecutionDoesNotExist dne )
		{
			log( "No need to destroy federation, it doesn't exist" );
		}
		catch( FederatesCurrentlyJoined fcj )
		{
			log( "Didn't destroy federation, federates still joined" );
		}
	}
	
	/**
	 * This method will attempt to enable the various time related properties for
	 * the federate
	 */
	private void enableTimePolicy() throws RTIexception
	{
		LogicalTime currentTime = convertTime( fedamb.federateTime );
		LogicalTimeInterval lookahead = convertInterval( fedamb.federateLookahead );
		
		this.rtiamb.enableTimeRegulation( currentTime, lookahead );

		while( fedamb.isRegulating == false )
		{
			rtiamb.tick();
		}
		
		this.rtiamb.enableTimeConstrained();
		
		while( fedamb.isConstrained == false )
		{
			rtiamb.tick();
		}
	}
	
	/**
	 * This method will inform the RTI about the types of data that the federate will
	 * be creating, and the types of data we are interested in hearing about as other
	 * federates produce it.
	 */
	private void publishAndSubscribe() throws RTIexception
	{

		int classHandle = rtiamb.getObjectClassHandle( "ObjectRoot.A" );
		int aaHandle    = rtiamb.getAttributeHandle( "aa", classHandle );
		int abHandle    = rtiamb.getAttributeHandle( "ab", classHandle );
		int acHandle    = rtiamb.getAttributeHandle( "ac", classHandle );

		AttributeHandleSet attributes =
			RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
		attributes.add( aaHandle );
		attributes.add( abHandle );
		attributes.add( acHandle );
		
		rtiamb.publishObjectClass( classHandle, attributes );

		
		rtiamb.subscribeObjectClassAttributes( classHandle, attributes );

		int interactionHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.X" );
		
		rtiamb.publishInteractionClass( interactionHandle );

		rtiamb.subscribeInteractionClass( interactionHandle );
	}
	
	/**
	 * This method will register an instance of the class ObjectRoot.A and will
	 * return the federation-wide unique handle for that instance. Later in the
	 * simulation, we will update the attribute values for this instance
	 */
	private int registerObject() throws RTIexception
	{
		int classHandle = rtiamb.getObjectClassHandle( "ObjectRoot.A" );
		return rtiamb.registerObjectInstance( classHandle );
	}
	
	/**
	 * This method will update all the values of the given object instance. It will
	 * set each of the values to be a string which is equal to the name of the
	 * attribute plus the current time. eg "aa:10.0" if the time is 10.0.
	 * <p/>
	 * Note that we don't actually have to update all the attributes at once, we
	 * could update them individually, in groups or not at all!
	 */
	private void updateAttributeValues( int objectHandle ) throws RTIexception
	{
		SuppliedAttributes attributes =
			RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
		
		byte[] aaValue = EncodingHelpers.encodeString( "aa:" + getLbts() );
		byte[] abValue = EncodingHelpers.encodeString( "ab:" + getLbts() );
		byte[] acValue = EncodingHelpers.encodeString( "ac:" + getLbts() );
		
		int classHandle = rtiamb.getObjectClass( objectHandle );
		int aaHandle = rtiamb.getAttributeHandle( "aa", classHandle );
		int abHandle = rtiamb.getAttributeHandle( "ab", classHandle );
		int acHandle = rtiamb.getAttributeHandle( "ac", classHandle );

		attributes.add( aaHandle, aaValue );
		attributes.add( abHandle, abValue );
		attributes.add( acHandle, acValue );

		rtiamb.updateAttributeValues( objectHandle,attributes, generateTag() );
		
		LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
		rtiamb.updateAttributeValues( objectHandle, attributes, generateTag(), time );
	}
	
	/**
	 * This method will send out an interaction of the type InteractionRoot.X. Any
	 * federates which are subscribed to it will receive a notification the next time
	 * they tick(). Here we are passing only two of the three parameters we could be
	 * passing, but we don't actually have to pass any at all!
	 */
	private void sendInteraction() throws RTIexception
	{
		SuppliedParameters parameters =
			RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
		
		byte[] xaValue = EncodingHelpers.encodeString( "xa:" + getLbts() );
		byte[] xbValue = EncodingHelpers.encodeString( "xb:" + getLbts() );
		
		int classHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.X" );
		int xaHandle = rtiamb.getParameterHandle( "xa", classHandle );
		int xbHandle = rtiamb.getParameterHandle( "xb", classHandle );

		parameters.add( xaHandle, xaValue );
		parameters.add( xbHandle, xbValue );

		rtiamb.sendInteraction( classHandle, parameters, generateTag() );
		
		LogicalTime time = convertTime( fedamb.federateTime +
		                                fedamb.federateLookahead );
		rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
	}

	/**
	 * This method will request a time advance to the current time, plus the given
	 * timestep. It will then wait until a notification of the time advance grant
	 * has been received.
	 */
	private void advanceTime( double timestep ) throws RTIexception
	{
		fedamb.isAdvancing = true;
		LogicalTime newTime = convertTime( fedamb.federateTime + timestep );
		rtiamb.timeAdvanceRequest( newTime );
		
		while( fedamb.isAdvancing )
		{
			rtiamb.tick();
		}
	}

	/**
	 * This method will attempt to delete the object instance of the given
	 * handle. We can only delete objects we created, or for which we own the
	 * privilegeToDelete attribute.
	 */
	private void deleteObject( int handle ) throws RTIexception
	{
		rtiamb.deleteObjectInstance( handle, generateTag() );
	}

	private double getLbts()
	{
		return fedamb.federateTime + fedamb.federateLookahead;
	}

	private byte[] generateTag()
	{
		return (""+System.currentTimeMillis()).getBytes();
	}

}
