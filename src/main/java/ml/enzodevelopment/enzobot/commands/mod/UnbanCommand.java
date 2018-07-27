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

package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import ml.enzodevelopment.enzobot.utils.ModUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnbanCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("You need the kick members and the ban members permission for this command, please contact your server administrator about this").queue();
            return;
        }

        if (args.length < 1) {
            event.getChannel().sendMessage("Usage is " + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + getAliases().get(0) + " <username>").queue();
            return;
        }

        try {
            event.getGuild().getBanList().queue(list -> {
                for (Guild.Ban ban : list) {
                    if (ban.getUser().getName().equalsIgnoreCase(StringUtils.join(args, " "))) {
                        event.getGuild().getController().unban(ban.getUser())
                                .reason("Unbanned by " + event.getAuthor().getName()).queue();
                        event.getChannel().sendMessage("User " + ban.getUser().getName() + " unbanned.").queue();
                        ModUtils.modLog(event.getAuthor(), ban.getUser(), "unbanned", event.getGuild());
                        return;
                    }
                }
                event.getChannel().sendMessage("This user is not banned").queue();
            });

        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("ERROR: " + e.getMessage()).queue();
        }
    }

    @Override
    public String getUsage() {
        return "unban";
    }

    @Override
    public String getDesc() {
        return "Unbans a Member";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("unban"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
