package gravity.gbot.utils;

import io.github.binaryoverload.JSONConfig;

import java.io.FileNotFoundException;
import java.sql.*;

public class Config {

    public static String Discord_Token;
    public static String dbConnection;
    public static Boolean loggingALL = false;
    public static Boolean loggingCMD = false;

    public void loadConfig() {
        try {
            JSONConfig config = new JSONConfig("config.json");
            Discord_Token = config.getString("Config.token").get();
            dbConnection = config.getString("Config.dbString").get();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getPrefix(String guild) {
        Connection conn;

        String bot_Prefix = "!";
        try {


            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("Prefix");
            }
            conn.close();
            //checks if the prefix is null and if not proceeds to make sure the prefix is not empty nor contain any spaces
            if (result != null) {
                if (result.contains(" ")) {
                    return bot_Prefix;
                }
                if (result.equals("")) {
                    return bot_Prefix;
                }
            }
            //return the non null prefix
            return result;


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            // return fallback prefix and output error
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return bot_Prefix;
        } catch (Exception e) {
            e.printStackTrace();
            // return fallback prefix and output error
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return bot_Prefix;
        }
    }

    public String isAdmin(String ID, String guild) {
        Connection conn;

        try {


            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("bot_Admins");
            }
            conn.close();
            String[] loopData = result.split(",");

            for (String loop : loopData) {
                if (ID.equals(loop)) {
                    return loop;
                }
            }
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return null;

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
