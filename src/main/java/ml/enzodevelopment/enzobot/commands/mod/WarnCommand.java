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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarnCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.getChannel().sendMessage("You require permission to kick members to use this command.").queue();
            return;
        }

        if (args.length < 3 || event.getMessage().getMentionedMembers().size() < 1) {
            return;
        }

        User mod = event.getAuthor();

        Member target = event.getMessage().getMentionedMembers().get(0);

        GuildSettings settings = GuildSettingsUtils.getGuild(event.getGuild());

        int warningThershold = settings.getWarningThreshold();

        if (target.getUser() == event.getJDA().getSelfUser()) {
            return;
        }

        if (!event.getMember().canInteract(target)) {
            return;
        }

        if (ModUtils.getWarningCountForUser(target.getUser(), event.getGuild()) >= warningThershold) {
            event.getGuild().getController().kick(target).reason("Warning threshold reached.").queue();
            ModUtils.modLog(event.getAuthor(), target.getUser(), PunishmentType.KICK, "Warning threshold reached.", event.getGuild());
            ModUtils.sendSuccess(event.getMessage());
            return;
        }

        String reason = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.WHITE);
        builder.setTitle("You have been warned");

        ModUtils.addWarningToDb(event.getAuthor(), target.getUser(), reason, event.getGuild());
        ModUtils.modLog(event.getAuthor(), target.getUser(), PunishmentType.WARN, reason, event.getGuild());
        
        builder.setDescription("**" + mod.getName() + "#" + mod.getDiscriminator() + "** warned you for `" + (reason.isEmpty() ? "No reason was provided`" : reason + "`") +
                " This warning will expire in 3 days. " + ModUtils.getWarningCountForUser(target.getUser(), event.getGuild()) + "/" + settings.getWarningThreshold() + " Warnings.");

        target.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.build()).queue(null, fail -> {}));
        ModUtils.sendSuccess(event.getMessage());
    }

    @Override
    public String getUsage() {
        return "warn (@user) (reason)";
    }

    @Override
    public String getDesc() {
        return "Warns a user, Warnings last for 3 days if warning threshold is met the user is kicked";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("warn", "warnuser"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
