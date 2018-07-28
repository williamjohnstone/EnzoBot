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
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetBotChannel implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        Config.DB.run(() -> {
            String channel = "";
            if (args.length == 2) {
                if (args[1].equals("off")) {
                    channel = "0";
                } else {
                    channel = event.getMessage().getMentionedChannels().get(0).getId();
                }
            } else if (args.length == 1) {
                channel = event.getMessage().getChannel().getId();
            }
            if (!"0".equals(channel)) {
                GuildSettingsUtils.updateGuildSettings(event.getGuild(), GuildSettingsUtils.getGuild(event.getGuild()).setBotChannel(channel));
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Channel Set");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success, bot channel set to: " + event.getGuild().getTextChannelById(channel).getAsMention());
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                GuildSettingsUtils.updateGuildSettings(event.getGuild(), GuildSettingsUtils.getGuild(event.getGuild()).setBotChannel("0").useBotChannel(false));
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Channel Disabled");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        });
    }

    @Override
    public String getUsage() {
        return "setBotChat (#Channel) or 'setBotChat off' to disable bot channel.";
    }

    @Override
    public String getDesc() {
        return "Changes the channel the bot uses this channel is used for all commands.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("botchat", "setbotchat", "botchannel", "setbotchannel"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
