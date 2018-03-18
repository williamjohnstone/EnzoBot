package gravity.gbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.*;

public class GuildConfig {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public String getPrefix(String guild, String name) {
        Connection conn;

        String fallback_bot_Prefix = Config.fallback_prefix;
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
                    return fallback_bot_Prefix;
                }
                if (result.equals("")) {
                    return fallback_bot_Prefix;
                }
            }
            //return the non null prefix
            return result;


        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            // return fallback prefix and output error
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error! Guild not in Database");
            MDC.clear();
            return fallback_bot_Prefix;
        } catch (Exception e) {
            e.printStackTrace();
            // return fallback prefix and output error
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error! Guild not in Database");
            MDC.clear();
            return fallback_bot_Prefix;
        }
    }

    public String isAdmin(String ID, String guild, String name) {
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
            String[] dbData = result.split(",");

            for (String admin : dbData) {
                if (ID.equals(admin)) {
                    return admin;
                }
            }
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error! Guild not in Database");
            MDC.clear();
            return null;

        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
