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

package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuildInfoCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        Guild g = event.getGuild();
        String time = g.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        int userCnt = (int)g.getMemberCache().size();
        int active = 0;
        int bots = 0;
        for (Member mem : g.getMemberCache()) {
            if (mem.getUser().isBot()) {
                bots++;
            }
            if (!mem.getUser().isBot() && mem.getOnlineStatus() != OnlineStatus.OFFLINE) {
                active++;
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(g.getName());
        builder.setThumbnail(g.getIconUrl());
        builder.setDescription("Guild Info for " + g.getName());
        builder.addField("Owner", g.getOwner().getAsMention(), true);
        builder.addField("Region", g.getRegion().getName(), true);
        builder.addField("Date Created", time, true);
        builder.addField("Users", userCnt + " (" + active + " Online, " + bots + " bots)", true);
        event.getChannel().sendMessage(builder.build()).queue();

    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "guildinfo";
    }

    @Override
    public String getDesc() {
        return "Displays info about the current guild.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("guildinfo", "serverinfo"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}
