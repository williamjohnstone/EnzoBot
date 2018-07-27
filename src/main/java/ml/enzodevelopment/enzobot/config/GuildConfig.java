/*
 * Enzo Bot, a multipurpose discord bot
 *
 * Copyright (c) 2018 William "Enzo" Johnstone
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ml.enzodevelopment.enzobot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GuildConfig {
    private static Logger logger = LoggerFactory.getLogger(GuildConfig.class.getName());

    private Connection conn = Config.DB.getConnManager().getConnection();

    public String getBotChannel(String guild) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Config` where guild_ID = ?;")) {
            stmt.setString(1, guild);
            ResultSet rs = stmt.executeQuery();
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
    }

    public String getPrefix(String guild) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Config` where guild_ID = ?;")) {
            stmt.setString(1, guild);
            ResultSet rs = stmt.executeQuery();
            String result = Config.fallback_prefix;
            if (rs != null && rs.next()) {
                result = rs.getString("Prefix");
            }
            if (result != null && result.contains(" ") || "".equals(result)) {
                logger.warn("Reverting to Fallback Prefix, ID:" + guild);
                return Config.fallback_prefix;
            }
            return result;
        } catch (SQLException ex) {
            logger.error("Database Error", ex);
            return Config.fallback_prefix;
        }
    }
}
