package common;

import hla.rti.LogicalTime;
import org.portico.impl.hla13.types.DoubleTime;
import java.util.Random;
import java.util.logging.Logger;

public class FedConfig {

    // Federation initialization properties
    private final static String    FED_NAME                = "FederationName";
    private final static String    FED_SYNC                = "ReadyToRun";
    private final static String    FED_FOM                 = "d:/workspace/shared-file-location/candy-game-map.fed";

    // Game properties
    private final static Dimension MAP_DIMENSION           = new Dimension(1500, 2000);
    private final static int       MAP_AVATAR_NUM          = 5;
    private final static int       MAP_HOUSE_NUM           = 5;

    // Player properties
    private final static int       PLAYER_VISIBILITY_RANGE = 20;
    private final static String    PLAYER_FED_TAG          = "PLAYER";

    // House properties
    private final static String    HOUSE_FED_TAG           = "HOUSE";
    private final static int       HOUSE_MAX_CANDY_AMOUNT  = 1000;
    private final static int       HOUSE_TRANSFER_RATE     = 400;
    private final static int       HOUSE_VISIBIlITY_RANGE  = 300;
    private final static int       HOUSE_RELOAD_INTERVAL   = 10;
    private final static int       HOUSE_ID_RANGE          = 1000;

    // Avatar properties
    private final static String    AVATAR_FED_TAG          = "AVATAR";

    // Mapmanager properties
    private final static String    MAP_MANAGER_TAG         = "MAPMANAGER";

    // Utility
    public final static Random     UTIL_RND                = new Random();

    // Log
    public final static int        L_INFO                  = 0;
    public final static int        L_ERROR                 = 1;
    public final static int        L_DEBUG                 = 2;

    public static String getFedName() {
        return FED_NAME;
    }

    public static String getFedSync() {
        return FED_SYNC;
    }

    public static String getFedFom() {
        return FED_FOM;
    }

    public static Dimension getMapDimension() {
        return MAP_DIMENSION;
    }

    public static int getMapAvatarNum() {
        return MAP_AVATAR_NUM;
    }

    public static int getMapHouseNum() {
        return MAP_HOUSE_NUM;
    }

    public static int getPlayerVisibilityRange() {
        return PLAYER_VISIBILITY_RANGE;
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
            System.out.println("DEBUG:: " + tag + ":: " + message);
            break;
        case FedConfig.L_ERROR:
            System.err.println("ERROR:: " + tag + ":: " + message);
            break;

        case FedConfig.L_INFO:
            System.out.println("INFO:: " + tag + ":: " + message);
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

    public static int getHouseIdRange() {
        return HOUSE_ID_RANGE;
    }

}
