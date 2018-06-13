package gravity.gbot.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.*;

public class GuildConfig {
    private static Logger logger = LoggerFactory.getLogger(GuildConfig.class.getName());

    public static String getBotChannel(String guild, String name) {
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
            if ("0".equals(result)) {
                    return null;
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
    public static String getPrefix(String guild, String name) {
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
                if ("".equals(result)) {
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

    public static boolean isAdmin(String ID, String guild, JDA jda) {
        Connection conn;
        if (jda == null) {
            return false;
        }
        Guild Guild = jda.getGuildById(guild);
        if (Guild == null)
            return false;
        Member member = Guild.getMemberById(ID);
        if (member == null)
            return false;
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
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
                return false;
            }
            String[] dbData = result.split(",");

            for (String admin : dbData) {
                if (ID.equals(admin)) {
                    return true;
                }
            }


        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
