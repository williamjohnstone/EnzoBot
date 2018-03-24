package gravity.gbot.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.*;

public class GuildConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public String isBotChannel(String guild, String name) {
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
                result = rs.getString("bot_Channel_ID");
            }
            conn.close();
            if (result != null) {
                if (result.equals("0")) {
                    return null;
                }
            }
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
            logger.error("Database Error!");
            MDC.clear();
        } catch (Exception e) {
            e.printStackTrace();
            // return fallback prefix and output error
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error!");
            MDC.clear();

        }
        return null;
    }
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
            logger.error("Database Error!");
            MDC.clear();
            return fallback_bot_Prefix;
        } catch (Exception e) {
            e.printStackTrace();
            // return fallback prefix and output error
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error!");
            MDC.clear();
            return fallback_bot_Prefix;
        }
    }

    public String isAdmin(String ID, String guild, JDA jda) {
        Connection conn;
        if (jda.getGuildById(guild).getMemberById(ID).hasPermission(Permission.ADMINISTRATOR)) {
            return ID;
        }

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
            if (result == null) {
                return null;
            }
            String[] dbData = result.split(",");

            for (String admin : dbData) {
                if (ID.equals(admin)) {
                    return admin;
                }
            }


        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
