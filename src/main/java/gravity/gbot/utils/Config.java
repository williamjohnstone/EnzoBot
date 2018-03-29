package gravity.gbot.utils;

import gravity.gbot.Main;
import gravity.gbot.Music.getJson;
import io.github.binaryoverload.JSONConfig;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.io.FileNotFoundException;

public class Config {

    public static String version;
    public static String Discord_Token;
    public static String dbConnection;
    public static String fallback_prefix;
    public static String API_Key;
    public static String sentry_dsn;
    public static String google_api;
    public static String gh_commit;
    public static Boolean loggingALL = false;
    public static Boolean loggingCMD = false;
    private String github_api = getJson.getLink("https://api.github.com/repos/GravityGamer/GravityBot/commits/master");

    public void loadConfig() {
        try {
            JSONConfig config = new JSONConfig("config.json");
            Discord_Token = config.getString("Config.token").get();
            dbConnection = config.getString("Config.dbString").get();
            fallback_prefix = config.getString("Config.fallback").get();
            API_Key = config.getString("Config.api_key").get();
            sentry_dsn = config.getString("Config.sentry_dsn").get();
            google_api = config.getString("Config.google_key").get();
            JSONObject gh = new JSONObject(github_api);
            JSONPointer ghPointer = new JSONPointer("/sha");
            String jenkins_build = "34";
            if (Main.userVerString != null) {
                version = Main.userVerString;
            } else {
                version = "3.13.2_" + jenkins_build;
                gh_commit = String.valueOf(ghPointer.queryFrom(gh));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
