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

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculatePunishmentTime {
    private boolean myResult;
    private GuildMessageReceivedEvent event;
    private String[] timeParts;
    private String finalUnbanDate;
    private int finalBanTime;
    private String alias;

    public CalculatePunishmentTime(GuildMessageReceivedEvent event, String alias, String... timeParts) {
        this.event = event;
        this.timeParts = timeParts;
        this.alias = alias;
    }

    public boolean is() {
        return myResult;
    }

    public String getFinalUnbanDate() {
        return finalUnbanDate;
    }

    public int getFinalBanTime() {
        return finalBanTime;
    }

    public CalculatePunishmentTime invoke() {
        String unbanDate = "";
        int banTime; // initial value is always 0
        try {
            banTime = Integer.parseInt(timeParts[0]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(e.getMessage() + " is not a valid number").queue();
            myResult = true;
            return this;
        } catch (ArrayIndexOutOfBoundsException ignored ) {
            event.getChannel().sendMessage("Incorrect time format, use `" + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + "help " + alias + "` for more info.").queue();
            myResult = true;
            return this;
        }
        if (banTime > 0) {
            if (timeParts.length != 2) {
                event.getChannel().sendMessage("Incorrect time format, use `" + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + "help " + alias + "` for more info.").queue();
                myResult = true;
                return this;
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = new Date(System.currentTimeMillis());

            switch (timeParts[1]) {
                case "m":
                    if (Integer.parseInt(timeParts[0]) < 10) {
                        event.getChannel().sendMessage("The minimum time for minutes is 10.").queue();
                        myResult = true;
                        return this;
                    }
                    dt = DateUtils.addMinutes(dt, banTime);
                    break;
                case "h":
                    dt = DateUtils.addHours(dt, banTime);
                    break;
                case "d":
                    dt = DateUtils.addDays(dt, banTime);
                    break;
                case "w":
                    dt = DateUtils.addWeeks(dt, banTime);
                    break;
                case "M":
                    dt = DateUtils.addMonths(dt, banTime);
                    break;
                case "Y":
                    dt = DateUtils.addYears(dt, banTime);
                    break;

                default:
                    event.getChannel().sendMessage( timeParts[1] + " is not defined, please choose from m, d, h, w, M or Y").queue();
                    myResult = true;
                    return this;
            }
            unbanDate = df.format(dt);
        }

        finalUnbanDate = unbanDate.isEmpty() ? "" : unbanDate;
        finalBanTime = banTime;
        myResult = false;
        return this;
    }
}
