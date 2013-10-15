package main;

import hla.rti.RTIexception;

import javax.swing.JFrame;

import map.MapMain;
import player.PlayerMain;
import player.PlayerView;
import rti.RTIFedPropertiesBase;
import common.FedConfig;

public class Main extends JFrame {

    public static final String LOG_TAG             = "MainFunction:: ";
    public static boolean      FEDERATE_IS_RUNNING = true;

    public static void main(String[] args) throws RTIexception, InterruptedException {
        
        // generic federation properties
        RTIFedPropertiesBase federationProperties = new RTIFedPropertiesBase(FedConfig.getFedFom(), FedConfig.getFedSync(), FedConfig.getFedName());

        // map manager runs in this node
        if (args[0].toLowerCase().equals(FedConfig.getMapManagerTag().toLowerCase()) == true)
            MapMain.Main(args, federationProperties);

        // player runs in this node
        else if (args[0].toLowerCase().equals(FedConfig.getPlayerFedTag().toLowerCase()) == true) {          
            // organize frame
            Main m = new Main();
            m.setTitle("Game::");            
            m.setSize(FedConfig.getFrameDimension().getWidth(), FedConfig.getFrameDimension().getHeight());
            m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            m.setLocationRelativeTo(null);

            // add player view to frame 
            PlayerMain playerMain = new PlayerMain(args, federationProperties, args[1]);
            PlayerView playerView = new PlayerView(playerMain, m);
            m.add(playerView);
            
            // initiate operations
            m.setVisible(true);            
            playerMain.start();
        }

        // unknown parameter
        else
            FedConfig.logDebug(LOG_TAG + "Main", "unknown parameters:: " + args.toString());

    }
}
