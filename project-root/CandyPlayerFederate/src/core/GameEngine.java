package core;

import hla.rti.RTIexception;
import common.FederationProperties;

public class GameEngine {

    public static void main(String[] args) throws RTIexception {
        Player player;
        boolean isSender = args.length > 0 && args[0].equals("sender") ? true : false;
        if (isSender == true) {
            player = new Player("MuratSender", FederationProperties.FEDERATION_NAME, FederationProperties.FED_FOM_FILE_PATH,
                                FederationProperties.READY_TO_RUN);
        } else {
            player = new Player("MuratReceiver", FederationProperties.FEDERATION_NAME, FederationProperties.FED_FOM_FILE_PATH,
                                FederationProperties.READY_TO_RUN);
        }

        player.constructFederate();

        if (isSender == true)
            player.sendInteraction("InteractionRoot.Player.Init", "query", "name=zhimin&position=123;313");
        else
            player.sendInteraction("InteractionRoot.Player.Init", "query", "name=murat&position=134,455");

        player.waitForUser();
    }
}
