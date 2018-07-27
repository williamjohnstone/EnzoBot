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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetRole implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I cant let you do that, For security reasons of course...").queue();
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I don't have the permission to do that.").queue();
            return;
        }
        if (!event.getMember().canInteract(event.getMessage().getMentionedMembers().get(0))) {
            event.getChannel().sendMessage("You cant manage roles for this member").queue();
            return;
        }
        if (!event.getMember().canInteract(event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", "")))) {
            event.getChannel().sendMessage("You cant manage roles higher or equal to yourself").queue();
            return;
        }
        if (args.length > 3 ) {
            event.getChannel().sendMessage("Command usage Incorrect! Correct usage: " + getUsage()).queue();
        } else {
            try {
                event.getGuild().getController().addRolesToMember(event.getMessage().getMentionedMembers().get(0), event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", ""))).queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " :white_check_mark: Roles assigned successfully.").queue();
            } catch (HierarchyException e) {
                event.getChannel().sendMessage("Sorry i can't set roles higher than my own.").queue();
            }
        }


    }

    @Override
    public String getUsage() {
        return "setRole (@member) (@role)";
    }

    @Override
    public String getDesc() {
        return "Changes the specified member's role.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setrole"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
