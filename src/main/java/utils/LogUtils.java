package utils;

import database.GameStoreRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class LogUtils {
    public static Logger getLogger(){
        return LogManager.getLogger(GameStoreRepository.class);
    }
}
