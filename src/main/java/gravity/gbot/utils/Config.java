package gravity.gbot.utils;

import io.github.binaryoverload.JSONConfig;

import java.awt.*;
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
    public static String EmbedGbotLogo = "https://cdn.discordapp.com/attachments/391558952757886978/431071158549348373/favicon.png";
    public static Color GBot_Blue = new Color(51, 102, 153);
    public static String config_file;
    public static Boolean dev_mode = false;
    public static String dev_bot_channel = "431463562393944064";

    public void loadConfig() {
        try {
            if (dev_mode) config_file = "dev_config.json";
            JSONConfig config = new JSONConfig(config_file);
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
