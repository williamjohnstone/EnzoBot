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

package ml.enzodevelopment.enzobot.utils;

import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.objects.guild.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class GuildSettingsUtils {

    private static final Logger logger = LoggerFactory.getLogger(GuildSettingsUtils.class);

    public static void loadGuildSettings() {
        logger.debug("Loading Guild settings.");

        Config.DB.run(() -> {
            Connection database = Config.DB.getConnManager().getConnection();
            try (PreparedStatement stmt = database.prepareStatement("SELECT * FROM guildSettings")){
                ResultSet res = stmt.executeQuery();

                while (res.next()) {
                    String guildId = res.getString("guildId");

                    Config.GUILD_SETTINGS.put(guildId, new GuildSettings(guildId)
                            .setCustomPrefix(res.getString("prefix"))
                            .setLogChannel(res.getString("logChannelId"))
                            .setMuteRoleId(res.getString("muteRoleId"))
                            .setBotChannel(res.getString("botChannelId"))
                            .useBotChannel(false)
                    );
                }
                res.close();
                logger.info("Loaded settings for " + Config.GUILD_SETTINGS.keySet().size() + " guilds.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This wil get a guild or register it if it's not there yet
     *
     * @param guild the guild to get
     * @return the guild
     */
    public static GuildSettings getGuild(Guild guild) {

        if (!Config.GUILD_SETTINGS.containsKey(guild.getId())) {
            return registerNewGuild(guild);
        }

        return Config.GUILD_SETTINGS.get(guild.getId());

    }

    /**
     * This will save the settings into the database when the guild owner/admin updates it
     *
     * @param guild    The guild to update it for
     * @param settings the new settings
     */
    public static void updateGuildSettings(Guild guild, GuildSettings settings) {
        if (!Config.GUILD_SETTINGS.containsKey(settings.getGuildId())) {
            registerNewGuild(guild);
            return;
        }
        Config.DB.run(() -> {
            Connection database = Config.DB.getConnManager().getConnection();

            try (PreparedStatement smt = database.prepareStatement("UPDATE guildSettings SET prefix= ? , logChannelId= ? , muteRoleId = ? , botChannelId = ? , useBotChannel = ? WHERE guildId = '" + guild.getId() + "'")){
                smt.setString(1, replaceUnicode(settings.getCustomPrefix()));
                smt.setString(2, settings.getLogChannel());
                smt.setString(3, settings.getMuteRoleId());
                smt.setString(4, settings.getBotChannel());
                smt.setBoolean(5, settings.usingBotChannel());
                smt.executeUpdate();
                Config.GUILD_SETTINGS.remove(guild.getId());
                Config.GUILD_SETTINGS.put(guild.getId(), settings);
            } catch (SQLException e1) {
                if (!e1.getLocalizedMessage().toLowerCase().startsWith("incorrect string value"))
                    e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This will register a new guild with their settings on bot join
     *
     * @param g The guild that we are joining
     * @return The new guild
     */
    public static GuildSettings registerNewGuild(Guild g) {
        if (Config.GUILD_SETTINGS.containsKey(g.getId())) {
            return Config.GUILD_SETTINGS.get(g.getId());
        }
        GuildSettings newGuildSettings = new GuildSettings(g.getId());
        Config.DB.run(() -> {
            Connection database = Config.DB.getConnManager().getConnection();

            try (PreparedStatement smt = database.prepareStatement("INSERT INTO guildSettings(guildId, guildName," +
                    "prefix, logChannelId, muteRoleId, botChannelId, useBotChannel) " +
                    "VALUES('" + g.getId() + "',  ? , ? , ? , ? , ?, ?)")){
                ResultSet resultSet = database.createStatement()
                        .executeQuery("SELECT id FROM guildSettings WHERE guildId='" + g.getId() + "'");
                int rows = 0;
                while (resultSet.next())
                    rows++;

                if (rows == 0) {
                    smt.setString(1, replaceUnicode(g.getName()));
                    smt.setString(2, Config.fallback_prefix);
                    smt.setString(3, "0");
                    smt.setString(4, "0");
                    smt.setString(5, "0");
                    smt.setBoolean(6, false);
                    smt.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Config.GUILD_SETTINGS.put(g.getId(), newGuildSettings);
            logger.info("Registered new Guild: " + g.getName());
        });
        return newGuildSettings;
    }

    /**
     * This will attempt to remove a guild wen we leave it
     *
     * @param g the guild to remove from the database
     */
    public static void deleteGuild(Guild g) {
        Config.GUILD_SETTINGS.remove(g.getId());
        Config.DB.run(() -> {
            Connection database = Config.DB.getConnManager().getConnection();

            try {
                Statement smt = database.createStatement();
                smt.execute("DELETE FROM guildSettings WHERE guildId='" + g.getId() + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String replaceUnicode(String entery) {
        if (entery == null || entery.isEmpty())
            return null;
        return entery.replaceAll("\\P{Print}", "");
    }

}
