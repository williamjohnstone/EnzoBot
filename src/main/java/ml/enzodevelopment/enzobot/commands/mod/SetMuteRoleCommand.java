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

import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetMuteRoleCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (!adminCheck) {
            event.getChannel().sendMessage("You require permission to manage the server to use this command.").queue();
            return;
        }
        if (event.getMessage().getMentionedRoles().size() != 1) {
            return;
        }
        Config.DB.run(() -> {
            String role = "";
            if (args.length == 2) {
                if (args[1].equals("off")) {
                    role = "0";
                } else {
                    role = event.getMessage().getMentionedRoles().get(0).getId();
                }
            }
            if (!"0".equals(role)) {
                GuildSettingsUtils.updateGuildSettings(event.getGuild(), GuildSettingsUtils.getGuild(event.getGuild()).setMuteRoleId(role));
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Mute Role Set");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success, mute role set to: " + event.getGuild().getRoleById(role).getAsMention());
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                GuildSettingsUtils.updateGuildSettings(event.getGuild(), GuildSettingsUtils.getGuild(event.getGuild()).setMuteRoleId("0"));
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Mute Role Disabled");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        });
    }

    @Override
    public String getUsage() {
        return "setmuterole (@role)";
    }

    @Override
    public String getDesc() {
        return "Sets the role that mute command uses";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setmuterole", "muterole"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
