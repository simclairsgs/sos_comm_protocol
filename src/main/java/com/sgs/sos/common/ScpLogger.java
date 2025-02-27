package com.sgs.sos.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class ScpLogger
{
    public static class LogFormatter extends Formatter
    {
        // ANSI escape code
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

        // Here you can configure the format of the output and
        // its color by using the ANSI escape codes defined above.

        // format is called for every console log message
        @Override
        public String format(LogRecord record)
        {
            // This example will print date/time, class, and log level in yellow,
            // followed by the log message and it's parameters in white .
            StringBuilder builder = new StringBuilder();
            if(record.getLevel().getName().equals("SEVERE"))
                builder.append(ANSI_RED);
            else if(record.getLevel().getName().equals("CONFIG"))
                builder.append(ANSI_BLUE);
            else
                builder.append(ANSI_BLACK);

            builder.append("[");
            builder.append(calcDate(record.getMillis()));
            builder.append("]");

            builder.append(" [");
            builder.append(record.getSourceClassName());
            builder.append("]");

            builder.append(" [");
            builder.append(record.getLevel().getName());
            builder.append("]");

            if(record.getLevel().getName().equals("SEVERE"))
                builder.append(ANSI_RED);
            else if (record.getLevel().getName().equals("INFO")) {
                builder.append(ANSI_GREEN);
            }
            else if (record.getLevel().getName().equals("WARNING")) {
                builder.append(ANSI_YELLOW);
            }else
                builder.append(ANSI_BLUE);
            builder.append(" - ");
            builder.append(record.getMessage());

            Object[] params = record.getParameters();

            if (params != null)
            {
                builder.append("\t");
                for (int i = 0; i < params.length; i++)
                {
                    builder.append(params[i]);
                    if (i < params.length - 1)
                        builder.append(", ");
                }
            }

            builder.append(ANSI_RESET);
            builder.append("\n");
            return builder.toString();
        }

        private String calcDate(long millisecs) {
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date resultdate = new Date(millisecs);
            return date_format.format(resultdate);
        }
    }
    private static Logger logger;

    public static void init()
    {
        logger = Logger.getLogger("scpLog");
        FileHandler fh;

        try {
            fh = new FileHandler("./logs/scplog.xml");
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.FINER);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.FINER);
            Formatter formatter = new LogFormatter();
            handler.setFormatter(formatter);
            logger.addHandler(handler);
            logger.addHandler(fh);

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
