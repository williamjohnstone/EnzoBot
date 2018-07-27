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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModUtils {

    public static void modLog(User mod, User punishedUser, String punishment, String reason, String time, Guild g) {
        String chan = GuildSettingsUtils.getGuild(g).getLogChannel();
        if (chan != null && !chan.isEmpty()) {
            TextChannel logChannel = g.getTextChannelById(GuildSettingsUtils.getGuild(g).getLogChannel());
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
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
