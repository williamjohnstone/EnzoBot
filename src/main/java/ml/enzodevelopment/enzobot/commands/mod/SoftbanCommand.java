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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoftbanCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.getChannel().sendMessage("You need the kick members permission for this command, please contact your server administrator about this").queue();
            return;
        }

        if (event.getMessage().getMentionedUsers().size() < 1 || args.length < 3) {
            return;
        }

        try {
            final User toBan = event.getMessage().getMentionedUsers().get(0);
            if (toBan.equals(event.getAuthor()) &&
                    !event.getGuild().getMember(event.getAuthor()).canInteract(event.getGuild().getMember(toBan))) {
                event.getChannel().sendMessage("You are not permitted to perform this action.").queue();
                return;
            }
            String reason = StringUtils.join(Arrays.asList(args).subList(2, args.length), " ");
            event.getGuild().getController().ban(toBan.getId(), 1, "Kicked by: " + event.getAuthor().getName() + "\nReason: " + reason).queue(
                    nothing -> {
                        ModUtils.modLog(event.getAuthor(), toBan, PunishmentType.SOFTBAN, reason, event.getGuild());
                        ModUtils.sendSuccess(event.getMessage());
                        event.getGuild().getController().unban(toBan.getId()).reason("(Softban) Kicked by: " + event.getAuthor().getName()).queue();
                    }
            );
        } catch (HierarchyException e) {
            //e.printStackTrace();
            event.getChannel().sendMessage("I can't ban that member because his roles are above or equals to mine.").queue();
        }
    }

    @Override
    public String getUsage() {
        return "softban (@member) (reason)";
    }

    @Override
    public String getDesc() {
        return "Kicks a member and removes their messages";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("softban"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
