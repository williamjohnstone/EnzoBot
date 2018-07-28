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
import ml.enzodevelopment.enzobot.objects.ConsoleUser;
import ml.enzodevelopment.enzobot.objects.FakeUser;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ModUtils {

    private static Logger logger = LoggerFactory.getLogger(ModUtils.class);

    public static void modLog(User mod, User punishedUser, String punishment, String reason, String time, Guild g) {
        String chan = GuildSettingsUtils.getGuild(g).getLogChannel();
        if (chan != null && !chan.isEmpty()) {
            TextChannel logChannel = g.getTextChannelById(GuildSettingsUtils.getGuild(g).getLogChannel());
            if (logChannel == null) {
                return;
            }
            String length = "";
            if (time != null && !time.isEmpty()) {
                length = " lasting " + time + "";
            }

            logChannel.sendMessage(String.format("User **%#s** got **%s** by **%#s**%s%s",
                    punishedUser,
                    punishment,
                    mod,
                    length,
                    reason.isEmpty() ? "" : " with reason _\"" + reason + "\"_"
            )).queue();
        }
    }


    public static void modLog(User mod, User punishedUser, String punishment, String reason, Guild g) {
        modLog(mod, punishedUser, punishment, reason, "", g);
    }

    public static void modLog(User mod, User unbannedUser, String punishment, Guild g) {
        modLog(mod, unbannedUser, punishment, "", g);
    }

    public static void addBannedUserToDb(String modID, String userName, String userDiscriminator, String userId, String unbanDate, String guildId) {

        Config.DB.run(() -> {
            Connection conn = Config.DB.getConnManager().getConnection();
            try {
                PreparedStatement smt = conn.prepareStatement("INSERT INTO bans(modUserId, Username, discriminator, userId, ban_date, unban_date, guildId) " +
                        "VALUES(? , ? , ? , ? , NOW() , ?, ?)");

                smt.setString(1, modID);
                smt.setString(2, userName);
                smt.setString(3, userDiscriminator);
                smt.setString(4, userId);
                smt.setString(5, unbanDate);
                smt.setString(6, guildId);
                smt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public static void checkUnbans(JDA jda) {
        Config.DB.run(() -> {
            logger.debug("Checking for users to unban");
            int usersUnbanned = 0;
            Connection database = Config.DB.getConnManager().getConnection();

            try (Statement smt = database.createStatement()) {
                ResultSet res = smt.executeQuery("SELECT * FROM bans");

                while (res.next()) {
                    java.util.Date unbanDate = res.getTimestamp("unban_date");
                    java.util.Date currDate = new java.util.Date();

                    if (currDate.after(unbanDate)) {
                        usersUnbanned++;
                        String username = res.getString("Username");
                        logger.debug("Unbanning " + username);
                        try {
                            String guildId = res.getString("guildId");
                            String userID = res.getString("userId");
                            Guild guild = jda.getGuildById(guildId);
                            if (guild != null) {
                                guild.getController()
                                        .unban(userID).reason("Ban expired").queue();
                                modLog(new ConsoleUser(), new FakeUser(username, userID, res.getString("discriminator")), "unbanned", guild);
                            }
                        } catch (NullPointerException ignored) {
                        }
                        database.createStatement().executeUpdate("DELETE FROM bans WHERE id=" + res.getInt("id") + "");
                    }
                }
                logger.debug("Checking done, unbanned " + usersUnbanned + " users.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
