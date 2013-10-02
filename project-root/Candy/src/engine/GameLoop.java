package engine;

import java.util.ArrayList;
import java.util.List;

import hla.rti.RTIexception;
import hla.rti.RTIinternalError;
import common.FederationProperties;
import common.GenericFederate;
import common.GenericFederateAmbassador;
import player.PlayerFederate;
import player.PlayerFederateAmbassador;

public class GameLoop extends Thread {
    public static void main(String[] args) throws RTIexception, InterruptedException {

        GameLoop.currentThread().run();
        
        if (args[0].equals("player")) {

            List<String> publishList = new ArrayList<String>();
            publishList.add("InteractionRoot.Player.Message");

            List<String> subscribeList = new ArrayList<String>();
            subscribeList.add("InteractionRoot.Map.Message");
            GenericFederateAmbassador fedamb = new GenericFederateAmbassador();

            GenericFederate player = new GenericFederate("Player", FederationProperties.FEDERATION_NAME, FederationProperties.FED_FOM_FILE_PATH,
                                                         FederationProperties.READY_TO_RUN, fedamb, publishList, subscribeList);

            player.runFederate();
            for (;;) {
                player.sendInteraction("InteractionRoot.Player.Message", "move:1,2&cupdate:45");
                Thread.sleep(400);
            }

        } else if (args[0].equals("map")) {

            List<String> publishList = new ArrayList<String>();
            publishList.add("InteractionRoot.Map.Message");

            List<String> subscribeList = new ArrayList<String>();
            subscribeList.add("InteractionRoot.Player.Message");

            GenericFederateAmbassador fedamb = new GenericFederateAmbassador();

            GenericFederate map = new GenericFederate("Map", FederationProperties.FEDERATION_NAME, FederationProperties.FED_FOM_FILE_PATH,
                                                      FederationProperties.READY_TO_RUN, fedamb, publishList, subscribeList);

            map.runFederate();
            for (;;) {
                Thread.sleep(200);
            }
        }

    }
}
