package uf.cs.cn;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;


public class MyLogger {
    // Get the Logger from the log manager which corresponds
    // to the given name <Logger.GLOBAL_LOGGER_NAME here>
    // static so that it is linked to the class and not to
    // a particular log instance because Log Manage is universal

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void InfoLog(String message)
    {
        LOGGER.log(Level.INFO, message);
    }
}
