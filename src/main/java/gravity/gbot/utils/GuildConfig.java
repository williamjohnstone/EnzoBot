package gravity.gbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GuildConfig {
    private static Logger logger = LoggerFactory.getLogger(GuildConfig.class.getName());

    private ResultSet getGuildRecord(String guild) {
        Config.DB.run(() -> {
            try {
                Connection conn = Config.DB.getConnManager().getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Config` where guild_ID = ?;");
                stmt.setString(1, guild);
                conn.close();
                return stmt.executeQuery();
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
                return null;
            }
        });
        return null;
    }

    public String getBotChannel(String guild) {
        Config.DB.run(() -> {
            try {
                ResultSet rs = getGuildRecord(guild);
                String result;
                if (rs != null && rs.next() && !"0".equals(rs.getString("bot_Channel_ID"))) {
                    result = rs.getString("bot_Channel_ID");
                } else {
                    return null;
                }
                return result;
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
                return null;
            }
        });
        return null;
    }

    public String getPrefix(String guild) {
        Config.DB.run(() -> {
            try {
                ResultSet rs = getGuildRecord(guild);
                String result = Config.fallback_prefix;
                if (rs != null && rs.next()) {
                    result = rs.getString("Prefix");
                }
                if (result != null) {
                    if (result.contains(" ") || "".equals(result)) {
                        return Config.fallback_prefix;
                    }
                }
                return result;
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
                return Config.fallback_prefix;
            }
        });
        return Config.fallback_prefix;
    }
}
