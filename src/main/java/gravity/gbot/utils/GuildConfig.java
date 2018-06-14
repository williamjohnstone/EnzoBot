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
        try {
            Database db = new Database(Config.dbConnection);
            db.init();
            ResultSet rs = db.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("bot_Channel_ID");
            }
            db.close();
            if ("0".equals(result)) {
                return null;
            }
            return result;

        } catch (SQLException ex) {
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error!");
            MDC.clear();
            return null;
        }
    }

    public static String getPrefix(String guild, String name) {
        String fallback_bot_Prefix = Config.fallback_prefix;
        try {
            Database db = new Database(Config.dbConnection);
            db.init();
            ResultSet rs = db.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");
            String result = null;
            if (rs.next()) {
                result = rs.getString("Prefix");
            }
            db.close();
            if (result != null) {
                if (result.contains(" ")) {
                    return fallback_bot_Prefix;
                }
                if ("".equals(result)) {
                    return fallback_bot_Prefix;
                }
            }
            return result;


        } catch (SQLException ex) {
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            MDC.put("GuildID", guild);
            MDC.put("Class", name);
            logger.error("Database Error!");
            MDC.clear();
            return fallback_bot_Prefix;
        }
    }

    public static boolean isAdmin(String ID, String guild, JDA jda) {
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
            Database db = new Database(Config.dbConnection);
            db.init();
            ResultSet rs = db.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("bot_Admins");
            }
            db.close();
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
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            return false;
        }
        return false;
    }
}
