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
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BanCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("You need the kick members and the ban members permission for this command, please contact your server administrator about this").queue();
            return;
        }

        if (event.getMessage().getMentionedUsers().size() < 1 || args.length < 3) {
            event.getChannel().sendMessage("Usage is " + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + getUsage()).queue();
            return;
        }

        try {
            final User toBan = event.getMessage().getMentionedUsers().get(0);
            if (toBan.equals(event.getAuthor()) &&
                    !Objects.requireNonNull(event.getGuild().getMember(event.getAuthor())).canInteract(Objects.requireNonNull(event.getGuild().getMember(toBan)))) {
                event.getChannel().sendMessage("You are not permitted to perform this action.").queue();
                return;
            }
            //noinspection ConstantConditions
            if (args.length >= 3) {
                String reason = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
                String[] timeParts = args[2].split("(?<=\\D)+(?=\\d)+|(?<=\\d)+(?=\\D)+"); //Split the string into ints and letters

                if (!isInt(timeParts[0])) {
                    String newReason = StringUtils.join(Arrays.asList(args).subList(1, args.length), " ");
                    event.getGuild().getController().ban(toBan.getId(), 1, reason).queue(
                            (m) -> {
                                ModUtils.modLog(event.getAuthor(), toBan, "banned", newReason, event.getGuild());
                                sendSuccess(event.getMessage());
                            }
                    );
                    return;
                }

                CalculateBanTime calculateBanTime = new CalculateBanTime(event, timeParts).invoke();
                if (calculateBanTime.is()) return;
                String finalUnbanDate = calculateBanTime.getFinalUnbanDate();
                int finalBanTime = calculateBanTime.getFinalBanTime();
                event.getGuild().getController().ban(toBan.getId(), 1, reason).queue(
                        (voidMethod) -> {
                            if (finalBanTime > 0) {
                                ModUtils.addBannedUserToDb(event.getAuthor().getId(), toBan.getName(), toBan.getDiscriminator(), toBan.getId(), finalUnbanDate, event.getGuild().getId());

                                ModUtils.modLog(event.getAuthor(), toBan, "banned", reason, args[2], event.getGuild());
                            } else {
                                final String newReason = StringUtils.join(Arrays.asList(args).subList(2, args.length), " ");
                                ModUtils.modLog(event.getAuthor(), toBan, "banned", newReason, event.getGuild());
                            }
                        }
                );
                sendSuccess(event.getMessage());
            } else {
                event.getGuild().getController().ban(toBan.getId(), 1, "No reason was provided").queue(
                        (v) -> ModUtils.modLog(event.getAuthor(), toBan, "banned", "*No reason was provided.*", event.getGuild())
                );
            }
        } catch (HierarchyException e) {
            //e.printStackTrace();
            event.getChannel().sendMessage("I can't ban that member because his roles are above or equals to mine.").queue();
        }
    }

    @Override
    public String getUsage() {
        return "ban (@user) [(time)(m/h/d/w/M/Y)] (Reason)";
    }

    @Override
    public String getDesc() {
        return "Bans a user and removes their messages from the last day";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("ban", "begone"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }

    private class CalculateBanTime {
        private boolean myResult;
        private GuildMessageReceivedEvent event;
        private String[] timeParts;
        private String finalUnbanDate;
        private int finalBanTime;

        CalculateBanTime(GuildMessageReceivedEvent event, String... timeParts) {
            this.event = event;
            this.timeParts = timeParts;
        }

        boolean is() {
            return myResult;
        }

        String getFinalUnbanDate() {
            return finalUnbanDate;
        }

        int getFinalBanTime() {
            return finalBanTime;
        }

        public CalculateBanTime invoke() {
            String unbanDate = "";
            int banTime; // initial value is always 0
            try {
                banTime = Integer.parseInt(timeParts[0]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(e.getMessage() + " is not a valid number").queue();
                myResult = true;
                return this;
            } catch (ArrayIndexOutOfBoundsException ignored /* https://youtube.com/DSHelmondGames */) {
                event.getChannel().sendMessage("Incorrect time format, use `" + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + "help " + getAliases().get(0) + "` for more info.").queue();
                myResult = true;
                return this;
            }
            if (banTime > 0) {
                if (timeParts.length != 2) {
                    event.getChannel().sendMessage("Incorrect time format, use `" + GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + "help " + getAliases().get(0) + "` for more info.").queue();
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

    private static void sendSuccess(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getTextChannel();
            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
                message.addReaction("✅").queue(null, ignored -> {
                });
            }
        }
    }

    private static boolean isInt(String integer) {
        return integer.matches("^\\d{1,11}$");
    }
}
