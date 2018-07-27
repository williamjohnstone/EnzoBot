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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GrantAccessCommand implements Command {
    private Collection<Permission> requiredPermissions = new ArrayList<>(Arrays.asList(Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES, Permission.MANAGE_PERMISSIONS));

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length != 3) {
            return;
        }
        if (event.getMessage().getMentionedMembers().size() != 1 && event.getMessage().getMentionedRoles().size() != 1) {
            return;
        }
        if (event.getMessage().getMentionedChannels().size() != 1) {
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(requiredPermissions)) {
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(event.getMessage().getMentionedChannels().get(0), requiredPermissions)) {
            return;
        }
        Member requester = event.getMember();
        if (requester.hasPermission(requiredPermissions) && requester.hasPermission(event.getMessage().getMentionedChannels().get(0), requiredPermissions)) {
            if (event.getMessage().getMentionedMembers().size() == 1) {
                if (event.getMember().canInteract(event.getMessage().getMentionedMembers().get(0))) {
                    grantAccess(event, event.getMessage().getMentionedMembers().get(0), null, event.getMessage().getMentionedChannels().get(0));
                }
            }
        } else if (event.getMessage().getMentionedRoles().size() == 1) {
            if (event.getMember().canInteract(event.getMessage().getMentionedRoles().get(0))) {
                grantAccess(event, null, event.getMessage().getMentionedRoles().get(0), event.getMessage().getMentionedChannels().get(0));
            }
        }
    }

    @Override
    public String getUsage() {
        return "allow (@User or @Role) (#Channel)";
    }

    @Override
    public String getDesc() {
        return "Grants a user's or role's permission to access the specified channel.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("grantaccess", "allowaccess", "allow"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }

    private void grantAccess(GuildMessageReceivedEvent event, Member m, Role r, TextChannel channel) {
        if (m == null && r != null) {
            grantAccess(event, r, channel);
        } else if (r == null && m != null) {
          grantAccess(event, m, channel);
        }
    }

    private void grantAccess(GuildMessageReceivedEvent event, Member m, TextChannel channel) {
        if (event.getGuild().getSelfMember().canInteract(m)) {
            channel.getPermissionOverride(m).delete().queue();
        }
    }

    private void grantAccess(GuildMessageReceivedEvent event, Role r, TextChannel channel) {
        if (event.getGuild().getSelfMember().canInteract(r)) {
            channel.getPermissionOverride(r).delete().queue();
        }
    }
}
