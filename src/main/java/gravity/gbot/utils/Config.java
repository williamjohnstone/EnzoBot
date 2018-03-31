package gravity.gbot.utils;

import io.github.binaryoverload.JSONConfig;

import java.io.FileNotFoundException;

public class Config {

    public static String Discord_Token;
    public static String dbConnection;
    public static String fallback_prefix;
    public static String API_Key;
    public static String sentry_dsn;
    public static String google_api;
    public static Boolean loggingALL = false;
    public static Boolean loggingCMD = false;

    public void loadConfig() {
        try {
            JSONConfig config = new JSONConfig("config.json");
            Discord_Token = config.getString("Config.token").get();
            dbConnection = config.getString("Config.dbString").get();
            fallback_prefix = config.getString("Config.fallback").get();
            API_Key = config.getString("Config.api_key").get();
            sentry_dsn = config.getString("Config.sentry_dsn").get();
            google_api = config.getString("Config.google_key").get();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
