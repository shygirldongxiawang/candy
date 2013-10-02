package common;

import java.util.Random;

public class FederationProperties {
	
	// Federation initialization properties
	public final static String FEDERATION_NAME = "FederationName";
	public final static String READY_TO_RUN = "ReadyToRun";
	public final static String FED_FOM_FILE_PATH = "d:/workspace/candy/examples/shared/testfom.fed";

	// Game properties
	public final static Dimension GMAP_DIMENSION = new Dimension(1500, 2000);
	
	
	// Communication properties
	public final static int MESSAGE_QUEUE_CAPACITY = 100;
	
	// Player properties
	public final static int PLAYER_ID_UPPERBOUND = 10;
	public final static int PLAYER_VISIBILITY_RANGE = 20;
	public final static String PLAYER_FEDERATE_TAG = "PLAYER";
	
	// Utility
	public final static Random R = new Random();
}
