package common;

import hla.rti.LogicalTime;

import org.portico.impl.hla13.types.DoubleTime;

import java.awt.Color;
import java.util.Random;
import java.util.logging.Logger;

public class FedConfig {

    // Federation initialization properties
    private final static String     FED_NAME                    = "FederationName";
    private final static String     FED_SYNC                    = "ReadyToRun";
    private final static String     FED_FOM                     = "d:/workspace/shared-file-location/candy-game-map.fed";
    private final static String     FED_OBJ_ROOT_TAG            = "ObjectRoot.";

    // View properties
    private final static CDimension FRAME_DIMENSION             = new CDimension(800, 800);

    // Map properties
    private final static CDimension MAP_DIMENSION               = new CDimension(700, 700);
    private final static int        MAP_AVATAR_NUM              = 5;
    private final static int        MAP_HOUSE_NUM               = 100;

    // PlayerMain properties
    private final static String     PLAYER_FED_TAG              = "ObjectRoot.Player";
    private final static int        PLAYER_VISIBILITY_RANGE     = 20;                                                     ;
    private final static CDimension PLAYER_DIMENSION            = new CDimension(10, 10);
    private final static int        PLAYER_INITIAL_CANDY_AMOUNT = 0;
    private final static Color      PLAYER_COLOR                = new Color(51, 0, 204);

    // House properties
    private final static String     HOUSE_FED_TAG               = "ObjectRoot.House";
    private final static int        HOUSE_MAX_CANDY_AMOUNT      = 1000;
    private final static int        HOUSE_TRANSFER_RATE         = 400;
    private final static int        HOUSE_VISIBIlITY_RANGE      = 300;
    private final static int        HOUSE_RELOAD_INTERVAL       = 10;
    private final static CDimension HOUSE_DIMENSION             = new CDimension(10, 10);
    private final static Color      HOUSE_COLOR                = new Color(179, 0, 45);

    // Avatar properties
    private final static String     AVATAR_FED_TAG              = "ObjectRoot.Avatar";

    // Mapmanager properties
    private final static String     MAP_MANAGER_TAG             = "ObjectRoot.MapManager";

    // Utility
    public final static Random      UTIL_RND                    = new Random();

    // Log
    public final static int         L_INFO                      = 0;
    public final static int         L_ERROR                     = 1;
    public final static int         L_DEBUG                     = 2;

    public static String getFedName() {
        return FED_NAME;
    }

    public static String getFedSync() {
        return FED_SYNC;
    }

    public static String getFedFom() {
        return FED_FOM;
    }

    public static CDimension getMapDimension() {
        return MAP_DIMENSION;
    }

    public static int getMapAvatarNum() {
        return MAP_AVATAR_NUM;
    }

    public static int getPlayerInitialCandyAmount() {
        return PLAYER_INITIAL_CANDY_AMOUNT;
    }

    public static int getMapHouseNum() {
        return MAP_HOUSE_NUM;
    }

    public static String getPlayerFedTag() {
        return PLAYER_FED_TAG;
    }

    public static String getHouseFedTag() {
        return HOUSE_FED_TAG;
    }

    public static String getAvatarFedTag() {
        return AVATAR_FED_TAG;
    }

    public static Random getUtilRnd() {
        return UTIL_RND;
    }

    public static String getMapManagerTag() {
        return MAP_MANAGER_TAG;
    }

    public static int getPlayerVisibilityRange() {
        return PLAYER_VISIBILITY_RANGE;
    }

    public static void logError(String tag, String message) {
        log(L_ERROR, tag, message);
    }

    public static void logInfo(String tag, String message) {
        log(L_INFO, tag, message);
    }

    public static void logDebug(String tag, String message) {
        log(L_DEBUG, tag, message);
    }

    private static void log(int level, String tag, String message) {
        switch (level) {
        case FedConfig.L_DEBUG:
            System.out.println("\nDEBUG:: " + tag + ":: " + message);
            break;
        case FedConfig.L_ERROR:
            System.err.println("\nERROR:: " + tag + ":: " + message);
            break;

        case FedConfig.L_INFO:
            System.out.println("\nINFO:: " + tag + ":: " + message);
            break;

        }
    }

    public double convertTime(LogicalTime logicalTime) {
        // PORTICO SPECIFIC!!
        return ((DoubleTime) logicalTime).getTime();
    }

    public static int getHouseMaxCandyAmount() {
        return HOUSE_MAX_CANDY_AMOUNT;
    }

    public static int getlInfo() {
        return L_INFO;
    }

    public static int getlError() {
        return L_ERROR;
    }

    public static int getlDebug() {
        return L_DEBUG;
    }

    public static int getHouseTransferRate() {
        return HOUSE_TRANSFER_RATE;
    }

    public static int getHouseVisibilityRange() {
        return HOUSE_VISIBIlITY_RANGE;
    }

    public static int getHouseReloadInterval() {
        return HOUSE_RELOAD_INTERVAL;
    }

    public static String getFedObjRootTag() {
        return FED_OBJ_ROOT_TAG;
    }

    public static CDimension getHouseDimension() {
        return HOUSE_DIMENSION;
    }

    public static CDimension getFrameDimension() {
        return FRAME_DIMENSION;
    }

    public static CDimension getPlayerDimension() {
        return PLAYER_DIMENSION;
    }

    public static Color getPlayerColor() {
        return PLAYER_COLOR;
    }

    public static Color getHouseColor() {
        return HOUSE_COLOR;
    }

}
