package org.oxff;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

//Burp will auto-detect and load any class that extends BurpExtension.
public class BigCharsGen implements BurpExtension
{
    public static final String EXTENSION_NAME = "Big chars generator";
    public static final String EXTENSION_VERSION = "1.0";
    public static final String EXTENSION_AUTHOR = "OxFF";
    public static final String EXTENSION_DESCRIPTION = "This is a big chars generator extension";
    public static final String EXTENSION_URL = "";
    public static final String EXTENSION_LICENSE = "MIT";

    public static MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api)
    {
        // set extension name
        BigCharsGen.api = api;
        api.extension().setName(EXTENSION_NAME);
        api.userInterface().registerContextMenuItemsProvider(new BigCharGenContextMenuItemsProvider(api));

        // print extension info
        printExtensionInfos(api);

    }

    private static void printExtensionInfos(MontoyaApi api) {
        Logging logging = api.logging();
        logging.logToOutput(EXTENSION_NAME + " v" + EXTENSION_VERSION + " by " + EXTENSION_AUTHOR);
        logging.logToOutput(EXTENSION_DESCRIPTION);
        logging.logToOutput(EXTENSION_URL);

    }
}