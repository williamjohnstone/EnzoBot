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

import ml.enzodevelopment.enzobot.connections.database.DBManager;
import io.github.binaryoverload.JSONConfig;
import ml.enzodevelopment.enzobot.objects.guild.GuildSettings;
import ml.enzodevelopment.enzobot.utils.MusicUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static DBManager DB;
    public static MusicUtils musicUtils;
    public static final Color ENZO_BLUE = new Color(51, 102, 153);
    public static Map<String, GuildSettings> GUILD_SETTINGS = new HashMap<>();


    public static String discordToken;
    public static Long discordId;
    public static String discordSecret;
    public static String discordCallback;
    public static String dbConnection;
    public static String fallbackPrefix;
    public static String discordBotsToken;
    public static String sentryDSN;
    public static String googleToken;


    public void loadConfig() {
        try {
            JSONConfig config = new JSONConfig("config.json");
            discordToken = config.getString("discord.token").get();
            discordId = config.getLong("discord.id").getAsLong();
            discordSecret = config.getString("discord.secret").get();
            discordCallback = config.getString("discord.callback").get();
            dbConnection = config.getString("settings.database").get();
            fallbackPrefix = config.getString("settings.fallbackPrefix").get();
            discordBotsToken = config.getString("authorization.discordbots").get();
            sentryDSN = config.getString("authorization.sentry").get();
            googleToken = config.getString("authorization.google").get();
            DB = new DBManager();
            musicUtils = new MusicUtils();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
