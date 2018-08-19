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
import ml.enzodevelopment.enzobot.objects.punishment.PunishmentType;
import ml.enzodevelopment.enzobot.utils.ModUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
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

        try {
            event.getGuild().getBanList().queue(list -> {
                for (Guild.Ban ban : list) {
                    if (ban.getUser().getName().equalsIgnoreCase(StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "))) {
                        event.getGuild().getController().unban(ban.getUser())
                                .reason("Unbanned by " + event.getAuthor().getName()).queue();
                        ModUtils.modLog(event.getAuthor(), ban.getUser(), PunishmentType.UNBAN, event.getGuild());
                        ModUtils.sendSuccess(event.getMessage());
                        return;
                    }
                }
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.WHITE);
                error.setTitle("Error");
                error.setDescription("This user is not banned.");
                event.getChannel().sendMessage(error.build()).queue();
            });

        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("ERROR: " + e.getMessage()).queue();
        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "unban (username)";
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
