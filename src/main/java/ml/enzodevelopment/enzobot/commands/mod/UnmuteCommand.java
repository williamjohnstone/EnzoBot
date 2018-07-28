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
import ml.enzodevelopment.enzobot.objects.guild.GuildSettings;
import ml.enzodevelopment.enzobot.objects.punishment.PunishmentType;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import ml.enzodevelopment.enzobot.utils.ModUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnmuteCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("You need the kick members and the ban members permission for this command, please contact your server administrator about this").queue();
            return;
        }

        if (event.getMessage().getMentionedMembers().size() < 1 || args.length < 2) {
            return;
        }

        GuildSettings settings = GuildSettingsUtils.getGuild(event.getGuild());

        if (settings.getMuteRoleId() == null || settings.getMuteRoleId().isEmpty()) {
            event.getChannel().sendMessage("You need to set the mute role to use this command").queue();
            return;
        }

        Member toMute = event.getMessage().getMentionedMembers().get(0);
        Role role = event.getGuild().getRoleById(settings.getMuteRoleId());

        event.getGuild().getController().removeSingleRoleFromMember(toMute, role)
                .reason("Unmuted by" + String.format("%#s", event.getAuthor())).queue(success -> {
                    ModUtils.modLog(event.getAuthor(), toMute.getUser(), PunishmentType.UNMUTE, event.getGuild());
                    ModUtils.sendSuccess(event.getMessage());
                }
        );
    }

    @Override
    public String getUsage() {
        return "unmute (@user)";
    }

    @Override
    public String getDesc() {
        return "Removes the mute role to the mentioned member";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("unmute"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
