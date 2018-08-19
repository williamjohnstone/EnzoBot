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
import ml.enzodevelopment.enzobot.utils.CalculatePunishmentTime;
import ml.enzodevelopment.enzobot.utils.ModUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TempBanCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("You need the kick members and the ban members permission for this command, please contact your server administrator about this").queue();
            return;
        }

        if (event.getMessage().getMentionedUsers().size() < 1 || args.length < 3) {
            return;
        }
        try {
            final User toBan = event.getMessage().getMentionedUsers().get(0);
            if (toBan.equals(event.getAuthor()) &&
                    !Objects.requireNonNull(event.getGuild().getMember(event.getAuthor())).canInteract(Objects.requireNonNull(event.getGuild().getMember(toBan)))) {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.WHITE);
                error.setTitle("Error");
                error.setDescription("You are not permitted to perform this action.");
                event.getChannel().sendMessage(error.build()).queue();
                return;
            }

            if (args.length >= 4) {
                String reason;
                reason = StringUtils.join(Arrays.copyOfRange(args, 3, args.length), " ");

                String[] timeParts = args[2].split("(?<=\\D)+(?=\\d)+|(?<=\\d)+(?=\\D)+");
                CalculatePunishmentTime calculateBanTime = new CalculatePunishmentTime(event, getAliases().get(0), timeParts).invoke();
                if (calculateBanTime.is()) return;
                String finalUnbanDate = calculateBanTime.getFinalUnbanDate();
                int finalBanTime = calculateBanTime.getFinalBanTime();

                event.getGuild().getController().ban(toBan.getId(), 1, reason).queue(
                        (voidMethod) -> {
                            if (finalBanTime > 0) {
                                ModUtils.addBannedUserToDb(event.getAuthor().getId(), toBan.getName(), toBan.getDiscriminator(), toBan.getId(), finalUnbanDate, event.getGuild().getId());
                                ModUtils.modLog(event.getAuthor(), toBan, PunishmentType.TEMP_BAN, reason, args[2], event.getGuild());
                            }
                        }
                );
                ModUtils.sendSuccess(event.getMessage());
            }
        } catch (HierarchyException e) {
            //e.printStackTrace();
            event.getChannel().sendMessage("I can't ban that member because their roles are above or equals to mine.").queue();
        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "tempban (@user) [(time)(m/h/d/w/M/Y)] (Reason)";
    }

    @Override
    public String getDesc() {
        return "Temporarily bans a user.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Collections.singletonList("tempban"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
