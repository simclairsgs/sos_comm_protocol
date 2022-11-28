package com.sgs.sos.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ScpLogger {

    private static Logger logger;

    public static void init()
    {
        logger = Logger.getLogger("scpLog");
        FileHandler fh;

        try {
            fh = new FileHandler("./logs/scplog.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getScpLogger()
    {
        return logger;
    }
}
